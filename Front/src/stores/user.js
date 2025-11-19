// stores/user.js
import { defineStore } from "pinia";
import { ref } from "vue";

const useUserStore = defineStore(
    "user",
    () => {
        // === 狀態（State）===
        const token = ref(null);        // JWT token
        const userId = ref(null);       // 使用者 ID
        const account = ref(null);      // 帳號（唯一識別）
        const username = ref(null);     // 暱稱
        const email = ref(null);        // 信箱
        const avatar = ref(null);       // 大頭貼
        const roles = ref([]);          // 角色
        const teacherId = ref(null);    //老師
        // === 登入設定（從後端回傳的資料）===
        function setUser(data) {
            token.value = data.token || null;
            userId.value = data.userId || null;
            account.value = data.account || null;
            username.value = data.username || null;
            email.value = data.email || null;
            avatar.value = data.avatar || null;
            roles.value = data.roles || [];
            teacherId.value = data.teacherId || null;
        }

        // === 登出（清除登入資訊）===
        function clearUser() {
            token.value = null;
            userId.value = null;
            account.value = null;
            username.value = null;
            email.value = null;
            avatar.value = null;
            roles.value = [];
            sessionStorage.removeItem("user");
            localStorage.removeItem("user");
        }

        function $reset() {
            clearUser();
        }

        return {
            token,
            userId,
            account,
            username,
            email,
            avatar,
            roles,
            teacherId,
            setUser,
            clearUser,
            $reset,

        };
    },
    {
        persist: {
            // 關閉瀏覽器時清除登入資料
            storage: sessionStorage,
        },
    }
);

export default useUserStore;
