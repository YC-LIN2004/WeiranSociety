import http from '@/api/axios'

// ==================== 前台 API ====================

/**
 * 查詢可領取的優惠券列表
 */
export function fetchAvailableCoupons() {
  return http.get('/coupons/available').then(res => res.data)
}

/**
 * 領取優惠券
 * @param {number} userId - 用戶ID
 * @param {number} couponId - 優惠券ID
 */
export function claimCoupon(userId, couponId) {
  return http.post('/coupons/claim', null, {
    params: { userId, couponId }
  }).then(res => res.data)
}

/**
 * 查詢我的可使用優惠券
 * @param {number} userId - 用戶ID
 */
export function fetchMyAvailableCoupons(userId) {
  return http.get('/coupons/my-coupons/available', {
    params: { userId }
  }).then(res => res.data)
}

/**
 * 查詢我的已使用優惠券
 * @param {number} userId - 用戶ID
 */
export function fetchMyUsedCoupons(userId) {
  return http.get('/coupons/my-coupons/used', {
    params: { userId }
  }).then(res => res.data)
}

/**
 * 查詢我的已過期優惠券
 * @param {number} userId - 用戶ID
 */
export function fetchMyExpiredCoupons(userId) {
  return http.get('/coupons/my-coupons/expired', {
    params: { userId }
  }).then(res => res.data)
}

/**
 * 計算優惠券折扣
 * @param {object} request - { userId, userCouponIds: [], totalAmount }
 */
export function calculateCouponDiscount(request) {
  return http.post('/coupons/calculate', request).then(res => res.data)
}

/**
 * 使用優惠券（結帳時）
 * @param {number} userId - 用戶ID
 * @param {number[]} userCouponIds - 用戶優惠券ID列表
 * @param {number} orderId - 訂單ID
 */
export function useCoupons(userId, userCouponIds, orderId) {
  return http.post('/coupons/use', null, {
    params: { userId, userCouponIds, orderId }
  }).then(res => res.data)
}

// ==================== 後台管理 API ====================

/**
 * 查詢所有優惠券類型
 */
export function fetchCouponTypes() {
  return http.get('/admin/coupons/types').then(res => res.data)
}

/**
 * 根據ID查詢優惠券類型
 */
export function fetchCouponTypeById(id) {
  return http.get(`/admin/coupons/types/${id}`).then(res => res.data)
}

/**
 * 創建優惠券類型
 */
export function createCouponType(data) {
  return http.post('/admin/coupons/types', data).then(res => res.data)
}

/**
 * 更新優惠券類型
 */
export function updateCouponType(id, data) {
  return http.patch(`/admin/coupons/types/${id}`, data).then(res => res.data)
}

/**
 * 刪除優惠券類型
 */
export function deleteCouponType(id) {
  return http.delete(`/admin/coupons/types/${id}`)
}

/**
 * 查詢所有優惠券
 */
export function fetchAllCoupons() {
  return http.get('/admin/coupons').then(res => res.data)
}

/**
 * 根據ID查詢優惠券
 */
export function fetchCouponById(id) {
  return http.get(`/admin/coupons/${id}`).then(res => res.data)
}

/**
 * 創建優惠券
 */
export function createCoupon(data) {
  return http.post('/admin/coupons', data).then(res => res.data)
}

/**
 * 更新優惠券
 */
export function updateCoupon(id, data) {
  return http.patch(`/admin/coupons/${id}`, data).then(res => res.data)
}

/**
 * 刪除優惠券
 */
export function deleteCoupon(id) {
  return http.delete(`/admin/coupons/${id}`)
}

