<template>
  <div class="sidebar">
    <div class="sidebar-header">
      <font-awesome-icon icon="book" class="logo-icon" />
      <span class="logo-text">未然學舍</span>
    </div>

    <nav class="sidebar-nav">
      <router-link
        v-for="item in menuItems"
        :key="item.path"
        :to="item.path"
        :class="['nav-link', { active: $route.path === item.path }]"
      >
        <font-awesome-icon v-if="item.icon" :icon="item.icon" class="nav-icon" />
        <span class="nav-text">{{ item.name }}</span>
        <span v-if="item.badge" class="nav-badge">{{ item.badge }}</span>
      </router-link>
    </nav>
  </div>
</template>

<script setup>
const menuItems = [
  { name: '統計資料', icon: 'tachometer-alt', path: '/admin' },
  { name: '會員管理', icon: 'users', path: '/admin/usersmanager' },
  { name: '課程', icon: 'graduation-cap', path: '/admin/courses' },
  { name: '課程分類', icon: 'list', path: '/admin/categoryadmin' },
  { name: '訂單管理', icon: 'file-invoice-dollar', path: '/admin/orders' },
  { name: '老師申請紀錄', icon: 'chalkboard-teacher', path: '/admin/teacher' },
  { name: '優惠券種類', icon: 'tags', path: '/admin/couponType' },
  { name: '優惠券管理', icon: 'ticket-alt', path: '/admin/coupon' },
]
</script>

<style scoped>
.sidebar {
  width: 240px;
  background: #2c3e50;
  color: #fff;
  display: flex;
  flex-direction: column;
  height: 100vh;
}

.sidebar-header {
  display: flex;
  align-items: center;
  padding: 20px;
  font-size: 1.5rem;
  font-weight: 700;
  border-bottom: 1px solid rgba(255, 255, 255, 0.1);
}

.logo-icon {
  margin-right: 10px;
  color: #3498db;
}

.sidebar-nav {
  flex-grow: 1;
  padding: 10px 0;
}

.nav-link {
  display: flex;
  align-items: center;
  padding: 10px 20px;
  color: #ecf0f1;
  text-decoration: none;
  border-radius: 6px;
  margin: 5px 10px;
}

.nav-link:hover {
  background: #34495e;
}

.nav-link.active {
  background: #3498db;
}

.nav-icon {
  margin-right: 12px;
}
</style>
