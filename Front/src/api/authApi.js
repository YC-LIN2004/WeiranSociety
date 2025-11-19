import axios from './axios'
import useUserStore from '@/stores/user.js'

//  註冊 API 
export const register = (data) => axios.post('/auth/register', data)

//  登入 API 
export const login = async (data) => {
    const response = await axios.post('/auth/login', data)

    const userStore = useUserStore()
    userStore.setUser(response.data)

    return response
}

//  忘記密碼 
export const forgotPassword = (email) =>
    axios.post('/auth/forgot-password', { email })

//  重設密碼 
export const resetPassword = (token, newPassword) =>
    axios.post('/auth/reset-password', { token, password: newPassword })
