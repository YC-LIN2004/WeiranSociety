<template>
  <div class="verify-page card shadow p-4">
    <h2 class="mb-3 text-center">帳號驗證</h2>

    <div v-if="!verified">
      <p class="text-muted text-center">
        驗證碼已寄至 <strong>{{ email }}</strong>，請於 15 分鐘內完成驗證。
      </p>

      <div class="mb-3">
        <input
          v-model="verificationCode"
          type="text"
          maxlength="6"
          class="form-control text-center"
          placeholder="請輸入 6 位驗證碼"
        />
      </div>

      <div class="d-flex justify-content-between align-items-center mb-3">
        <button
          class="btn btn-success w-100"
          :disabled="isVerifying"
          @click="submitVerification"
        >
          {{ isVerifying ? '驗證中...' : '驗證帳號' }}
        </button>
      </div>

      <div class="text-center">
        <button
          class="btn btn-outline-primary btn-sm"
          @click="resendCode"
          :disabled="isResending || countdown > 0"
        >
          {{ countdown > 0 ? `重新寄送 (${countdown}s)` : '重新寄送驗證碼' }}
        </button>
      </div>

      <p v-if="errorMessage" class="text-danger text-center mt-3">
        {{ errorMessage }}
      </p>
      <p v-if="successMessage" class="text-success text-center mt-3">
        {{ successMessage }}
      </p>
    </div>

    <div v-else class="text-center">
      <h3 class="text-success mb-2">✅ 驗證成功！</h3>
      <p>歡迎 {{ realname }}！</p>
      <button class="btn btn-primary w-100" @click="goLogin">
        返回登入頁面
      </button>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import axios from 'axios'
import { useRoute, useRouter } from 'vue-router'

const API_BASE_URL = import.meta.env.VITE_API_BASE_URL
const route = useRoute()
const router = useRouter()

const verificationCode = ref('')
const verified = ref(false)
const realname = ref('')
const errorMessage = ref('')
const successMessage = ref('')
const isVerifying = ref(false)
const isResending = ref(false)
const countdown = ref(0)
const timer = ref(null)

// 帳號 / Email 從 query 或 localStorage 取得
const account = route.query.account || localStorage.getItem('account')
const email = route.query.email || localStorage.getItem('email')

onMounted(() => {
  if (account) localStorage.setItem('account', account)
  if (email) localStorage.setItem('email', email)
})

// 驗證帳號
const submitVerification = async () => {
  if (!verificationCode.value) {
    errorMessage.value = '請輸入驗證碼'
    return
  }

  errorMessage.value = ''
  successMessage.value = ''
  isVerifying.value = true

  try {
    const res = await axios.post(`${API_BASE_URL}/auth/verify`, null, {
      params: { account, code: verificationCode.value }
    })
    verified.value = true
    realname.value = res.data.split('歡迎 ')[1]?.replace('！', '') || ''
  } catch (err) {
    errorMessage.value = err.response?.data?.message || '驗證失敗，請確認驗證碼是否正確'
  } finally {
    isVerifying.value = false
  }
}

// 重新寄送驗證碼
const resendCode = async () => {
  errorMessage.value = ''
  successMessage.value = ''
  isResending.value = true

  try {
    await axios.post(`${API_BASE_URL}/auth/resend-verification`, null, {
      params: { email }
    })
    successMessage.value = '驗證碼已重新寄送，請於 15 分鐘內完成驗證。'
    startCountdown()
  } catch (err) {
    errorMessage.value = err.response?.data?.message || '寄送失敗，請稍後再試'
  } finally {
    isResending.value = false
  }
}

// 倒數計時控制
const startCountdown = () => {
  countdown.value = 60
  timer.value && clearInterval(timer.value)
  timer.value = setInterval(() => {
    if (countdown.value > 0) countdown.value--
    else clearInterval(timer.value)
  }, 1000)
}

const goLogin = () => {
  localStorage.removeItem('account')
  localStorage.removeItem('email')
  router.push('/login')
}
</script>

<style scoped>
.verify-page {
  max-width: 420px;
  margin: 80px auto;
  text-align: center;
  border-radius: 12px;
  background: #fff;
}
</style>
