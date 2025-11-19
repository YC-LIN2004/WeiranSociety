<template>
  <div class="container mt-5" style="max-width: 500px;">
    <h2 class="text-center mb-4">會員註冊</h2>

    <div v-if="message" class="alert alert-success text-center">{{ message }}</div>
    <div v-if="errors.general" class="alert alert-danger text-center">{{ errors.general }}</div>

    <form @submit.prevent="handleRegister">
      <div class="mb-3">
        <label class="form-label">帳號</label>
        <input v-model.trim="form.account" type="text" class="form-control" :class="{ 'is-invalid': errors.account }" />
        <div class="invalid-feedback">{{ errors.account }}</div>
        <small class="text-muted">密碼需包含大寫、小寫字母及數字，長度 6~20 碼</small>
      </div>
      

      <div class="mb-3">
        <label class="form-label">暱稱</label>
        <input v-model.trim="form.username" type="text" class="form-control" :class="{ 'is-invalid': errors.username }" />
        <div class="invalid-feedback">{{ errors.username }}</div>
      </div>

      <div class="mb-3">
        <label class="form-label">真實姓名</label>
        <input v-model.trim="form.realname" type="text" class="form-control" :class="{ 'is-invalid': errors.realname }" />
        <div class="invalid-feedback">{{ errors.realname }}</div>
      </div>

      <div class="mb-3">
        <label class="form-label">Email</label>
        <input v-model.trim="form.email" type="email" class="form-control" :class="{ 'is-invalid': errors.email }" />
        <div class="invalid-feedback">{{ errors.email }}</div>
      </div>

      <div class="mb-3">
        <label class="form-label">電話</label>
        <input v-model.trim="form.phone" type="text" class="form-control" :class="{ 'is-invalid': errors.phone }" />
        <div class="invalid-feedback">{{ errors.phone }}</div>
      </div>

      <div class="mb-3">
        <label class="form-label">密碼</label>
        <div class="position-relative">
          <input
            :type="showPassword ? 'text' : 'password'"
            v-model.trim="form.password"
            class="form-control"
            style="padding-right:2.5rem"
            :class="{ 'is-invalid': errors.password }"
          />
          <div class="invalid-feedback">{{ errors.password }}</div>
          <button
            type="button"
            @click="showPassword = !showPassword"
            class="btn btn-sm"
            style="position:absolute; top:50%; right:0.25rem; transform:translateY(-50%); border:none; background:transparent; padding:0; line-height:1;"
          >
            {{ showPassword ? '📖' : '📕' }}
          </button>
        </div>
        <small class="text-muted">密碼需包含大寫、小寫字母及數字，長度 6~20 碼</small>
      </div>

      <button type="submit" class="btn btn-success w-100" :disabled="isSubmitting">
        {{ isSubmitting ? '註冊中...' : '註冊' }}
      </button>
    </form>

    <div class="text-center mt-3">
      <RouterLink to="/login" class="text-decoration-none">返回登入</RouterLink>
    </div>
  </div>
</template>

<script setup>
import { reactive, ref } from 'vue'
import { useRouter } from 'vue-router'
import { register } from '@/api/authApi'

const router = useRouter()

const form = reactive({
  account: '',
  username: '',
  realname: '',
  email: '',
  phone: '',
  password: ''
})

const errors = reactive({
  account: '',
  username: '',
  realname: '',
  email: '',
  phone: '',
  password: '',
  general: ''
})

const showPassword = ref(false)
const isSubmitting = ref(false)
const message = ref('')

const handleRegister = async () => {
  Object.keys(errors).forEach(k => (errors[k] = ''))
  message.value = ''
  isSubmitting.value = true

  try {
    const res = await register(form)
    message.value = '驗證碼已寄出，請於 15 分鐘內完成驗證。'
    console.log(res.data)
    setTimeout(() => {
      router.push({
        path: '/verify',
        query: { account: form.account, email: form.email }
      })
    }, 1200)
  } catch (error) {
    const res = error.response?.data || {}
    errors.general = res.message || '註冊失敗，請稍後再試。'
    for (const key of Object.keys(errors)) {
      if (res[key]) errors[key] = res[key]
    }
  } finally {
    isSubmitting.value = false
  }
}
</script>
