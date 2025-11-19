<template>
    <div class="order-page">
        <div class="container">
            <!-- 页面标题 -->
            <div class="page-header">
                <h1 class="title">📊 統計資料</h1>
                <p class="subtitle">即時查看平台數據與營運狀態</p>
            </div>

            <!-- 统计数据卡片 -->
            <section class="search-section">
                <div v-if="statsLoading" class="loading-stats">
                    <div class="spinner"></div>
                    <p>載入訂單統計中...</p>
                </div>
                <div v-else class="stats-bar">
                    <div class="stat-item">
                        <span class="stat-label">總訂單數</span>
                        <span class="stat-value">{{ orderStats.total }}</span>
                    </div>
                    <div class="stat-item">
                        <span class="stat-label">已完成</span>
                        <span class="stat-value stat-success">{{ orderStats.completed }}</span>
                    </div>
                    <div class="stat-item">
                        <span class="stat-label">付款中</span>
                        <span class="stat-value stat-pending">{{ orderStats.processing }}</span>
                    </div>
                    <div class="stat-item">
                        <span class="stat-label">未付款</span>
                        <span class="stat-value stat-warning">{{ orderStats.pending }}</span>
                    </div>
                </div>
            </section>

            <!-- 图表容器 -->
            <section class="table-container">
                <div class="charts-wrapper">
                    <PlatformStatsChart />
                </div>
            </section>
        </div>
    </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import PlatformStatsChart from '@/components/PlatformStatsChart.vue'
import axios from 'axios'

const api = axios.create({
    baseURL: 'http://localhost:8080/api/orders',
    timeout: 10000
})

// 訂單統計數據
const orderStats = ref({
    total: 0,
    completed: 0,      // 完成
    processing: 0,     // 付款中
    pending: 0         // 未付款
})

const statsLoading = ref(false)

// 獲取訂單統計
const fetchOrderStats = async () => {
    statsLoading.value = true
    try {
        // 獲取所有訂單
        const { data } = await api.get('')

        // 統計各狀態數量
        const stats = {
            total: data.length,
            completed: data.filter(o => o.orderStatus === '完成').length,
            processing: data.filter(o => o.orderStatus === '付款中').length,
            pending: data.filter(o => o.orderStatus === '未付款').length
        }

        orderStats.value = stats

        console.log('📊 訂單統計載入成功:')
        console.log(`  總訂單: ${stats.total}`)
        console.log(`  已完成: ${stats.completed}`)
        console.log(`  付款中: ${stats.processing}`)
        console.log(`  未付款: ${stats.pending}`)

    } catch (error) {
        console.error('獲取訂單統計失敗:', error)
        // 失敗時顯示 0
        orderStats.value = {
            total: 0,
            completed: 0,
            processing: 0,
            pending: 0
        }
    } finally {
        statsLoading.value = false
    }
}

onMounted(() => {
    fetchOrderStats()
})
</script>

<style scoped>
.order-page {
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
    min-height: 120px;
}

/* ===== 載入統計中 ===== */
.loading-stats {
    text-align: center;
    padding: 30px 20px;
}

.spinner {
    width: 40px;
    height: 40px;
    margin: 0 auto 15px;
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

.loading-stats p {
    color: #718096;
    font-size: 14px;
    margin: 0;
}

/* ===== 統計資訊 ===== */
.stats-bar {
    display: grid;
    grid-template-columns: repeat(auto-fit, minmax(200px, 1fr));
    gap: 16px;
}

.stat-item {
    display: flex;
    flex-direction: column;
    align-items: center;
    padding: 20px;
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

.stat-pending {
    color: #4299e1;
}

.stat-warning {
    color: #f6ad55;
}

/* ===== 圖表容器 ===== */
.table-container {
    background: white;
    border-radius: 16px;
    overflow: hidden;
    box-shadow: 0 8px 32px rgba(0, 0, 0, 0.15);
    padding: 30px;
}

.charts-wrapper {
    display: flex;
    gap: 20px;
    flex-wrap: wrap;
}

.charts-wrapper>* {
    flex: 1 1 0;
    min-width: 300px;
}

/* ===== 響應式設計 ===== */
@media (max-width: 768px) {
    .title {
        font-size: 2rem;
    }

    .subtitle {
        font-size: 1rem;
    }

    .stats-bar {
        grid-template-columns: repeat(2, 1fr);
    }

    .charts-wrapper {
        flex-direction: column;
    }

    .charts-wrapper>* {
        min-width: 100%;
    }
}

@media (max-width: 480px) {
    .order-page {
        padding: 20px 10px;
    }

    .search-section {
        padding: 16px;
    }

    .title {
        font-size: 1.75rem;
    }

    .stats-bar {
        grid-template-columns: 1fr;
    }

    .stat-item {
        padding: 16px;
    }

    .stat-value {
        font-size: 28px;
    }
}
</style>