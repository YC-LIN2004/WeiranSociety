<template>
  <div class="my-coupon-card" :class="{ 'used': coupon.isUsed, 'expired': isExpired }">
    <div class="row g-0 align-items-center">
      <!-- 左側：折扣標籤 -->
      <div class="col-3 col-md-2 text-center">
        <div class="discount-badge">
          <span class="discount-text">{{ discountText }}</span>
        </div>
      </div>

      <!-- 中間：優惠券資訊 -->
      <div class="col-9 col-md-10">
        <div class="p-3">
          <h6 class="mb-1">{{ coupon.couponName || '優惠券' }}</h6>
          <p class="text-muted small mb-1">{{ coupon.description || '' }}</p>
          <p class="text-muted small mb-1">
            有效期限：{{ formatDate(coupon.expiryDate) }}
          </p>
          <p class="text-muted small mb-0">
            券號：<code>{{ coupon.code }}</code>
          </p>
          
          <!-- 已使用標籤 -->
          <span v-if="coupon.isUsed" class="badge bg-secondary mt-2">已使用</span>
          <!-- 已過期標籤 -->
          <span v-else-if="isExpired" class="badge bg-danger mt-2">已過期</span>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { computed } from 'vue'

const props = defineProps({
  coupon: {
    type: Object,
    required: true
  }
})

// 計算折扣顯示文字
const discountText = computed(() => {
  const type = props.coupon.discountType
  const value = props.coupon.discountValue

  if (!type || !value) return ''

  // discountType: 1=固定金額, 2=百分比
  if (type === 1 || type === '1') {
    return `NT$${value}`
  } else if (type === 2 || type === '2') {
    const percentage = (1 - parseFloat(value)) * 100
    return `${(percentage * 0.1).toFixed(1).replace('.0', '')}折`
  }
  return ''
})

// 是否已過期
const isExpired = computed(() => {
  if (!props.coupon.expiryDate) return false
  return new Date(props.coupon.expiryDate) < new Date()
})

// 格式化日期
function formatDate(dateStr) {
  if (!dateStr) return ''
  const date = new Date(dateStr)
  return date.toLocaleDateString('zh-TW', {
    year: 'numeric',
    month: '2-digit',
    day: '2-digit',
    hour: '2-digit',
    minute: '2-digit'
  })
}
</script>

<style scoped>
.my-coupon-card {
  border: 2px solid #dee2e6;
  border-radius: 8px;
  background: #fff;
  transition: all 0.3s ease;
}

.my-coupon-card:hover {
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.1);
}

.my-coupon-card.used,
.my-coupon-card.expired {
  opacity: 0.6;
  background: #f8f9fa;
}

.discount-badge {
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  color: white;
  padding: 15px 10px;
  border-radius: 8px;
  margin: 10px;
}

.my-coupon-card.used .discount-badge,
.my-coupon-card.expired .discount-badge {
  background: linear-gradient(135deg, #999 0%, #666 100%);
}

.discount-text {
  font-size: 1.3rem;
  font-weight: bold;
}

code {
  color: #e83e8c;
  font-size: 0.9rem;
}
</style>

