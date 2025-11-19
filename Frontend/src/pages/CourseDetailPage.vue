<template>
  <div class="container py-4" v-if="loaded">
    <RouterLink
      to="/courselist"
      class="text-decoration-none small mb-3 d-inline-block"
    >
      ← 回課程列表
    </RouterLink>

    <div class="row g-4 align-items-start">
      <!-- 🖼️ 封面 -->
      <div class="col-md-6">
        <img
          :src="coverSrc(course.coverUrl)"
          class="img-fluid rounded shadow-sm w-100"
          alt="課程封面"
          @error="onImgError"
        />
      </div>

      <!-- 🧑‍🏫 課程資訊 -->
      <div class="col-md-6">
        <!-- 老師資訊卡 -->
        <div class="card mb-4 shadow-sm p-3 teacher-card">
          <div class="card-body d-flex gap-4 align-items-center">
            <img
              :src="avatarSrc(course.teacher?.avatarUrl)"
              class="teacher-avatar-lg rounded-circle shadow-sm"
              alt="老師頭像"
              @error="onAvatarError"
            />

            <div class="flex-grow-1">
              <h4 class="fw-bold mb-1">
                {{ course.teacher?.name || "未提供姓名" }}
              </h4>

              <!-- ⭐ 星星評分 -->
              <div class="text-warning fs-4 mb-2">
                <i
                  v-for="i in 5"
                  :key="i"
                  class="bi"
                  :class="i <= tempRating ? 'bi-star-fill' : 'bi-star'"
                  @mouseenter="tempRating = i"
                  @mouseleave="tempRating = rating"
                  @click="rateCourse(i)"
                  style="cursor: pointer"
                ></i>
                <span class="small text-muted ms-2">
                  {{ rating ? rating.toFixed(1) : "尚無評分" }}
                </span>
              </div>

              <div
                v-if="course.teacher?.expertise"
                class="text-muted small mb-2"
              >
                專長：{{ course.teacher.expertise }}
              </div>

              <p class="mb-0 text-secondary small" style="white-space: pre-line">
                {{ course.teacher?.bio || "（尚無老師自介）" }}
              </p>
            </div>
          </div>
        </div>

        <!-- ✅ 修正課程標題 -->
        <h3 class="fw-bold mb-1">
          {{ course.courseTitle || course.title || "未命名課程" }}
        </h3>
        <p class="text-muted mb-2">
          分類：{{ course.categoryName || "未分類" }}
        </p>
        <div class="h4 text-danger fw-bold mb-3">
          {{ formatPrice(course.price) }} 元
        </div>

        <!-- 🛒 加入購物車 -->
        <button class="btn btn-primary w-100 mb-4" @click="handleAddToCart">
          加入購物車
        </button>
      </div>
    </div>

    <!-- 🧾 課程介紹 -->
    <div class="card mt-5 shadow-sm">
      <div class="card-body">
        <h5 class="fw-bold mb-3">課程介紹</h5>
        <p class="text-secondary mb-0" style="white-space: pre-line">
          {{
            course.courseDescription ||
              course.description ||
              "（尚無課程介紹）"
          }}
        </p>
      </div>
    </div>

    <!-- 📚 課程章節 -->
    <div v-if="course.sections?.length" class="mt-5">
      <h5 class="fw-bold mb-3">課程章節</h5>

      <div
        v-for="(sec, idx) in course.sections"
        :key="idx"
        class="border rounded mb-3 shadow-sm"
      >
        <!-- 標題 -->
        <div
          class="d-flex justify-content-between align-items-center bg-light p-3 fw-bold"
          style="cursor: pointer"
          @click="toggleSection(idx)"
        >
          <span>{{ sec.sectionTitle || `章節 ${idx + 1}` }}</span>
          <i
            class="bi"
            :class="activeSections[idx] ? 'bi-chevron-up' : 'bi-chevron-down'"
          ></i>
        </div>

        <transition name="fade">
          <div v-show="activeSections[idx]" class="p-3 bg-white">
            <div v-if="sec.courseMedias?.length || sec.videos?.length">
              <div
                v-for="(v, i) in sec.courseMedias?.length
                  ? sec.courseMedias
                  : sec.videos"
                :key="v.courseMediaId || v.videoId || i"
                class="d-flex align-items-start gap-3 mb-3 border rounded p-2"
              >
                <img
                  :src="getYoutubeThumbnail(v.mediaUrl ?? v.videoUrl)"
                  class="rounded"
                  style="width: 120px; height: 68px; object-fit: cover"
                  alt="影片縮圖"
                />
                <div class="flex-grow-1">
                  <p class="fw-bold mb-2">
                    {{ v.mediaTitle ?? v.videoTitle ?? "未命名影片" }}
                  </p>

                  <div
                    v-if="isYoutube(v.mediaUrl ?? v.videoUrl) && idx === 0 && i === 0"
                    class="ratio ratio-16x9 rounded overflow-hidden"
                  >
                    <iframe
                      :src="convertToEmbedUrl(v.mediaUrl ?? v.videoUrl)"
                      title="YouTube video player"
                      allowfullscreen
                    ></iframe>
                  </div>

                  <p v-else class="text-muted small">
                    {{ v.mediaUrl ?? v.videoUrl }}
                  </p>
                </div>
              </div>
            </div>
            <p v-else class="text-muted small mb-0">(尚無影片)</p>
          </div>
        </transition>
      </div>
    </div>

    <div v-if="error" class="alert alert-warning mt-4">{{ error }}</div>
  </div>

  <div v-else class="container py-5 text-center">
    <div class="spinner-border" role="status">
      <span class="visually-hidden">Loading...</span>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from "vue"
import { useRoute, useRouter } from "vue-router"
import axios from "axios"
import Swal from "sweetalert2"
import { useCartStore } from "@/stores/cartStore"
import useUserStore from "@/stores/user"
import { fetchCourseById, resolveCoverUrl } from "@/api/courseApi"
import defaultCover from "@/assets/default-course.png"
import defaultAvatar from "@/assets/default-avatar.png"

const route = useRoute()
const router = useRouter()
const course = ref({})
const loaded = ref(false)
const error = ref("")
const activeSections = ref({})
const rating = ref(0)
const tempRating = ref(0)
const cartStore = useCartStore()
const userStore = useUserStore()

function toggleSection(index) {
  activeSections.value[index] = !activeSections.value[index]
}

function extractYTId(url) {
  try {
    if (url.includes("watch?v=")) return url.split("watch?v=")[1].split("&")[0]
    if (url.includes("youtu.be/")) return url.split("youtu.be/")[1].split("?")[0]
  } catch {}
  return ""
}

function getYoutubeThumbnail(url) {
  const id = extractYTId(url)
  return id ? `https://img.youtube.com/vi/${id}/mqdefault.jpg` : ""
}

function isYoutube(url) {
  return url && (url.includes("youtube.com") || url.includes("youtu.be"))
}

function convertToEmbedUrl(url) {
  const id = extractYTId(url)
  return id ? `https://www.youtube.com/embed/${id}` : url
}

function coverSrc(url) {
  return resolveCoverUrl(url) || defaultCover
}
function avatarSrc(url) {
  if (!url) return defaultAvatar
  if (url.startsWith("http")) return url
  return `${import.meta.env.VITE_API_BASE_URL || "http://localhost:8080/api"}${url}`
}
function onImgError(e) {
  e.target.src = defaultCover
}
function onAvatarError(e) {
  e.target.src = defaultAvatar
}

function formatPrice(v) {
  const n = Number(v)
  return Number.isFinite(n) ? n.toLocaleString() : v
}

async function rateCourse(value) {
  try {
    const t = course.value.teacher || {}
    const teacherId =
      t.teacherId ?? t.id ?? course.value.teacherId ?? course.value.teacherID

    if (!teacherId) {
      Swal.fire("❌ 錯誤", "找不到老師資料，請重新整理後再試", "error")
      return
    }

    rating.value = value
    tempRating.value = value

    await axios.post(
      `${import.meta.env.VITE_API_BASE_URL || "http://localhost:8080/api"}/teachers/${teacherId}/rate`,
      { rating: value }
    )

    Swal.fire("⭐ 感謝您的回饋！", "", "success")
  } catch (err) {
    console.error("評分失敗", err)
    Swal.fire("❌ 評分失敗", err?.response?.data?.message || "請稍後再試", "error")
  }
}

async function handleAddToCart() {
  try {
    if (!userStore.userId) {
      Swal.fire("請先登入", "登入後才能將課程加入購物車", "warning");
      return;
    }

    // ✅ 新增到購物車
    await cartStore.addCourseToCart(course.value);

    // ✅ 立即重新抓購物車（同步前端資料）
    await cartStore.fetchCarts(userStore.userId);

    // ✅ 顯示提示視窗
    Swal.fire({
      icon: "success",
      title: "已加入購物車",
      showCancelButton: true,
      confirmButtonText: "前往購物車",
      cancelButtonText: "繼續逛課程",
    }).then((res) => {
      if (res.isConfirmed) {
        // ✅ 強制觸發 /cart 頁面重新載入
        router.push({ path: "/cart", query: { refresh: Date.now() } });
      }
    });
  } catch (err) {
    console.error("加入購物車失敗", err);
    Swal.fire(
      "❌ 加入購物車失敗",
      err?.response?.data?.message || "請稍後再試",
      "error"
    );
  }
}

onMounted(async () => {
  try {
    const id = route.params.id
    course.value = await fetchCourseById(id)
    rating.value = course.value.teacher?.teacherRating ?? course.value.teacherRating ?? 0
    tempRating.value = rating.value
  } catch (e) {
    error.value = e?.response?.data?.message || e?.message || "載入失敗"
  } finally {
    loaded.value = true
  }
})
</script>

<style scoped>
.teacher-card {
  transform: scale(1.05);
  transition: all 0.3s ease;
  border-radius: 12px;
  padding: 1.5rem !important;
}
.teacher-card:hover {
  transform: scale(1.07);
  box-shadow: 0 4px 20px rgba(0, 0, 0, 0.15);
}
.teacher-avatar-lg {
  width: 120px;
  height: 120px;
  object-fit: cover;
}
.bi-star,
.bi-star-fill {
  transition: color 0.2s ease;
}
.bi-star:hover,
.bi-star-fill:hover {
  color: #ffcc00 !important;
}
.fade-enter-active,
.fade-leave-active {
  transition: all 0.3s ease;
}
.fade-enter-from,
.fade-leave-to {
  opacity: 0;
  max-height: 0;
  overflow: hidden;
}
.fade-enter-to,
.fade-leave-from {
  opacity: 1;
  max-height: 2000px;
}
</style>
