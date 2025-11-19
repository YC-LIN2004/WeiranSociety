<script setup>
import { ref } from 'vue'
import { useRouter, RouterLink } from 'vue-router'
import axios from '@/api/axios.js'
import useUserStore from '@/stores/user.js'
import { useCartStore } from '@/stores/cartStore'

const router = useRouter()
const userStore = useUserStore()
const cartStore = useCartStore() 

const account = ref('')
const password = ref('')

async function handleLogin() {
  try {
    //  登入取得 token 與使用者資料
    const res = await axios.post('/auth/login', {
      account: account.value,
      password: password.value
    })

    const {token, userId, account: acc, username, email, avatar, roles } = res.data

    //  存到 Pinia store
    userStore.setUser({
      token,
      userId,
      account: acc,
      username,
      email,
      avatar,
      roles
    })

    cartStore.userId = userId
    console.log('🟢 登入成功, cartStore.userId =', cartStore.userId)

    // 跳轉首頁
    router.push('/')
  } catch (err) {
    alert('登入失敗')
    console.error(err)
  }
}
</script>

<template>
  <div class="d-flex justify-content-center align-items-center vh-100">
    <div class="card p-4" style="width: 400px;">
      <h2 class="text-center mb-3">登入</h2>
      <form @submit.prevent="handleLogin">
        <div class="mb-3">
          <label>帳號</label>
          <input type="text" v-model="account" class="form-control" required />
        </div>
        <div class="mb-3">
          <label>密碼</label>
          <input type="password" v-model="password" class="form-control" required />
        </div>
        <div class="d-flex justify-content-between mb-3">
          <RouterLink to="/register">註冊</RouterLink>
          <RouterLink to="/reset-password">忘記密碼?</RouterLink>
        </div>
        <button type="submit" class="btn btn-primary w-100">登入</button>
      </form>
    </div>
  </div>
</template>
