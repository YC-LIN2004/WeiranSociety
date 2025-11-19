<template>
  <div class="teacher-profile container py-4">
    <h3 class="fw-bold mb-3">我的資料</h3>

    <div v-if="loading" class="text-center text-muted py-5">載入中...</div>

    <div v-else class="card p-4 shadow-sm mx-auto" style="max-width: 600px;">
      <div class="mb-3">
        <label class="form-label fw-bold">個人簡介</label>
        <textarea
          v-model="teacher.bio"
          class="form-control"
          rows="4"
          placeholder="請輸入您的教學簡介..."
        ></textarea>
      </div>

      <div class="mb-3">
        <label class="form-label fw-bold">專業領域</label>
        <input
          type="text"
          v-model="teacher.expertise"
          class="form-control"
          placeholder="例如：前端開發、資料庫設計..."
        />
      </div>

      <div class="text-end">
        <button class="btn btn-primary px-4" @click="updateProfile">
          儲存修改
        </button>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import useUserStore from '@/stores/user.js'
import axios from '@/api/axios'
import Swal from 'sweetalert2'

const userStore = useUserStore()
const teacher = ref({ bio: '', expertise: '' })
const loading = ref(true)

onMounted(async () => {
  try {
    const res = await axios.get(`/teachers/user/${userStore.userId}`)
    teacher.value = res.data
  } catch (err) {
    Swal.fire('錯誤', '無法取得老師資料', 'error')
  } finally {
    loading.value = false
  }
})

async function updateProfile() {
  try {
    const res = await axios.post('/teacher', {
      teacherId: teacher.value.teacherId,
      userId: userStore.userId,
      bio: teacher.value.bio,
      expertise: teacher.value.expertise,
    })
    teacher.value = res.data
    Swal.fire({
      icon: 'success',
      title: '已更新',
      text: '您的資料已成功儲存。',
      timer: 1500,
      showConfirmButton: false,
    })
  } catch (err) {
    Swal.fire('錯誤', '更新失敗，請稍後再試', 'error')
  }
}
</script>

<style scoped>
.teacher-profile {
  user-select: none;
}
textarea {
  resize: none;
  user-select: none;
}
input,
label,
button,
h3,
p {
  user-select: none;
}
</style>
