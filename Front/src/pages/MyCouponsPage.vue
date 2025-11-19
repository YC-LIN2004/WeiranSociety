  <template>
    <div class="container py-4">
      <h2 class="text-center mb-4">我的優惠券</h2>

      <!-- 標籤頁 -->
      <ul class="nav nav-tabs mb-4">
        <li class="nav-item">
          <a class="nav-link" :class="{ active: activeTab === 'available' }" @click="activeTab = 'available'" href="javascript:;">
            可使用
          </a>
        </li>
        <li class="nav-item">
          <a class="nav-link" :class="{ active: activeTab === 'used' }" @click="activeTab = 'used'" href="javascript:;">
            已使用
          </a>
        </li>
        <li class="nav-item">
          <a class="nav-link" :class="{ active: activeTab === 'expired' }" @click="activeTab = 'expired'" href="javascript:;">
            已過期
          </a>
        </li>
      </ul>

      <!-- 載入中 -->
      <div v-if="loading" class="text-center py-5">
        <div class="spinner-border" role="status">
          <span class="visually-hidden">Loading...</span>
        </div>
      </div>

      <!-- 錯誤訊息 -->
      <div v-if="error" class="alert alert-danger">{{ error }}</div>

      <!-- 優惠券列表 -->
      <div v-if="!loading && !error">
        <!-- 可使用 -->
        <div v-if="activeTab === 'available'">
          <div v-if="availableCoupons.length === 0" class="text-center py-5">
            <div class="empty-box">
              <i class="bi bi-inbox" style="font-size: 4rem; color: #ccc;"></i>
              <p class="mt-3 text-muted">目前沒有優惠券</p>
            </div>
          </div>
          <div v-else class="row g-3">
            <div v-for="coupon in availableCoupons" :key="coupon.id" class="col-12">
              <MyCouponCard :coupon="coupon" />
            </div>
          </div>
        </div>

        <!-- 已使用 -->
        <div v-if="activeTab === 'used'">
          <div v-if="usedCoupons.length === 0" class="text-center py-5">
            <div class="empty-box">
              <i class="bi bi-inbox" style="font-size: 4rem; color: #ccc;"></i>
              <p class="mt-3 text-muted">目前沒有優惠券</p>
            </div>
          </div>
          <div v-else class="row g-3">
            <div v-for="coupon in usedCoupons" :key="coupon.id" class="col-12">
              <MyCouponCard :coupon="coupon" />
            </div>
          </div>
        </div>

        <!-- 已過期 -->
        <div v-if="activeTab === 'expired'">
          <div v-if="expiredCoupons.length === 0" class="text-center py-5">
            <div class="empty-box">
              <i class="bi bi-inbox" style="font-size: 4rem; color: #ccc;"></i>
              <p class="mt-3 text-muted">目前沒有優惠券</p>
            </div>
          </div>
          <div v-else class="row g-3">
            <div v-for="coupon in expiredCoupons" :key="coupon.id" class="col-12">
              <MyCouponCard :coupon="coupon" />
            </div>
          </div>
        </div>
      </div>
    </div>
  </template>

  <script setup>
  import { ref, watch, onMounted } from 'vue'
  import { storeToRefs } from 'pinia'
  import useUserStore from '@/stores/user.js'
  import { fetchMyAvailableCoupons, fetchMyUsedCoupons, fetchMyExpiredCoupons } from '@/api/couponApi'
  import MyCouponCard from '@/components/MyCouponCard.vue'

  const userStore = useUserStore()
  const { token } = storeToRefs(userStore)

  const activeTab = ref('available')
  const loading = ref(false)
  const error = ref('')

  const availableCoupons = ref([])
  const usedCoupons = ref([])
  const expiredCoupons = ref([])

  // 解析 JWT 取得 userId
  function getUserId() {
    if (!token.value) return null
    try {
      const base64Url = token.value.split('.')[1]
      const base64 = base64Url.replace(/-/g, '+').replace(/_/g, '/')
      const jsonPayload = decodeURIComponent(atob(base64).split('').map(c => {
        return '%' + ('00' + c.charCodeAt(0).toString(16)).slice(-2)
      }).join(''))
      const payload = JSON.parse(jsonPayload)
      return payload.sub
    } catch (e) {
      return null
    }
  }

  // 載入優惠券
  async function loadCoupons() {
    const userId = getUserId()
    if (!userId) {
      error.value = '請先登入'
      return
    }

    loading.value = true
    error.value = ''

    try {
      if (activeTab.value === 'available') {
        availableCoupons.value = await fetchMyAvailableCoupons(userId)
      } else if (activeTab.value === 'used') {
        usedCoupons.value = await fetchMyUsedCoupons(userId)
      } else if (activeTab.value === 'expired') {
        expiredCoupons.value = await fetchMyExpiredCoupons(userId)
      }
    } catch (e) {
      error.value = e?.response?.data?.message || '載入失敗'
    } finally {
      loading.value = false
    }
  }

  // 監聽 tab 切換
  watch(activeTab, () => {
    loadCoupons()
  })

  onMounted(() => {
    loadCoupons()
  })
  </script>

  <style scoped>
  .nav-tabs .nav-link {
    cursor: pointer;
  }

  .empty-box {
    padding: 40px;
  }
  </style>

