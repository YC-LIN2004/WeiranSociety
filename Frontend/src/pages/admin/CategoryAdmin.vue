<template>
  <div class="container py-4">
    <h4 class="mb-3">分類管理</h4>

    <!-- 顯示錯誤訊息 -->
    <div v-if="error" class="alert alert-warning d-flex justify-content-between align-items-center">
      <span>{{ error }}</span>
      <button class="btn-close" @click="error = ''"></button>
    </div>

    <!-- 新增 -->
    <div class="text-end mb-3">
      <button class="btn btn-success" @click="openModal()">＋ 新增分類</button>
    </div>

    <!-- 列表 -->
    <div class="table-responsive">
      <table class="table table-bordered align-middle text-center">
        <thead class="table-light">
          <tr>
            <th style="width: 100px">ID</th>
            <th>分類名稱</th>
            <th style="width: 180px">建立時間</th>
            <th style="width: 180px">更新時間</th>
            <th style="width: 180px">操作</th>
          </tr>
        </thead>
        <tbody>
          <tr v-for="c in categories" :key="c.categoryId">
            <td>{{ c.categoryId }}</td>
            <td class="text-start px-3">{{ c.categoryName }}</td>
            <td>{{ formatDate(c.createdAt) }}</td>
            <td>{{ formatDate(c.updatedAt) }}</td>
            <td>
              <button class="btn btn-sm btn-outline-primary me-1" @click="openModal(c)">編輯</button>
              <button class="btn btn-sm btn-outline-danger" @click="deleteCategory(c.categoryId)">刪除</button>
            </td>
          </tr>
          <tr v-if="!loading && categories.length === 0">
            <td colspan="5" class="text-muted py-4">無分類資料</td>
          </tr>
        </tbody>
      </table>
    </div>

    <div v-if="loading" class="text-center my-4">
      <div class="spinner-border" role="status"></div>
    </div>

    <!-- Modal -->
    <div class="modal fade" id="categoryModal" tabindex="-1">
      <div class="modal-dialog">
        <div class="modal-content">
          <div class="modal-header">
            <h5 class="modal-title">{{ form.id ? '編輯分類' : '新增分類' }}</h5>
            <button type="button" class="btn-close" data-bs-dismiss="modal"></button>
          </div>
          <div class="modal-body">
            <label class="form-label">分類名稱</label>
            <input
              v-model.trim="form.name"
              type="text"
              class="form-control"
              placeholder="請輸入分類名稱"
            />
            <div v-if="nameInvalid" class="text-danger small mt-2">分類名稱不可為空</div>
          </div>
          <div class="modal-footer">
            <button class="btn btn-secondary" data-bs-dismiss="modal">取消</button>
            <button class="btn btn-primary" @click="saveCategory">儲存</button>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted, computed } from 'vue'
import { Modal } from 'bootstrap'
import http from '@/api/axios'

const categories = ref([])
const loading = ref(false)
const error = ref('')
const form = ref({ id: null, name: '' })
let modal = null

const nameInvalid = computed(() => !form.value.name || form.value.name.length === 0)

onMounted(() => {
  modal = new Modal(document.getElementById('categoryModal'))
  fetchCategories()
})

const fetchCategories = async () => {
  loading.value = true
  error.value = ''
  try {
    const res = await http.get('/categories')
    const data = res?.data
    categories.value = Array.isArray(data) ? data : (Array.isArray(data?.data) ? data.data : [])
  } catch (e) {
    error.value = readErr(e, '讀取分類失敗')
    categories.value = []
  } finally {
    loading.value = false
  }
}

const openModal = (c = null) => {
  form.value = c
    ? { id: c.categoryId, name: c.categoryName }
    : { id: null, name: '' }
  modal.show()
}

// ✅ 修正這裡：新增分類與更新分類
const saveCategory = async () => {
  if (nameInvalid.value) return
  try {
    const payload = { categoryName: form.value.name }

    if (form.value.id) {
      await http.put(`/categories/${form.value.id}`, payload)
    } else {
      await http.post('/categories/create', payload)
    }

    modal.hide()
    await fetchCategories()
  } catch (e) {
    error.value = readErr(e, '儲存分類失敗')
  }
}

const deleteCategory = async (id) => {
  if (!confirm('確定要刪除此分類嗎？')) return
  try {
    await http.delete(`/categories/${id}`)
    await fetchCategories()
  } catch (e) {
    error.value = readErr(e, '刪除分類失敗')
  }
}

const formatDate = (d) => (d ? String(d).replace('T', ' ').slice(0, 16) : '-')

const readErr = (e, fallback) => {
  return e?.response?.data?.message
    || e?.response?.data?.error
    || (typeof e?.response?.data === 'string' ? e.response.data : '')
    || fallback
}
</script>

<style scoped>
.container {
  max-width: 1200px;
}
h4 {
  color: #2c3e50;
  font-weight: 600;
  letter-spacing: -0.5px;
}
.btn {
  border-radius: 8px;
  font-weight: 500;
  padding: 0.5rem 1.25rem;
  transition: all 0.3s ease;
  box-shadow: 0 2px 4px rgba(0, 0, 0, 0.08);
}
.btn:hover {
  transform: translateY(-2px);
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.15);
}
.btn:active {
  transform: translateY(0);
}
.btn-success {
  background: linear-gradient(135deg, #28a745 0%, #20c997 100%);
  border: none;
}
.btn-success:hover {
  background: linear-gradient(135deg, #218838 0%, #1aa179 100%);
}
.btn-primary {
  background: linear-gradient(135deg, #007bff 0%, #0056b3 100%);
  border: none;
}
.btn-primary:hover {
  background: linear-gradient(135deg, #0056b3 0%, #004085 100%);
}
.btn-outline-primary {
  border: 2px solid #007bff;
  color: #007bff;
  background: transparent;
  transition: all 0.3s ease;
}
.btn-outline-primary:hover {
  background: #007bff;
  color: white;
  transform: translateY(-1px);
}
.btn-outline-danger {
  border: 2px solid #dc3545;
  color: #dc3545;
  background: transparent;
  transition: all 0.3s ease;
}
.btn-outline-danger:hover {
  background: #dc3545;
  color: white;
  transform: translateY(-1px);
}
.btn-sm {
  padding: 0.375rem 0.875rem;
  font-size: 0.875rem;
  border-radius: 6px;
}
.table-responsive {
  border-radius: 12px;
  overflow: hidden;
  box-shadow: 0 4px 6px rgba(0, 0, 0, 0.07);
}
.table {
  margin-bottom: 0;
  background: white;
}
.table thead th {
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  color: white;
  font-weight: 600;
  text-transform: uppercase;
  font-size: 0.85rem;
  letter-spacing: 0.5px;
  border: none;
  padding: 1rem;
  vertical-align: middle;
}
.table tbody tr {
  transition: all 0.3s ease;
  border-bottom: 1px solid #f0f0f0;
}
.table tbody tr:hover {
  background-color: #f8f9ff;
  transform: scale(1.01);
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.08);
}
.table tbody tr:last-child {
  border-bottom: none;
}
.table tbody td {
  padding: 1rem;
  vertical-align: middle;
  color: #495057;
  font-size: 0.95rem;
}
.table tbody td:first-child {
  font-weight: 600;
  color: #667eea;
}
.alert {
  border-radius: 10px;
  border: none;
  box-shadow: 0 3px 10px rgba(0, 0, 0, 0.1);
  animation: slideDown 0.3s ease;
}
@keyframes slideDown {
  from {
    opacity: 0;
    transform: translateY(-10px);
  }
  to {
    opacity: 1;
    transform: translateY(0);
  }
}
.alert-warning {
  background: linear-gradient(135deg, #fff3cd 0%, #ffe5a1 100%);
  color: #856404;
}
.modal-content {
  border-radius: 16px;
  border: none;
  box-shadow: 0 20px 60px rgba(0, 0, 0, 0.3);
  overflow: hidden;
}
.modal-header {
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  color: white;
  border: none;
  padding: 1.5rem;
}
.modal-title {
  font-weight: 600;
  font-size: 1.25rem;
}
.modal-body {
  padding: 2rem;
  background: #fafafa;
}
.modal-footer {
  background: #fafafa;
  border: none;
  padding: 1.5rem 2rem;
}
.form-label {
  font-weight: 600;
  color: #495057;
  margin-bottom: 0.5rem;
}
.form-control {
  border: 2px solid #e9ecef;
  border-radius: 8px;
  padding: 0.75rem 1rem;
  transition: all 0.3s ease;
  font-size: 1rem;
}
.form-control:focus {
  border-color: #667eea;
  box-shadow: 0 0 0 0.2rem rgba(102, 126, 234, 0.15);
  background-color: white;
}
.form-control::placeholder {
  color: #adb5bd;
  font-style: italic;
}
.spinner-border {
  width: 3rem;
  height: 3rem;
  border-width: 0.3rem;
  color: #667eea;
}
.text-muted {
  color: #6c757d !important;
}
tbody tr:has(.text-muted) {
  background: linear-gradient(135deg, #f8f9fa 0%, #e9ecef 100%);
}
tbody tr:has(.text-muted):hover {
  background: linear-gradient(135deg, #e9ecef 0%, #dee2e6 100%);
  transform: none;
}
@media (max-width: 768px) {
  .btn {
    padding: 0.4rem 1rem;
    font-size: 0.9rem;
  }
  .table {
    font-size: 0.85rem;
  }
  .table thead th,
  .table tbody td {
    padding: 0.75rem;
  }
  h4 {
    font-size: 1.5rem;
  }
}
.btn-close {
  transition: all 0.3s ease;
}
.btn-close:hover {
  transform: rotate(90deg) scale(1.1);
}
* {
  transition-timing-function: cubic-bezier(0.4, 0, 0.2, 1);
}
</style>
