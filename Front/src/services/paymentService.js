// src/services/paymentService.js
import axios from 'axios'
import useUserStore from '@/stores/user'

const API_BASE_URL = import.meta.env.VITE_PAYMENT_API_URL || 'http://localhost:8080/api/payment'

/**
 * 取得 JWT Token
 */
const getAuthToken = () => {
    const auth = useUserStore()
    return auth.token || localStorage.getItem('token')
}

/**
 * 建立 Axios 請求的 headers（包含 JWT Token）
 */
const getAuthHeaders = () => {
    const token = getAuthToken()

    if (!token) {
        throw new Error('請先登入')
    }

    return {
        'Content-Type': 'application/json',
        'Authorization': `Bearer ${token}`
    }
}

/**
 * 綠界金流 API 服務
 */
const paymentService = {
    /**
     * 建立綠界付款訂單
     * @param {Object} paymentData - 付款資料
     * @param {number} paymentData.userId - 使用者 ID
     * @param {number} paymentData.amount - 付款金額
     * @param {string} paymentData.itemName - 商品名稱
     * @param {Array} paymentData.items - 購買項目列表（選用）
     * @returns {Promise<Object>} 付款表單資料
     */
    async createPayment(paymentData) {
        try {
            console.log('📤 發送付款請求:', paymentData)

            // ✅ 加上 JWT Token
            const response = await axios.post(`${API_BASE_URL}/create`, paymentData, {
                headers: getAuthHeaders()
            })

            console.log('📥 收到付款回應:', response.data)
            return response.data

        } catch (error) {
            console.error('❌ 建立付款失敗:', error)

            // 處理不同類型的錯誤
            if (error.response) {
                // 401: 未授權（未登入或 Token 過期）
                if (error.response.status === 401) {
                    throw new Error('登入已過期，請重新登入')
                }
                // 403: 禁止訪問（權限不足）
                if (error.response.status === 403) {
                    throw new Error('無權限執行此操作，請重新登入')
                }
                // 伺服器回應錯誤
                const errorMessage = error.response.data?.message || error.response.data?.error || '付款請求失敗'
                throw new Error(errorMessage)
            } else if (error.request) {
                // 請求發送但沒有收到回應
                throw new Error('無法連接到付款服務，請檢查網路連線')
            } else {
                // 其他錯誤（例如：未登入）
                throw new Error(error.message || '付款處理發生錯誤')
            }
        }
    },

    /**
     * 查詢訂單狀態
     * @param {number} orderId - 訂單 ID
     * @returns {Promise<Object>} 訂單資訊
     */
    async getOrderStatus(orderId) {
        try {
            console.log('📤 查詢訂單狀態:', orderId)

            // ✅ 加上 JWT Token
            const response = await axios.get(`${API_BASE_URL}/order/${orderId}`, {
                headers: getAuthHeaders()
            })

            console.log('📥 訂單狀態:', response.data)
            return response.data

        } catch (error) {
            console.error('❌ 查詢訂單失敗:', error)

            if (error.response?.status === 404) {
                throw new Error('找不到訂單資訊')
            }

            if (error.response?.status === 401) {
                throw new Error('登入已過期，請重新登入')
            }

            throw new Error(error.response?.data?.error || '查詢訂單失敗')
        }
    },

    /**
     * 根據商店訂單編號查詢
     * @param {string} merchantTradeNo - 商店訂單編號
     * @returns {Promise<Object>} 訂單資訊
     */
    async getOrderByMerchantTradeNo(merchantTradeNo) {
        try {
            // ✅ 加上 JWT Token
            const response = await axios.get(`${API_BASE_URL}/order/merchant/${merchantTradeNo}`, {
                headers: getAuthHeaders()
            })
            return response.data
        } catch (error) {
            console.error('❌ 查詢訂單失敗:', error)

            if (error.response?.status === 401) {
                throw new Error('登入已過期，請重新登入')
            }

            throw new Error(error.response?.data?.error || '查詢訂單失敗')
        }
    }
}

export default paymentService