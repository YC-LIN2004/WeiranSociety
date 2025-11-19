<template>
  <nav class="navbar">
    <div class="navbar-container">
      <!-- Logo / 品牌名稱 -->
      <router-link to="/" class="navbar-brand">
        <span class="brand-icon">🎓</span>
        <span class="brand-name">線上課程平台</span>
      </router-link>

      <!-- 導航連結 -->
      <div class="navbar-menu">
        <router-link to="/" class="nav-link">首頁</router-link>
      </div>

      <!-- 右側：使用者選單 -->
      <div class="navbar-right">
        <!-- 如果已登入，顯示使用者下拉選單 -->
        <div v-if="userStore.token" class="user-menu-container">
          <!-- 使用者頭像 -->
          <div class="user-avatar" @click="toggleDropdown">
            <img v-if="userStore.userInfo?.avatar" :src="userStore.userInfo.avatar" alt="使用者頭像" class="avatar-img" />
            <div v-else class="avatar-placeholder">
              {{ getUserInitial() }}
            </div>
            <span class="dropdown-arrow" :class="{ 'active': isDropdownOpen }">▼</span>
          </div>

          <!-- 下拉選單 -->
          <transition name="dropdown">
            <div v-if="isDropdownOpen" class="dropdown-menu">
              <!-- 使用者資訊 -->
              <div class="user-info-section">
                <div class="user-name">{{ userStore.userInfo?.name || userStore.userInfo?.username || '使用者' }}</div>
                <div v-if="userStore.userInfo?.email" class="user-email">{{ userStore.userInfo.email }}</div>
              </div>

              <div class="dropdown-divider"></div>

              <!-- 選單項目 -->
              <router-link to="/settings" class="dropdown-item" @click="closeDropdown">
                <span class="item-icon">⚙️</span>
                <span>個人設定</span>
              </router-link>

              <div class="dropdown-divider"></div>

              <button class="dropdown-item logout-item" @click="handleLogout">
                <span class="item-icon">🚪</span>
                <span>登出</span>
              </button>
            </div>
          </transition>

          <!-- 背景遮罩（點擊外部關閉） -->
          <div v-if="isDropdownOpen" class="dropdown-overlay" @click="closeDropdown"></div>
        </div>

        <!-- 如果未登入，顯示登入按鈕 -->
        <router-link v-else to="/login" class="login-btn">
          登入
        </router-link>
      </div>
    </div>
  </nav>
</template>

<script setup>
import { ref, onMounted, onUnmounted } from 'vue'
import { useRouter } from 'vue-router'
import useUserStore from '@/stores/user.js'

const router = useRouter()
const userStore = useUserStore()
const isDropdownOpen = ref(false)

// 切換下拉選單
const toggleDropdown = () => {
  isDropdownOpen.value = !isDropdownOpen.value
}

// 關閉下拉選單
const closeDropdown = () => {
  isDropdownOpen.value = false
}

// 取得使用者名稱的首字母
const getUserInitial = () => {
  const name = userStore.userInfo?.name || userStore.userInfo?.username || 'U'
  return name.charAt(0).toUpperCase()
}

// 登出處理
const handleLogout = () => {
  closeDropdown()
  userStore.logout()
  router.push('/login')
}

// 點擊 ESC 鍵關閉
const handleEscape = (e) => {
  if (e.key === 'Escape') {
    closeDropdown()
  }
}

onMounted(() => {
  document.addEventListener('keydown', handleEscape)
})

onUnmounted(() => {
  document.removeEventListener('keydown', handleEscape)
})
</script>

<style scoped>
.navbar {
  background: white;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
  position: sticky;
  top: 0;
  z-index: 100;
}

.navbar-container {
  max-width: 1200px;
  margin: 0 auto;
  padding: 0 20px;
  display: flex;
  align-items: center;
  justify-content: space-between;
  height: 64px;
}

/* Logo 品牌 */
.navbar-brand {
  display: flex;
  align-items: center;
  gap: 8px;
  text-decoration: none;
  color: #2c3e50;
  font-weight: 600;
  font-size: 18px;
}

.brand-icon {
  font-size: 24px;
}

.brand-name {
  font-weight: 700;
}

/* 導航選單 */
.navbar-menu {
  display: flex;
  gap: 24px;
  align-items: center;
}

.nav-link {
  text-decoration: none;
  color: #2c3e50;
  font-size: 15px;
  font-weight: 500;
  padding: 8px 12px;
  border-radius: 6px;
  transition: all 0.2s;
}

.nav-link:hover {
  background-color: #f5f5f5;
  color: #3498db;
}

.nav-link.router-link-active {
  color: #3498db;
  background-color: #e3f2fd;
}

/* 右側區域 */
.navbar-right {
  display: flex;
  align-items: center;
}

.login-btn {
  padding: 8px 20px;
  background-color: #3498db;
  color: white;
  text-decoration: none;
  border-radius: 6px;
  font-weight: 500;
  transition: background-color 0.2s;
}

.login-btn:hover {
  background-color: #2980b9;
}

/* === 使用者下拉選單 === */
.user-menu-container {
  position: relative;
  display: inline-block;
}

/* 使用者頭像 */
.user-avatar {
  display: flex;
  align-items: center;
  gap: 8px;
  padding: 8px 12px;
  cursor: pointer;
  border-radius: 20px;
  transition: background-color 0.2s;
}

.user-avatar:hover {
  background-color: rgba(0, 0, 0, 0.05);
}

.avatar-img {
  width: 40px;
  height: 40px;
  border-radius: 50%;
  object-fit: cover;
  border: 2px solid #3498db;
}

.avatar-placeholder {
  width: 40px;
  height: 40px;
  border-radius: 50%;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  color: white;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 18px;
  font-weight: bold;
  border: 2px solid #3498db;
}

.dropdown-arrow {
  font-size: 10px;
  color: #666;
  transition: transform 0.3s;
}

.dropdown-arrow.active {
  transform: rotate(180deg);
}

/* 下拉選單 */
.dropdown-menu {
  position: absolute;
  top: calc(100% + 8px);
  right: 0;
  min-width: 240px;
  background: white;
  border-radius: 12px;
  box-shadow: 0 4px 20px rgba(0, 0, 0, 0.15);
  z-index: 1000;
  overflow: hidden;
}

/* 使用者資訊區塊 */
.user-info-section {
  padding: 16px;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  color: white;
}

.user-name {
  font-size: 16px;
  font-weight: 600;
  margin-bottom: 4px;
}

.user-email {
  font-size: 13px;
  opacity: 0.9;
}

/* 分隔線 */
.dropdown-divider {
  height: 1px;
  background-color: #e0e0e0;
  margin: 4px 0;
}

/* 選單項目 */
.dropdown-item {
  display: flex;
  align-items: center;
  gap: 12px;
  padding: 12px 16px;
  color: #2c3e50;
  text-decoration: none;
  transition: background-color 0.2s;
  cursor: pointer;
  border: none;
  background: none;
  width: 100%;
  text-align: left;
  font-size: 14px;
}

.dropdown-item:hover {
  background-color: #f5f5f5;
}

.item-icon {
  font-size: 18px;
  width: 24px;
  text-align: center;
}

.logout-item {
  color: #e74c3c;
}

.logout-item:hover {
  background-color: #fef5f5;
}

/* 背景遮罩 */
.dropdown-overlay {
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  z-index: 999;
}

/* 動畫效果 */
.dropdown-enter-active,
.dropdown-leave-active {
  transition: all 0.3s ease;
}

.dropdown-enter-from {
  opacity: 0;
  transform: translateY(-10px);
}

.dropdown-leave-to {
  opacity: 0;
  transform: translateY(-10px);
}

/* 響應式設計 */
@media (max-width: 768px) {
  .navbar-menu {
    display: none;
  }

  .brand-name {
    font-size: 16px;
  }

  .dropdown-menu {
    min-width: 200px;
  }
}
</style>