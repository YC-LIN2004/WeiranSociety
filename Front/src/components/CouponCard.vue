<template>
  <div class="coupon-card">
    <div class="row g-0 align-items-center">
      <!-- 左側：折扣標籤 -->
      <div class="col-3 col-md-2 text-center">
        <div class="discount-badge">
          <span class="discount-text">{{ discountText }}</span>
        </div>
      </div>

      <!-- 中間：優惠券資訊 -->
      <div class="col-6 col-md-8">
        <div class="p-3">
          <h6 class="mb-1">{{ coupon.couponType?.name || '優惠券' }}</h6>
          <p class="text-muted small mb-1">{{ coupon.couponType?.description || '' }}</p>
          <p class="text-muted small mb-0">
            有效期限：{{ formatDate(coupon.expiryDate) }}
          </p>
        </div>
      </div>

      <!-- 右側：領取按鈕 -->
      <div class="col-3 col-md-2 text-center">
        <button 
          class="btn btn-sm" 
          :class="isAlreadyClaimed ? 'btn-secondary' : 'btn-primary'"
          :disabled="isAlreadyClaimed"
          @click="handleClaim"
        >
          {{ isAlreadyClaimed ? '已領取' : '領取' }}
        </button>
      </div>
    </div>
  </div>
</template>

<script setup>
import { computed } from 'vue'

// ==================== Props ====================
const props = defineProps({
  coupon: {
    type: Object,
    required: true
  },
  // 是否已領取
  isAlreadyClaimed: {
    type: Boolean,
    default: false
  }
})

// ==================== Emits ====================
const emit = defineEmits(['claim'])

// ==================== 方法 ====================
// 處理領取事件
function handleClaim() {
  if (!props.isAlreadyClaimed) {
    emit('claim', props.coupon.id)
  }
}

// ==================== 計算屬性 ====================
// 計算折扣顯示文字
const discountText = computed(() => {
  const type = props.coupon.couponType?.discountType
  const value = props.coupon.couponType?.discountValue

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

// ==================== 工具函數 ====================
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
.coupon-card {
  border: 2px solid #dee2e6;
  border-radius: 8px;
  background: #fff;
  transition: all 0.3s ease;
}

.coupon-card:hover {
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.1);
}

/* 折扣標籤樣式 */
.discount-badge {
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  color: white;
  padding: 15px 10px;
  border-radius: 8px;
  margin: 10px;
}

.discount-text {
  font-size: 1.3rem;
  font-weight: bold;
}

/* 已領取按鈕樣式 */
.btn:disabled {
  cursor: not-allowed;
  opacity: 0.6;
}
</style>