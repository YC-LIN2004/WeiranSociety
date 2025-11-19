<template>
  <div class="container py-4">
    <h2 class="mb-3 fw-bold text-primary">課程列表</h2>

    <!-- 🔍 搜尋與篩選 -->
    <div class="row g-2 align-items-center mb-3">
      <!-- 關鍵字 -->
      <div class="col-12 col-md-5">
        <input
          v-model.trim="keyword"
          type="search"
          class="form-control"
          placeholder="搜尋課程關鍵字..."
          @keyup.enter="reload"
        />
      </div>

      <!-- 分類 -->
      <div class="col-6 col-md-3">
        <select v-model="categoryId" class="form-select" @change="reload">
          <option value="">全部分類</option>
          <option
            v-for="c in categories"
            :key="c.categoryId"
            :value="c.categoryId"
          >
            {{ c.categoryName }}
          </option>
        </select>
      </div>

      <!-- 價格排序 -->
      <div class="col-6 col-md-2">
        <select v-model="priceOrder" class="form-select" @change="reload">
          <option value="desc">價格：高 → 低</option>
          <option value="asc">價格：低 → 高</option>
        </select>
      </div>

      <!-- 重置 -->
      <div class="col-12 col-md-2">
        <button class="btn btn-outline-secondary w-100" @click="resetFilters">
          重置
        </button>
      </div>
    </div>

    <!-- 載入中 / 錯誤提示 -->
    <div v-if="loading" class="text-center my-4">
      <div class="spinner-border" role="status">
        <span class="visually-hidden">Loading...</span>
      </div>
    </div>
    <div v-if="error" class="alert alert-warning">{{ error }}</div>

    <!-- 🧱 課程卡片 -->
    <div class="row">
      <div
        class="col-sm-6 col-md-4 col-lg-3 mb-4"
        v-for="item in paged"
        :key="item.courseId"
      >
        <RouterLink
          :to="`/coursedetail/${item.courseId}`"
          class="text-decoration-none text-reset"
        >
          <div class="card h-100 shadow-sm">
            <img
              :src="coverSrc(item.coverUrl)"
              class="card-img-top"
              alt="封面"
              @error="onImgError"
            />

            <div class="card-body">
              <!-- ✅ 改這裡 -->
              <h6
                class="card-title fw-bold text-truncate"
                :title="item.courseTitle || item.title"
              >
                {{ item.courseTitle || item.title || '未命名課程' }}
              </h6>

              <div class="text-muted small mb-1">
                分類：{{ item.categoryName || '未分類' }}
              </div>

              <!-- 🧑‍🏫 老師資訊 -->
              <div class="d-flex align-items-center mt-1">
                <img
                  v-if="item.teacherAvatarUrl"
                  :src="coverSrc(item.teacherAvatarUrl)"
                  class="rounded-circle me-2 border"
                  style="width: 32px; height: 32px; object-fit: cover"
                  alt="老師頭像"
                  @error="onImgError"
                />
                <span class="text-muted small">
                  老師：{{ item.teacherName || '未知' }}
                </span>
              </div>

              <div class="fw-bold text-danger fs-6">
                {{ formatPrice(item.price) }} 元
              </div>
            </div>
          </div>
        </RouterLink>
      </div>

      <div
        v-if="!loading && !error && list.length === 0"
        class="col-12 text-center text-muted py-5"
      >
        找不到符合條件的課程
      </div>
    </div>

    <!-- 📄 分頁 -->
    <div v-if="list.length" class="d-flex justify-content-center mt-4">
      <VueAwesomePaginate
        :total-items="list.length"
        :items-per-page="pageSize"
        :max-pages-shown="5"
        v-model="page"
      />
    </div>
  </div>
</template>

<script setup>
import { ref, computed, onMounted, watch } from 'vue'
import { fetchCourses, fetchCategories, resolveCoverUrl } from '@/api/courseApi'
import { VueAwesomePaginate } from 'vue-awesome-paginate'
import defaultCover from '@/assets/default-course.png'

const keyword = ref('')
const categoryId = ref('')
const priceOrder = ref('desc')
const loading = ref(false)
const error = ref('')

const categories = ref([])
const list = ref([])

const page = ref(1)
const pageSize = ref(12)
const paged = computed(() => {
  const start = (page.value - 1) * pageSize.value
  return list.value.slice(start, start + pageSize.value)
})

async function loadCategories() {
  try {
    const data = await fetchCategories()
    categories.value = Array.isArray(data) ? data : []
  } catch (e) {
    console.warn('載入分類失敗：', e)
  }
}

async function reload() {
  loading.value = true
  error.value = ''
  try {
    const data = await fetchCourses({
      keyword: keyword.value,
      categoryId: categoryId.value,
      priceOrder: priceOrder.value
    })
    list.value = Array.isArray(data) ? data : []
    page.value = 1
  } catch (e) {
    error.value = e?.response?.data?.message || e?.message || '載入失敗'
  } finally {
    loading.value = false
  }
}

function resetFilters() {
  keyword.value = ''
  categoryId.value = ''
  priceOrder.value = 'desc'
  reload()
}

function formatPrice(v) {
  try {
    return Number(v).toLocaleString()
  } catch {
    return v
  }
}

function coverSrc(url) {
  return resolveCoverUrl(url) || defaultCover
}

function onImgError(e) {
  e.target.src = defaultCover
}

onMounted(async () => {
  await loadCategories()
  await reload()
})

watch(page, () => window.scrollTo({ top: 0, behavior: 'smooth' }))
</script>

<style scoped>
.card-img-top {
  height: 180px;
  object-fit: cover;
}
</style>
