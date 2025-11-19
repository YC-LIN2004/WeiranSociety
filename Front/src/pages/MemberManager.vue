<template>
    <div class="user-manage">
        <h2>使用者管理</h2>

        <!-- 搜尋和篩選 -->
        <div class="filter-bar">
            <div class="search-bar">
                <input 
                    type="text" 
                    v-model="keyword" 
                    placeholder="搜尋使用者..." 
                    @keyup.enter="handleSearch"
                />
                <button @click="handleSearch">搜尋</button>
                <button @click="handleReset">重置</button>
            </div>
            
            <div class="status-filter">
                <label>狀態篩選：</label>
                <select v-model="filterStatus" @change="handleFilterByStatus">
                    <option value="">全部</option>
                    <option value="ACTIVE">啟用中</option>
                    <option value="PENDING">待驗證</option>
                    <option value="SUSPENDED">已停用</option>
                </select>
            </div>
        </div>

        <!-- 狀態 -->
        <div v-if="loading" class="loading">載入中...</div>
        <div v-if="error" class="error">{{ error }}</div>
        
        <div v-if="!loading">
            <table class="user-table">
                <thead>
                    <tr>
                        <th>ID</th>
                        <th>帳號</th>
                        <th>真實姓名</th>
                        <th>Email</th>
                        <th>電話</th>
                        <th>角色</th>
                        <th>狀態</th>
                        <th>操作</th>
                    </tr>
                </thead>
                <tbody>
                    <tr v-for="user in users" :key="user.userId">
                        <td>{{ user.userID }}</td>
                        <td>{{ user.account }}</td>
                        <td>{{ user.realName || '-' }}</td>
                        <td>{{ user.email }}</td>
                        <td>{{ user.phone || '-' }}</td>
                        <td>
                            <span v-if="user.roles && user.roles.length > 0">
                                <span v-for="role in user.roles" :key="role.roleID" class="role-tag">
                                    {{ role.roleName }}
                                </span>
                            </span>
                            <span v-else>無角色</span>
                        </td>
                        <td>
                            <span :class="['status', getStatusClass(user.userStatus)]">
                                {{ getStatusText(user.userStatus) }}
                            </span>
                        </td>
                        <td class="action-cell">
                            <button 
                                type="button"
                                @click="handleOpenStatusModal(user)" 
                                class="action-btn"
                            >
                                切換狀態
                            </button>
                            <button 
                                type="button"
                                @click="handleDeleteUser(user.userID)" 
                                class="delete-btn"
                            >
                                刪除
                            </button>
                        </td>
                    </tr>
                    <tr v-if="users.length === 0 && !loading">
                        <td colspan="8" style="text-align: center; padding: 40px;">
                            {{ error ? '載入失敗' : '無資料' }}
                        </td>
                    </tr>
                </tbody>
            </table>

            <!-- 分頁 -->
            <div class="pagination" v-if="totalPages > 0">
                <button :disabled="page === 0" @click="handlePrevPage">上一頁</button>
                <span>第 {{ page + 1 }} 頁 / 共 {{ totalPages }} 頁 (共 {{ totalItems }} 筆)</span>
                <button :disabled="page >= totalPages - 1" @click="handleNextPage">下一頁</button>
            </div>
        </div>

        <!-- 狀態更新 Modal -->
        <div v-if="showStatusModal" class="modal" @click="handleCloseModal">
            <div class="modal-content" @click.stop>
                <h3>更新使用者狀態</h3>
                <p><strong>使用者：</strong>{{ selectedUser?.realname || selectedUser?.account }}</p>
                <p><strong>目前狀態：</strong>{{ getStatusText(selectedUser?.userStatus) }}</p>
                <div class="form-group">
                    <label>新狀態：</label>
                    <select v-model="newStatus">
                        <option value="ACTIVE">啟用中</option>
                        <option value="PENDING">待驗證</option>
                        <option value="SUSPENDED">已停用</option>
                    </select>
                </div>
                <div class="modal-actions">
                    <button type="button" @click="handleCloseModal">取消</button>
                    <button type="button" class="primary-btn" @click="handleUpdateStatus">確認更新</button>
                </div>
            </div>
        </div>
    </div>
</template>

<script setup>
import { ref, onMounted } from "vue";
import axios from "axios";

const API_BASE_URL = "http://localhost:8080/api/users/manage";

// 響應式變數
const users = ref([]);
const page = ref(0);
const size = ref(10);
const totalPages = ref(0);
const totalItems = ref(0);
const keyword = ref("");
const filterStatus = ref("");
const loading = ref(false);
const error = ref("");

const showStatusModal = ref(false);
const selectedUser = ref(null);
const newStatus = ref("");

// 獲取使用者列表（分頁）
const fetchUsers = async () => {
    loading.value = true;
    error.value = "";

    try {
        console.log("=== 開始載入使用者列表 ===");
        console.log("請求參數:", { page: page.value, size: size.value });
        
        const res = await axios.get(`${API_BASE_URL}/page`, {
            params: {
                page: page.value,
                size: size.value,
                sortBy: "userId",
                direction: "asc"
            },
        });

        console.log("API 回應:", res.data);

        if (res.data && res.data.content) {
            users.value = res.data.content;
            totalItems.value = res.data.totalElements;
            totalPages.value = res.data.totalPages;
            page.value = res.data.number;
            console.log("✓ 載入成功，共 " + users.value.length + " 筆資料");
        } else {
            error.value = "資料格式錯誤";
            console.error("✗ 資料格式錯誤:", res.data);
        }
    } catch (e) {
        console.error("✗ 請求失敗:", e);
        error.value = "載入使用者失敗：" + (e.response?.data?.message || e.message);
    } finally {
        loading.value = false;
    }
};

// 根據狀態篩選
const filterByStatus = async () => {
    console.log("=== 開始篩選狀態 ===");
    console.log("篩選狀態:", filterStatus.value);
    
    if (!filterStatus.value) {
        console.log("無篩選狀態，重置");
        keyword.value = "";
        page.value = 0;
        await fetchUsers();
        return;
    }

    loading.value = true;
    error.value = "";

    try {
        const res = await axios.get(`${API_BASE_URL}/status/${filterStatus.value}`);
        console.log("篩選結果:", res.data);

        users.value = res.data;
        totalItems.value = res.data.length;
        totalPages.value = 1;
        page.value = 0;
        console.log("✓ 篩選成功，共 " + users.value.length + " 筆資料");
    } catch (e) {
        console.error("✗ 篩選錯誤:", e);
        error.value = "篩選失敗：" + (e.response?.data?.message || e.message);
    } finally {
        loading.value = false;
    }
};

// 搜尋使用者
const searchUsers = async () => {
    console.log("=== 開始搜尋 ===");
    console.log("搜尋關鍵字:", keyword.value);
    
    if (!keyword.value || !keyword.value.trim()) {
        console.log("關鍵字為空，重置");
        filterStatus.value = "";
        page.value = 0;
        await fetchUsers();
        return;
    }

    loading.value = true;
    error.value = "";

    try {
        const res = await axios.get(`${API_BASE_URL}/search`, {
            params: { keyword: keyword.value.trim() },
        });

        console.log("搜尋結果:", res.data);

        users.value = res.data;
        totalItems.value = res.data.length;
        totalPages.value = 1;
        page.value = 0;
        console.log("✓ 搜尋成功，共 " + users.value.length + " 筆資料");
    } catch (e) {
        console.error("✗ 搜尋錯誤:", e);
        error.value = "搜尋失敗：" + (e.response?.data?.message || e.message);
    } finally {
        loading.value = false;
    }
};

// 打開狀態更新 Modal
const openStatusModal = (user) => {
    console.log("=== 打開狀態 Modal ===");
    console.log("選擇的使用者:", user);
    
    selectedUser.value = user;
    newStatus.value = user.userStatus;
    showStatusModal.value = true;
    
    console.log("Modal 狀態:", showStatusModal.value);
    console.log("選擇的狀態:", newStatus.value);
};

// 關閉 Modal
const closeModal = () => {
    console.log("=== 關閉 Modal ===");
    showStatusModal.value = false;
    selectedUser.value = null;
    newStatus.value = "";
};

// 更新狀態
const updateStatus = async () => {
    console.log("=== 開始更新狀態 ===");
    
    if (!selectedUser.value) {
        console.error("✗ 沒有選擇使用者");
        alert("沒有選擇使用者");
        return;
    }

    console.log("使用者ID:", selectedUser.value.userID);
    console.log("目前狀態:", selectedUser.value.userStatus);
    console.log("新狀態:", newStatus.value);

    if (newStatus.value === selectedUser.value.userStatus) {
        alert("新狀態與目前狀態相同");
        return;
    }

    try {
        const url = `${API_BASE_URL}/${selectedUser.value.userID}/status?status=${newStatus.value}`;
        console.log("請求 URL:", url);
        
        const res = await axios.post(url);
        console.log("更新結果:", res.data);

        alert("狀態更新成功");
        closeModal();
        
        // 重新載入資料
        if (filterStatus.value) {
            await filterByStatus();
        } else if (keyword.value) {
            await searchUsers();
        } else {
            await fetchUsers();
        }
    } catch (e) {
        console.error("✗ 更新錯誤:", e);
        console.error("錯誤回應:", e.response?.data);
        alert("更新狀態失敗：" + (e.response?.data?.message || e.message));
    }
};

// 刪除使用者
const deleteUser = async (userID) => {
    console.log("=== 準備刪除使用者 ===");
    console.log("使用者ID:", userID);
    
    if (!confirm("確定要刪除此使用者嗎？")) {
        console.log("取消刪除");
        return;
    }

    try {
        console.log("刪除使用者:", userID);
        
        const res = await axios.delete(`${API_BASE_URL}/${userID}`);
        console.log("刪除結果:", res.data);

        alert("刪除成功");
        
        // 重新載入資料
        if (filterStatus.value) {
            await filterByStatus();
        } else if (keyword.value) {
            await searchUsers();
        } else {
            await fetchUsers();
        }
    } catch (e) {
        console.error("✗ 刪除錯誤:", e);
        console.error("錯誤回應:", e.response?.data);
        alert("刪除失敗：" + (e.response?.data?.message || e.message));
    }
};

// === 事件處理函數（包裝層） ===
const handleSearch = () => {
    console.log(">>> 搜尋按鈕被點擊");
    searchUsers();
};

const handleReset = () => {
    console.log(">>> 重置按鈕被點擊");
    keyword.value = "";
    filterStatus.value = "";
    page.value = 0;
    fetchUsers();
};

const handleFilterByStatus = () => {
    console.log(">>> 狀態篩選被改變");
    filterByStatus();
};

const handleOpenStatusModal = (user) => {
    console.log(">>> 切換狀態按鈕被點擊");
    console.log("使用者資料:", user);
    openStatusModal(user);
};

const handleCloseModal = () => {
    console.log(">>> 關閉 Modal");
    closeModal();
};

const handleUpdateStatus = () => {
    console.log(">>> 確認更新按鈕被點擊");
    updateStatus();
};

const handleDeleteUser = (userID) => {
    console.log(">>> 刪除按鈕被點擊");
    console.log("使用者ID:", userID);
    deleteUser(userID);
};

const handlePrevPage = () => {
    console.log(">>> 上一頁按鈕被點擊");
    if (page.value > 0) {
        page.value--;
        fetchUsers();
    }
};

const handleNextPage = () => {
    console.log(">>> 下一頁按鈕被點擊");
    if (page.value < totalPages.value - 1) {
        page.value++;
        fetchUsers();
    }
};

// 狀態文字轉換
const getStatusText = (status) => {
    const statusMap = {
        'ACTIVE': '啟用中',
        'PENDING': '待驗證',
        'SUSPENDED': '已停用'
    };
    return statusMap[status] || status || '未知';
};

// 狀態樣式
const getStatusClass = (status) => {
    const classMap = {
        'ACTIVE': 'active',
        'PENDING': 'pending',
        'SUSPENDED': 'inactive'
    };
    return classMap[status] || '';
};

// 組件掛載時獲取資料
onMounted(() => {
    console.log("=== 組件已掛載 ===");
    fetchUsers();
});
</script>

<style scoped>
.user-manage {
    max-width: 1400px;
    margin: 0 auto;
    padding: 20px;
    font-family: "Microsoft JhengHei", sans-serif;
}

h2 {
    text-align: center;
    margin-bottom: 20px;
    color: #333;
}

.filter-bar {
    display: flex;
    justify-content: space-between;
    align-items: center;
    margin-bottom: 20px;
    gap: 20px;
}

.search-bar {
    flex: 1;
    display: flex;
    gap: 8px;
}

.search-bar input {
    flex: 1;
    padding: 10px 15px;
    font-size: 14px;
    border: 1px solid #ddd;
    border-radius: 4px;
}

.status-filter {
    display: flex;
    align-items: center;
    gap: 10px;
}

.status-filter label {
    font-weight: 500;
    color: #555;
    white-space: nowrap;
}

.status-filter select {
    padding: 10px;
    border: 1px solid #ddd;
    border-radius: 4px;
    font-size: 14px;
    min-width: 120px;
}

button {
    padding: 10px 20px;
    border: 1px solid #ccc;
    background-color: #f6f6f6;
    cursor: pointer;
    border-radius: 4px;
    font-size: 14px;
    transition: all 0.3s;
}

button:hover:not(:disabled) {
    background-color: #e0e0e0;
}

button:active:not(:disabled) {
    transform: scale(0.98);
}

button:disabled {
    opacity: 0.5;
    cursor: not-allowed;
}

.action-btn {
    background-color: #2196F3;
    color: white;
    border: none;
    margin-right: 5px;
    padding: 8px 16px;
}

.action-btn:hover {
    background-color: #1976D2;
}

.primary-btn {
    background-color: #4caf50;
    color: white;
    border: none;
}

.primary-btn:hover {
    background-color: #45a049;
}

.delete-btn {
    background-color: #f44336;
    color: white;
    border: none;
    padding: 8px 16px;
}

.delete-btn:hover {
    background-color: #da190b;
}

.user-table {
    width: 100%;
    border-collapse: collapse;
    background: white;
    box-shadow: 0 2px 4px rgba(0,0,0,0.1);
}

.user-table th,
.user-table td {
    border: 1px solid #ddd;
    padding: 12px;
    text-align: center;
}

.user-table th {
    background-color: #667eea;
    color: white;
    font-weight: 600;
}

.user-table tbody tr:hover {
    background-color: #f5f5f5;
}

.action-cell {
    white-space: nowrap;
}

.role-tag {
    display: inline-block;
    background-color: #e7f3ff;
    color: #0066cc;
    padding: 4px 10px;
    border-radius: 4px;
    font-size: 12px;
    margin: 2px;
}

.status {
    display: inline-block;
    padding: 5px 15px;
    border-radius: 15px;
    color: white;
    font-size: 12px;
    font-weight: 500;
}

.status.active {
    background-color: #4caf50;
}

.status.pending {
    background-color: #ff9800;
}

.status.inactive {
    background-color: #f44336;
}

.loading {
    text-align: center;
    padding: 60px;
    font-size: 16px;
    color: #666;
}

.error {
    background-color: #f8d7da;
    color: #721c24;
    padding: 15px;
    border-radius: 4px;
    margin-bottom: 20px;
    border: 1px solid #f5c6cb;
}

.pagination {
    display: flex;
    justify-content: center;
    align-items: center;
    gap: 15px;
    margin-top: 30px;
}

.pagination span {
    font-size: 14px;
    color: #666;
}

.modal {
    position: fixed;
    top: 0;
    left: 0;
    width: 100%;
    height: 100%;
    background-color: rgba(0, 0, 0, 0.6);
    display: flex;
    justify-content: center;
    align-items: center;
    z-index: 1000;
}

.modal-content {
    background: white;
    padding: 30px;
    border-radius: 10px;
    min-width: 400px;
    box-shadow: 0 4px 6px rgba(0, 0, 0, 0.1);
}

.modal-content h3 {
    margin-top: 0;
    color: #333;
}

.modal-content p {
    margin: 10px 0;
    color: #666;
}

.form-group {
    margin: 20px 0;
}

.form-group label {
    display: block;
    margin-bottom: 8px;
    font-weight: 600;
    color: #333;
}

.form-group select {
    width: 100%;
    padding: 10px;
    border: 1px solid #ddd;
    border-radius: 4px;
    font-size: 14px;
}

.modal-actions {
    display: flex;
    justify-content: flex-end;
    gap: 10px;
    margin-top: 25px;
}
</style>