import { createApp } from 'vue'
import App from './App.vue'
import router from '@/router/router.js'

// ✅ Bootstrap 核心樣式與 JS
import 'bootstrap/dist/css/bootstrap.min.css'
import 'bootstrap/dist/js/bootstrap.bundle.min.js'

//新增分頁套件樣式
import 'vue-awesome-paginate/dist/style.css'

// ✅ Bootstrap Icons
import 'bootstrap-icons/font/bootstrap-icons.css'

// ✅ Pinia 初始化
import { createPinia } from 'pinia'
import piniaPluginPersistedstate from 'pinia-plugin-persistedstate'

// ✅ Font Awesome (for Navbar.vue)
import { library } from '@fortawesome/fontawesome-svg-core'
import { FontAwesomeIcon } from '@fortawesome/vue-fontawesome'
import { faBell, faEnvelope, faSignOutAlt, faSignInAlt } from '@fortawesome/free-solid-svg-icons'

// 加入需要用到的 icon
library.add(faBell, faEnvelope, faSignOutAlt, faSignInAlt)

const pinia = createPinia()
pinia.use(piniaPluginPersistedstate)

createApp(App)
    .component('font-awesome-icon', FontAwesomeIcon) // ✅ 全域註冊
    .use(router)
    .use(pinia)
    .mount('#app')
