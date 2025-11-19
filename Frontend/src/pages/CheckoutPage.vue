<script setup>
import { ref, onMounted, computed } from 'vue'
import { useRouter } from 'vue-router'
import paymentService from '@/services/paymentService'
import useUserStore from '@/stores/user'

const router = useRouter()
const auth = useUserStore()
const selectedPayment = ref('ALL')
const isProcessing = ref(false)
const paymentFormContainer = ref(null)
const orderItems = ref([])
const phoneNumber = ref('')  // ✅ 預設為空，使用者手填
const email = ref('')
const discountAmount = ref(0)

// 載入資料
onMounted(() => {
  console.log('=== CheckoutPage 載入 ===')

  // 檢查登入
  if (!auth.userId || !auth.token) {
    alert('請先登入')
    router.push('/login')
    return
  }

  // 載入商品
  const checkoutItems = localStorage.getItem('checkoutItems')
  if (checkoutItems) {
    orderItems.value = JSON.parse(checkoutItems)
    console.log('✅ 載入商品:', orderItems.value.length, '項')
  } else {
    console.warn('⚠️ 沒有商品資料')
  }

  // 載入折扣
  const savedDiscount = localStorage.getItem('checkoutDiscount')
  if (savedDiscount) {
    discountAmount.value = parseFloat(savedDiscount)
    console.log('✅ 載入折扣:', discountAmount.value)
  }

  // 自動填入 Email
  if (auth.email) {
    email.value = auth.email
    console.log('✅ 自動填入 Email:', email.value)
  }

  // ✅ 嘗試從 localStorage 載入手機號碼（如果之前填過）
  const savedPhone = localStorage.getItem('userPhone')
  if (savedPhone) {
    phoneNumber.value = savedPhone
    console.log('✅ 自動填入手機號碼:', phoneNumber.value)
  } else {
    console.log('ℹ️ 請手動輸入手機號碼')
  }
})

// 計算原始總金額
const originalTotal = computed(() => {
  return orderItems.value.reduce((sum, item) => {
    return sum + (item.price * (item.quantity || 1))
  }, 0)
})

// 計算最終金額
const finalTotal = computed(() => {
  const total = originalTotal.value - discountAmount.value
  return Math.max(0, total)
})

// 取得商品名稱
const getItemNames = () => {
  return orderItems.value.map(item => item.name).join('#') || '課程'
}

// 驗證手機號碼格式
const isValidPhone = computed(() => {
  const phoneRegex = /^09\d{8}$/
  return phoneRegex.test(phoneNumber.value)
})

const proceedToPayment = async () => {
  if (isProcessing.value) return

  // 驗證手機號碼
  if (!phoneNumber.value) {
    alert('請輸入手機號碼')
    document.getElementById('phone')?.focus()
    return
  }

  if (!isValidPhone.value) {
    alert('請輸入正確的手機號碼格式\n格式：09 開頭，共 10 碼\n例如：0912345678')
    document.getElementById('phone')?.focus()
    return
  }

  // 驗證 Email
  if (!email.value) {
    alert('請輸入 Email')
    document.getElementById('email')?.focus()
    return
  }

  const emailRegex = /^[^\s@]+@[^\s@]+\.[^\s@]+$/
  if (!emailRegex.test(email.value)) {
    alert('請輸入正確的 Email 格式')
    document.getElementById('email')?.focus()
    return
  }

  isProcessing.value = true

  try {
    // ✅ 儲存手機號碼和 Email（下次自動帶入）
    localStorage.setItem('userPhone', phoneNumber.value)
    localStorage.setItem('userEmail', email.value)
    console.log('✅ 已儲存聯絡資訊供下次使用')

    const paymentData = {
      userId: auth.userId,
      amount: finalTotal.value,
      itemName: getItemNames(),
      paymentMethod: selectedPayment.value || 'ALL',
      phoneNumber: phoneNumber.value,
      email: email.value,
      username: auth.username || '會員',
      items: orderItems.value,
      discountAmount: discountAmount.value
    }

    console.log('📤 發送付款請求:', paymentData)

    const response = await paymentService.createPayment(paymentData)

    if (response.orderId) {
      localStorage.setItem('currentOrderId', response.orderId)
    }

    const formHtml = response.htmlForm || response.paymentForm || response.form || response.html

    if (formHtml) {
      paymentFormContainer.value.innerHTML = formHtml
      const form = paymentFormContainer.value.querySelector('form')
      if (form) {
        console.log('✅ 導向綠界付款頁面')
        setTimeout(() => form.submit(), 100)
      } else {
        throw new Error('找不到付款表單')
      }
    } else {
      throw new Error('未收到付款表單')
    }
  } catch (error) {
    console.error('❌ 付款失敗:', error)
    alert(error.message || '付款處理失敗，請稍後再試')
  } finally {
    isProcessing.value = false
  }
}


// function confirmPayment() {
//   cartStore.clearImmediatePurchase?.()
//   router.push('/payment-fail')
// }
</script>

<template>
  <div class="checkout-page">
    <div class="container">
      <!-- 訂單摘要 -->
      <div class="order-summary">
        <h2>訂單摘要</h2>
        <div v-if="orderItems.length > 0" class="order-items">
          <div v-for="item in orderItems" :key="item.id" class="order-item">
            <div class="item-info">
              <span class="item-name">{{ item.name }}</span>
            </div>
            <div class="item-quantity">x {{ item.quantity || 1 }}</div>
            <div class="item-price">NT$ {{ (item.price * (item.quantity || 1)).toLocaleString() }}</div>
          </div>

          <!-- 小計 -->
          <div class="summary-row subtotal">
            <span>小計</span>
            <span>NT$ {{ originalTotal.toLocaleString() }}</span>
          </div>

          <!-- 折扣 -->
          <div v-if="discountAmount > 0" class="summary-row discount">
            <span>優惠折抵</span>
            <span class="discount-amount">-NT$ {{ discountAmount.toLocaleString() }}</span>
          </div>

          <!-- 總計 -->
          <div class="summary-row total">
            <strong>總計</strong>
            <strong class="total-amount">NT$ {{ finalTotal.toLocaleString() }}</strong>
          </div>
        </div>
        <div v-else class="empty-cart">購物車是空的</div>
      </div>

      <!-- 聯絡資訊 -->
      <div class="contact-info">
        <h2>聯絡資訊</h2>

        <!-- 手機號碼 -->
        <div class="form-group">
          <label for="phone">
            手機號碼 <span class="required">*</span>
          </label>
          <input id="phone" type="tel" v-model="phoneNumber" placeholder="請輸入手機號碼（例如：0912345678）" maxlength="10"
            class="form-input" :class="{ 'input-error': phoneNumber && !isValidPhone }"
            @input="phoneNumber = phoneNumber.replace(/[^0-9]/g, '')" />
          <small v-if="phoneNumber && !isValidPhone" class="error-hint">
            ⚠️ 請輸入正確格式：09 開頭，共 10 碼
          </small>
          <small v-else class="form-hint">
            格式：09 開頭的 10 位數字
          </small>
        </div>

        <!-- Email -->
        <div class="form-group">
          <label for="email">
            Email <span class="required">*</span>
          </label>
          <input id="email" type="email" v-model="email" placeholder="請輸入您的 Email" class="form-input" />
          <small class="form-hint">
            用於接收訂單通知
          </small>
        </div>
      </div>

      <!-- 付款方式 -->
      <div class="payment-methods">
        <h2>選擇付款方式</h2>

        <label class="payment-option">
          <input type="radio" name="payment" value="ALL" v-model="selectedPayment">
          <div class="payment-content">
            <span class="payment-name">綠界金流（全部）</span>
            <span class="payment-desc">信用卡、ATM、超商代碼</span>
          </div>
        </label>

        <label class="payment-option">
          <input type="radio" name="payment" value="Credit" v-model="selectedPayment">
          <div class="payment-content">
            <span class="payment-name">信用卡</span>
            <span class="payment-desc">VISA、Master、JCB</span>
          </div>
        </label>

        <label class="payment-option">
          <input type="radio" name="payment" value="ATM" v-model="selectedPayment">
          <div class="payment-content">
            <span class="payment-name">ATM 轉帳</span>
            <span class="payment-desc">虛擬帳號轉帳</span>
          </div>
        </label>
      </div>

      <!-- 確認按鈕 -->
      <button class="checkout-button" @click="proceedToPayment"
        :disabled="isProcessing || orderItems.length === 0 || !phoneNumber || !isValidPhone || !email">
        {{ isProcessing ? '處理中...' : `確認付款 NT$ ${finalTotal.toLocaleString()}` }}
      </button>

      <p v-if="!phoneNumber || !isValidPhone || !email" class="button-hint">
        ⚠️ 請填寫完整的聯絡資訊
      </p>
    </div>

    <div ref="paymentFormContainer" style="display: none;"></div>
  </div>
</template>

<style scoped>
.checkout-page {
  min-height: 100vh;
  background-color: #f5f5f5;
  padding: 2rem 0;
}

.container {
  max-width: 800px;
  margin: 0 auto;
  padding: 0 1rem;
}

.order-summary,
.contact-info,
.payment-methods {
  background: white;
  border-radius: 12px;
  padding: 2rem;
  margin-bottom: 1.5rem;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.08);
}

h2 {
  margin: 0 0 1.5rem 0;
  font-size: 1.5rem;
  font-weight: 600;
  color: #333;
}

.order-items {
  display: flex;
  flex-direction: column;
  gap: 0.75rem;
}

.order-item {
  display: grid;
  grid-template-columns: 1fr auto auto;
  gap: 1rem;
  align-items: center;
  padding: 1rem 0;
  border-bottom: 1px solid #f0f0f0;
}

.item-info {
  display: flex;
  flex-direction: column;
}

.item-name {
  font-weight: 500;
  color: #333;
}

.item-quantity {
  color: #666;
  text-align: right;
  min-width: 60px;
}

.item-price {
  font-weight: 500;
  color: #333;
  text-align: right;
  min-width: 120px;
}

.summary-row {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 1rem 0;
  border-bottom: 1px solid #f0f0f0;
}

.summary-row.subtotal {
  color: #666;
  font-size: 0.95rem;
}

.summary-row.discount {
  color: #4CAF50;
  font-weight: 500;
}

.discount-amount {
  color: #4CAF50;
}

.summary-row.total {
  border-bottom: none;
  border-top: 2px solid #333;
  padding: 1.5rem 0 0 0;
  font-size: 1.25rem;
}

.total-amount {
  color: #d32f2f;
}

.empty-cart {
  text-align: center;
  color: #999;
  padding: 3rem 0;
}

.form-group {
  margin-bottom: 1.5rem;
}

.form-group:last-child {
  margin-bottom: 0;
}

.form-group label {
  display: block;
  margin-bottom: 0.5rem;
  font-weight: 500;
  color: #333;
  font-size: 0.95rem;
}

.required {
  color: #d32f2f;
}

.form-input {
  width: 100%;
  padding: 0.875rem;
  border: 2px solid #ddd;
  border-radius: 8px;
  font-size: 1rem;
  transition: all 0.2s;
  box-sizing: border-box;
}

.form-input:focus {
  outline: none;
  border-color: #4CAF50;
  box-shadow: 0 0 0 3px rgba(76, 175, 80, 0.1);
}

.form-input.input-error {
  border-color: #d32f2f;
}

.form-input.input-error:focus {
  box-shadow: 0 0 0 3px rgba(211, 47, 47, 0.1);
}

.form-hint {
  display: block;
  margin-top: 0.375rem;
  font-size: 0.85rem;
  color: #666;
}

.error-hint {
  display: block;
  margin-top: 0.375rem;
  font-size: 0.85rem;
  color: #d32f2f;
  font-weight: 500;
}

.payment-option {
  display: flex;
  align-items: center;
  gap: 1rem;
  border: 2px solid #e0e0e0;
  border-radius: 10px;
  padding: 1.25rem;
  margin-bottom: 1rem;
  cursor: pointer;
  transition: all 0.2s;
}

.payment-option:hover {
  border-color: #4CAF50;
  background-color: #f9f9f9;
}

.payment-option:has(input:checked) {
  border-color: #4CAF50;
  background-color: #f0f8f0;
}

.payment-option input[type="radio"] {
  width: 20px;
  height: 20px;
  cursor: pointer;
  flex-shrink: 0;
}

.payment-content {
  display: flex;
  flex-direction: column;
  gap: 0.25rem;
  flex: 1;
}

.payment-name {
  font-weight: 600;
  font-size: 1.05rem;
  color: #333;
}

.payment-option:has(input:checked) .payment-name {
  color: #4CAF50;
}

.payment-desc {
  color: #666;
  font-size: 0.9rem;
}

.checkout-button {
  width: 100%;
  padding: 1.25rem;
  font-size: 1.125rem;
  font-weight: 600;
  color: white;
  background: linear-gradient(135deg, #4CAF50 0%, #45a049 100%);
  border: none;
  border-radius: 10px;
  cursor: pointer;
  transition: all 0.3s;
  box-shadow: 0 4px 12px rgba(76, 175, 80, 0.2);
}

.checkout-button:hover:not(:disabled) {
  transform: translateY(-2px);
  box-shadow: 0 6px 16px rgba(76, 175, 80, 0.3);
}

.checkout-button:disabled {
  background: linear-gradient(135deg, #bbb 0%, #999 100%);
  cursor: not-allowed;
  transform: none;
  box-shadow: none;
  opacity: 0.6;
}

.checkout-button:active:not(:disabled) {
  transform: translateY(0);
}

.button-hint {
  text-align: center;
  margin-top: 0.75rem;
  color: #d32f2f;
  font-size: 0.9rem;
  font-weight: 500;
}

@media (max-width: 768px) {
  .container {
    padding: 0 0.5rem;
  }

  .order-summary,
  .contact-info,
  .payment-methods {
    padding: 1.5rem;
  }

  .order-item {
    grid-template-columns: 1fr auto;
    gap: 0.5rem;
  }

  .item-quantity {
    grid-column: 1;
    text-align: left;
    min-width: auto;
  }

  .item-price {
    grid-column: 2;
    grid-row: 1 / 3;
    min-width: 100px;
  }
}
</style>