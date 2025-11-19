<template>
    <div class="teacher-page">
        <div class="container">
            <!-- 頁面標題 -->
            <div class="page-header">
                <h1 class="title">👨‍🏫 老師管理</h1>
                <p class="subtitle">管理和審核所有老師申請</p>
            </div>

            <!-- 搜尋與篩選區塊 -->
            <div class="search-section">
                <div class="search-bar">
                    <!-- 關鍵字搜尋 -->
                    <div class="search-input-wrapper">
                        <span class="search-icon">🔍</span>
                        <input v-model="searchKeyword" @input="handleSearch" type="text"
                            placeholder="搜尋姓名、Email、專業領域..." class="search-input" />
                        <button v-if="searchKeyword" @click="clearSearch" class="clear-btn" title="清除搜尋">
                            ✕
                        </button>
                    </div>

                    <!-- 狀態篩選 -->
                    <div class="filter-wrapper">
                        <span class="filter-icon">📋</span>
                        <select v-model="selectedStatus" @change="handleSearch" class="status-filter">
                            <option value="">全部狀態</option>
                            <option value="PENDING">待審核</option>
                            <option value="ACTIVE">已啟用</option>
                            <option value="REJECTED">已拒絕</option>
                            <option value="SUSPENDED">已停權</option>
                        </select>
                    </div>

                    <!-- 重新載入按鈕 -->
                    <button @click="fetchTeachers" class="btn btn-primary">
                        <span class="btn-icon">🔄</span>
                        重新載入
                    </button>
                </div>

                <!-- 搜尋結果提示 -->
                <div v-if="searchKeyword || selectedStatus" class="search-info">
                    <button @click="resetFilters" class="btn-reset">重置篩選</button>
                </div>

                <!-- 統計資訊 -->
                <div v-if="teachers.length > 0" class="stats-bar">
                    <div class="stat-item">
                        <span class="stat-label">{{ searchKeyword || selectedStatus ? '篩選結果' : '總老師數' }}</span>
                        <span class="stat-value">{{ filteredTeachers.length }}</span>
                    </div>
                    <div class="stat-item">
                        <span class="stat-label">待審核</span>
                        <span class="stat-value stat-warning">{{ getFilteredCountByStatus('PENDING') }}</span>
                    </div>
                    <div class="stat-item">
                        <span class="stat-label">已啟用</span>
                        <span class="stat-value stat-success">{{ getFilteredCountByStatus('ACTIVE') }}</span>
                    </div>
                    <div class="stat-item">
                        <span class="stat-label">已停權</span>
                        <span class="stat-value stat-danger">{{ getFilteredCountByStatus('SUSPENDED') }}</span>
                    </div>
                </div>
            </div>

            <!-- Loading 指示器 -->
            <div v-if="loading" class="loading-container">
                <div class="spinner"></div>
                <p>載入中...</p>
            </div>

            <!-- 空狀態 -->
            <div v-else-if="filteredTeachers.length === 0" class="empty-state">
                <div class="empty-icon">👨‍🏫</div>
                <h3>{{ searchKeyword || selectedStatus ? '找不到符合條件的老師' : '目前沒有老師資料' }}</h3>
                <p>{{ searchKeyword || selectedStatus ? '請嘗試調整搜尋條件' : '等待老師申請或調整篩選條件' }}</p>
            </div>

            <!-- 老師列表 -->
            <div v-else class="table-container">
                <table class="teachers-table">
                    <thead>
                        <tr>
                            <th>老師ID</th>
                            <th>姓名</th>
                            <th>電子郵件</th>
                            <th>專業領域</th>
                            <th>評分</th>
                            <th>狀態</th>
                            <th>課程數量</th>
                            <th>建立時間</th>
                            <th>操作</th>
                        </tr>
                    </thead>
                    <tbody>
                        <tr v-for="teacher in filteredTeachers" :key="teacher.teacherId" class="table-row">
                            <td class="teacher-id">#{{ teacher.teacherId }}</td>
                            <td class="teacher-name"><span v-html="highlightText(teacher.username || '-')"></span></td>
                            <td class="email"><span v-html="highlightText(teacher.email || '-')"></span></td>
                            <td class="expertise"><span v-html="highlightText(teacher.expertise || '-')"></span></td>
                            <td class="rating">
                                <span v-if="teacher.teacherRating" class="rating-value">{{ teacher.teacherRating }} ⭐</span>
                                <span v-else class="no-rating">尚無</span>
                            </td>
                            <td>
                                <span :class="['status-badge', getStatusClass(teacher.teacherStatus)]">
                                    {{ getStatusText(teacher.teacherStatus) }}
                                </span>
                            </td>
                            <td class="course-count">{{ teacher.totalCourses || 0 }}</td>
                            <td class="date">{{ formatDate(teacher.createdAt) }}</td>
                            <td>
                                <div class="action-buttons">
                                    <button class="btn-action btn-info" @click="viewDetail(teacher)" title="查看詳細">
                                        <span class="btn-icon">👁️</span>
                                    </button>
                                    <button v-if="isPending(teacher.teacherStatus)" class="btn-action btn-approve"
                                        @click="approve(teacher.teacherId)" :disabled="loading" title="批准">
                                        ✅
                                    </button>
                                    <button v-if="isPending(teacher.teacherStatus)" class="btn-action btn-reject"
                                        @click="reject(teacher.teacherId)" :disabled="loading" title="拒絕">
                                        ❌
                                    </button>
                                    <button v-if="isActive(teacher.teacherStatus)" class="btn-action btn-suspend"
                                        @click="suspend(teacher.teacherId)" :disabled="loading" title="停權">
                                        🚫
                                    </button>
                                    <button v-if="isSuspended(teacher.teacherStatus)" class="btn-action btn-reactivate"
                                        @click="reactivate(teacher.teacherId)" :disabled="loading" title="復權">
                                        🔓
                                    </button>
                                </div>
                            </td>
                        </tr>
                    </tbody>
                </table>
            </div>

            <!-- 詳細資料彈窗 -->
            <div v-if="detailVisible" class="modal-overlay" @click.self="detailVisible = false">
                <div class="modal-content">
                    <div class="modal-header">
                        <h3>👨‍🏫 老師詳細資料</h3>
                        <button class="modal-close" @click="detailVisible = false">✕</button>
                    </div>

                    <div class="modal-body">
                        <div class="detail-grid">
                            <div class="detail-item">
                                <span class="detail-label">老師ID</span>
                                <span class="detail-value">{{ selectedTeacher?.teacherId || '-' }}</span>
                            </div>
                            <div class="detail-item">
                                <span class="detail-label">姓名</span>
                                <span class="detail-value">{{ selectedTeacher?.username || '-' }}</span>
                            </div>
                            <div class="detail-item">
                                <span class="detail-label">Email</span>
                                <span class="detail-value">{{ selectedTeacher?.email || '-' }}</span>
                            </div>
                            <div class="detail-item">
                                <span class="detail-label">專業領域</span>
                                <span class="detail-value">{{ selectedTeacher?.expertise || '-' }}</span>
                            </div>
                            <div class="detail-item">
                                <span class="detail-label">狀態</span>
                                <span :class="['status-badge', getStatusClass(selectedTeacher?.teacherStatus)]">
                                    {{ getStatusText(selectedTeacher?.teacherStatus) }}
                                </span>
                            </div>
                            <div class="detail-item">
                                <span class="detail-label">評分</span>
                                <span class="detail-value">
                                    <span v-if="selectedTeacher?.teacherRating">{{ selectedTeacher.teacherRating }} ⭐</span>
                                    <span v-else>尚無評分</span>
                                </span>
                            </div>
                            <div class="detail-item full-width">
                                <span class="detail-label">個人簡介</span>
                                <span class="detail-value bio">{{ selectedTeacher?.bio || '無' }}</span>
                            </div>

                            <div class="detail-item">
                                <span class="detail-label">課程數量</span>
                                <span class="detail-value">{{ selectedTeacher?.totalCourses || 0 }}</span>
                            </div>
                            <div class="detail-item">
                                <span class="detail-label">建立時間</span>
                                <span class="detail-value">{{ formatDate(selectedTeacher?.createdAt) }}</span>
                            </div>
                            <div class="detail-item">
                                <span class="detail-label">更新時間</span>
                                <span class="detail-value">{{ formatDate(selectedTeacher?.updatedAt) }}</span>
                            </div>

                            <!-- ✅ 新增：學歷證明 -->
                            <div class="detail-item full-width">
                                <span class="detail-label">學歷證明</span>
                                <span class="detail-value">
                                    <template v-if="selectedTeacher?.certificateUrl">
                                        <!-- PDF 預覽 -->
                                        <iframe v-if="selectedTeacher.certificateUrl.endsWith('.pdf')" 
                                            :src="selectedTeacher.certificateUrl"
                                            width="100%" height="400px" style="border-radius: 12px; border: 1px solid #ccc;">
                                        </iframe>

                                        <!-- 圖片預覽 -->
                                        <img v-else :src="selectedTeacher.certificateUrl"
                                            alt="學歷證明"
                                            style="max-width: 100%; max-height: 300px; border-radius: 12px; margin-top: 10px;"
                                            @click="openInNewTab(selectedTeacher.certificateUrl)" />

                                        <!-- 開啟連結 -->
                                        <div style="margin-top: 10px;">
                                            <a :href="selectedTeacher.certificateUrl" target="_blank" class="btn btn-primary"
                                                style="text-decoration: none;">
                                                📄 在新頁面開啟
                                            </a>
                                        </div>
                                    </template>
                                    <template v-else>
                                        尚未上傳
                                    </template>
                                </span>
                            </div>

                            <!-- 課程列表 -->
                            <div v-if="selectedTeacher?.courseTitles?.length > 0" class="detail-item full-width">
                                <span class="detail-label">開設課程</span>
                                <div class="course-list">
                                    <span v-for="(course, index) in selectedTeacher.courseTitles" :key="index"
                                        class="course-tag">{{ course }}</span>
                                </div>
                            </div>
                        </div>
                    </div>

                    <div class="modal-footer">
                        <button class="btn btn-secondary" @click="detailVisible = false">關閉</button>
                    </div>
                </div>
            </div>

            <!-- 提示訊息 -->
            <div v-if="message" :class="['toast-message', messageType === 'error' ? 'toast-error' : 'toast-success']">
                {{ message }}
            </div>
        </div>
    </div>
</template>

<script setup>
import axios from 'axios'
import { onMounted, ref, computed } from 'vue'

// API FUNCTION
const API_URL = 'http://localhost:8080/api/teachers'
const getAllTeachers = () => axios.get(API_URL)
const approveTeacher = (id) => axios.put(`${API_URL}/approve/${id}`)
const rejectTeacher = (id) => axios.put(`${API_URL}/reject/${id}`)
const suspendTeacher = (id) => axios.put(`${API_URL}/suspend/${id}`)
const reactivateTeacher = (id) => axios.put(`${API_URL}/reactivate/${id}`)

// STATE
const teachers = ref([])
const selectedStatus = ref('')
const searchKeyword = ref('')
const detailVisible = ref(false)
const selectedTeacher = ref(null)
const message = ref('')
const messageType = ref('success')
const loading = ref(false)

// OPEN FILE
const openInNewTab = (url) => {
    window.open(url, '_blank')
}

// COMPUTED
const filteredTeachers = computed(() => {
    let result = teachers.value
    if (selectedStatus.value) {
        result = result.filter(t => t.teacherStatus === selectedStatus.value)
    }
    if (searchKeyword.value) {
        const keyword = searchKeyword.value.toLowerCase().trim()
        result = result.filter(teacher => {
            const username = (teacher.username || '').toLowerCase()
            const email = (teacher.email || '').toLowerCase()
            const expertise = (teacher.expertise || '').toLowerCase()
            return username.includes(keyword) || email.includes(keyword) || expertise.includes(keyword)
        })
    }
    return result
})

// 狀態與顯示函式
const getStatusText = (s) => ({ PENDING: '待審核', ACTIVE: '已啟用', REJECTED: '已拒絕', SUSPENDED: '已停權' }[s] || '-')
const getStatusClass = (s) => ({ PENDING: 'status-pending', ACTIVE: 'status-active', REJECTED: 'status-rejected', SUSPENDED: 'status-suspended' }[s] || 'status-default')
const isPending = s => s?.toUpperCase() === 'PENDING'
const isActive = s => s?.toUpperCase() === 'ACTIVE'
const isSuspended = s => s?.toUpperCase() === 'SUSPENDED'

// 日期格式化
const formatDate = d => !d ? '-' : new Date(d).toLocaleString('zh-TW', { year: 'numeric', month: '2-digit', day: '2-digit', hour: '2-digit', minute: '2-digit' })
const highlightText = t => !searchKeyword.value ? t : t?.replace(new RegExp(`(${searchKeyword.value})`, 'gi'), '<mark class="highlight">$1</mark>') || t
const getFilteredCountByStatus = s => filteredTeachers.value.filter(t => t.teacherStatus === s).length
const clearSearch = () => searchKeyword.value = ''
const resetFilters = () => { searchKeyword.value = ''; selectedStatus.value = '' }

// FETCH
const fetchTeachers = async () => {
    try {
        loading.value = true
        const res = await getAllTeachers()
        teachers.value = res.data
    } catch (err) {
        console.error('❌ 取得老師資料失敗:', err)
        showMessage('取得老師資料失敗', 'error')
    } finally {
        loading.value = false
    }
}

// 操作動作
const viewDetail = t => { selectedTeacher.value = t; detailVisible.value = true }
const approve = async (id) => handleAction(approveTeacher, id, '✅ 已批准該老師', '批准失敗')
const reject = async (id) => handleAction(rejectTeacher, id, '✅ 已拒絕該老師申請', '拒絕失敗')
const suspend = async (id) => handleAction(suspendTeacher, id, '✅ 已停權該老師', '停權失敗')
const reactivate = async (id) => handleAction(reactivateTeacher, id, '✅ 已恢復該老師權限', '復權失敗')

const handleAction = async (fn, id, successMsg, errorMsg) => {
    if (!confirm(successMsg.replace('✅ ', '確定要') + '嗎？')) return
    try {
        loading.value = true
        await fn(id)
        showMessage(successMsg)
        await fetchTeachers()
    } catch (err) {
        console.error('❌', errorMsg, err)
        showMessage(errorMsg, 'error')
    } finally {
        loading.value = false
    }
}

// 提示訊息
const showMessage = (msg, type = 'success') => {
    message.value = msg
    messageType.value = type
    setTimeout(() => message.value = '', 3000)
}

// Mounted
onMounted(fetchTeachers)
</script>

<style scoped>
.teacher-page {
    min-height: 100vh;
    background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
    padding: 40px 20px;
}

.container {
    max-width: 1600px;
    margin: 0 auto;
}

/* ===== 頁面標題 ===== */
.page-header {
    text-align: center;
    margin-bottom: 40px;
    color: white;
}

.title {
    font-size: 2.5rem;
    margin: 0 0 10px 0;
    font-weight: 700;
    text-shadow: 2px 2px 4px rgba(0, 0, 0, 0.2);
}

.subtitle {
    font-size: 1.1rem;
    margin: 0;
    opacity: 0.95;
}

/* ===== 搜尋區塊 ===== */
.search-section {
    background: white;
    border-radius: 16px;
    padding: 24px;
    box-shadow: 0 8px 32px rgba(0, 0, 0, 0.15);
    margin-bottom: 30px;
}

.search-bar {
    display: flex;
    gap: 12px;
    margin-bottom: 20px;
}

/* 關鍵字搜尋輸入框 */
.search-input-wrapper {
    flex: 2;
    position: relative;
    display: flex;
    align-items: center;
}

.search-icon {
    position: absolute;
    left: 16px;
    font-size: 18px;
    color: #999;
    z-index: 1;
}

.search-input {
    width: 100%;
    padding: 14px 48px 14px 48px;
    border: 2px solid #e0e0e0;
    border-radius: 12px;
    font-size: 16px;
    transition: all 0.3s;
}

.search-input:focus {
    outline: none;
    border-color: #667eea;
    box-shadow: 0 0 0 3px rgba(102, 126, 234, 0.1);
}

.clear-btn {
    position: absolute;
    right: 12px;
    background: #e0e0e0;
    border: none;
    width: 28px;
    height: 28px;
    border-radius: 50%;
    cursor: pointer;
    transition: all 0.3s;
    color: #666;
    font-size: 14px;
    display: flex;
    align-items: center;
    justify-content: center;
}

.clear-btn:hover {
    background: #d0d0d0;
    transform: scale(1.1);
}

/* 狀態篩選 */
.filter-wrapper {
    flex: 1;
    position: relative;
    display: flex;
    align-items: center;
}

.filter-icon {
    position: absolute;
    left: 16px;
    font-size: 18px;
    color: #999;
    z-index: 1;
}

.status-filter {
    width: 100%;
    padding: 14px 16px 14px 48px;
    border: 2px solid #e0e0e0;
    border-radius: 12px;
    font-size: 16px;
    cursor: pointer;
    transition: all 0.3s;
    background: white;
}

.status-filter:focus {
    outline: none;
    border-color: #667eea;
    box-shadow: 0 0 0 3px rgba(102, 126, 234, 0.1);
}

/* 搜尋結果提示 */
.search-info {
    display: flex;
    justify-content: space-between;
    align-items: center;
    padding: 12px 16px;
    background: linear-gradient(135deg, #e0e7ff 0%, #ddd6fe 100%);
    border-radius: 10px;
    margin-bottom: 16px;
}

.search-info-text {
    font-size: 14px;
    color: #5b21b6;
    font-weight: 500;
}

.search-info-text strong {
    font-weight: 700;
}

.btn-reset {
    padding: 6px 14px;
    background: white;
    border: 2px solid #5b21b6;
    border-radius: 8px;
    color: #5b21b6;
    font-size: 13px;
    font-weight: 600;
    cursor: pointer;
    transition: all 0.3s;
}

.btn-reset:hover {
    background: #5b21b6;
    color: white;
    transform: translateY(-1px);
}

/* ===== 按鈕 ===== */
.btn {
    padding: 14px 28px;
    border: none;
    border-radius: 12px;
    font-size: 16px;
    font-weight: 600;
    cursor: pointer;
    transition: all 0.3s;
    display: flex;
    align-items: center;
    gap: 6px;
    white-space: nowrap;
}

.btn-primary {
    background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
    color: white;
}

.btn-primary:hover {
    transform: translateY(-2px);
    box-shadow: 0 6px 20px rgba(102, 126, 234, 0.4);
}

.btn-secondary {
    background: #6b7280;
    color: white;
}

.btn-secondary:hover {
    background: #4b5563;
    transform: translateY(-2px);
}

.btn-icon {
    font-size: 18px;
}

/* ===== 統計資訊 ===== */
.stats-bar {
    display: grid;
    grid-template-columns: repeat(auto-fit, minmax(150px, 1fr));
    gap: 16px;
    padding-top: 20px;
    border-top: 2px solid #f0f0f0;
}

.stat-item {
    display: flex;
    flex-direction: column;
    align-items: center;
    padding: 16px;
    background: linear-gradient(135deg, #f8f9fa 0%, #e9ecef 100%);
    border-radius: 12px;
    transition: all 0.3s;
}

.stat-item:hover {
    transform: translateY(-3px);
    box-shadow: 0 6px 16px rgba(0, 0, 0, 0.12);
}

.stat-label {
    font-size: 14px;
    color: #666;
    margin-bottom: 8px;
    font-weight: 500;
}

.stat-value {
    font-size: 32px;
    font-weight: 700;
    color: #2c3e50;
}

.stat-success {
    color: #48bb78;
}

.stat-warning {
    color: #f6ad55;
}

.stat-danger {
    color: #fc8181;
}

/* ===== 高亮顯示 ===== */
:deep(.highlight) {
    background: #fef08a;
    color: #854d0e;
    padding: 2px 4px;
    border-radius: 3px;
    font-weight: 600;
}

/* ===== 載入中 ===== */
.loading-container {
    text-align: center;
    padding: 80px 20px;
    background: white;
    border-radius: 16px;
    box-shadow: 0 8px 32px rgba(0, 0, 0, 0.15);
}

.spinner {
    width: 50px;
    height: 50px;
    margin: 0 auto 20px;
    border: 4px solid #f3f3f3;
    border-top: 4px solid #667eea;
    border-radius: 50%;
    animation: spin 1s linear infinite;
}

@keyframes spin {
    0% {
        transform: rotate(0deg);
    }

    100% {
        transform: rotate(360deg);
    }
}

/* ===== 空狀態 ===== */
.empty-state {
    text-align: center;
    padding: 80px 20px;
    background: white;
    border-radius: 16px;
    box-shadow: 0 8px 32px rgba(0, 0, 0, 0.15);
}

.empty-icon {
    font-size: 80px;
    margin-bottom: 20px;
    opacity: 0.6;
}

.empty-state h3 {
    color: #2c3e50;
    font-size: 24px;
    margin: 0 0 12px 0;
}

.empty-state p {
    color: #718096;
    font-size: 16px;
    margin: 0;
}

/* ===== 表格容器 ===== */
.table-container {
    background: white;
    border-radius: 16px;
    overflow: hidden;
    box-shadow: 0 8px 32px rgba(0, 0, 0, 0.15);
    overflow-x: auto;
}

.teachers-table {
    width: 100%;
    border-collapse: collapse;
    min-width: 1200px;
}

thead {
    background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
    color: white;
}

th {
    padding: 18px 16px;
    text-align: left;
    font-weight: 600;
    font-size: 14px;
    text-transform: uppercase;
    letter-spacing: 0.5px;
    white-space: nowrap;
}

tbody tr {
    border-bottom: 1px solid #f0f0f0;
    transition: all 0.3s;
}

tbody tr:hover {
    background: #f8f9fa;
}

tbody tr:last-child {
    border-bottom: none;
}

td {
    padding: 16px;
    font-size: 14px;
    color: #2c3e50;
}

.teacher-id {
    font-weight: 700;
    color: #667eea;
    font-size: 15px;
}

.teacher-name {
    font-weight: 600;
    color: #2d3748;
}

.email {
    color: #718096;
    font-size: 13px;
}

.expertise {
    color: #4a5568;
    font-weight: 500;
}

.rating {
    text-align: center;
}

.rating-value {
    color: #f6ad55;
    font-weight: 600;
}

.no-rating {
    color: #a0aec0;
    font-size: 13px;
}

.course-count {
    text-align: center;
    font-weight: 600;
    color: #48bb78;
}

.date {
    color: #718096;
    font-size: 13px;
    white-space: nowrap;
}

/* ===== 狀態標籤 ===== */
.status-badge {
    display: inline-block;
    padding: 6px 14px;
    border-radius: 20px;
    font-size: 13px;
    font-weight: 600;
    white-space: nowrap;
}

.status-pending {
    background: #fed7d7;
    color: #742a2a;
}

.status-active {
    background: #c6f6d5;
    color: #22543d;
}

.status-rejected {
    background: #e2e8f0;
    color: #4a5568;
}

.status-suspended {
    background: #feebc8;
    color: #7c2d12;
}

/* ===== 操作按鈕 ===== */
.action-buttons {
    display: flex;
    gap: 6px;
    align-items: center;
    flex-wrap: wrap;
}

.btn-action {
    padding: 8px 12px;
    border: none;
    border-radius: 8px;
    font-size: 16px;
    cursor: pointer;
    transition: all 0.3s;
    display: inline-flex;
    align-items: center;
    justify-content: center;
}

.btn-action:hover:not(:disabled) {
    transform: translateY(-2px);
    box-shadow: 0 4px 12px rgba(0, 0, 0, 0.2);
}

.btn-action:disabled {
    opacity: 0.5;
    cursor: not-allowed;
}

.btn-info {
    background: #4299e1;
    color: white;
}

.btn-info:hover:not(:disabled) {
    background: #3182ce;
}

.btn-approve {
    background: #48bb78;
    color: white;
}

.btn-approve:hover:not(:disabled) {
    background: #38a169;
}

.btn-reject {
    background: #fc8181;
    color: white;
}

.btn-reject:hover:not(:disabled) {
    background: #f56565;
}

.btn-suspend {
    background: #f6ad55;
    color: white;
}

.btn-suspend:hover:not(:disabled) {
    background: #ed8936;
}

.btn-reactivate {
    background: #68d391;
    color: white;
}

.btn-reactivate:hover:not(:disabled) {
    background: #48bb78;
}

/* ===== 彈窗 ===== */
.modal-overlay {
    position: fixed;
    inset: 0;
    background: rgba(0, 0, 0, 0.6);
    display: flex;
    align-items: center;
    justify-content: center;
    z-index: 1000;
    padding: 20px;
}

.modal-content {
    background: white;
    border-radius: 20px;
    box-shadow: 0 20px 60px rgba(0, 0, 0, 0.3);
    max-width: 700px;
    width: 100%;
    max-height: 90vh;
    overflow-y: auto;
    animation: modalSlideIn 0.3s ease-out;
}

@keyframes modalSlideIn {
    from {
        opacity: 0;
        transform: translateY(-50px);
    }

    to {
        opacity: 1;
        transform: translateY(0);
    }
}

.modal-header {
    display: flex;
    justify-content: space-between;
    align-items: center;
    padding: 24px;
    border-bottom: 2px solid #f0f0f0;
}

.modal-header h3 {
    font-size: 24px;
    color: #2c3e50;
    margin: 0;
}

.modal-close {
    background: #f0f0f0;
    border: none;
    width: 36px;
    height: 36px;
    border-radius: 50%;
    font-size: 20px;
    cursor: pointer;
    transition: all 0.3s;
    color: #666;
}

.modal-close:hover {
    background: #e0e0e0;
    transform: rotate(90deg);
}

.modal-body {
    padding: 24px;
}

.detail-grid {
    display: grid;
    grid-template-columns: repeat(2, 1fr);
    gap: 20px;
}

.detail-item {
    display: flex;
    flex-direction: column;
    gap: 6px;
}

.detail-item.full-width {
    grid-column: 1 / -1;
}

.detail-label {
    font-size: 13px;
    color: #718096;
    font-weight: 600;
    text-transform: uppercase;
    letter-spacing: 0.5px;
}

.detail-value {
    font-size: 15px;
    color: #2c3e50;
    font-weight: 500;
}

.detail-value.bio {
    line-height: 1.6;
    color: #4a5568;
}

.course-list {
    display: flex;
    flex-wrap: wrap;
    gap: 8px;
    margin-top: 8px;
}

.course-tag {
    display: inline-block;
    padding: 6px 12px;
    background: linear-gradient(135deg, #e0e7ff 0%, #ddd6fe 100%);
    color: #5b21b6;
    border-radius: 16px;
    font-size: 13px;
    font-weight: 600;
}

.modal-footer {
    padding: 20px 24px;
    border-top: 2px solid #f0f0f0;
    display: flex;
    justify-content: flex-end;
}

/* ===== Toast 訊息 ===== */
.toast-message {
    position: fixed;
    bottom: 30px;
    right: 30px;
    padding: 16px 24px;
    border-radius: 12px;
    box-shadow: 0 10px 30px rgba(0, 0, 0, 0.3);
    z-index: 2000;
    animation: slideInRight 0.3s ease-out;
    font-weight: 600;
    color: white;
}

@keyframes slideInRight {
    from {
        opacity: 0;
        transform: translateX(100px);
    }

    to {
        opacity: 1;
        transform: translateX(0);
    }
}

.toast-success {
    background: linear-gradient(135deg, #48bb78 0%, #38a169 100%);
}

.toast-error {
    background: linear-gradient(135deg, #fc8181 0%, #f56565 100%);
}

/* ===== 響應式設計 ===== */
@media (max-width: 1024px) {
    .teachers-table {
        min-width: 1000px;
    }

    .search-bar {
        flex-wrap: wrap;
    }

    .search-input-wrapper {
        flex: 1 1 100%;
    }
}

@media (max-width: 768px) {
    .title {
        font-size: 2rem;
    }

    .search-bar {
        flex-direction: column;
    }

    .btn {
        width: 100%;
        justify-content: center;
    }

    .stats-bar {
        grid-template-columns: repeat(2, 1fr);
    }

    .detail-grid {
        grid-template-columns: 1fr;
    }

    th,
    td {
        padding: 12px 10px;
        font-size: 13px;
    }
}

@media (max-width: 480px) {
    .teacher-page {
        padding: 20px 10px;
    }

    .search-section {
        padding: 16px;
    }

    .title {
        font-size: 1.75rem;
    }

    .subtitle {
        font-size: 1rem;
    }

    .stats-bar {
        grid-template-columns: 1fr;
    }

    .modal-content {
        margin: 10px;
    }

    .search-info {
        flex-direction: column;
        gap: 10px;
        align-items: flex-start;
    }

    .btn-reset {
        width: 100%;
    }
}
</style>