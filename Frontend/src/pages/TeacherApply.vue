<template>
  <div class="container">
    <div class="form-card">
      <h1>🎓 申請成為老師</h1>
      <p class="subtitle">加入我們的教學團隊，分享您的專業知識</p>

      <!-- 成功/錯誤訊息 -->
      <div v-if="message.text" :class="message.type">
        {{ message.text }}
      </div>

      <form @submit.prevent="submitForm">
        <!-- 用戶 ID -->
        <div class="form-group">
          <label>用戶 ID <span class="required">*</span></label>
          <input type="number" v-model.number="form.userId" required />
        </div>

        <!-- 專業領域 -->
        <div class="form-group">
          <label>專業領域 <span class="required">*</span></label>
          <input
            type="text"
            v-model="form.expertise"
            placeholder="例如：程式設計、數學、英文"
            required
          />
        </div>

        <!-- 個人簡介 -->
        <div class="form-group">
          <label>個人簡介 <span class="required">*</span></label>
          <textarea
            v-model="form.bio"
            rows="5"
            placeholder="請簡單介紹您的教學理念與專長 (最多500字)"
            required
            maxlength="500"
          ></textarea>
          <small>{{ form.bio.length }}/500 字</small>
        </div>

        <!-- ✅ 證書上傳 -->
        <div class="form-group">
          <label>上傳證書</label>
          <div class="d-flex align-items-center gap-2">
            <input
              type="file"
              accept=".pdf,.png,.jpg,.jpeg"
              @change="handleCertificateUpload"
              ref="fileInput"
              class="form-control"
            />
            <button type="button" class="btn btn-primary" @click="uploadCertificate">
              新增證書
            </button>
          </div>

          <div v-if="form.certificateUrl" class="mt-2 text-success small">
            ✅ 已上傳：
            <a :href="form.certificateUrl" target="_blank">{{ form.certificateUrl }}</a>
          </div>
          <div v-else class="mt-2 text-muted small">
            請上傳您的教學相關證書（PDF 或 圖片）
          </div>
        </div>

        <button type="submit" :disabled="loading">
          {{ loading ? '送出中...' : '送出申請' }}
        </button>
      </form>
    </div>
  </div>
</template>

<script setup>
import { reactive, ref } from 'vue'
import axios from 'axios'
import { useRouter } from 'vue-router'

const router = useRouter()
const API_URL = 'http://localhost:8080/api/teachers'

// 表單資料
const form = reactive({
  userId: null,
  bio: '',
  expertise: '',
  certificateUrl: ''
})

// 狀態
const loading = ref(false)
const message = reactive({
  text: '',
  type: ''
})

// 📂 上傳相關
const fileInput = ref(null)
const selectedFile = ref(null)

const handleCertificateUpload = (e) => {
  selectedFile.value = e.target.files[0]
}

const uploadCertificate = async () => {
  if (!selectedFile.value) {
    message.text = '⚠️ 請先選擇要上傳的檔案'
    message.type = 'error'
    return
  }

  const formData = new FormData()
  formData.append('file', selectedFile.value)

  try {
    const res = await axios.post('http://localhost:8080/api/files/upload', formData, {
      headers: { 'Content-Type': 'multipart/form-data' }
    })

    if (res.data.url) {
      form.certificateUrl = res.data.url
      message.text = '✅ 證書上傳成功！'
      message.type = 'success'
    } else {
      throw new Error('上傳回傳資料錯誤')
    }
  } catch (err) {
    console.error(err)
    message.text = '❌ 上傳失敗，請稍後再試'
    message.type = 'error'
  }
}

// 提交表單
const submitForm = async () => {
  loading.value = true
  message.text = ''

  if (!form.userId) {
    message.text = '❌ 請輸入用戶 ID'
    message.type = 'error'
    loading.value = false
    return
  }

  try {
    // ✅ 修正重點：後端期望 user 為物件包 userID
    const data = {
      user: { userID: Number(form.userId) },
      bio: form.bio || null,
      expertise: form.expertise || null,
      certificateUrl: form.certificateUrl || null,
      teacherStatus: 'PENDING',
      teacherRating: null,
      updatedAt: null
    }

    console.log('送出的資料:', JSON.stringify(data, null, 2))

    await axios.post(API_URL, data)

    message.text = '✅ 申請成功！請等待審核（約 3-5 工作天）'
    message.type = 'success'

    setTimeout(() => {
      router.push('/') // 回首頁
    }, 2000)

    Object.keys(form).forEach(key => {
      form[key] = typeof form[key] === 'number' ? 0 : ''
    })
  } catch (error) {
    message.text = '❌ 申請失敗：' + (error.response?.data?.error || '請稍後再試')
    message.type = 'error'
    console.error(error)
  } finally {
    loading.value = false
  }
}
</script>

<style scoped>
.container {
  min-height: 100vh;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  padding: 20px;
  display: flex;
  align-items: center;
  justify-content: center;
}

.form-card {
  background: white;
  border-radius: 16px;
  padding: 40px;
  max-width: 700px;
  width: 100%;
  box-shadow: 0 20px 60px rgba(0, 0, 0, 0.3);
}

h1 {
  color: #667eea;
  text-align: center;
  margin-bottom: 10px;
  font-size: 28px;
}

.subtitle {
  text-align: center;
  color: #666;
  margin-bottom: 30px;
}

.form-group {
  margin-bottom: 20px;
}

.form-group label {
  display: block;
  margin-bottom: 6px;
  font-weight: 600;
  color: #333;
  font-size: 14px;
}

.required {
  color: #e74c3c;
}

input,
textarea {
  width: 100%;
  padding: 10px 12px;
  border: 2px solid #e0e0e0;
  border-radius: 8px;
  font-size: 14px;
  font-family: inherit;
  transition: border-color 0.3s;
}

input:focus,
textarea:focus {
  outline: none;
  border-color: #667eea;
}

textarea {
  resize: vertical;
}

button {
  width: 100%;
  padding: 14px;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  color: white;
  border: none;
  border-radius: 8px;
  font-size: 16px;
  font-weight: 600;
  cursor: pointer;
  margin-top: 10px;
  transition: transform 0.2s;
}

button:hover:not(:disabled) {
  transform: translateY(-2px);
}

button:disabled {
  opacity: 0.6;
  cursor: not-allowed;
}

.success {
  background: #d4edda;
  color: #155724;
  padding: 12px;
  border-radius: 8px;
  margin-bottom: 20px;
  border: 1px solid #c3e6cb;
}

.error {
  background: #f8d7da;
  color: #721c24;
  padding: 12px;
  border-radius: 8px;
  margin-bottom: 20px;
  border: 1px solid #f5c6cb;
}

@media (max-width: 768px) {
  .form-card {
    padding: 25px;
  }

  h1 {
    font-size: 24px;
  }
}
</style>
