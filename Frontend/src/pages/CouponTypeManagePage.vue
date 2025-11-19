<template>
  <div class="container-fluid py-4">
    <div class="row">
      <div class="col-12">
        <div class="card">
          <div class="card-header d-flex justify-content-between align-items-center">
            <h4 class="mb-0">優惠券類型管理</h4>
            <button class="btn btn-primary" @click="showCreateModal">
              <i class="bi bi-plus-circle me-1"></i>新增類型
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
                  <th>名稱</th>
                  <th>描述</th>
                  <th>折扣類型</th>
                  <th>折扣值</th>
                  <th>最低金額</th>
                  <!-- <th>適用範圍</th>
                  <th>有效期</th> -->
                  <th>操作</th>
                </tr>
              </thead>
              <tbody>
                <tr v-for="item in couponTypes" :key="item.id">
                  <td>{{ item.id }}</td>
                  <td>{{ item.name }}</td>
                  <td>{{ item.description }}</td>
                  <td>{{ item.discountType == 1 ? '固定金額' : '百分比' }}</td>
                  <td>{{ item.discountType == 1 ? `NT$${item.discountValue}` : `${(1-item.discountValue)*100}%` }}</td>
                  <td>NT${{ item.minAmount }}</td>
                  <!-- <td>{{ item.applicableScope }}</td>
                  <td>{{ formatDateRange(item.startDate, item.endDate) }}</td> -->
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

            <div v-if="!loading && couponTypes.length === 0" class="text-center py-4 text-muted">
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
            <h5 class="modal-title">{{ isEdit ? '編輯優惠券類型' : '新增優惠券類型' }}</h5>
            <button type="button" class="btn-close" @click="closeModal"></button>
          </div>
          <div class="modal-body">
            <form>
              <div class="mb-3">
                <label class="form-label">名稱 *</label>
                <input v-model="form.name" type="text" class="form-control" required />
              </div>

              <div class="mb-3">
                <label class="form-label">描述</label>
                <textarea v-model="form.description" class="form-control" rows="2"></textarea>
              </div>

              <div class="mb-3">
                <label class="form-label">折扣類型 *</label>
                <select v-model="form.discountType" class="form-select" required>
                  <option value="1">固定金額</option>
                  <option value="2">百分比折扣</option>
                </select>
              </div>

              <div class="mb-3">
                <label class="form-label">折扣值 *</label>
                <input v-model="form.discountValue" type="number" step="0.01" class="form-control" required />
                <small class="text-muted">
                  {{ form.discountType == 1 ? '輸入金額，如：200' : '輸入比例，如：0.1 代表9折' }}
                </small>
              </div>

              <div class="mb-3">
                <label class="form-label">最低消費金額 *</label>
                <input v-model="form.minAmount" type="number" class="form-control" required />
              </div>

              <!-- <div class="mb-3">
                <label class="form-label">適用範圍 *</label>
                <select v-model="form.applicableScope" class="form-select" required>
                  <option value="ALL">全站</option>
                  <option value="CATEGORY">特定分類</option>
                  <option value="COURSE">特定課程</option>
                </select>
              </div> -->

              <!-- <div class="row">
                <div class="col-6 mb-3">
                  <label class="form-label">開始日期</label>
                  <input v-model="form.startDate" type="datetime-local" class="form-control" />
                </div>
                <div class="col-6 mb-3">
                  <label class="form-label">結束日期</label>
                  <input v-model="form.endDate" type="datetime-local" class="form-control" />
                </div>
              </div> -->
            </form>
          </div>
          <div class="modal-footer">
            <button type="button" class="btn btn-secondary" @click="closeModal">取消</button>
            <button type="button" class="btn btn-primary" @click="handleSubmit">
              {{ isEdit ? '更新' : '新增' }}
            </button>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { fetchCouponTypes, createCouponType, updateCouponType, deleteCouponType } from '@/api/couponApi'

const loading = ref(false)
const couponTypes = ref([])
const showModal = ref(false)
const isEdit = ref(false)
const form = ref({
  id: null,
  name: '',
  description: '',
  discountType: '1',
  discountValue: 0,
  minAmount: 0,
  applicableScope: 'ALL',
  startDate: '',
  endDate: ''
})

async function loadData() {
  loading.value = true
  try {
    couponTypes.value = await fetchCouponTypes()
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
    name: '',
    description: '',
    discountType: '1',
    discountValue: 0,
    minAmount: 0,
    applicableScope: 'ALL',
    startDate: '',
    endDate: ''
  }
  showModal.value = true
}

function showEditModal(item) {
  isEdit.value = true
  form.value = {
    id: item.id,
    name: item.name,
    description: item.description,
    discountType: String(item.discountType),
    discountValue: item.discountValue,
    minAmount: item.minAmount,
    applicableScope: item.applicableScope,
    startDate: item.startDate ? item.startDate.slice(0, 16) : '',
    endDate: item.endDate ? item.endDate.slice(0, 16) : ''
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
      discountType: Number(form.value.discountType),
      discountValue: Number(form.value.discountValue),
      minAmount: Number(form.value.minAmount),
      startDate: form.value.startDate ? new Date(form.value.startDate).toISOString() : null,
      endDate: form.value.endDate ? new Date(form.value.endDate).toISOString() : null
    }

    if (isEdit.value) {
      await updateCouponType(form.value.id, data)
      alert('更新成功')
    } else {
      await createCouponType(data)
      alert('新增成功')
    }

    closeModal()
    loadData()
  } catch (e) {
    alert('操作失敗：' + (e?.response?.data?.message || e.message))
  }
}

async function handleDelete(id) {
  if (!confirm('確定要刪除此優惠券類型？')) return

  try {
    await deleteCouponType(id)
    alert('刪除成功')
    loadData()
  } catch (e) {
    alert('刪除失敗：' + (e?.response?.data?.message || e.message))
  }
}

function formatDateRange(start, end) {
  if (!start && !end) return '-'
  const s = start ? new Date(start).toLocaleDateString('zh-TW') : ''
  const e = end ? new Date(end).toLocaleDateString('zh-TW') : ''
  return `${s} ~ ${e}`
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

