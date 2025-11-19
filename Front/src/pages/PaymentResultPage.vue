<script setup>
import { ref, onMounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'

const route = useRoute()
const router = useRouter()

const status = ref('')
const orderId = ref('')
const isLoading = ref(true)

onMounted(() => {
    // 從 URL 參數取得狀態
    status.value = route.query.status || 'unknown'
    orderId.value = route.query.orderId || ''

    console.log('📋 付款結果:', {
        status: status.value,
        orderId: orderId.value
    })

    // 模擬載入
    setTimeout(() => {
        isLoading.value = false
    }, 500)

    // ✅ 清除購物車資料（付款成功後）
    if (status.value === 'success') {
        setTimeout(() => {
            localStorage.removeItem('checkoutItems')
            localStorage.removeItem('checkoutDiscount')
            localStorage.removeItem('checkoutTotal')
            console.log('✅ 已清除購物車資料')
        }, 1000)
    }
})

const goToHome = () => {
    router.push('/')
}

const goToOrders = () => {
    router.push('/orderhistorypage')  // 假設您有訂單列表頁面
}

// const goToCourses = () => {
//     router.push('/courses')
// }
</script>

<template>
    <div class="payment-result-page">
        <div class="container">
            <!-- 載入中 -->
            <div v-if="isLoading" class="loading-container">
                <div class="spinner"></div>
                <p>處理中...</p>
            </div>

            <!-- 付款成功 -->
            <div v-else-if="status === 'success'" class="result-container success">
                <div class="icon-container">
                    <div class="success-icon">
                        <svg viewBox="0 0 24 24" fill="none" xmlns="http://www.w3.org/2000/svg">
                            <path d="M9 11L12 14L22 4" stroke="currentColor" stroke-width="2" stroke-linecap="round"
                                stroke-linejoin="round" />
                            <path
                                d="M21 12V19C21 19.5304 20.7893 20.0391 20.4142 20.4142C20.0391 20.7893 19.5304 21 19 21H5C4.46957 21 3.96086 20.7893 3.58579 20.4142C3.21071 20.0391 3 19.5304 3 19V5C3 4.46957 3.21071 3.96086 3.58579 3.58579C3.96086 3.21071 4.46957 3 5 3H16"
                                stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round" />
                        </svg>
                    </div>
                </div>

                <h1 class="title">付款成功！</h1>
                <p class="message">感謝您的購買，我們已收到您的付款。</p>

                <div v-if="orderId" class="order-info">
                    <p class="order-label">訂單編號</p>
                    <p class="order-number">{{ orderId }}</p>
                </div>

                <div class="info-box">
                    <p>✅ 付款已完成</p>
                    <p>📧 訂單確認信已發送到您的信箱</p>
                    <p>📚 您可以在「我的課程」中查看已購買的課程</p>
                </div>

                <div class="button-group">
                    <!-- <button @click="goToCourses" class="btn btn-primary">
                        查看我的課程
                    </button> -->
                    <button @click="goToOrders" class="btn btn-secondary">
                        查看訂單記錄
                    </button>
                    <button @click="goToHome" class="btn btn-text">
                        返回首頁
                    </button>
                </div>
            </div>

            <!-- 付款失敗 -->
            <div v-else class="result-container failed">
                <div class="icon-container">
                    <div class="failed-icon">
                        <svg viewBox="0 0 24 24" fill="none" xmlns="http://www.w3.org/2000/svg">
                            <path
                                d="M12 8V12M12 16H12.01M21 12C21 16.9706 16.9706 21 12 21C7.02944 21 3 16.9706 3 12C3 7.02944 7.02944 3 12 3C16.9706 3 21 7.02944 21 12Z"
                                stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round" />
                        </svg>
                    </div>
                </div>

                <h1 class="title">付款失敗</h1>
                <p class="message">付款過程中發生問題，請稍後再試。</p>

                <div class="info-box error">
                    <p>❌ 付款未完成</p>
                    <p>💡 可能的原因：</p>
                    <ul>
                        <li>付款資訊有誤</li>
                        <li>餘額不足</li>
                        <li>網路連線中斷</li>
                        <li>取消付款</li>
                    </ul>
                </div>

                <div class="button-group">
                    <button @click="router.push('/cart')" class="btn btn-primary">
                        返回購物車重試
                    </button>
                    <button @click="goToHome" class="btn btn-secondary">
                        返回首頁
                    </button>
                </div>
            </div>
        </div>
    </div>
</template>

<style scoped>
.payment-result-page {
    min-height: 100vh;
    background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
    display: flex;
    align-items: center;
    justify-content: center;
    padding: 2rem;
}

.container {
    max-width: 600px;
    width: 100%;
}

.loading-container {
    background: white;
    border-radius: 20px;
    padding: 4rem 2rem;
    text-align: center;
    box-shadow: 0 20px 60px rgba(0, 0, 0, 0.3);
}

.spinner {
    width: 50px;
    height: 50px;
    margin: 0 auto 1rem;
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

.result-container {
    background: white;
    border-radius: 20px;
    padding: 3rem 2rem;
    text-align: center;
    box-shadow: 0 20px 60px rgba(0, 0, 0, 0.3);
    animation: slideUp 0.5s ease-out;
}

@keyframes slideUp {
    from {
        opacity: 0;
        transform: translateY(30px);
    }

    to {
        opacity: 1;
        transform: translateY(0);
    }
}

.icon-container {
    margin-bottom: 2rem;
}

.success-icon {
    width: 100px;
    height: 100px;
    margin: 0 auto;
    background: linear-gradient(135deg, #4CAF50 0%, #45a049 100%);
    border-radius: 50%;
    display: flex;
    align-items: center;
    justify-content: center;
    color: white;
    animation: scaleIn 0.5s ease-out;
}

.failed-icon {
    width: 100px;
    height: 100px;
    margin: 0 auto;
    background: linear-gradient(135deg, #f44336 0%, #d32f2f 100%);
    border-radius: 50%;
    display: flex;
    align-items: center;
    justify-content: center;
    color: white;
    animation: scaleIn 0.5s ease-out;
}

@keyframes scaleIn {
    from {
        transform: scale(0);
    }

    to {
        transform: scale(1);
    }
}

.icon-container svg {
    width: 50px;
    height: 50px;
}

.title {
    font-size: 2rem;
    font-weight: 700;
    margin-bottom: 1rem;
    color: #333;
}

.success .title {
    color: #4CAF50;
}

.failed .title {
    color: #f44336;
}

.message {
    font-size: 1.1rem;
    color: #666;
    margin-bottom: 2rem;
}

.order-info {
    background: #f5f5f5;
    border-radius: 12px;
    padding: 1.5rem;
    margin-bottom: 2rem;
}

.order-label {
    font-size: 0.9rem;
    color: #666;
    margin-bottom: 0.5rem;
}

.order-number {
    font-size: 1.2rem;
    font-weight: 600;
    color: #333;
    font-family: 'Courier New', monospace;
}

.info-box {
    background: #f0f8f0;
    border-left: 4px solid #4CAF50;
    border-radius: 8px;
    padding: 1.5rem;
    margin-bottom: 2rem;
    text-align: left;
}

.info-box.error {
    background: #fff5f5;
    border-left-color: #f44336;
}

.info-box p {
    margin: 0.5rem 0;
    color: #333;
}

.info-box ul {
    margin: 0.5rem 0 0 1.5rem;
    color: #666;
}

.info-box li {
    margin: 0.25rem 0;
}

.button-group {
    display: flex;
    flex-direction: column;
    gap: 1rem;
}

.btn {
    padding: 1rem 2rem;
    font-size: 1rem;
    font-weight: 600;
    border: none;
    border-radius: 10px;
    cursor: pointer;
    transition: all 0.3s;
    text-decoration: none;
    display: inline-block;
}

.btn-primary {
    background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
    color: white;
}

.btn-primary:hover {
    transform: translateY(-2px);
    box-shadow: 0 10px 25px rgba(102, 126, 234, 0.4);
}

.btn-secondary {
    background: white;
    color: #667eea;
    border: 2px solid #667eea;
}

.btn-secondary:hover {
    background: #f5f5f5;
}

.btn-text {
    background: transparent;
    color: #666;
}

.btn-text:hover {
    background: #f5f5f5;
}

@media (max-width: 768px) {
    .payment-result-page {
        padding: 1rem;
    }

    .result-container {
        padding: 2rem 1.5rem;
    }

    .title {
        font-size: 1.5rem;
    }

    .message {
        font-size: 1rem;
    }

    .success-icon,
    .failed-icon {
        width: 80px;
        height: 80px;
    }

    .icon-container svg {
        width: 40px;
        height: 40px;
    }
}
</style>