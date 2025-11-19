<template>
  <nav class="navbar navbar-expand-lg navbar-light bg-light shadow-sm py-2">
    <div class="container-fluid">
      <!-- Logo -->
      <RouterLink class="navbar-brand fw-bold d-flex align-items-center no-drag" to="/">
        <img :src="logo" width="60" height="60" class="me-2 no-drag" alt="未然學舍 Logo" />
        未然學舍
      </RouterLink>

      <!-- 手機漢堡選單 -->
      <button
        class="navbar-toggler"
        type="button"
        data-bs-toggle="collapse"
        data-bs-target="#navbarSupportedContent"
        aria-controls="navbarSupportedContent"
        aria-expanded="false"
        aria-label="Toggle navigation"
      >
        <span class="navbar-toggler-icon"></span>
      </button>

      <!-- 導覽列內容 -->
      <div class="collapse navbar-collapse justify-content-between" id="navbarSupportedContent">
        <!-- 🔍 搜尋欄 -->
        <form class="search-wrapper mx-auto" @submit.prevent="handleSearch">
          <div class="search-container">
            <input
              v-model="keyword"
              class="form-control search-input"
              type="search"
              placeholder="搜尋課程 / 老師"
              aria-label="Search"
            />
            <i class="bi bi-search search-icon" @click="handleSearch"></i>
          </div>
        </form>

        <!-- 🔗 功能列 -->
        <ul class="navbar-nav mx-auto align-items-center main-nav">
          <li class="nav-item"><RouterLink class="nav-link" to="/courselist">課程總覽</RouterLink></li>
          <li class="nav-item"><RouterLink class="nav-link" to="/couponzonepage">優惠券專區</RouterLink></li>
          <li class="nav-item"><RouterLink class="nav-link" to="/cart">購物車</RouterLink></li>
          <li class="nav-item"><RouterLink class="nav-link" to="/">客服</RouterLink></li>
          <li class="nav-item"><RouterLink class="nav-link" to="/">推播</RouterLink></li>
        </ul>

        <!-- 👤 登入 / 大頭貼區 -->
        <div class="d-flex align-items-center ms-auto">
          <!-- 未登入 -->
          <div v-if="!token">
            <RouterLink class="nav-link fw-semibold me-3" to="/login">登入 / 註冊</RouterLink>
          </div>

          <!-- 已登入 -->
          <div v-else class="dropdown user-dropdown">
            <a
              href="#"
              class="d-flex align-items-center nav-link dropdown-toggle"
              id="userMenuLink"
              role="button"
              data-bs-toggle="dropdown"
              aria-expanded="false"
            >
              <img
                :src="avatarUrl"
                @error="onImageError"
                class="avatar rounded-circle me-2 no-drag"
                alt="avatar"
              />
              <span class="fw-semibold">{{ username }}</span>
            </a>

            <!-- 下拉選單 -->
            <ul class="dropdown-menu dropdown-menu-end shadow-sm" aria-labelledby="userMenuLink">
              <li><RouterLink class="dropdown-item" to="/profile">個人資料</RouterLink></li>
              <li><RouterLink class="dropdown-item" to="/settings">設定</RouterLink></li>
              <li><hr class="dropdown-divider" /></li>
              <li v-if="isAdmin"><RouterLink class="dropdown-item" to="/admin">管理員後台</RouterLink></li>
              <li v-if="isTeacher"><RouterLink class="dropdown-item" to="/teacher">老師中心</RouterLink></li>
              <li v-if="isSupport"><RouterLink class="dropdown-item" to="/support">客服專區</RouterLink></li>
              <li v-if="isStudent"><RouterLink class="dropdown-item" to="/mycouponspage">我的優惠券</RouterLink></li>
              <li v-if="isStudent"><RouterLink class="dropdown-item" to="/orderhistorypage">訂單紀錄</RouterLink></li>
              <li v-if="isStudent"><RouterLink class="dropdown-item" to="/studenthub">我的課程</RouterLink></li>
              <li><hr class="dropdown-divider" /></li>
              <li><button class="dropdown-item text-danger" @click="logout">登出</button></li>

            </ul>
          </div>
        </div>
      </div>
    </div>
  </nav>
</template>

<script setup>
import { ref, computed, onMounted, onUnmounted, nextTick } from "vue";
import { storeToRefs } from "pinia";
import { useRouter } from "vue-router";
import useUserStore from "@/stores/user.js";
import logo from "@/assets/logo.png";
import * as bootstrap from "bootstrap";

const router = useRouter();
const userStore = useUserStore();
const { token, username, avatar, roles } = storeToRefs(userStore);

// 🔧 Bootstrap Dropdown 初始化 + 保持事件代理
let dropdownInstances = [];
onMounted(async () => {
  await nextTick();
  const dropdownEls = document.querySelectorAll(".dropdown-toggle");
  dropdownEls.forEach((el) => {
    const instance = bootstrap.Dropdown.getOrCreateInstance(el);
    dropdownInstances.push(instance);
  });

  // ✅ 綁定 document 層級點擊事件，確保重新渲染也能正常開關
  document.addEventListener("click", (e) => {
    if (e.target.closest(".dropdown-toggle")) {
      const el = e.target.closest(".dropdown-toggle");
      const instance = bootstrap.Dropdown.getOrCreateInstance(el);
      instance.toggle();
    }
  });

  window.addEventListener("scroll", handleScroll);
});

onUnmounted(() => {
  window.removeEventListener("scroll", handleScroll);
  document.removeEventListener("click", () => {});
  dropdownInstances = [];
});

// 🔍 搜尋功能
const keyword = ref("");
function handleSearch() {
  if (!keyword.value.trim()) return;
  router.push("/not-found");
  keyword.value = "";
}

// 👤 頭貼處理
const avatarUrl = computed(() => {
  if (!avatar.value || avatar.value === "") return logo;
  if (!avatar.value.startsWith("http")) {
    return `http://localhost:8080${avatar.value}`;
  }
  return avatar.value;
});
function onImageError(e) {
  e.target.src = logo;
}

// 🎭 角色判斷
const normalizedRoles = computed(() =>
  roles.value ? roles.value.map((r) => r.toLowerCase()) : []
);
const isAdmin = computed(() => normalizedRoles.value.includes("admin"));
const isTeacher = computed(() => normalizedRoles.value.includes("teacher"));
const isSupport = computed(() => normalizedRoles.value.includes("support"));
const isStudent = computed(() => normalizedRoles.value.includes("student"));

// 🚪 登出
async function logout() {
  userStore.$reset();
  await router.replace("/login");
}

// 🧭 滾動陰影
function handleScroll() {
  const navbar = document.querySelector(".navbar");
  if (window.scrollY > 20) {
    navbar.classList.add("scrolled");
  } else {
    navbar.classList.remove("scrolled");
  }
}
</script>

<style scoped>
.navbar {
  position: sticky;
  top: 0;
  z-index: 1050;
  user-select: none;
  background-color: #fff !important;
  transition: box-shadow 0.3s ease;
}
.navbar.scrolled {
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
}
.no-drag {
  -webkit-user-drag: none;
  user-select: none;
}

/* 搜尋欄 */
.search-wrapper {
  position: relative;
  width: 480px;
  max-width: 100%;
}
.search-input {
  border-radius: 999px;
  padding: 0.6rem 2.5rem 0.6rem 1.2rem;
  border: 1px solid #ccc;
  background-color: #fff;
  box-shadow: 0 1px 3px rgba(0, 0, 0, 0.08);
  font-size: 15px;
}
.search-input:focus {
  outline: none;
  border-color: #0d6efd;
  box-shadow: 0 2px 6px rgba(13, 110, 253, 0.2);
}
.search-icon {
  position: absolute;
  right: 15px;
  top: 50%;
  transform: translateY(-50%);
  color: #888;
  font-size: 1.1rem;
  cursor: pointer;
}

/* 功能列 */
.main-nav .nav-item {
  margin: 0 10px;
}
.main-nav .nav-link {
  font-weight: 500;
  font-size: 16px;
  color: #212529 !important;
  transition: color 0.2s ease-in-out;
}
.main-nav .nav-link:hover {
  color: #0d6efd !important;
}

/* 使用者頭貼與下拉 */
.avatar {
  width: 48px;
  height: 48px;
  object-fit: cover;
  cursor: pointer;
  transition: transform 0.2s ease;
}
.avatar:hover {
  transform: scale(1.05);
}
.user-dropdown .dropdown-menu {
  min-width: 200px;
  right: 0;
  left: auto;
  transform: translateY(5px);
}
.navbar {
  min-height: 68px;
}
</style>
