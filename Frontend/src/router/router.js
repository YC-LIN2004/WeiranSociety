import { createRouter, createWebHistory } from 'vue-router'
import useAuthStore from '@/stores/user.js'

/* ================= 前台頁面 ================= */
import Home from '@/pages/HomePage.vue'
import LoginPage from '@/pages/LoginPage.vue'
import RegisterPage from '@/pages/RegisterPage.vue'
import ResetPasswordPage from '@/pages/ResetPasswordPage.vue'
import VerifyPage from '@/pages/VerifyPage.vue'
import ProfilePage from '@/pages/ProfilePage.vue'
import NotFound from '@/pages/NotFound.vue'
import CourseListPage from '@/pages/CourseListPage.vue'
import CourseDetailPage from '@/pages/CourseDetailPage.vue'
import CartPage from '@/pages/CartPage.vue'
import CheckoutPage from '@/pages/CheckoutPage.vue'
import PaymentResult from '@/pages/PaymentResult.vue'
import PaymentResultPage from '@/pages/PaymentResultPage.vue'
import CouponZonePage from '@/pages/CouponZonePage.vue'
import MyCouponsPage from '@/pages/MyCouponsPage.vue'
import StudentHub from '@/pages/StudentHub.vue'
import TeacherLayout from '@/layout/TeacherLayout.vue'
import TeacherDashboard from '@/pages/TeacherDashboard.vue'
import TeacherProfile from '@/pages/TeacherProfile.vue'
import TeacherCourseUpload from '@/pages/TeacherCourseUpload.vue'
import TeacherCourseManage from '@/pages/TeacherCourseManage.vue'
import OrderHistoryPage from '@/pages/OrderHistoryPage.vue'
import TeacherApply from '@/pages/TeacherApply.vue'
/* ================= 後台佈局與頁面 ================= */
import AdminLayout from '@/layout/AdminLayout.vue'
import Dashboard from '@/pages/Dashboard.vue'
import MemberManager from '@/pages/MemberManager.vue'
import CouponManagePage from '@/pages/CouponManagePage.vue'
import CouponTypeManagePage from '@/pages/CouponTypeManagePage.vue'
import CategoryAdmin from '@/pages/admin/CategoryAdmin.vue'
import CourseAdmin from '@/pages/admin/CourseAdmin.vue'
import OrderPage from '@/pages/OrderPage.vue'
import TeacherManage from '@/pages/admin/TeacherManage.vue'
/* ================= 路由定義 ================= */
const routes = [
    /* 前台區 */
    { path: '/', name: 'Home', component: Home },
    { path: '/login', name: 'Login', component: LoginPage },
    { path: '/register', name: 'Register', component: RegisterPage },
    { path: '/reset-password', name: 'ResetPassword', component: ResetPasswordPage },
    { path: '/verify', name: 'Verify', component: VerifyPage },
    { path: '/profile', name: 'Profile', component: ProfilePage },
    { path: '/courselist', name: 'CourseList', component: CourseListPage },
    { path: '/coursedetail/:id', name: 'CourseDetail', component: CourseDetailPage },
    { path: '/cart', name: 'Cart', component: CartPage },
    { path: '/teacherapply', name: 'TeacherApply', component: TeacherApply },

    // 老師中心
    {
        path: '/teacher',
        component: TeacherLayout,
        meta: { requiresAuth: true, role: 'teacher' },
        children: [
            { path: 'dashboard', name: 'TeacherDashboard', component: TeacherDashboard },
            { path: 'profile', name: 'TeacherProfile', component: TeacherProfile },
            { path: 'upload', name: 'TeacherCourseUpload', component: TeacherCourseUpload },
            { path: 'manage', name: 'TeacherCourseManage', component: TeacherCourseManage },
            // 預設跳首頁
            { path: '', redirect: '/teacher/dashboard' }
        ]
    },

    // === 付款相關路由 ===
    {
        path: '/checkout',
        name: 'CheckoutPage',
        component: CheckoutPage,
        meta: { requiresAuth: true }
    },
    {
        path: '/paymentresult',
        name: 'PaymentResult',
        component: PaymentResult
    },
    {
        path: '/payment/result',  // ✅ 新增這個路由
        name: 'PaymentResultPage',
        component: PaymentResultPage
    },
    // === 付款路由結束 ===

    { path: '/courses', name: 'CourseAdmin', component: CourseAdmin },
    { path: '/categoryadmin', name: 'CategoryAdmin', component: CategoryAdmin },
    { path: '/couponzonepage', name: 'CouponZonePage', component: CouponZonePage },
    { path: '/mycouponspage', name: 'MyCouponsPage', component: MyCouponsPage },
    { path: '/studenthub', name: 'StudentHub', component: StudentHub },
    { path: '/orderhistorypage', name: 'OrderHistoryPage', component: OrderHistoryPage },

    /* 後台區 (巢狀結構) */
    {
        path: '/admin',
        component: AdminLayout,
        meta: { requiresAuth: true, role: 'admin' },
        children: [
            { path: '', redirect: '/admin/dashboard' },
            { path: 'dashboard', name: 'AdminDashboard', component: Dashboard },
            { path: 'usersmanager', name: 'MemberManager', component: MemberManager },
            { path: 'coupon', name: 'CouponManage', component: CouponManagePage },
            { path: 'couponType', name: 'CouponTypeManage', component: CouponTypeManagePage },
            { path: 'courses', name: 'CourseAdmin', component: CourseAdmin },
            { path: 'orders', name: 'OrderPage', component: OrderPage },
            { path: 'categoryadmin', name: 'CategoryAdmin', component: CategoryAdmin },
            { path: 'teacher', name: 'TeacherManager', component: TeacherManage }
        ],
    },

    /* 全域 404 錯誤頁 */
    { path: '/:pathMatch(.*)*', name: 'NotFound', component: NotFound },
]

const router = createRouter({
    history: createWebHistory(),
    routes,
})

/* ================= 全域守衛：登入驗證 ================= */
router.beforeEach((to, from, next) => {
    const auth = useAuthStore()

    // 需要登入的路由（後台）
    if (to.meta.requiresAuth && !auth.token) {
        next({ path: '/login', query: { redirect: to.fullPath } })
    }
    // 已登入卻訪問登入頁，導回首頁或後台
    else if (to.path === '/login' && auth.token) {
        next('/')
    }
    else {
        next()
    }
})

export default router