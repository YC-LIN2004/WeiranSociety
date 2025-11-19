<template>
  <div class="card">
    <div class="card-body">
      <router-link :to="{ name: 'CourseDetail', params: { id: postData.id } }" class="card-title-link">
        <h5 class="card-title">{{ postData.title }}</h5>
        </router-link>
      <p class="card-text">{{ postData.content }}</p>
      
      <div class="d-flex justify-content-between align-items-center mt-3">
        
        <small class="text-muted">發表人：{{ postData.author }}</small>
        
        <div>
            <button 
                class="btn btn-sm btn-outline-secondary me-2"
                @click="emit('edit-post', postData.id)"
            >
                編輯
            </button>
            
            <button 
                class="btn btn-sm btn-outline-danger"
                @click="emit('delete-post', postData.id)"
            >
                刪除
            </button>
        </div>
      </div>
      
    </div>
  </div>
</template>

<script setup>
import { defineProps, defineEmits } from 'vue'; // 引入 defineEmits

// 1. 【定義 Props】: 接收父元件傳來的貼文數據
const props = defineProps({
  postData: {
    type: Object,
    required: true,
  }
});

// 2. 【定義 Emits】: 聲明這個元件會發出哪些事件
// 父元件會監聽這些事件 (例如 @delete-post)
const emit = defineEmits(['delete-post', 'edit-post']);

// 備註：在 Vue 3 的 <script setup> 中，通常不需要手動引入 defineProps 和 defineEmits
// 但為了教學目的，我這裡將其明確寫出。
</script>

<style scoped>
.card {
  box-shadow: 0 2px 6px rgba(0,0,0,0.1);
}
</style>