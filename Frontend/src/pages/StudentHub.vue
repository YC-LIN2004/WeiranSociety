<template>
  <div class="container my-4 student-hub">

    <!-- Header -->
    <div class="d-flex flex-wrap align-items-center justify-content-between mb-3">
      <h4 class="mb-2 mb-md-0">
        🛡️ 尊貴的 {{ username }}，歡迎回到你的學習基地。
      </h4>
      <div class="text-muted">
        已購課程： <strong>{{ stats.totalPurchased }}</strong> 門
      </div>
    </div>

    <!-- Tabs -->
    <div class="btn-group d-flex sticky-top bg-white pt-2 pb-2" style="top: 56px; z-index: 10;">
      <button type="button" class="btn"
        :class="activeTab === 'goals' ? 'btn-primary' : 'btn-outline-primary'"
        @click="activeTab = 'goals'">我的目標</button>

      <button type="button" class="btn"
        :class="activeTab === 'courses' ? 'btn-primary' : 'btn-outline-primary'"
        @click="activeTab = 'courses'">我的課程</button>
    </div>

    <!-- ===== 我的目標 ===== -->
    <section v-show="activeTab === 'goals'" class="mt-3">
      <div class="card shadow-sm">
        <div class="card-body">

          <!-- 選擇欄位 -->
          <div class="mb-3">
            <select class="form-select text-center"
              v-model="goalSelect" @change="openGoal">
              <option :value="null" disabled>請選擇要編輯的項目</option>
              <option value="bio">🧠 個人傳記</option>
              <option value="learningGoal">🎯 勇者志向</option>
            </select>
          </div>

          <!-- 編輯區 -->
          <div v-if="openedGoal" class="card bg-light">
            <div class="card-body">
              <div class="d-flex justify-content-between">
                <strong>{{ getGoalLabel }}</strong>
                <small class="text-muted">{{ charCount }}/{{ maxLen }}</small>
              </div>

              <textarea class="form-control"
                rows="4"
                v-model="editingText"
                :maxlength="maxLen"></textarea>

              <div class="d-flex justify-content-end gap-2 mt-3">
                <button class="btn btn-outline-secondary" @click="closeGoal">取消</button>
                <button class="btn btn-primary"
                  :disabled="saving || !editingText"
                  @click="saveGoal">✅ 儲存</button>
              </div>
            </div>
          </div>

          <!-- 顯示資料 -->
          <div class="row mt-4">
            <div class="col-md-6">
              <div class="card shadow-sm">
                <div class="card-body">
                  <h6>🧠 個人傳記</h6>
                  <p>{{ profile.bio || '（尚未填寫）' }}</p>
                </div>
              </div>
            </div>

            <div class="col-md-6">
              <div class="card shadow-sm">
                <div class="card-body">
                  <h6>🎯 勇者志向</h6>
                  <p>{{ profile.learningGoal || '（尚未填寫）' }}</p>
                </div>
              </div>
            </div>
          </div>

        </div>
      </div>
    </section>

    <!-- ===== 我的課程 ===== -->
    <section v-show="activeTab === 'courses'" class="mt-3">
      <div v-if="loading" class="text-center py-5">載入中…</div>

      <div v-else-if="courses.length === 0" class="text-center py-5 text-muted">
        尚無課程
      </div>

      <div v-else class="row g-4">

        <!-- 🎞️ 左側播放清單 -->
        <div class="col-md-4">
          <div class="card shadow-sm h-100 overflow-auto" style="max-height: 70vh;">
            <div class="card-header fw-bold bg-light text-center">🎞️ 我的課程清單</div>
            <ul class="list-group list-group-flush">
              <li v-for="c in courses" :key="c.enrollmentId"
                  class="list-group-item list-group-item-action d-flex align-items-center gap-3"
                  :class="{ active: selectedCourse && selectedCourse.enrollmentId === c.enrollmentId }"
                  @click="selectCourse(c)">
                <img :src="c.coverUrl || defaultCover"
                     alt="cover"
                     class="rounded"
                     style="width: 60px; height: 40px; object-fit: cover;">
                <span>{{ c.title }}</span>
              </li>
            </ul>
          </div>
        </div>

        <!-- 🎬 右側播放區 -->
        <div class="col-md-8">
          <div v-if="!selectedCourse" class="text-center text-muted py-5">
            <h4>🎓 準備好繼續學習了嗎，{{ username }}？</h4>
            <p>選擇左邊的課程清單開始學習吧！</p>
          </div>

          <div v-else class="card shadow-sm">
            <div class="card-header bg-primary text-white fw-bold">
              {{ selectedCourse.title }}
            </div>
            <div class="card-body">
              <!-- 🎥 模擬 YT 播放器 -->
              <div class="ratio ratio-16x9 mb-3">
                <iframe :src="embedVideoUrl" frameborder="0" allowfullscreen></iframe>
              </div>

              <!-- 📚 章節清單 -->
              <div v-if="selectedCourse.sections?.length">
                <h6 class="fw-bold mb-2">章節內容</h6>
                <ul class="list-group">
                  <li v-for="s in selectedCourse.sections" :key="s.sectionId" class="list-group-item">
                    {{ s.sectionTitle }}
                  </li>
                </ul>
              </div>
              <div v-else class="text-muted text-center py-3">
                尚無章節資料
              </div>
            </div>
          </div>
        </div>

      </div>
    </section>

  </div>
</template>

<script setup>
import { ref, reactive, computed, onMounted } from 'vue'
import useUserStore from '@/stores/user'
import axios from '@/api/axios'
import defaultCover from '@/assets/course-placeholder.jpg'

// 使用者資訊
const userStore = useUserStore()
const username = computed(() => userStore.username)
const userId = computed(() => userStore.userId)

// 資料變數
const stats = reactive({ totalPurchased: 0 })
const profile = reactive({ bio: '', learningGoal: '', totalCourses: 0 })
const courses = ref([])
const activeTab = ref('courses')

// UI 狀態
const goalSelect = ref(null)
const openedGoal = ref(null)
const editingText = ref('')
const saving = ref(false)
const loading = ref(true)
const selectedCourse = ref(null)

// 開啟選項時顯示輸入框
function openGoal() {
  openedGoal.value = goalSelect.value
  editingText.value = profile[openedGoal.value] || ''
}

function closeGoal() {
  openedGoal.value = null
  goalSelect.value = null
}

// 字數控制
const maxLen = computed(() => openedGoal.value === 'bio' ? 500 : 200)
const charCount = computed(() => editingText.value?.length || 0)
const getGoalLabel = computed(() =>
  openedGoal.value === 'bio' ? '🧠 個人傳記' : '🎯 勇者志向'
)

// ✅ 初始化資料
async function fetchInit() {
  loading.value = true
  try {
    const p = await axios.get('/profile')
    Object.assign(profile, p.data)
    stats.totalPurchased = p.data.totalCourses

    // 🔧 修正版：統一後端回傳結構轉換
    const res = await axios.get(`/api/enrollment/user/${userId.value}`)
    const enrollments = res.data.data || res.data

    courses.value = enrollments.map(e => ({
      enrollmentId: e.enrollmentId,
      title: e.course?.courseTitle || e.courseTitle || '未命名課程',
      coverUrl: e.course?.coverUrl || e.coverUrl || defaultCover,
      videoUrl: e.course?.videoUrl || e.videoUrl || '',
      sections: e.course?.sections || e.sections || []
    }))
  } finally {
    loading.value = false
  }
}

// ✅ 更新個人資料
async function saveGoal() {
  saving.value = true
  await axios.put('/profile', { [openedGoal.value]: editingText.value })
  profile[openedGoal.value] = editingText.value
  closeGoal()
  saving.value = false
}

// 🎬 選擇課程後顯示播放區
function selectCourse(course) {
  selectedCourse.value = course
}

// 🎥 轉換影片連結
const embedVideoUrl = computed(() =>
  selectedCourse.value?.videoUrl
    ? selectedCourse.value.videoUrl.replace('watch?v=', 'embed/')
    : ''
)

onMounted(fetchInit)
</script>

<style scoped>
.object-fit-cover { object-fit: cover; }
.fade-enter-active, .fade-leave-active {
  transition: opacity 0.2s ease-in-out;
}
.fade-enter-from, .fade-leave-to {
  opacity: 0;
}
.list-group-item.active {
  background-color: #0d6efd;
  color: white;
}
.list-group-item:hover {
  background-color: #f8f9fa;
  cursor: pointer;
}
</style>
