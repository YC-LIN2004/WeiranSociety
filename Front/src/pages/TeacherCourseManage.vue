<template>
  <div class="teacher-upload container-fluid py-4">
    <h3 class="fw-bold mb-4 text-primary">🎬 管理課程</h3>

    <!-- 🔽 選擇課程 -->
    <div class="card shadow-sm p-3 mb-4">
      <label class="form-label fw-bold mb-2">選擇要修改的課程</label>
      <select class="form-select" v-model="selectedCourseId" @change="onSelectCourse">
        <option value="">請選擇課程</option>
        <option
          v-for="course in myCourses"
          :key="`${course.courseId}-${course.updatedAt ?? ''}`"
          :value="course.courseId"
        >
          {{ course.courseTitle }}
        </option>
      </select>

      <div v-if="selectedCourse" class="d-flex align-items-center mt-3">
        <img
          :src="selectedCourse.coverUrl"
          alt="封面"
          class="rounded me-3"
          style="width: 120px; height: 70px; object-fit: cover"
        />
        <div>
          <h6 class="fw-bold mb-1 text-primary">
            🛠 現在正在修改：{{ selectedCourse.courseTitle }}
          </h6>
          <small class="text-muted">{{ selectedCourse.courseDescription }}</small>
        </div>
      </div>
    </div>

    <div class="row g-4">
      <!-- 左側 -->
      <div class="col-md-7">
        <!-- 🎥 影片播放預覽 -->
        <div class="card shadow-sm mb-4">
          <div class="card-header fw-bold bg-light">影片預覽</div>
          <div class="card-body preview-box text-center video-box">
            <div v-if="currentVideoUrl">
              <iframe :src="embedUrl" frameborder="0" allowfullscreen></iframe>
            </div>
            <div v-else class="text-muted">請於右側選擇影片播放預覽</div>
          </div>
        </div>

        <!-- 🖼️ 封面與課程設定 -->
        <div class="card shadow-sm p-4">
          <h5 class="fw-bold border-start border-3 border-primary ps-2 mb-3">課程設定</h5>

          <div class="mb-3">
            <label class="form-label fw-bold">封面圖片</label>
            <div class="preview-box text-center cover-box" @click="triggerCoverUpload">
              <input type="file" ref="coverInput" accept="image/*" @change="onCoverChange" class="d-none" />
              <div v-if="coverPreviewUrl || form.coverUrl" :key="coverPreviewUrl || form.coverUrl">
                <img :src="coverPreviewUrl || form.coverUrl" alt="封面預覽" />
              </div>
              <div v-else class="text-muted">
                <i class="bi bi-plus-lg fs-3"></i><br />點擊上傳封面
              </div>
            </div>
          </div>

          <div class="mb-3">
            <label class="form-label fw-bold">課程名稱</label>
            <input type="text" v-model="form.title" class="form-control" placeholder="請輸入課程名稱" />
          </div>

          <div class="mb-3">
            <label class="form-label fw-bold">課程分類</label>
            <select v-model="form.category" class="form-select">
              <option disabled value="">請選擇分類</option>
              <option v-for="cat in categories" :key="cat.categoryId" :value="cat.categoryName">
                {{ cat.categoryName }}
              </option>
            </select>
          </div>

          <div class="mb-3">
            <label class="form-label fw-bold">課程簡介</label>
            <textarea
              v-model="form.description"
              class="form-control"
              rows="3"
              placeholder="請輸入課程介紹..."
            ></textarea>
          </div>

          <div class="mb-3">
            <label class="form-label fw-bold">售價 (NT$)</label>
            <input type="number" v-model="form.price" class="form-control" placeholder="請輸入售價，例如 499" />
          </div>

          <div class="text-end">
            <button class="btn btn-primary px-4" :disabled="isSubmitting" @click="submitCourse">
              <span v-if="isSubmitting">⏳ 處理中...</span>
              <span v-else>{{ selectedCourseId ? '💾 更新課程' : '🚀 發布課程' }}</span>
            </button>
          </div>
        </div>
      </div>

      <!-- 右側：章節管理 -->
      <div class="col-md-5">
        <div class="card shadow-sm p-3">
          <div class="d-flex justify-content-between align-items-center mb-3">
            <button class="btn btn-outline-success" @click="addSection">➕ 新增章節</button>
            <button class="btn btn-outline-secondary" @click="saveDraft">💾 暫存草稿</button>
          </div>

          <h5 class="fw-bold border-start border-3 border-success ps-2 mb-3">章節與影片管理</h5>

          <div v-if="form.sections.length === 0" class="text-muted text-center py-4">尚未新增章節</div>

          <div
            v-for="(section, idx) in form.sections"
            :key="idx"
            class="border rounded p-3 mb-3 bg-light"
          >
            <div class="d-flex justify-content-between align-items-center mb-2">
              <h6 class="fw-bold text-primary mb-0">{{ section.title }}</h6>
              <button class="btn btn-sm btn-outline-danger" @click="removeSection(idx)">刪除</button>
            </div>

            <div v-if="section.videos.length">
              <div
                v-for="(video, vIdx) in section.videos"
                :key="vIdx"
                class="d-flex align-items-center mb-2 video-item"
                @click="playVideo(video.url)"
              >
                <img :src="getYoutubeThumbnail(video.url)" alt="影片縮圖" class="rounded me-2" width="80" />
                <div class="flex-grow-1">
                  <div class="fw-bold small mb-0">{{ video.title || '未命名影片' }}</div>
                  <small class="text-muted">{{ video.url }}</small>
                </div>
                <button class="btn btn-sm btn-outline-danger ms-2" @click.stop="removeVideo(idx, vIdx)">✕</button>
              </div>
            </div>

            <div v-else class="text-muted small">尚無影片內容</div>

            <button class="btn btn-sm btn-outline-primary mt-2 w-100" @click="addVideoToSection(idx)">
              ➕ 新增影片
            </button>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import axios from '@/api/axios'
import Swal from 'sweetalert2'
import useUserStore from '@/stores/user.js'

const userStore = useUserStore()

const form = ref({
  title: '',
  category: '',
  description: '',
  price: '',
  coverUrl: '',
  sections: [],
})

const myCourses = ref([])
const selectedCourseId = ref('')
const selectedCourse = ref(null)
const coverInput = ref(null)
const coverPreviewUrl = ref('')
const currentVideoUrl = ref('')
const isSubmitting = ref(false)
const categories = ref([])

onMounted(async () => {
  if (!userStore.teacherId) {
    const res = await axios.get(`/teachers/user/${userStore.userId}`)
    userStore.teacherId = res.data.teacherId
  }
  await fetchCategories()
  await fetchMyCourses()

  const draft = localStorage.getItem('teacherCourseDraft')
  if (draft) {
    form.value = JSON.parse(draft)
    coverPreviewUrl.value = form.value.coverUrl
  }
})

// 🔹 抓分類
async function fetchCategories() {
  try {
    const res = await axios.get('/categories/all')
    categories.value = res.data
  } catch {
    console.warn('無法載入分類列表')
  }
}

// 🔹 抓取老師所有課程
async function fetchMyCourses() {
  try {
    const res = await axios.get(`/courses/teacher/${userStore.teacherId}`)
    const data = Array.isArray(res.data) ? res.data : []
    myCourses.value = data
  } catch (err) {
    console.error('載入課程錯誤:', err)
    Swal.fire('錯誤', '無法載入課程列表', 'error')
  }
}

// 🔹 選取課程 → 載入章節與影片
async function onSelectCourse() {
  if (!selectedCourseId.value) return
  try {
    const courseRes = await axios.get(`/courses/${selectedCourseId.value}`)
    const secRes = await axios.get(`/course-section/${selectedCourseId.value}`)
    const base = import.meta.env.VITE_API_BASE_URL.replace('/api', '')

    const course = courseRes.data
    if (course.coverUrl && !course.coverUrl.startsWith('http')) {
      course.coverUrl = `${base}${course.coverUrl}`
    }

    const sections = Array.isArray(secRes.data) ? secRes.data : []

    selectedCourse.value = course
    form.value = {
      title: course.courseTitle || '',
      category: course.category?.categoryName ?? '',
      description: course.courseDescription || '',
      price: course.price ?? '',
      coverUrl: course.coverUrl || '',
      sections: sections.map((s, i) => ({
        title: s.sectionTitle || `章節 ${i + 1}`,
        videos: Array.isArray(s.videos) ? s.videos : [],
      })),
    }

    if (form.value.sections.length && form.value.sections[0].videos.length) {
      currentVideoUrl.value = form.value.sections[0].videos[0].url
    } else {
      currentVideoUrl.value = ''
    }
  } catch (e) {
    console.error('課程載入錯誤:', e)
    Swal.fire('錯誤', '載入課程資料失敗', 'error')
  }
}

// ======= 影片控制 =======
function playVideo(url) {
  currentVideoUrl.value = url
}
function getYoutubeThumbnail(url) {
  const m = url.match(/(?:v=|youtu\.be\/)([^&]+)/)
  return m ? `https://img.youtube.com/vi/${m[1]}/hqdefault.jpg` : 'https://via.placeholder.com/80x45?text=Video'
}
const embedUrl = computed(() => {
  if (!currentVideoUrl.value) return ''
  const id = currentVideoUrl.value.match(/(?:v=|youtu\.be\/)([^&]+)/)
  return id ? `https://www.youtube.com/embed/${id[1]}` : ''
})

// ======= 封面上傳 =======
function triggerCoverUpload() {
  coverInput.value?.click()
}
async function onCoverChange(e) {
  const file = e.target.files?.[0]
  if (!file) return
  coverPreviewUrl.value = URL.createObjectURL(file)
  const fd = new FormData()
  fd.append('file', file)
  try {
    const res = await axios.post('/courses/uploadCover', fd, {
      headers: { 'Content-Type': 'multipart/form-data' },
    })
    form.value.coverUrl = res.data.url
  } catch {
    Swal.fire('錯誤', '封面上傳失敗', 'error')
  }
}

// ======= 草稿 =======
function saveDraft() {
  localStorage.setItem('teacherCourseDraft', JSON.stringify(form.value))
  Swal.fire('✅ 已暫存', '草稿已儲存', 'success')
  coverPreviewUrl.value = form.value.coverUrl
}

// ======= 提交 / 更新課程 =======
async function submitCourse() {
  try {
    if (!form.value.category || form.value.category === '') {
      await Swal.fire('⚠️ 請選擇分類', '課程分類不能留空', 'warning')
      return
    }

    const payload = {
      teacherId: userStore.teacherId,
      courseTitle: form.value.title,
      courseDescription: form.value.description,
      price: form.value.price,
      coverUrl: form.value.coverUrl,
      category: form.value.category,
      sections: form.value.sections.map((s) => ({
        title: s.title,
        videos: s.videos.map((v) => ({
          title: v.title,
          url: v.url
        }))
      }))
    }

    const url = selectedCourseId.value
      ? `/courses/update/${selectedCourseId.value}`
      : '/courses/createFull'
    const method = selectedCourseId.value ? 'put' : 'post'

    await axios[method](url, payload)
    await Swal.fire('✅ 成功', selectedCourseId.value ? '課程已更新！' : '課程已發布！', 'success')

    // ✅ 重整課程列表 + 清空表單
    await fetchMyCourses()
    form.value = { title: '', category: '', description: '', price: '', coverUrl: '', sections: [] }
    coverPreviewUrl.value = ''
    selectedCourseId.value = ''
    selectedCourse.value = null
    currentVideoUrl.value = ''
    localStorage.removeItem('teacherCourseDraft')
    window.scrollTo({ top: 0, behavior: 'smooth' })

  } catch (err) {
    console.error('❌ 提交錯誤:', err)
    Swal.fire('❌ 失敗', '課程提交失敗', 'error')
  }
}
</script>

<style scoped>
.teacher-upload {
  user-select: none;
}
.preview-box,
.cover-box {
  width: 100%;
  aspect-ratio: 16 / 9;
  background-color: #f8f9fa;
  border: 2px dashed #cfd8dc;
  border-radius: 10px;
  overflow: hidden;
  position: relative;
  display: flex;
  align-items: center;
  justify-content: center;
}
.preview-box iframe {
  position: absolute;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  border: none;
  border-radius: 10px;
}
.cover-box img {
  width: 100%;
  height: 100%;
  object-fit: contain;
  background-color: #fff;
  border-radius: 10px;
}
.video-item {
  cursor: pointer;
  transition: background-color 0.2s;
}
.video-item:hover {
  background-color: #e9ecef;
}
</style>
