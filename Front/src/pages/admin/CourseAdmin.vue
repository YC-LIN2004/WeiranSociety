<template>
  <div class="container py-4">
    <h4 class="mb-3">課程管理</h4>

    <!-- 錯誤提示 -->
    <div v-if="errorMsg" class="alert alert-warning d-flex justify-content-between align-items-center">
      <span>{{ errorMsg }}</span>
      <button class="btn-close" @click="errorMsg = ''"></button>
    </div>

    <!-- 篩選列 -->
    <div class="row g-2 mb-3">
      <div class="col-md-4">
        <input v-model="keyword" type="text" class="form-control" placeholder="搜尋課程或老師名稱..." />
      </div>

      <div class="col-md-3">
        <select v-model="filterStatus" class="form-select">
          <option value="">全部狀態</option>
          <option value="Active">上架</option>
          <option value="Inactive">下架</option>
        </select>
      </div>

      <div class="col-md-3">
        <select v-model="filterCategory" class="form-select">
          <option value="">全部分類</option>
          <option v-for="c in categories" :key="c.categoryId" :value="c.categoryId">{{ c.categoryName }}</option>
        </select>
      </div>

      <div class="col-md-2 d-flex justify-content-end">
        <button class="btn btn-primary w-100" @click="fetchCourses">搜尋</button>
      </div>
    </div>

    <!-- 新增按鈕 -->
    <div class="text-end mb-3">
      <button class="btn btn-success" @click="openModal()">＋ 新增課程</button>
    </div>

    <!-- 課程表格 -->
    <div class="table-responsive">
      <table class="table table-bordered align-middle text-center">
        <thead class="table-light">
          <tr>
            <th>封面</th>
            <th>課程名稱</th>
            <th>分類</th>
            <th>老師</th>
            <th>價格</th>
            <th>狀態</th>
            <th>建立時間</th>
            <th>操作</th>
          </tr>
        </thead>

        <tbody>
          <tr v-for="c in courses" :key="c.courseId">
            <td style="width: 90px">
              <img :src="coverSrc(c.coverUrl)" class="img-fluid rounded" style="max-height: 60px" @error="onImgError" />
            </td>
            <td class="text-start px-2">{{ c.courseTitle }}</td>
            <td>{{ c.categoryName || '未分類' }}</td>
            <td>{{ c.teacherName || '—' }}</td>
            <td>{{ c.price }} 元</td>
            <td>
              <span :class="['badge', isActive(c.courseStatus) ? 'bg-success' : 'bg-secondary']">
                {{ isActive(c.courseStatus) ? '上架' : '下架' }}
              </span>
            </td>
            <td>{{ formatDate(c.createdAt) }}</td>
            <td>
              <button class="btn btn-sm btn-outline-primary me-1" @click="openModal(c)">編輯</button>
              <button class="btn btn-sm btn-outline-danger me-1" @click="deleteCourse(c.courseId)">刪除</button>
              <button
                class="btn btn-sm"
                :class="isActive(c.courseStatus) ? 'btn-outline-warning' : 'btn-outline-success'"
                @click="toggleStatus(c)"
              >
                {{ isActive(c.courseStatus) ? '下架' : '上架' }}
              </button>
            </td>
          </tr>
          <tr v-if="!loading && courses.length === 0">
            <td colspan="8" class="text-muted py-4">無資料</td>
          </tr>
        </tbody>
      </table>
    </div>

    <!-- 載入中 -->
    <div v-if="loading" class="text-center my-4">
      <div class="spinner-border" role="status"></div>
    </div>

    <!-- Modal：新增 / 編輯 -->
    <div class="modal fade" id="courseModal" tabindex="-1">
      <div class="modal-dialog modal-lg">
        <div class="modal-content">
          <div class="modal-header">
            <h5 class="modal-title">{{ form.courseId ? "編輯課程" : "新增課程" }}</h5>
            <button type="button" class="btn-close" data-bs-dismiss="modal"></button>
          </div>

          <div class="modal-body">
            <div class="row g-3">
              <div class="col-md-6">
                <label class="form-label">課程名稱</label>
                <input v-model.trim="form.courseTitle" type="text" class="form-control" />
              </div>

              <div class="col-md-6">
                <label class="form-label">分類</label>
                <select v-model.number="form.categoryId" class="form-select">
                  <option disabled value="">請選擇</option>
                  <option v-for="c in categories" :key="c.categoryId" :value="c.categoryId">
                    {{ c.categoryName }}
                  </option>
                </select>
              </div>

              <div class="col-md-6">
                <label class="form-label">老師</label>
                <select v-model.number="form.teacherId" class="form-select">
                  <option disabled value="">請選擇</option>
                  <option v-for="t in teacherOptions" :key="t.teacherId" :value="t.teacherId">
                    {{ t.teacherName }}
                  </option>
                </select>
              </div>

              <div class="col-md-6">
                <label class="form-label">價格</label>
                <input v-model.number="form.price" type="number" min="0" class="form-control" />
              </div>

              <div class="col-md-6">
                <label class="form-label">狀態</label>
                <select v-model="form.courseStatus" class="form-select">
                  <option value="Active">上架</option>
                  <option value="Inactive">下架</option>
                </select>
              </div>

              <div class="col-12">
                <label class="form-label">描述</label>
                <textarea v-model.trim="form.courseDescription" class="form-control" rows="3"></textarea>
              </div>

              <div class="col-12">
                <label class="form-label">封面（可空）</label>
                <input type="file" class="form-control mb-2" @change="onFileChange" />
                <img :src="preview" class="img-thumbnail" style="max-height:150px" @error="onImgError" />
              </div>
            </div>
          </div>

          <div class="modal-footer">
            <button class="btn btn-secondary" data-bs-dismiss="modal">取消</button>
            <button class="btn btn-primary" @click="saveCourse">儲存</button>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted, watch } from "vue";
import { Modal } from "bootstrap";
import http from "@/api/axios";
import defaultCourse from "@/assets/default-course.png";

const courses = ref([]);
const categories = ref([]);
const teacherOptions = ref([]);
const loading = ref(false);
const errorMsg = ref("");
const keyword = ref("");
const filterStatus = ref("");
const filterCategory = ref("");
const form = ref({});
const preview = ref(defaultCourse);
let modal = null;

onMounted(() => {
  modal = new Modal(document.getElementById("courseModal"));
  fetchCategories();
  fetchTeacherOptions();
  fetchCourses();
});

watch([filterStatus, filterCategory], fetchCourses);

const isActive = (s) => (s ?? "").toLowerCase() === "active";
const formatDate = (d) => (d ? d.replace("T", " ").slice(0, 16) : "-");
const onImgError = (e) => (e.target.src = defaultCourse);

const coverSrc = (url) => {
  if (!url) return defaultCourse;
  if (url.startsWith("http")) return url;
  return `${import.meta.env.VITE_API_BASE_URL}${url}`;
};

// 取得分類
async function fetchCategories() {
  try {
    const res = await http.get("/categories/all");
    categories.value = Array.isArray(res.data) ? res.data : [];
  } catch {
    categories.value = [];
  }
}

// 取得老師選項
async function fetchTeacherOptions() {
  try {
    const res = await http.get("/teachers/options");
    teacherOptions.value = Array.isArray(res.data) ? res.data : [];
  } catch {
    teacherOptions.value = [];
  }
}

// 搜尋課程
async function fetchCourses() {
  loading.value = true;
  try {
    const res = await http.get("/courses/admin/search", {
      params: {
        keyword: keyword.value || null,
        status: filterStatus.value || null,
        categoryId: filterCategory.value || null,
      },
    });
    courses.value = Array.isArray(res.data) ? res.data : [];
  } catch (e) {
    console.error(e);
  } finally {
    loading.value = false;
  }
}

// 上下架
async function toggleStatus(c) {
  const path = isActive(c.courseStatus)
    ? `/courses/admin/${c.courseId}/unpublish`
    : `/courses/admin/${c.courseId}/publish`;
  await http.put(path);
  fetchCourses();
}

// 新增 / 編輯 Modal
function openModal(c = null) {
  form.value = c
    ? { ...c }
    : {
        courseTitle: "",
        categoryId: "",
        teacherId: "",
        price: 0,
        courseStatus: "Inactive",
        courseDescription: "",
        coverUrl: "",
      };
  preview.value = c?.coverUrl ? coverSrc(c.coverUrl) : defaultCourse;
  modal.show();
}

// 上傳封面
async function onFileChange(e) {
  const file = e.target.files?.[0];
  if (!file) return;
  const fd = new FormData();
  fd.append("file", file);
  const res = await http.post("/upload", fd, {
    headers: { "Content-Type": "multipart/form-data" },
  });
  form.value.coverUrl = res.data.url;
  preview.value = coverSrc(res.data.url);
}

// 儲存課程
async function saveCourse() {
  try {
    if (!form.value.courseTitle || !form.value.teacherId || !form.value.categoryId) {
      errorMsg.value = "請填寫完整資料";
      return;
    }

    const payload = {
      courseTitle: form.value.courseTitle,
      categoryId: form.value.categoryId,
      teacherId: form.value.teacherId,
      price: form.value.price,
      courseStatus: form.value.courseStatus,
      courseDescription: form.value.courseDescription,
      coverUrl: form.value.coverUrl,
    };

    if (form.value.courseId) {
      await http.put(`/courses/admin/${form.value.courseId}`, payload);
    } else {
      await http.post("/courses/create", payload);
    }

    modal.hide();
    fetchCourses();
  } catch (e) {
    console.error(e);
    errorMsg.value = "儲存課程失敗";
  }
}

// 刪除課程
async function deleteCourse(id) {
  if (!confirm("確定要刪除此課程嗎？")) return;
  await http.delete(`/courses/admin/${id}`);
  fetchCourses();
}
</script>

<style scoped>
.table td,
.table th {
  vertical-align: middle;
}
</style>
