<!-- src/views/OrderResultPage.vue -->
<template>
  <div class="order-result-page">
    <div class="container">
      <!-- 載入中 -->
      <div v-if="isLoading" class="loading">
        <div class="spinner"></div>
        <p>正在確認付款狀態...</p>
      </div>

      <!-- 付款成功 -->
      <div v-else-if="orderStatus === 'Paid'" class="result-card success">
        <div class="icon">✓</div>
        <h1>付款成功！</h1>
        <p class="message">感謝您的購買，訂單已完成付款</p>

        <div class="order-details">
          <div class="detail-item">
            <span class="label">訂單編號：</span>
            <span class="value">{{ orderData.merchantTradeNo }}</span>
          </div>
          <div class="detail-item">
            <span class="label">交易編號：</span>
            <span class="value">{{ orderData.tradeNo }}</span>
          </div>
          <div class="detail-item">
            <span class="label">付款金額：</span>
            <span class="value">NT$ {{ orderData.totalAmount }}</span>
          </div>
          <div class="detail-item">
            <span class="label">付款時間：</span>
            <span class="value">{{ formatDate(orderData.paymentDate) }}</span>
          </div>
        </div>

        <div class="actions">
          <button class="btn btn-primary" @click="goToMyCourses">
            前往我的課程
          </button>
          <button class="btn btn-secondary" @click="goToHome">
            返回首頁
          </button>
        </div>
      </div>

      <!-- 付款失敗 -->
      <div v-else class="result-card failed">
        <div class="icon">✕</div>
        <h1>付款失敗</h1>
        <p class="message">{{ orderData.rtnMsg || '付款處理失敗，請稍後再試' }}</p>

        <div class="actions">
          <button class="btn btn-primary" @click="retryPayment">
            重新付款
          </button>
          <button class="btn btn-secondary" @click="goToHome">
            返回首頁
          </button>
        </div>
      </div>
    </div>
  </div>
</template>

<script>
import paymentService from '@/services/paymentService';

export default {
  name: 'OrderResultPage',
  data() {
    return {
      isLoading: true,
      orderStatus: null,
      orderData: {}
    };
  },
  async mounted() {
    await this.checkOrderStatus();
  },
  methods: {
    async checkOrderStatus() {
      try {
        // 從 URL 參數取得訂單 ID（實際可能從路由參數取得）
        const orderId = this.$route.query.orderId ||
          localStorage.getItem('currentOrderId');

        if (!orderId) {
          throw new Error('找不到訂單資訊');
        }

        // 查詢訂單狀態
        const response = await paymentService.getOrderStatus(orderId);
        this.orderData = response;
        this.orderStatus = response.orderStatus;

      } catch (error) {
        console.error('查詢訂單狀態失敗:', error);
        this.orderStatus = 'Failed';
        this.orderData.rtnMsg = '無法取得訂單資訊';
      } finally {
        this.isLoading = false;
      }
    },

    formatDate(dateString) {
      if (!dateString) return '-';
      const date = new Date(dateString);
      return date.toLocaleString('zh-TW');
    },

    goToMyCourses() {
      this.$router.push('/my-courses');
    },

    goToHome() {
      this.$router.push('/');
    },

    retryPayment() {
      this.$router.push('/payment');
    }
  }
};
</script>

<style scoped>
.order-result-page {
  min-height: 100vh;
  background-color: #f5f5f5;
  padding: 40px 20px;
  display: flex;
  align-items: center;
  justify-content: center;
}

.container {
  max-width: 600px;
  width: 100%;
}

.loading {
  text-align: center;
  padding: 60px;
  background: white;
  border-radius: 8px;
}

.spinner {
  width: 50px;
  height: 50px;
  border: 4px solid #f3f3f3;
  border-top: 4px solid #4CAF50;
  border-radius: 50%;
  animation: spin 1s linear infinite;
  margin: 0 auto 20px;
}

@keyframes spin {
  0% {
    transform: rotate(0deg);
  }

  100% {
    transform: rotate(360deg);
  }
}

.result-card {
  background: white;
  border-radius: 8px;
  padding: 60px 40px;
  text-align: center;
  box-shadow: 0 2px 10px rgba(0, 0, 0, 0.1);
}

.icon {
  width: 80px;
  height: 80px;
  line-height: 80px;
  font-size: 50px;
  border-radius: 50%;
  margin: 0 auto 30px;
  font-weight: bold;
}

.success .icon {
  background-color: #4CAF50;
  color: white;
}

.failed .icon {
  background-color: #f44336;
  color: white;
}

h1 {
  font-size: 32px;
  margin-bottom: 15px;
  color: #333;
}

.success h1 {
  color: #4CAF50;
}

.failed h1 {
  color: #f44336;
}

.message {
  font-size: 16px;
  color: #666;
  margin-bottom: 40px;
}

.order-details {
  background-color: #f9f9f9;
  border-radius: 8px;
  padding: 30px;
  margin-bottom: 40px;
  text-align: left;
}

.detail-item {
  display: flex;
  justify-content: space-between;
  padding: 12px 0;
  border-bottom: 1px solid #e0e0e0;
}

.detail-item:last-child {
  border-bottom: none;
}

.detail-item .label {
  color: #666;
  font-weight: 500;
}

.detail-item .value {
  color: #333;
  font-weight: 600;
}

.actions {
  display: flex;
  gap: 15px;
}

.btn {
  flex: 1;
  padding: 15px 30px;
  font-size: 16px;
  font-weight: 600;
  border: none;
  border-radius: 8px;
  cursor: pointer;
  transition: all 0.3s;
}

.btn-primary {
  background-color: #4CAF50;
  color: white;
}

.btn-primary:hover {
  background-color: #45a049;
}

.btn-secondary {
  background-color: #f5f5f5;
  color: #666;
}

.btn-secondary:hover {
  background-color: #e0e0e0;
}
</style>