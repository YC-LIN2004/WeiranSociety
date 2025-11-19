<template>
  <div class="teacher-layout d-flex">
    <!-- 左側導覽欄 -->
    <aside class="sidebar bg-light p-3 shadow-sm" style="width: 240px; min-height: 100vh;">
      <h5 class="fw-bold mb-3 text-primary">👨‍🏫 老師中心</h5>
      <ul class="nav flex-column gap-2">
        <li><RouterLink to="/teacher/dashboard" class="nav-link">🏠 首頁</RouterLink></li>
        <li><RouterLink to="/teacher/profile" class="nav-link">📝 我的資料</RouterLink></li>
        <li><RouterLink to="/teacher/upload" class="nav-link">🎬 上傳課程</RouterLink></li>
        <li><RouterLink to="/teacher/manage" class="nav-link">📚 管理課程</RouterLink></li>
      </ul>
    </aside>

    <!-- 右側內容區 -->
    <main class="flex-grow-1 p-4">
      <RouterView />
    </main>
  </div>
</template>

<script setup>
import { RouterView, RouterLink } from 'vue-router'
</script>

<style scoped>
.teacher-layout {
  user-select: none;
}
.sidebar {
  user-select: none;
}
.nav-link.router-link-active {
  font-weight: bold;
  color: #0d6efd;
}
</style>
