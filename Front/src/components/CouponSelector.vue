<template>
  <div class="coupon-selector">
    <div class="d-flex justify-content-between align-items-center mb-3">
      <h6 class="mb-0">折價券</h6>
      <button class="btn btn-sm btn-outline-primary" @click="showModal = true">
        選擇其他
      </button>
    </div>

    <!-- 已選擇的優惠券 -->
    <div v-if="selectedCoupons.length > 0" class="selected-coupons">
      <div v-for="coupon in selectedCoupons" :key="coupon.id" class="selected-coupon-tag">
        <span>{{ getCouponDisplay(coupon) }}</span>
        <button class="btn-remove" @click="removeCoupon(coupon.id)">
          <i class="bi bi-x"></i>
        </button>
      </div>
    </div>
    <div v-else class="text-muted small">
      尚未選擇優惠券
    </div>

    <!-- 優惠券選擇彈窗 -->
    <div v-if="showModal" class="modal d-block" tabindex="-1" style="background: rgba(0,0,0,0.5);">
      <div class="modal-dialog modal-dialog-scrollable">
        <div class="modal-content">
          <div class="modal-header">
            <h5 class="modal-title">選擇優惠券</h5>
            <button type="button" class="btn-close" @click="closeModal"></button>
          </div>
          <div class="modal-body">
            <!-- 載入中 -->
            <div v-if="loading" class="text-center py-4">
              <div class="spinner-border spinner-border-sm" role="status"></div>
            </div>

            <!-- 優惠券列表 -->
            <div v-else-if="availableCoupons.length > 0">
              <div
                v-for="coupon in availableCoupons"
                :key="coupon.id"
                class="coupon-item"
                :class="{ 'selected': isSelected(coupon.id) }"
                @click="toggleCoupon(coupon)"
              >
                <div class="form-check">
                  <input
                    class="form-check-input"
                    type="checkbox"
                    :checked="isSelected(coupon.id)"
                    :id="`coupon-${coupon.id}`"
                  />
                  <label class="form-check-label w-100" :for="`coupon-${coupon.id}`">
                    <div class="d-flex justify-content-between align-items-center">
                      <div>
                        <strong>{{ getCouponDisplay(coupon) }}</strong>
                        <div class="small text-muted">{{ coupon.couponName }}</div>
                        <div class="small text-muted">有效期：{{ formatDate(coupon.expiryDate) }}</div>
                      </div>
                    </div>
                  </label>
                </div>
              </div>
            </div>

            <!-- 無優惠券 -->
            <div v-else class="text-center text-muted py-4">
              目前沒有可用的優惠券
            </div>
          </div>
          <div class="modal-footer">
            <button type="button" class="btn btn-secondary" @click="closeModal">取消</button>
            <button type="button" class="btn btn-primary" @click="confirmSelection">確定</button>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { storeToRefs } from 'pinia'
import useUserStore from '@/stores/user.js'
import { fetchMyAvailableCoupons } from '@/api/couponApi'

const props = defineProps({
  modelValue: {
    type: Array,
    default: () => []
  }
})

const emit = defineEmits(['update:modelValue'])

const userStore = useUserStore()
const { token } = storeToRefs(userStore)

const showModal = ref(false)
const loading = ref(false)
const availableCoupons = ref([])
const selectedCoupons = ref([...props.modelValue])

// 載入可用優惠券
async function loadCoupons() {
  const userId = getUserId()
  if (!userId) return

  loading.value = true
  try {
    availableCoupons.value = await fetchMyAvailableCoupons(userId)
  } catch (e) {
    console.error('載入優惠券失敗', e)
  } finally {
    loading.value = false
  }
}

// 解析 JWT 取得 userId
function getUserId() {
  if (!token.value) return null
  try {
    const base64Url = token.value.split('.')[1]
    const base64 = base64Url.replace(/-/g, '+').replace(/_/g, '/')
    const jsonPayload = decodeURIComponent(atob(base64).split('').map(c => {
      return '%' + ('00' + c.charCodeAt(0).toString(16)).slice(-2)
    }).join(''))
    return JSON.parse(jsonPayload).sub
  } catch (e) {
    return null
  }
}

// 是否已選擇
function isSelected(couponId) {
  return selectedCoupons.value.some(c => c.id === couponId)
}

// 切換選擇
function toggleCoupon(coupon) {
  const index = selectedCoupons.value.findIndex(c => c.id === coupon.id)
  if (index > -1) {
    selectedCoupons.value.splice(index, 1)
  } else {
    selectedCoupons.value.push(coupon)
  }
}

// 移除優惠券
function removeCoupon(couponId) {
  const index = selectedCoupons.value.findIndex(c => c.id === couponId)
  if (index > -1) {
    selectedCoupons.value.splice(index, 1)
    emit('update:modelValue', selectedCoupons.value)
  }
}

// 關閉彈窗
function closeModal() {
  showModal.value = false
}

// 確認選擇
function confirmSelection() {
  emit('update:modelValue', selectedCoupons.value)
  showModal.value = false
}

// 取得優惠券顯示文字
function getCouponDisplay(coupon) {
  const type = coupon.discountType
  const value = coupon.discountValue

  if (type === 1 || type === '1') {
    return `NT$${value}`
  } else if (type === 2 || type === '2') {
    const percentage = (1 - parseFloat(value)) * 100
    return `${(percentage * 0.1).toFixed(1).replace('.0', '')}折`
  }
  return coupon.code
}

// 格式化日期
function formatDate(dateStr) {
  if (!dateStr) return ''
  return new Date(dateStr).toLocaleString('zh-TW', {
    year: 'numeric',
    month: '2-digit',
    day: '2-digit',
    hour: '2-digit',
    minute: '2-digit'
  })
}

onMounted(() => {
  loadCoupons()
})
</script>

<style scoped>
.selected-coupons {
  display: flex;
  flex-wrap: wrap;
  gap: 8px;
}

.selected-coupon-tag {
  display: inline-flex;
  align-items: center;
  gap: 4px;
  padding: 4px 8px;
  background: #e7f3ff;
  border: 1px solid #0d6efd;
  border-radius: 4px;
  font-size: 0.9rem;
}

.btn-remove {
  border: none;
  background: transparent;
  padding: 0;
  line-height: 1;
  cursor: pointer;
  color: #dc3545;
}

.coupon-item {
  padding: 12px;
  border: 1px solid #dee2e6;
  border-radius: 4px;
  margin-bottom: 8px;
  cursor: pointer;
  transition: all 0.2s;
}

.coupon-item:hover {
  background: #f8f9fa;
}

.coupon-item.selected {
  background: #e7f3ff;
  border-color: #0d6efd;
}

.modal {
  display: block;
}
</style>

