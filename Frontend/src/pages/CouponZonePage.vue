<template>
  <div class="container py-4">
    <h2 class="text-center mb-4">優惠券專區</h2>

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
      <!-- ==================== 限時89折 ==================== -->
      <div v-if="coupons89.length > 0" class="mb-5">
        <h4 class="mb-3">限時89折</h4>
        <div class="row g-3">
          <div v-for="coupon in coupons89" :key="coupon.id" class="col-12">
            <!-- 優惠券卡片組件 -->
            <CouponCard 
              :coupon="coupon" 
              :isAlreadyClaimed="isAlreadyClaimed(coupon.id)"
              @claim="handleClaim" 
            />
          </div>
        </div>
      </div>

      <!-- ==================== 限時79折 ==================== -->
      <div v-if="coupons79.length > 0" class="mb-5">
        <h4 class="mb-3">限時79折</h4>
        <div class="row g-3">
          <div v-for="coupon in coupons79" :key="coupon.id" class="col-12">
            <!-- 優惠券卡片組件 -->
            <CouponCard 
              :coupon="coupon" 
              :isAlreadyClaimed="isAlreadyClaimed(coupon.id)"
              @claim="handleClaim" 
            />
          </div>
        </div>
      </div>

      <!-- ==================== 其他優惠券 ==================== -->
      <div v-if="otherCoupons.length > 0" class="mb-5">
        <h4 class="mb-3">其他優惠</h4>
        <div class="row g-3">
          <div v-for="coupon in otherCoupons" :key="coupon.id" class="col-12">
            <!-- 優惠券卡片組件 -->
            <CouponCard 
              :coupon="coupon" 
              :isAlreadyClaimed="isAlreadyClaimed(coupon.id)"
              @claim="handleClaim" 
            />
          </div>
        </div>
      </div>

      <!-- 無優惠券提示 -->
      <div v-if="coupons.length === 0" class="text-center text-muted py-5">
        <p class="fs-5">目前沒有可領取的優惠券</p>
      </div>
    </div>

    <!-- ==================== 成功領取彈窗 ==================== -->
    <div v-if="showSuccessModal" class="modal d-block" tabindex="-1" style="background: rgba(0,0,0,0.5);">
      <div class="modal-dialog modal-dialog-centered">
        <div class="modal-content">
          <div class="modal-header">
            <h5 class="modal-title">成功領取優惠券</h5>
            <button type="button" class="btn-close" @click="closeModal"></button>
          </div>
          <div class="modal-body text-center py-4">
            <i class="bi bi-check-circle-fill text-success" style="font-size: 4rem;"></i>
            <p class="mt-3 mb-0">優惠券已加入「我的優惠券」</p>
          </div>
          <div class="modal-footer">
            <button type="button" class="btn btn-primary" @click="confirmModal">確定</button>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
// ==================== Import 區 ====================
// 引入 Vue Composition API
import { ref, computed, onMounted } from 'vue'
// 引入 Pinia store 輔助函數
import { storeToRefs } from 'pinia'
// 引入用戶 store（取得 token）
import useUserStore from '@/stores/user.js'
// 引入優惠券相關 API
import { fetchAvailableCoupons, claimCoupon, fetchMyAvailableCoupons } from '@/api/couponApi'
// 引入優惠券卡片組件
import CouponCard from '@/components/CouponCard.vue'

// ==================== Store 初始化 ====================
// 取得用戶 store 實例
const userStore = useUserStore()
// 使用 storeToRefs 解構出 token（保持響應式）
const { token } = storeToRefs(userStore)

// ==================== 響應式資料 ====================
// 載入狀態
const loading = ref(false)
// 錯誤訊息
const error = ref('')
// 所有優惠券列表
const coupons = ref([])
// 控制成功領取彈窗的顯示
const showSuccessModal = ref(false)
// 用戶已領取的優惠券ID列表
const claimedCouponIds = ref([])

// ==================== 計算屬性 ====================
// 89折優惠券：篩選 discountValue 為 0.11 的券（1 - 0.11 = 0.89 = 89折）
const coupons89 = computed(() => {
  return coupons.value.filter(c => {
    const discount = c.couponType?.discountValue
    return discount && discount.toString() === '0.11'
  })
})

// 79折優惠券：篩選 discountValue 為 0.21 的券（1 - 0.21 = 0.79 = 79折）
const coupons79 = computed(() => {
  return coupons.value.filter(c => {
    const discount = c.couponType?.discountValue
    return discount && discount.toString() === '0.21'
  })
})

// 其他優惠券：不是 89折 也不是 79折 的券
const otherCoupons = computed(() => {
  return coupons.value.filter(c => {
    const discount = c.couponType?.discountValue
    return !discount || (discount.toString() !== '0.11' && discount.toString() !== '0.21')
  })
})

// ==================== 方法 ====================
// 判斷優惠券是否已領取
function isAlreadyClaimed(couponId) {
  return claimedCouponIds.value.includes(couponId)
}

// 載入可領取的優惠券列表
async function loadCoupons() {
  // 開始載入
  loading.value = true
  // 清空錯誤訊息
  error.value = ''
  try {
    // 調用 API 取得可用優惠券
    const data = await fetchAvailableCoupons()
    coupons.value = data || []
    
    // 載入用戶已領取的優惠券列表
    const userId = getUserId()
    if (userId) {
      const myCoupons = await fetchMyAvailableCoupons(userId)
      // 提取已領取的優惠券ID
      claimedCouponIds.value = myCoupons.map(uc => uc.couponId)
    }
  } catch (e) {
    // 捕捉錯誤，設定錯誤訊息
    error.value = e?.response?.data?.message || '載入失敗'
  } finally {
    // 無論成功或失敗，結束載入狀態
    loading.value = false
  }
}

// 處理領取優惠券
async function handleClaim(couponId) {
  // 檢查用戶是否已登入（有 token）
  if (!token.value) {
    alert('請先登入')
    return
  }

  // 從 JWT token 中解析出 userId
  const userId = getUserId()
  if (!userId) {
    alert('無法取得用戶資訊')
    return
  }

  try {
    // 調用 API 領取優惠券
    const response = await claimCoupon(userId, couponId)
    // 判斷是否成功
    if (response.success) {
      // 領取成功，將優惠券ID加入已領取列表
      claimedCouponIds.value.push(couponId)
      // 顯示成功彈窗
      showSuccessModal.value = true
    } else {
      // 顯示後端返回的錯誤訊息
      alert(response.message || '領取失敗')
    }
  } catch (e) {
    // 捕捉錯誤，顯示錯誤訊息
    const msg = e?.response?.data?.message || '領取失敗'
    alert(msg)
  }
}

// 關閉成功彈窗
function closeModal() {
  showSuccessModal.value = false
}

// 確認彈窗
function confirmModal() {
  // 關閉彈窗
  showSuccessModal.value = false
  // 可選：導航到「我的優惠券」頁面（目前被註解）
  // router.push('/my-coupons')
}

// ==================== 工具函數 ====================
// 解析 JWT token 取得 userId
function getUserId() {
  if (!token.value) return null
  try {
    const payload = parseJwt(token.value)
    return payload.sub
  } catch (e) {
    return null
  }
}

// 解析 JWT token 取得 payload 資料
function parseJwt(token) {
  try {
    // JWT 格式：header.payload.signature
    // 取得第二部分（payload）
    const base64Url = token.split('.')[1]
    // 將 Base64URL 格式轉換為 Base64 格式
    const base64 = base64Url.replace(/-/g, '+').replace(/_/g, '/')
    // 解碼 Base64 並處理 Unicode 字元
    const jsonPayload = decodeURIComponent(atob(base64).split('').map(c => {
      // 將每個字元轉為 %XX 格式
      return '%' + ('00' + c.charCodeAt(0).toString(16)).slice(-2)
    }).join(''))
    // 將 JSON 字串解析為物件並返回
    return JSON.parse(jsonPayload)
  } catch (e) {
    // 解析失敗，返回空物件
    return {}
  }
}

// ==================== 生命週期 ====================
// 組件掛載後執行
onMounted(() => {
  // 載入優惠券列表
  loadCoupons()
})
</script>

<style scoped>
/* 強制 modal 顯示（Bootstrap modal 預設為 display: none） */
.modal {
  display: block;
}
</style>