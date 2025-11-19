import axios from 'axios'

// ✅ 建立 axios 實例
const instance = axios.create({
    baseURL: import.meta.env.VITE_API_BASE_URL,
    timeout: 10000,
    headers: { 'Content-Type': 'application/json' }
})

// ✅ 攔截請求：延後載入 useUserStore（用 import 而非 require）
instance.interceptors.request.use(
    async (config) => {
        // 🟡 Debug: 顯示請求來源與完整 URL
        const stack = new Error().stack
        const caller = stack?.split('\n')[2]?.trim() || 'Unknown Source'
        console.log(`📡 [API Request] ${config.method?.toUpperCase()} ${config.url}  ← 來源: ${caller}`)

        const module = await import('@/stores/user.js')
        const useUserStore = module.default || module.useUserStore
        const userStore = useUserStore()

        let token = userStore.token?.value
        if (!token) {
            const storedUser =
                JSON.parse(sessionStorage.getItem('user')) ||
                JSON.parse(localStorage.getItem('user'))
            token = storedUser?.token
        }

        if (token) config.headers.Authorization = `Bearer ${token}`
        return config
    },
    (error) => Promise.reject(error)
)

// ✅ 攔截回應：同樣延後載入 userStore
instance.interceptors.response.use(
    (response) => response,
    async (error) => {
        const status = error.response?.status

        try {
            const module = await import('@/stores/user.js')
            const useUserStore = module.default || module.useUserStore
            const userStore = useUserStore()

            if (status === 401) {
                userStore.clearUser()
                window.location.href = '/login'
            } else if (status === 403) {
                console.warn('❌ 權限不足或 token 無效：', error.response?.data)
            }
        } catch (e) {
            console.warn('攔截器處理時出錯:', e)
        }

        return Promise.reject(error)
    }
)

export default instance
