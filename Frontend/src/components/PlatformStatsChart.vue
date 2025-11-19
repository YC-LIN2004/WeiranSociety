<template>
    <div class="stats-container">
        <h2>📊 平台概況統計</h2>

        <div v-if="!isLoggedIn" class="not-logged-in">
            ⚠️ 請先登入才能查看統計資料
        </div>

        <div v-else-if="loading" class="loading">載入中...</div>
        <div v-else-if="error" class="error">{{ error }}</div>

        <div v-else>
            <!-- 統計摘要 -->
            <div class="stats-summary">
                <div class="stat-card">
                    <span>👤 用戶總數</span>
                    <strong>{{ stats.userCount.toLocaleString() }}</strong>
                </div>
                <div class="stat-card">
                    <span>📚 課程總數</span>
                    <strong>{{ stats.courseCount.toLocaleString() }}</strong>
                </div>
                <div class="stat-card">
                    <span>💰 總交易金額</span>
                    <strong>NT$ {{ stats.totalTransactionAmount.toLocaleString() }}</strong>
                </div>
            </div>

            <!-- 周數選擇器 -->
            <div class="week-selector">
                <label for="weeks">顯示周數：</label>
                <select id="weeks" v-model="selectedWeeks" @change="handleWeekChange">
                    <option :value="4">最近 4 周</option>
                    <option :value="8">最近 8 周</option>
                    <option :value="12">最近 12 周</option>
                    <option :value="24">最近 24 周</option>
                </select>
            </div>

            <!-- 三個折線圖 -->
            <div class="chart-grid">
                <div class="chart-wrapper">
                    <h3>👤 用戶數趨勢</h3>
                    <canvas ref="userChartCanvas"></canvas>
                    <p class="chart-description">累計用戶數 & 新增用戶數</p>
                </div>

                <div class="chart-wrapper">
                    <h3>📚 課程數量趨勢</h3>
                    <canvas ref="courseChartCanvas"></canvas>
                    <p class="chart-description">累計課程數 & 新增課程數</p>
                </div>

                <div class="chart-wrapper">
                    <h3>💰 交易金額趨勢 (NT$)</h3>
                    <canvas ref="transactionChartCanvas"></canvas>
                    <p class="chart-description">每周交易金額</p>
                </div>
            </div>
        </div>
    </div>
</template>

<script setup>
import { ref, computed, onMounted, onUnmounted, nextTick, watch } from 'vue'
import {
    Chart,
    LineController,
    LineElement,
    PointElement,
    CategoryScale,
    LinearScale,
    Tooltip,
    Legend,
    Filler
} from 'chart.js'
import useUserStore from '@/stores/user.js'

Chart.register(
    LineController,
    LineElement,
    PointElement,
    CategoryScale,
    LinearScale,
    Tooltip,
    Legend,
    Filler
)

const userStore = useUserStore()

// Canvas refs
const userChartCanvas = ref(null)
const courseChartCanvas = ref(null)
const transactionChartCanvas = ref(null)

// Chart instances
const userChart = ref(null)
const courseChart = ref(null)
const transactionChart = ref(null)

// 選擇的周數
const selectedWeeks = ref(4)

// 統計資料
const stats = ref({
    userCount: 0,
    courseCount: 0,
    totalTransactionAmount: 0,
    weeklyStats: []
})

const loading = ref(false)
const error = ref(null)
const isAlive = ref(true)

const isLoggedIn = computed(() => {
    return !!(userStore && userStore.token)
})

// 處理周數變更
const handleWeekChange = async () => {
    await fetchStats()
}

// 驗證並處理數據
const validateAndProcessData = (data) => {
    if (!data) {
        throw new Error('API 返回空數據')
    }

    if (!Array.isArray(data.weeklyStats)) {
        data.weeklyStats = []
    }

    if (data.weeklyStats.length === 0) {
        return data
    }

    data.weeklyStats = data.weeklyStats.map((item, index) => ({
        weekLabel: item.weekLabel || item.week || `第 ${index + 1} 周`,
        userCount: Number(item.userCount) || 0,
        newUsers: Number(item.newUsers) || 0,
        courseCount: Number(item.courseCount) || 0,
        newCourses: Number(item.newCourses) || 0,
        transactionAmount: Number(item.transactionAmount) || 0
    }))

    return data
}

// 建立用戶數折線圖
const createUserChart = async () => {
    await nextTick()

    if (!userChartCanvas.value || !stats.value.weeklyStats || stats.value.weeklyStats.length === 0) {
        return
    }

    if (userChart.value) {
        userChart.value.destroy()
        userChart.value = null
    }

    const ctx = userChartCanvas.value.getContext('2d')
    if (!ctx) return

    const labels = stats.value.weeklyStats.map(item => item.weekLabel)
    const cumulativeData = stats.value.weeklyStats.map(item => item.userCount)
    const newUsersData = stats.value.weeklyStats.map(item => item.newUsers)

    try {
        userChart.value = new Chart(ctx, {
            type: 'line',
            data: {
                labels: labels,
                datasets: [
                    {
                        label: '累計用戶數',
                        data: cumulativeData,
                        borderColor: '#4e79a7',
                        backgroundColor: 'rgba(78, 121, 167, 0.1)',
                        tension: 0.3,
                        fill: true,
                        pointRadius: 4,
                        pointHoverRadius: 6
                    },
                    {
                        label: '新增用戶數',
                        data: newUsersData,
                        borderColor: '#59a14f',
                        backgroundColor: 'rgba(89, 161, 79, 0.1)',
                        tension: 0.3,
                        fill: true,
                        pointRadius: 4,
                        pointHoverRadius: 6
                    }
                ]
            },
            options: {
                responsive: true,
                maintainAspectRatio: false,
                interaction: {
                    mode: 'index',
                    intersect: false
                },
                scales: {
                    y: {
                        beginAtZero: true,
                        ticks: {
                            callback: val => val.toLocaleString()
                        }
                    }
                },
                plugins: {
                    legend: {
                        display: true,
                        position: 'top'
                    },
                    tooltip: {
                        callbacks: {
                            label: ctx => `${ctx.dataset.label}: ${ctx.parsed.y.toLocaleString()}`
                        }
                    }
                }
            }
        })
    } catch (err) {
        console.error('建立用戶圖表失敗:', err)
    }
}

// 建立課程數折線圖
const createCourseChart = async () => {
    await nextTick()

    if (!courseChartCanvas.value || !stats.value.weeklyStats || stats.value.weeklyStats.length === 0) {
        return
    }

    if (courseChart.value) {
        courseChart.value.destroy()
        courseChart.value = null
    }

    const ctx = courseChartCanvas.value.getContext('2d')
    if (!ctx) return

    const labels = stats.value.weeklyStats.map(item => item.weekLabel)
    const cumulativeData = stats.value.weeklyStats.map(item => item.courseCount)
    const newCoursesData = stats.value.weeklyStats.map(item => item.newCourses)

    try {
        courseChart.value = new Chart(ctx, {
            type: 'line',
            data: {
                labels: labels,
                datasets: [
                    {
                        label: '累計課程數',
                        data: cumulativeData,
                        borderColor: '#f28e2b',
                        backgroundColor: 'rgba(242, 142, 43, 0.1)',
                        tension: 0.3,
                        fill: true,
                        pointRadius: 4,
                        pointHoverRadius: 6
                    },
                    {
                        label: '新增課程數',
                        data: newCoursesData,
                        borderColor: '#e15759',
                        backgroundColor: 'rgba(225, 87, 89, 0.1)',
                        tension: 0.3,
                        fill: true,
                        pointRadius: 4,
                        pointHoverRadius: 6
                    }
                ]
            },
            options: {
                responsive: true,
                maintainAspectRatio: false,
                interaction: {
                    mode: 'index',
                    intersect: false
                },
                scales: {
                    y: {
                        beginAtZero: true,
                        ticks: {
                            callback: val => val.toLocaleString()
                        }
                    }
                },
                plugins: {
                    legend: {
                        display: true,
                        position: 'top'
                    },
                    tooltip: {
                        callbacks: {
                            label: ctx => `${ctx.dataset.label}: ${ctx.parsed.y.toLocaleString()}`
                        }
                    }
                }
            }
        })
    } catch (err) {
        console.error('建立課程圖表失敗:', err)
    }
}

// 建立交易金額折線圖
const createTransactionChart = async () => {
    await nextTick()

    if (!transactionChartCanvas.value || !stats.value.weeklyStats || stats.value.weeklyStats.length === 0) {
        return
    }

    if (transactionChart.value) {
        transactionChart.value.destroy()
        transactionChart.value = null
    }

    const ctx = transactionChartCanvas.value.getContext('2d')
    if (!ctx) return

    const labels = stats.value.weeklyStats.map(item => item.weekLabel)
    const transactionData = stats.value.weeklyStats.map(item => item.transactionAmount)

    try {
        transactionChart.value = new Chart(ctx, {
            type: 'line',
            data: {
                labels: labels,
                datasets: [
                    {
                        label: '周交易金額',
                        data: transactionData,
                        borderColor: '#76b7b2',
                        backgroundColor: 'rgba(118, 183, 178, 0.2)',
                        tension: 0.3,
                        fill: true,
                        pointRadius: 4,
                        pointHoverRadius: 6
                    }
                ]
            },
            options: {
                responsive: true,
                maintainAspectRatio: false,
                scales: {
                    y: {
                        beginAtZero: true,
                        ticks: {
                            callback: val => `NT$ ${val.toLocaleString()}`
                        }
                    }
                },
                plugins: {
                    legend: {
                        display: true,
                        position: 'top'
                    },
                    tooltip: {
                        callbacks: {
                            label: ctx => `NT$ ${ctx.parsed.y.toLocaleString()}`
                        }
                    }
                }
            }
        })
    } catch (err) {
        console.error('建立交易圖表失敗:', err)
    }
}

// 建立所有圖表
const createAllCharts = async () => {
    if (!stats.value.weeklyStats || stats.value.weeklyStats.length === 0) {
        return
    }

    await nextTick()

    if (!userChartCanvas.value || !courseChartCanvas.value || !transactionChartCanvas.value) {
        setTimeout(createAllCharts, 100)
        return
    }

    await createUserChart()
    await createCourseChart()
    await createTransactionChart()
}

// 取得統計資料
const fetchStats = async () => {
    if (!isLoggedIn.value) {
        return
    }

    loading.value = true
    error.value = null

    try {
        const headers = {
            'Content-Type': 'application/json'
        }

        if (userStore.token) {
            headers['Authorization'] = `Bearer ${userStore.token}`
        }

        const url = `http://localhost:8080/api/stats/platform/weekly?weeks=${selectedWeeks.value}`

        const response = await fetch(url, {
            method: 'GET',
            headers: headers,
            credentials: 'include'
        })

        if (!response.ok) {
            const errorText = await response.text()
            throw new Error(`HTTP ${response.status}: ${errorText}`)
        }

        const rawData = await response.json()

        if (!isAlive.value) return

        const validatedData = validateAndProcessData(rawData)

        stats.value = {
            userCount: Number(validatedData.userCount) || 0,
            courseCount: Number(validatedData.courseCount) || 0,
            totalTransactionAmount: Number(validatedData.totalTransactionAmount) || 0,
            weeklyStats: validatedData.weeklyStats || []
        }

        await createAllCharts()

    } catch (err) {
        if (isAlive.value) {
            error.value = `載入統計資料失敗：${err.message || err}`
        }
    } finally {
        if (isAlive.value) loading.value = false
    }
}

// 監控登入狀態
watch(isLoggedIn, (loggedIn) => {
    if (loggedIn) {
        fetchStats()
    }
}, { immediate: true })

onMounted(() => {
    if (isLoggedIn.value) {
        fetchStats()
    }
})

onUnmounted(() => {
    isAlive.value = false

    if (userChart.value) {
        userChart.value.destroy()
        userChart.value = null
    }
    if (courseChart.value) {
        courseChart.value.destroy()
        courseChart.value = null
    }
    if (transactionChart.value) {
        transactionChart.value.destroy()
        transactionChart.value = null
    }
})
</script>

<style scoped>
.stats-container {
    max-width: 1400px;
    margin: 40px auto;
    padding: 30px;
    font-family: 'Inter', sans-serif;
    color: #2c3e50;
    background-color: #ffffff;
    border-radius: 12px;
    box-shadow: 0 4px 12px rgba(0, 0, 0, 0.08);
}

h2 {
    text-align: center;
    margin-bottom: 30px;
    font-size: 1.8rem;
    color: #3498db;
    font-weight: 600;
    border-bottom: 2px solid #ecf0f1;
    padding-bottom: 10px;
}

.not-logged-in {
    text-align: center;
    padding: 40px;
    font-size: 1.2rem;
    color: #e74c3c;
    background-color: #fef5f5;
    border-radius: 8px;
    border: 1px solid #fadbd8;
}

.loading {
    text-align: center;
    padding: 40px;
    font-size: 1.2rem;
    color: #3498db;
}

.error {
    text-align: center;
    padding: 20px;
    background-color: #fef5f5;
    border: 1px solid #e74c3c;
    border-radius: 8px;
    color: #e74c3c;
    margin: 20px 0;
}

/* 統計卡片 */
.stats-summary {
    display: grid;
    grid-template-columns: repeat(auto-fit, minmax(200px, 1fr));
    gap: 20px;
    margin-bottom: 30px;
}

.stat-card {
    background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
    padding: 20px;
    border-radius: 10px;
    box-shadow: 0 4px 12px rgba(0, 0, 0, 0.1);
    text-align: center;
    transition: all 0.3s ease;
    color: white;
}

.stat-card:hover {
    transform: translateY(-5px);
    box-shadow: 0 6px 20px rgba(0, 0, 0, 0.15);
}

.stat-card span {
    display: block;
    font-size: 0.9rem;
    opacity: 0.9;
    margin-bottom: 8px;
}

.stat-card strong {
    display: block;
    font-size: 2rem;
    font-weight: 700;
}

/* 周數選擇器 */
.week-selector {
    text-align: center;
    margin-bottom: 30px;
    padding: 15px;
    background: #f8f9fa;
    border-radius: 8px;
}

.week-selector label {
    font-weight: 600;
    margin-right: 10px;
    color: #555;
}

.week-selector select {
    padding: 8px 15px;
    border: 2px solid #ddd;
    border-radius: 6px;
    font-size: 1rem;
    cursor: pointer;
    transition: all 0.3s ease;
}

.week-selector select:hover {
    border-color: #3498db;
}

.week-selector select:focus {
    outline: none;
    border-color: #3498db;
    box-shadow: 0 0 0 3px rgba(52, 152, 219, 0.1);
}

/* 圖表區 */
.chart-grid {
    display: grid;
    grid-template-columns: repeat(auto-fit, minmax(350px, 1fr));
    gap: 25px;
    margin-top: 30px;
}

.chart-wrapper {
    background: #ffffff;
    padding: 20px;
    border-radius: 10px;
    box-shadow: 0 2px 10px rgba(0, 0, 0, 0.08);
    transition: all 0.3s ease;
}

.chart-wrapper:hover {
    box-shadow: 0 4px 20px rgba(0, 0, 0, 0.12);
}

.chart-wrapper h3 {
    font-size: 1.1rem;
    color: #2c3e50;
    margin-bottom: 15px;
    text-align: center;
    font-weight: 600;
}

.chart-wrapper canvas {
    width: 100% !important;
    height: 250px !important;
}

.chart-description {
    text-align: center;
    font-size: 0.85rem;
    color: #777;
    margin-top: 10px;
    font-style: italic;
}

/* 響應式設計 */
@media (max-width: 1024px) {
    .chart-grid {
        grid-template-columns: 1fr;
    }
}

@media (max-width: 768px) {
    .stats-container {
        padding: 20px;
    }

    .chart-wrapper canvas {
        height: 200px !important;
    }

    .stat-card strong {
        font-size: 1.5rem;
    }
}

@media (max-width: 480px) {
    h2 {
        font-size: 1.4rem;
    }

    .chart-wrapper canvas {
        height: 180px !important;
    }
}
</style>