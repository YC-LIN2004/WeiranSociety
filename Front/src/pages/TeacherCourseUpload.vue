<template>
  <div class="teacher-upload container-fluid py-4">
    <h3 class="fw-bold mb-4 text-primary">🎬 上傳課程</h3>

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
              <option>程式開發</option>
              <option>設計藝術</option>
              <option>行銷經營</option>
              <option>語言學習</option>
              <option>其他</option>
            </select>
          </div>

          <div class="mb-3">
            <label class="form-label fw-bold">課程簡介</label>
            <textarea v-model="form.description" class="form-control" rows="3" placeholder="請輸入課程介紹..."></textarea>
          </div>

          <div class="mb-3">
            <label class="form-label fw-bold">售價 (NT$)</label>
            <input type="number" v-model="form.price" class="form-control" placeholder="請輸入售價，例如 499" />
          </div>

          <div class="text-end">
            <button class="btn btn-primary px-4" :disabled="isSubmitting" @click="submitCourse">
              <span v-if="isSubmitting">⏳ 發布中...</span>
              <span v-else>🚀 發布課程</span>
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

          <div v-if="form.sections.length === 0" class="text-muted text-center py-4">
            尚未新增章節
          </div>

          <div v-for="(section, idx) in form.sections" :key="idx" class="border rounded p-3 mb-3 bg-light">
            <div class="d-flex justify-content-between align-items-center mb-2">
              <input type="text" v-model="section.title" class="form-control form-control-sm me-2" placeholder="章節名稱" />
              <button class="btn btn-sm btn-outline-danger" @click="removeSection(idx)">刪除</button>
            </div>

            <div v-for="(video, vIdx) in section.videos" :key="vIdx" class="d-flex align-items-center mb-2 video-item" @click="playVideo(video.url)">
              <img :src="getYoutubeThumbnail(video.url)" class="rounded me-2" width="80" />
              <div class="flex-grow-1">
                <div class="fw-bold small mb-0">{{ video.title || "未命名影片" }}</div>
                <small class="text-muted">{{ video.url }}</small>
              </div>
              <button class="btn btn-sm btn-outline-danger ms-2" @click.stop="removeVideo(idx, vIdx)">✕</button>
            </div>

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
  sections: []
})

const currentVideoUrl = ref('')
const coverInput = ref(null)
const coverPreviewUrl = ref('')
const isSubmitting = ref(false)

// === 登入與 teacherId 自動載入 ===
onMounted(async () => {
  if (!userStore.userId) {
    Swal.fire('請先登入', '請登入教師帳號後再進入此頁面', 'warning')
    return
  }

  if (!userStore.teacherId) {
    const res = await axios.get(`/teachers/user/${userStore.userId}`)
    userStore.teacherId = res.data.teacherId
  }

  // 讀取暫存草稿
  try {
    const saved = localStorage.getItem('teacherCourseDraft')
    if (saved) {
      const draft = JSON.parse(saved)
      form.value = draft
      if (draft.currentVideoUrl) currentVideoUrl.value = draft.currentVideoUrl
    }
  } catch {
    localStorage.removeItem('teacherCourseDraft')
  }
})

// === 封面上傳 ===
function triggerCoverUpload() { coverInput.value?.click() }
async function onCoverChange(e) {
  const file = e.target.files?.[0]
  if (!file) return
  coverPreviewUrl.value = URL.createObjectURL(file)
  const fd = new FormData()
  fd.append('file', file)
  const res = await axios.post('/courses/uploadCover', fd, { headers: { 'Content-Type': 'multipart/form-data' } })
  form.value.coverUrl = res.data.url
}

// === 章節管理 ===
function addSection() { form.value.sections.push({ title: `章節 ${form.value.sections.length + 1}`, videos: [] }) }
function removeSection(idx) { form.value.sections.splice(idx, 1) }
function addVideoToSection(idx) {
  Swal.fire({
    title: '新增影片',
    html: `<input id="vtitle" class="swal2-input" placeholder="影片標題">
           <input id="vurl" class="swal2-input" placeholder="YouTube 影片 URL">`,
    confirmButtonText: '新增', showCancelButton: true,
    preConfirm: () => ({ title: document.getElementById('vtitle').value, url: document.getElementById('vurl').value })
  }).then(r => { if (r.isConfirmed && r.value.url) form.value.sections[idx].videos.push(r.value) })
}
function removeVideo(sIdx, vIdx) { form.value.sections[sIdx].videos.splice(vIdx, 1) }
function playVideo(url) { currentVideoUrl.value = url }
function getYoutubeThumbnail(url) {
  const m = url.match(/v=([^&]+)/)
  return m ? `https://img.youtube.com/vi/${m[1]}/hqdefault.jpg` : 'https://via.placeholder.com/80x45?text=Video'
}

// === 暫存草稿 ===
function saveDraft() {
  const draft = { ...form.value, currentVideoUrl: currentVideoUrl.value }
  localStorage.setItem('teacherCourseDraft', JSON.stringify(draft))
  Swal.fire('✅ 已暫存', '草稿已儲存', 'success')
  // 🔹 同步顯示影片
  if (draft.currentVideoUrl) {
    currentVideoUrl.value = draft.currentVideoUrl
  } else if (form.value.sections.length && form.value.sections[0].videos.length) {
    // 若有影片就顯示第一支
    currentVideoUrl.value = form.value.sections[0].videos[0].url
  }
}

// === 發布課程 ===
async function submitCourse() {
  if (isSubmitting.value) return
  isSubmitting.value = true
  try {
    await axios.post('/courses/createFull', {
      teacherId: userStore.teacherId,
      courseTitle: form.value.title,
      description: form.value.description,
      category: form.value.category,
      price: form.value.price,
      coverUrl: form.value.coverUrl,
      sections: form.value.sections
    })
    Swal.fire('成功', '課程已發布成功！', 'success').then(() => {
      localStorage.setItem('needRefreshCourses', 'true') // 🔹 通知管理頁刷新
      form.value = { title: '', category: '', description: '', price: '', coverUrl: '', sections: [] }
      coverPreviewUrl.value = ''
      currentVideoUrl.value = ''
      localStorage.removeItem('teacherCourseDraft')
    })
  } catch (err) {
    Swal.fire('錯誤', '發布失敗，請稍後再試', 'error')
  } finally {
    isSubmitting.value = false
  }
}

const embedUrl = computed(() => {
  if (!currentVideoUrl.value) return ''
  const id = currentVideoUrl.value.match(/(?:v=|youtu\.be\/)([^&]+)/)
  return id ? `https://www.youtube.com/embed/${id[1]}` : ''
})
</script>

<style scoped>
/* ===== 預覽框通用樣式 ===== */
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

/* ===== 影片預覽區 ===== */
.preview-box iframe {
  position: absolute;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  border: none;
  border-radius: 10px;
}

/* ===== 封面圖片 ===== */
.cover-box img {
  width: 100%;
  height: 100%;
  object-fit: contain;   /* ✅ 完整顯示整張封面，不被切上下 */
  background-color: #fff;
  border-radius: 10px;
  display: block;
}

/* ===== 小影片縮圖區 ===== */
.video-item {
  cursor: pointer;
  transition: background-color 0.2s;
}
.video-item:hover {
  background-color: #e9ecef;
}
</style>
