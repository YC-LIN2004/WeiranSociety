<template>
  <div class="container my-4">
    <h3 class="mb-3">購物車</h3>

    <div class="row">
      <!-- 左側商品清單 -->
      <div class="col-lg-8">
        <div class="card p-3">
          <!-- 全選 -->
          <div class="form-check mb-2">
            <input
              class="form-check-input"
              type="checkbox"
              id="selectAll"
              :checked="cartStore.allSelected"
              @change="(e) => cartStore.toggleSelectAll(e.target.checked)"
            />
            <label class="form-check-label" for="selectAll">全選</label>
          </div>

          <!-- 商品列表 -->
          <div
            v-for="(item, index) in cartStore.items"
            :key="item.id"
            class="d-flex align-items-center border-bottom py-3"
          >
            <div class="form-check me-3">
              <input
                class="form-check-input"
                type="checkbox"
                v-model="item.selected"
              />
            </div>

            <img
              :src="item.image || '/images/spring-boot.jpg'"
              @error="e => e.target.src = '/images/spring-boot.jpg'"
              alt="商品圖片"
              class="rounded"
              style="width: 120px; height: 80px; object-fit: cover"
            />

            <div class="flex-grow-1 ms-3">
              <small class="badge bg-info text-dark">課程</small>
              <RouterLink
                :to="`/coursedetail/${item.id}`"
                class="fw-bold text-decoration-none text-dark ms-1"
              >
                {{ item.title }}
              </RouterLink>
            </div>

            <div
              class="fw-bold ms-3"
              style="width: 120px; text-align: right;"
            >
              NT${{ item.price.toLocaleString() }}
            </div>

            <div class="ms-3 text-muted" style="font-size: 0.9rem;">
              <div style="cursor:pointer" @click="cartStore.removeItem(item.cartDetailId)">
                刪除
              </div>
            </div>
          </div>

          <div v-if="cartStore.items.length === 0" class="text-center py-4 text-muted">
            購物車目前沒有商品。
          </div>
        </div>
      </div>

      <!-- 右側訂單明細 -->
      <div class="col-lg-4">
        <div class="card p-3">
          <h5 class="mb-3">訂單明細</h5>

          <CouponSelector v-model="selectedCoupons" class="mb-3" />

          <hr />

          <div class="mb-2">
            <span>{{ cartStore.selectedCount }} 件小計</span>
            <span class="float-end">NT${{ cartStore.totalPrice.toLocaleString() }}</span>
          </div>

          <div v-if="discountAmount > 0" class="mb-2 text-danger">
            <span>優惠券折抵</span>
            <span class="float-end">-NT${{ discountAmount.toLocaleString() }}</span>
          </div>

          <hr />

          <div class="mb-3">
            <span class="fw-bold">總計</span>
            <span class="float-end fw-bold text-danger fs-5">
              NT${{ finalAmount.toLocaleString() }}
            </span>
          </div>

          <button class="btn btn-primary w-100" @click="checkout">
            去結帳
          </button>
        </div>
      </div>
    </div>

    <!-- Bootstrap Modal -->
    <div
      class="modal fade"
      id="emptyCartModal"
      tabindex="-1"
      aria-labelledby="emptyCartModalLabel"
      aria-hidden="true"
    >
      <div class="modal-dialog modal-dialog-centered">
        <div class="modal-content">
          <div class="modal-header">
            <h5 class="modal-title" id="emptyCartModalLabel">購物車提醒</h5>
            <button
              type="button"
              class="btn-close"
              data-bs-dismiss="modal"
              aria-label="Close"
            ></button>
          </div>
          <div class="modal-body text-center">
            您的購物車還沒有商品，請先將商品加入購物車。
          </div>
          <div class="modal-footer">
            <button type="button" class="btn btn-secondary" data-bs-dismiss="modal">
              關閉
            </button>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, computed, watch, onMounted } from 'vue'
import { useCartStore } from '@/stores/cartStore'
import { useRouter } from 'vue-router'
import { storeToRefs } from 'pinia'
import useUserStore from '@/stores/user.js'
import CouponSelector from '@/components/CouponSelector.vue'
import { calculateCouponDiscount } from '@/api/couponApi'

// ✅ 引入 Bootstrap JS (for Modal)
import * as bootstrap from 'bootstrap'

const cartStore = useCartStore()
const userStore = useUserStore()
const { token } = storeToRefs(userStore)
const router = useRouter()

const selectedCoupons = ref([])
const discountAmount = ref(0)

// 載入購物車資料
onMounted(() => {
  const userId = getUserId()
  if (!userId) {
    console.warn('⚠️ 無法載入購物車，userId 不存在')
    return
  }
  console.log('🛒 載入購物車 userId:', userId)
  cartStore.fetchCarts(userId)
})

// 計算最終金額
const finalAmount = computed(() => {
  return Math.max(0, cartStore.totalPrice - discountAmount.value)
})

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

// 監聽優惠券變化
watch([selectedCoupons, () => cartStore.totalPrice], async () => {
  if (selectedCoupons.value.length === 0 || cartStore.totalPrice === 0) {
    discountAmount.value = 0
    return
  }

  const userId = getUserId()
  if (!userId) return

  try {
    const request = {
      userId: parseInt(userId),
      userCouponIds: selectedCoupons.value.map(c => c.id),
      totalAmount: cartStore.totalPrice
    }

    const response = await calculateCouponDiscount(request)
    discountAmount.value = response.discountAmount || 0

    if (response.message) {
      console.warn('優惠券提示：', response.message)
    }
  } catch (e) {
    console.error('計算折扣失敗', e)
    discountAmount.value = 0
  }
}, { deep: true })

// ✅ 修正版 checkout 函數
function checkout() {
  console.log('=== 開始結帳流程 ===')

  // 檢查是否有選中商品
  if (cartStore.selectedCount === 0) {
    const modalEl = document.getElementById('emptyCartModal')
    const modal = new bootstrap.Modal(modalEl)
    modal.show()
    return
  }

  try {
    const selectedItems = cartStore.items.filter(item => item.selected)

    if (selectedItems.length === 0) {
      alert('請選擇要結帳的商品')
      return
    }

    // 2️⃣ 準備結帳資料
    const checkoutData = selectedItems.map(item => ({
      id: item.id || item.cartDetailId,
      cartDetailId: item.cartDetailId,
      name: item.title || item.name,
      price: item.price,
      quantity: item.quantity || 1,
      image: item.image || '/images/spring-boot.jpg'
    }))

    // 3️⃣ 儲存資料到 localStorage
    localStorage.setItem('checkoutItems', JSON.stringify(checkoutData))
    localStorage.setItem('checkoutDiscount', discountAmount.value.toString())
    localStorage.setItem('checkoutTotal', finalAmount.value.toString())
    localStorage.setItem('checkoutOriginalTotal', cartStore.totalPrice.toString())

    // 4️⃣ 同步 pinia store
    cartStore.selectedCoupons = selectedCoupons.value
    cartStore.discountAmount = discountAmount.value
    cartStore.finalAmount = finalAmount.value

    console.log('✅ 資料已儲存到 localStorage')

    // 5️⃣ 跳轉
    router.push('/checkout')

  } catch (error) {
    console.error('❌ 結帳失敗:', error)
    alert('結帳失敗，請稍後再試')
  }
}
</script>

<style scoped>
/* 可自行加上樣式 */
</style>
