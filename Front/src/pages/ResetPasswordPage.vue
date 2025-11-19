<template>
  <div class="container mt-5" style="max-width: 500px;">
    <h2 class="text-center mb-4">忘記密碼 / 更新密碼</h2>

    <!-- 輸入 Email 發送驗證碼 -->
    <div v-if="!codeSent">
      <div class="mb-3">
        <label>Email</label>
        <input v-model="email" type="email" class="form-control" required />
      </div>
      <!-- 增加 disabled 條件，確保 email 不為空 -->
      <button 
        class="btn btn-primary w-100" 
        @click="sendCode" 
        :disabled="sending || !email"
      >
        <span v-if="sending">寄送中...</span>
        <span v-else>寄送驗證碼</span>
      </button>
      <div v-if="errorMessage" class="text-danger mt-2">{{ errorMessage }}</div>
      <div class="text-center mt-3">
        <RouterLink to="/login">返回登入</RouterLink>
      </div>
    </div>

    <!-- 輸入驗證碼 + 新密碼 -->
    <div v-else-if="!success">
      <div class="mb-3">
        <label>驗證碼</label>
        <input v-model="code" type="text" class="form-control" required />
      </div>
      <div class="mb-3">
        <label>新密碼</label>
        <input v-model="newPassword" type="password" class="form-control" required />
      </div>
      <div v-if="errorMessage" class="text-danger mb-3">{{ errorMessage }}</div>
      <!-- 增加 disabled 條件，確保所有欄位不為空 -->
      <button 
        class="btn btn-primary w-100" 
        @click="updatePassword" 
        :disabled="updating || !code || !newPassword"
      >
        <span v-if="updating">更新中...</span>
        <span v-else>更新密碼</span>
      </button>
      <div class="text-center mt-3">
        <RouterLink to="/login">返回登入</RouterLink>
      </div>
    </div>

    <!-- 成功訊息 -->
    <div v-else>
      <h3 class="text-center text-success">密碼更新成功！</h3>
      <div class="text-center mt-3">
        <RouterLink to="/login" class="btn btn-success">返回登入</RouterLink>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref } from 'vue'
import axios from 'axios'
import { useRouter, RouterLink } from 'vue-router'

const router = useRouter()
const API_BASE_URL = import.meta.env.VITE_API_BASE_URL

const email = ref('')
const code = ref('')
const newPassword = ref('')
const codeSent = ref(false)
const success = ref(false)
const sending = ref(false)
const updating = ref(false)
const errorMessage = ref('')

const getErrorMessage = (err) => {
    // 優先從 response.data 獲取詳細錯誤訊息
    const responseMessage = err.response?.data?.message || err.response?.data;
    
    // 檢查 HTTP 狀態碼
    if (err.response?.status === 400) {
        return responseMessage || '請求錯誤：請檢查輸入資料或 Email 格式。';
    }
    
    // 處理 403, 404, 500 等其他錯誤
    if (err.response?.status) {
        return `伺服器錯誤 (${err.response.status})：${responseMessage || '操作失敗'}`;
    }
    
    // 連線錯誤
    return '連線或服務失敗，請檢查後端服務狀態。';
}


// 發送驗證碼
const sendCode = async () => {
    // 簡單的客戶端驗證
    if (!email.value) {
        errorMessage.value = '請輸入 Email 地址。';
        return;
    }

    sending.value = true
    errorMessage.value = ''
    try {
        // 發送 JSON Body，匹配後端 @RequestBody
        await axios.post(`${API_BASE_URL}/auth/forgot-password`, { 
            email: email.value
        })
        codeSent.value = true
        errorMessage.value = '驗證碼已寄送，請檢查您的信箱！' // 成功時顯示提示
    } catch (err) {
        errorMessage.value = getErrorMessage(err)
    } finally {
        sending.value = false
    }
}

// 步驟 2：驗證 + 更新密碼
const updatePassword = async () => {
    if (!code.value || !newPassword.value) {
        errorMessage.value = '請輸入驗證碼和新密碼。';
        return;
    }

    updating.value = true
    errorMessage.value = ''
    

    try {
        // 路徑必須匹配後端 Controller 的 /verify-reset
        await axios.post(`${API_BASE_URL}/auth/verify-reset`, { 
            email: email.value,
            code: code.value,
            newPassword: newPassword.value
        })
        success.value = true
        errorMessage.value = ''
    } catch (err) {
        errorMessage.value = getErrorMessage(err)
    } finally {
        updating.value = false
    }
}
</script>

<style scoped>
.container {
  margin-top: 50px;
}
input {
  padding: 8px;
  width: 100%;
  border: 1px solid #ccc; 
  border-radius: 4px;
}
button {
  padding: 10px 16px; 
  font-weight: 600;
  border-radius: 4px;
  transition: background-color 0.2s;
}
.btn-primary {
    background-color: #007bff;
    border-color: #007bff;
    color: white;
}
.btn-success {
    background-color: #28a745;
    border-color: #28a745;
}
.text-danger {
  color: #dc3545; 
  font-weight: bold;
}
.text-success {
    color: #28a745;
}
</style>
