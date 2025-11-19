<template>
  <div class="teacher-dashboard text-center py-5">
    <h2 class="fw-bold mb-3">歡迎 {{ username }} 成為 Weiran Society 教育者的一員！</h2>
    <p class="text-muted fs-5">
      感謝您加入未然學舍的教學團隊 💙<br />
      請點擊左側功能列以編輯您的教學資料、上傳課程或管理內容。
    </p>
  </div>
</template>

<script setup>
import useUserStore from '@/stores/user.js'
const userStore = useUserStore()
const username = userStore.username || '老師'
</script>

<style scoped>
.teacher-dashboard {
  user-select: none;
  color: #333;
}
h2 {
  color: #0d6efd;
}
p {
  line-height: 1.8;
}
</style>
