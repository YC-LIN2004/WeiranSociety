<template>
  <div class="container my-5">
    <h3 class="mb-4 fw-bold">訂單紀錄</h3>

    <!-- 狀態切換 -->
    <div class="d-flex gap-3 mb-4">
      <button v-for="s in statusTabs" :key="s.value" class="btn"
        :class="s.value === selectedStatus ? 'btn-success text-white fw-bold' : 'btn-outline-secondary'"
        @click="changeStatus(s.value)">
        <i :class="s.icon"></i>
        <span class="ms-2">{{ s.label }}</span>
      </button>
    </div>

    <!-- 載入中 -->
    <div v-if="loading" class="text-center py-5">
      <div class="spinner-border text-success" role="status">
        <span class="visually-hidden">載入中...</span>
      </div>
      <p class="mt-3 text-muted">正在載入訂單...</p>
    </div>

    <!-- 訂單清單 -->
    <div v-else-if="orders.length > 0">
      <div v-for="order in orders" :key="order.orderID" class="card mb-3 shadow-sm p-3">

        <!-- 訂單標頭 -->
        <div class="d-flex justify-content-between align-items-center mb-2">
          <div>
            <strong>訂單編號：</strong>{{ order.orderID }}
            <span v-if="order.paymentMethod === 'ECPay'" class="badge bg-info ms-2">綠界支付</span>
          </div>
          <span class="badge" :class="getStatusBadgeClass(order.orderStatus)">
            {{ getDisplayStatus(order.orderStatus) }}
          </span>
        </div>

        <!-- 訂單主要資訊 -->
        <div class="d-flex justify-content-between align-items-center">
          <div class="text-muted small">
            建立時間：{{ formatDate(order.createdAt) }}
          </div>
          <div class="text-end">
            <div class="fw-bold text-danger">
              金額：NT${{ formatNumber(order.totalAmount) }}
            </div>
            <div v-if="order.discountAmount > 0" class="text-success small">
              折扣金額：-NT${{ formatNumber(order.discountAmount) }}
            </div>
            <div class="fw-bold">
              實付金額：NT${{ formatNumber(calculateNetAmount(order)) }}
            </div>

            <div class="mt-2">
              <button class="btn btn-outline-secondary btn-sm me-2" @click="toggleExpand(order.orderID)">
                {{ expandedOrders.has(order.orderID) ? '收合明細' : '查看明細' }}
              </button>
            </div>
          </div>
        </div>

        <!-- 展開明細 -->
        <transition name="fade">
          <div v-if="expandedOrders.has(order.orderID)" class="mt-3 border-top pt-3">
            <div v-if="order.items && order.items.length > 0">
              <div v-for="item in order.items" :key="item.courseId || 'ecpay'" class="d-flex align-items-center mb-3">
                <img :src="item.coverUrl || defaultImage" class="rounded me-3 border"
                  style="width: 80px; height: 80px; object-fit: cover;" />
                <div class="flex-grow-1">
                  <div class="fw-bold">{{ item.courseTitle || '綠界支付訂單' }}</div>
                  <div class="text-muted small">NT${{ formatNumber(item.unitPrice) }}</div>
                </div>
              </div>
            </div>
            <div v-else class="text-muted text-center py-3">
              此訂單無商品明細
            </div>
          </div>
        </transition>
      </div>
    </div>

    <!-- 空清單 -->
    <div v-else class="text-center text-muted py-5">
      <img src="https://cdn-icons-png.flaticon.com/512/10126/10126636.png" alt="empty"
        style="width: 100px; opacity: 0.5;" />
      <p class="mt-3">哎呀，目前沒有「{{ activeTabLabel }}」的訂單唷～</p>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted, computed } from 'vue'
import { useRouter } from 'vue-router'
import axios from 'axios'
import useUserStore from '@/stores/user'

const userStore = useUserStore()
const router = useRouter()
const userId = userStore.userId
const baseUrl = 'http://localhost:8080/api/orders/user'

// 狀態 tabs
const statusTabs = [
  { label: '已完成訂單', value: '完成', icon: 'bi bi-check-circle' },
  { label: '等待付款', value: '未付款', icon: 'bi bi-clock' },
  { label: '失效訂單', value: '已取消', icon: 'bi bi-x-circle' },
]

const selectedStatus = ref('完成')
const orders = ref([])
const loading = ref(false)
const expandedOrders = ref(new Set())
const defaultImage = '/images/spring-boot.jpg'

onMounted(() => {
  console.log('✅ OrderHistoryPage 載入，userId:', userId)
  fetchOrders()
})

async function fetchOrders() {
  if (!userId) {
    console.error('❌ 用戶未登入')
    alert('請先登入')
    router.push('/login')
    return
  }

  loading.value = true
  try {
    console.log('📤 發送訂單查詢請求:', {
      url: `${baseUrl}/${userId}`,
      status: selectedStatus.value
    })

    const res = await axios.get(`${baseUrl}/${userId}`, {
      params: { status: selectedStatus.value },
    })

    console.log('📥 收到訂單資料:', res.data)
    orders.value = res.data || []
    console.log(`✅ 載入 ${orders.value.length} 筆訂單`)

  } catch (err) {
    console.error('❌ 載入訂單失敗', err)
    console.error('錯誤詳情:', err.response?.data)
    orders.value = []
    alert('載入訂單失敗，請稍後再試')
  } finally {
    loading.value = false
  }
}

function changeStatus(status) {
  console.log('🔄 切換狀態:', status)
  selectedStatus.value = status
  expandedOrders.value.clear()
  fetchOrders()
}

function toggleExpand(orderID) {
  if (expandedOrders.value.has(orderID)) {
    expandedOrders.value.delete(orderID)
  } else {
    expandedOrders.value.add(orderID)
  }
}

// ✅ 新增：將 Pending 狀態視為「完成」顯示
function getDisplayStatus(status) {
  // 如果是 Pending 或付款中，在測試環境下視為「完成」
  if (status === 'Pending' || status === '付款中') {
    return '完成'
  }
  return status
}

function formatDate(dateString) {
  if (!dateString) return '-'
  const d = new Date(dateString)
  return d.toLocaleString('zh-TW', { hour12: false })
}

function formatNumber(n) {
  return Number(n || 0).toLocaleString()
}

function calculateNetAmount(order) {
  const total = Number(order.totalAmount) || 0
  const discount = Number(order.discountAmount) || 0
  return Math.max(0, total - discount)
}

function getStatusBadgeClass(status) {
  // ✅ 修改：Pending 和付款中也使用成功的樣式
  if (status === 'Pending' || status === '付款中') {
    return 'bg-success'
  }

  switch (status) {
    case '完成':
      return 'bg-success'
    case '未付款':
      return 'bg-warning text-dark'
    case '已取消':
      return 'bg-danger'
    default:
      return 'bg-secondary'
  }
}

const activeTabLabel = computed(() => {
  const tab = statusTabs.find(t => t.value === selectedStatus.value)
  return tab ? tab.label : ''
})
</script>

<style scoped>
.btn {
  flex: 1;
  border-radius: 10px;
  padding: 10px;
}

.fade-enter-active,
.fade-leave-active {
  transition: opacity 0.3s ease;
}

.fade-enter-from,
.fade-leave-to {
  opacity: 0;
}

.spinner-border {
  width: 3rem;
  height: 3rem;
}
</style>