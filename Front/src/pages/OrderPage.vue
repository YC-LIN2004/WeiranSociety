<template>
  <div class="order-page">
    <div class="container">
      <!-- 頁面標題 -->
      <div class="page-header">
        <h1 class="title">📦 訂單管理</h1>
        <p class="subtitle">管理和查詢所有訂單資訊</p>
      </div>

      <!-- 搜尋區塊 -->
      <div class="search-section">
        <div class="search-bar">
          <div class="search-input-wrapper">
            <span class="search-icon">🔍</span>
            <input v-model="searchKeyword" type="text" placeholder="搜尋訂單編號或用戶ID..." class="search-input"
              @keyup.enter="searchOrders" />
          </div>
          <button @click="searchOrders" class="btn btn-primary">
            搜尋
          </button>
          <button @click="loadOrders" class="btn btn-secondary">
            <span class="btn-icon">🔄</span>
            全部訂單
          </button>
        </div>

        <!-- 統計資訊 -->
        <div v-if="orders.length > 0" class="stats-bar">
          <div class="stat-item">
            <span class="stat-label">總訂單數</span>
            <span class="stat-value">{{ orders.length }}</span>
          </div>
          <div class="stat-item">
            <span class="stat-label">已完成</span>
            <span class="stat-value stat-success">{{ getCountByStatus('完成') }}</span>
          </div>
          <div class="stat-item">
            <span class="stat-label">付款中</span>
            <span class="stat-value stat-pending">{{ getCountByStatus('付款中') }}</span>
          </div>
          <div class="stat-item">
            <span class="stat-label">未付款</span>
            <span class="stat-value stat-warning">{{ getCountByStatus('未付款') }}</span>
          </div>
        </div>
      </div>

      <!-- 載入中 -->
      <div v-if="loading" class="loading-container">
        <div class="spinner"></div>
        <p>載入中...</p>
      </div>

      <!-- 訂單表格 -->
      <div v-else-if="orders.length > 0" class="table-container">
        <table class="orders-table">
          <thead>
            <tr>
              <th>訂單編號</th>
              <th>用戶ID</th>
              <th>總金額</th>
              <th>折扣金額</th>
              <th>實付金額</th>
              <th>訂單狀態</th>
              <th>付款方式</th>
              <th>建立時間</th>
              <th>操作</th>
            </tr>
          </thead>
          <tbody>
            <tr v-for="order in orders" :key="order.orderID" class="table-row">
              <td class="order-id">#{{ order.orderID }}</td>
              <td>{{ order.userID }}</td>
              <td class="amount">NT$ {{ formatAmount(order.totalAmount) }}</td>
              <td class="discount">-NT$ {{ formatAmount(order.discountAmount) }}</td>
              <td class="amount net-amount"> NT$ {{ formatAmount((order.totalAmount || 0) - (order.discountAmount || 0)) }}</td>
              <td>
                <span :class="['status-badge', getStatusClass(order.orderStatus)]">
                  {{ order.orderStatus }}
                </span>
              </td>
              <td class="payment-method">{{ order.paymentMethod }}</td>
              <td class="date">{{ formatDate(order.createdAt) }}</td>
              <td>
                <div class="action-buttons">
                  <!-- 狀態選擇器 -->
                  <select :value="order.orderStatus" @change="updateOrderStatus(order, $event)"
                    :class="['status-select', getStatusClass(order.orderStatus)]">
                    <option value="未付款">未付款</option>
                    <option value="付款中">付款中</option>
                    <option value="完成">完成</option>
                    <option value="已取消">已取消</option>
                    <option value="已退款">已退款</option>
                  </select>

                  <!-- 刪除按鈕 -->
                  <button @click="deleteOrder(order)" class="btn-delete" title="刪除訂單">
                    <span class="btn-icon">🗑️</span>
                  </button>
                </div>
              </td>
            </tr>
          </tbody>
        </table>
      </div>

      <!-- 空狀態 -->
      <div v-else class="empty-state">
        <div class="empty-icon">📭</div>
        <h3>目前沒有訂單資料</h3>
        <p>開始新增訂單或調整搜尋條件</p>
      </div>
    </div>
  </div>
</template>

<script>
import axios from 'axios'

const api = axios.create({
  baseURL: 'http://localhost:8080/api/orders',
  timeout: 10000
})

export default {
  name: 'OrderManagement',
  data() {
    return {
      orders: [],
      searchKeyword: '',
      loading: false,
      // 訂單狀態常數（與後端一致）
      ORDER_STATUS: {
        UNPAID: '未付款',
        PENDING: '付款中',
        PAID: '完成',
        CANCELLED: '已取消',
        REFUNDED: '已退款'
      }
    }
  },
  mounted() {
    this.loadOrders()
  },
  methods: {
    /**
     * 載入所有訂單
     */
    async loadOrders() {
      this.loading = true
      this.searchKeyword = ''
      try {
        const { data } = await api.get('')
        this.orders = data
        console.log(`✅ 載入 ${data.length} 筆訂單`)
        console.log(`💰 已完成: ${this.getCountByStatus('完成')} 筆`)
        console.log(`⏳ 付款中: ${this.getCountByStatus('付款中')} 筆`)
        console.log(`❌ 未付款: ${this.getCountByStatus('未付款')} 筆`)
      } catch (error) {
        console.error('載入訂單失敗:', error)
        alert('載入失敗：' + error.message)
      } finally {
        this.loading = false
      }
    },

    /**
     * 搜尋訂單
     */
    async searchOrders() {
      if (!this.searchKeyword.trim()) {
        this.loadOrders()
        return
      }

      this.loading = true
      try {
        const { data } = await api.get('/search', {
          params: { keyword: this.searchKeyword }
        })
        this.orders = data

        if (data.length === 0) {
          alert(`查無「${this.searchKeyword}」相關訂單`)
        } else {
          console.log(`🔍 搜尋到 ${data.length} 筆訂單`)
        }
      } catch (error) {
        console.error('搜尋失敗:', error)
        alert('搜尋失敗：' + error.message)
      } finally {
        this.loading = false
      }
    },

    /**
     * ✅ 更新訂單狀態 - 使用 Request Body
     */
    async updateOrderStatus(order, event) {
      const newStatus = event.target.value
      const oldStatus = order.orderStatus

      // 如果狀態沒有改變，直接返回
      if (newStatus === oldStatus) {
        return
      }

      try {
        // ✅ 使用 Request Body 發送狀態
        const response = await api.put(`/${order.orderID}/status`, {
          status: newStatus  // Request Body: { "status": "完成" }
        })

        // 檢查後端回應
        if (response.data.success) {
          // ✅ 直接更新本地資料
          order.orderStatus = newStatus

          console.log(`✅ 訂單 #${order.orderID} 狀態已更新: ${oldStatus} → ${newStatus}`)
          alert(`✅ 訂單狀態已更新為「${newStatus}」`)
        } else {
          throw new Error('更新失敗')
        }

      } catch (error) {
        console.error('更新狀態失敗:', error)

        // 顯示詳細錯誤訊息
        let errorMsg = '更新失敗'
        if (error.response?.data?.error) {
          errorMsg = error.response.data.error
        } else if (error.message) {
          errorMsg = error.message
        }

        alert(`❌ ${errorMsg}`)

        // ✅ 失敗時恢復原狀態
        event.target.value = oldStatus
      }
    },

    /**
     * 刪除訂單
     */
    async deleteOrder(order) {
      const confirmed = confirm(
        `確定要刪除訂單 #${order.orderID} 嗎？\n` +
        `用戶ID: ${order.userID}\n` +
        `金額: NT$ ${this.formatAmount(order.netAmount)}\n` +
        `狀態: ${order.orderStatus}\n\n` +
        `此操作無法復原！`
      )

      if (!confirmed) return

      try {
        await api.delete(`/${order.orderID}`)
        console.log(`✅ 訂單 #${order.orderID} 已刪除`)
        alert('✅ 訂單刪除成功')

        // 從列表中移除
        this.orders = this.orders.filter(o => o.orderID !== order.orderID)
      } catch (error) {
        console.error('刪除失敗:', error)
        alert('❌ 刪除失敗：' + error.message)
      }
    },

    /**
     * 格式化日期時間
     */
    formatDate(dateString) {
      if (!dateString) return '-'
      const date = new Date(dateString)
      return date.toLocaleString('zh-TW', {
        year: 'numeric',
        month: '2-digit',
        day: '2-digit',
        hour: '2-digit',
        minute: '2-digit',
        second: '2-digit'
      })
    },

    /**
     * 格式化金額
     */
    formatAmount(amount) {
      if (amount === null || amount === undefined) return '0'
      return Number(amount).toLocaleString('zh-TW', {
        minimumFractionDigits: 0,
        maximumFractionDigits: 2
      })
    },

    /**
     * 獲取狀態對應的 CSS 類別
     */
    getStatusClass(status) {
      const statusMap = {
        '未付款': 'status-unpaid',
        '付款中': 'status-pending',
        '完成': 'status-paid',
        '已取消': 'status-cancelled',
        '已退款': 'status-refunded'
      }
      return statusMap[status] || 'status-default'
    },

    /**
     * 根據狀態統計數量
     */
    getCountByStatus(status) {
      return this.orders.filter(o => o.orderStatus === status).length
    }
  }
}
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
}

.search-bar {
  display: flex;
  gap: 12px;
  margin-bottom: 20px;
}

.search-input-wrapper {
  flex: 1;
  position: relative;
  display: flex;
  align-items: center;
}

.search-icon {
  position: absolute;
  left: 16px;
  font-size: 18px;
  color: #999;
}

.search-input {
  width: 100%;
  padding: 14px 16px 14px 48px;
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
  background: #48bb78;
  color: white;
}

.btn-secondary:hover {
  background: #38a169;
  transform: translateY(-2px);
  box-shadow: 0 6px 20px rgba(72, 187, 120, 0.4);
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

.stat-pending {
  color: #4299e1;
}

.stat-warning {
  color: #f6ad55;
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

/* ===== 表格容器 ===== */
.table-container {
  background: white;
  border-radius: 16px;
  overflow: hidden;
  box-shadow: 0 8px 32px rgba(0, 0, 0, 0.15);
  overflow-x: auto;
}

.orders-table {
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

.order-id {
  font-weight: 700;
  color: #667eea;
  font-size: 15px;
}

.amount {
  font-weight: 600;
  color: #2d3748;
}

.net-amount {
  font-weight: 700;
  color: #48bb78;
  font-size: 15px;
}

.discount {
  color: #f6ad55;
  font-weight: 500;
}

.payment-method {
  color: #718096;
  font-size: 13px;
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

.status-paid {
  background: #c6f6d5;
  color: #22543d;
}

.status-pending {
  background: #bee3f8;
  color: #2c5282;
}

.status-unpaid {
  background: #fed7d7;
  color: #742a2a;
}

.status-cancelled {
  background: #e2e8f0;
  color: #4a5568;
}

.status-refunded {
  background: #feebc8;
  color: #7c2d12;
}

/* ===== 操作按鈕區域 ===== */
.action-buttons {
  display: flex;
  gap: 8px;
  align-items: center;
}

/* 狀態選擇器 */
.status-select {
  padding: 8px 12px;
  border: 2px solid #e0e0e0;
  border-radius: 8px;
  font-size: 13px;
  font-weight: 600;
  cursor: pointer;
  transition: all 0.3s;
  min-width: 100px;
}

.status-select:hover {
  border-color: #667eea;
}

.status-select:focus {
  outline: none;
  border-color: #667eea;
  box-shadow: 0 0 0 3px rgba(102, 126, 234, 0.1);
}

.status-select.status-paid {
  background: #c6f6d5;
  color: #22543d;
  border-color: #9ae6b4;
}

.status-select.status-pending {
  background: #bee3f8;
  color: #2c5282;
  border-color: #90cdf4;
}

.status-select.status-unpaid {
  background: #fed7d7;
  color: #742a2a;
  border-color: #fc8181;
}

.status-select.status-cancelled {
  background: #e2e8f0;
  color: #4a5568;
  border-color: #cbd5e0;
}

.status-select.status-refunded {
  background: #feebc8;
  color: #7c2d12;
  border-color: #fbd38d;
}

/* 刪除按鈕 */
.btn-delete {
  padding: 8px 12px;
  background: #fc8181;
  color: white;
  border: none;
  border-radius: 8px;
  font-size: 18px;
  cursor: pointer;
  transition: all 0.3s;
  display: inline-flex;
  align-items: center;
  justify-content: center;
}

.btn-delete:hover {
  background: #f56565;
  transform: translateY(-2px);
  box-shadow: 0 4px 12px rgba(245, 101, 101, 0.4);
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

/* ===== 響應式設計 ===== */
@media (max-width: 1024px) {
  .orders-table {
    min-width: 1000px;
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

  th,
  td {
    padding: 12px 10px;
    font-size: 13px;
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

  .subtitle {
    font-size: 1rem;
  }

  .stats-bar {
    grid-template-columns: 1fr;
  }
}
</style>