<template>
  <div class="container-fluid py-4">
    <div class="row">
      <div class="col-12">
        <div class="card">
          <div class="card-header d-flex justify-content-between align-items-center">
            <h4 class="mb-0">優惠券管理</h4>
            <button class="btn btn-primary" @click="showCreateModal">
              <i class="bi bi-plus-circle me-1"></i>發行優惠券
            </button>
          </div>

          <div class="card-body">
            <!-- 載入中 -->
            <div v-if="loading" class="text-center py-5">
              <div class="spinner-border" role="status"></div>
            </div>

            <!-- 表格 -->
            <table v-else class="table table-hover">
              <thead>
                <tr>
                  <th>ID</th>
                  <th>代碼</th>
                  <th>優惠券類型</th>
                  <th>總發行量</th>
                  <th>已領取</th>
                  <th>狀態</th>
                  <th>過期日期</th>
                  <th>操作</th>
                </tr>
              </thead>
              <tbody>
                <tr v-for="item in coupons" :key="item.id">
                  <td>{{ item.id }}</td>
                  <td><code>{{ item.code }}</code></td>
                  <td>{{ item.couponType?.name || '-' }}</td>
                  <td>{{ item.totalIssued }}</td>
                  <td>{{ item.useCount }}</td>
                  <td>
                    <span class="badge" :class="getStatusClass(item.status)">
                      {{ item.status }}
                    </span>
                  </td>
                  <td>{{ formatDate(item.expiryDate) }}</td>
                  <td>
                    <button class="btn btn-sm btn-warning me-1" @click="showEditModal(item)">
                      <i class="bi bi-pencil"></i>
                    </button>
                    <button class="btn btn-sm btn-danger" @click="handleDelete(item.id)">
                      <i class="bi bi-trash"></i>
                    </button>
                  </td>
                </tr>
              </tbody>
            </table>

            <div v-if="!loading && coupons.length === 0" class="text-center py-4 text-muted">
              尚無資料
            </div>
          </div>
        </div>
      </div>
    </div>

    <!-- 新增/編輯彈窗 -->
    <div v-if="showModal" class="modal d-block" tabindex="-1" style="background: rgba(0,0,0,0.5);">
      <div class="modal-dialog">
        <div class="modal-content">
          <div class="modal-header">
            <h5 class="modal-title">{{ isEdit ? '編輯優惠券' : '發行優惠券' }}</h5>
            <button type="button" class="btn-close" @click="closeModal"></button>
          </div>
          <div class="modal-body">
            <form>
              <div class="mb-3">
                <label class="form-label">優惠券類型 *</label>
                <select v-model="form.couponTypeId" class="form-select" required>
                  <option value="">請選擇</option>
                  <option v-for="type in couponTypes" :key="type.id" :value="type.id">
                    {{ type.name }}
                  </option>
                </select>
              </div>

              <div class="mb-3">
                <label class="form-label">優惠券代碼 *</label>
                <input v-model="form.code" type="text" class="form-control" required />
                <small class="text-muted">唯一識別碼，如：SUMMER2025</small>
              </div>

              <div class="mb-3">
                <label class="form-label">發行數量 *</label>
                <input v-model="form.totalIssued" type="number" class="form-control" required />
              </div>

              <div class="mb-3">
                <label class="form-label">狀態 *</label>
                <select v-model="form.status" class="form-select" required>
                  <option value="Active">啟用</option>
                  <option value="Inactive">停用</option>
                  <!-- <option value="Expired">已過期</option> -->
                </select>
              </div>

              <div class="mb-3">
                <label class="form-label">過期日期 *</label>
                <input v-model="form.expiryDate" type="datetime-local" class="form-control" required />
              </div>
            </form>
          </div>
          <div class="modal-footer">
            <button type="button" class="btn btn-secondary" @click="closeModal">取消</button>
            <button type="button" class="btn btn-primary" @click="handleSubmit">
              {{ isEdit ? '更新' : '發行' }}
            </button>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { 
  fetchAllCoupons, 
  fetchCouponTypes,
  createCoupon, 
  updateCoupon, 
  deleteCoupon 
} from '@/api/couponApi'

const loading = ref(false)
const coupons = ref([])
const couponTypes = ref([])
const showModal = ref(false)
const isEdit = ref(false)
const form = ref({
  id: null,
  couponTypeId: '',
  code: '',
  totalIssued: 100,
  status: 'Active',
  expiryDate: ''
})

async function loadData() {
  loading.value = true
  try {
    const [couponData, typeData] = await Promise.all([
      fetchAllCoupons(),
      fetchCouponTypes()
    ])
    coupons.value = couponData
    couponTypes.value = typeData
  } catch (e) {
    alert('載入失敗：' + (e?.response?.data?.message || e.message))
  } finally {
    loading.value = false
  }
}

function showCreateModal() {
  isEdit.value = false
  form.value = {
    id: null,
    couponTypeId: '',
    code: '',
    totalIssued: 100,
    status: 'Active',
    expiryDate: ''
  }
  showModal.value = true
}

function showEditModal(item) {
  isEdit.value = true
  form.value = {
    id: item.id,
    couponTypeId: item.couponTypeId,
    code: item.code,
    totalIssued: item.totalIssued,
    status: item.status,
    expiryDate: item.expiryDate ? item.expiryDate.slice(0, 16) : ''
  }
  showModal.value = true
}

function closeModal() {
  showModal.value = false
}

async function handleSubmit() {
  try {
    const data = {
      ...form.value,
      couponTypeId: Number(form.value.couponTypeId),
      totalIssued: Number(form.value.totalIssued),
      expiryDate: form.value.expiryDate ? new Date(form.value.expiryDate).toISOString() : null
    }

    if (isEdit.value) {
      await updateCoupon(form.value.id, data)
      alert('更新成功')
    } else {
      await createCoupon(data)
      alert('發行成功')
    }

    closeModal()
    loadData()
  } catch (e) {
    alert('操作失敗：' + (e?.response?.data?.message || e.message))
  }
}

async function handleDelete(id) {
  if (!confirm('確定要刪除此優惠券？')) return

  try {
    await deleteCoupon(id)
    alert('刪除成功')
    loadData()
  } catch (e) {
    alert('刪除失敗：' + (e?.response?.data?.message || e.message))
  }
}

function getStatusClass(status) {
  const map = {
    'Active': 'bg-success',
    'Inactive': 'bg-secondary',
    'Expired': 'bg-danger'
  }
  return map[status] || 'bg-secondary'
}

function formatDate(dateStr) {
  if (!dateStr) return '-'
  return new Date(dateStr).toLocaleString('zh-TW')
}

onMounted(() => {
  loadData()
})
</script>

<style scoped>
.modal {
  display: block;
}
</style>

