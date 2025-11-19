<template>
    <div id="admin-layout">
        <!-- 側邊欄 -->
        <Sidebar />

        <!-- 主內容區 -->
        <div class="main-content">
            <!-- 頂部導航欄 -->
            <Navbar />

            <!-- 頁面動態內容 -->
            <div class="page-content">
                <router-view />
            </div>
        </div>
    </div>
</template>

<script setup>
import Sidebar from '@/components/Sidebar.vue'
import Navbar from '@/components/Navbar.vue'

</script>

<style scoped>
#admin-layout {
    display: flex;
    height: 100vh;
}

/* 主內容區 (包含 Navbar + 內容) */
.main-content {
    flex: 1;
    display: flex;
    flex-direction: column;
    background-color: #f8f9fa;
}

.page-content {
    flex: 1;
    padding: 20px;
    overflow-y: auto;
}
</style>
