<template>
  <div class="container py-4">
    <h4 class="mb-3">課程管理</h4>

    <div
      v-if="errorMsg"
      class="alert alert-warning d-flex justify-content-between align-items-center"
    >
      <span>{{ errorMsg }}</span>
      <button class="btn-close" @click="errorMsg = ''"></button>
    </div>

    <!-- 篩選列 -->
    <div class="row g-2 mb-3">
      <div class="col-md-4">
        <input
          v-model="keyword"
          type="text"
          class="form-control"
          placeholder="搜尋課程..."
        />
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
          <option v-for="c in categories" :key="c.id" :value="c.id">
            {{ c.name }}
          </option>
        </select>
      </div>
      <div class="col-md-2 d-flex justify-content-end">
        <button class="btn btn-primary w-100" @click="fetchCourses">搜尋</button>
      </div>
    </div>

    <div class="text-end mb-3">
      <button class="btn btn-success" @click="openModal()">＋ 新增課程</button>
    </div>

    <!-- 課程表 -->
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
          <tr v-for="c in courses" :key="c.id">
            <td style="width:90px">
              <img
                :src="coverSrc(c.coverUrl)"
                class="img-fluid rounded"
                style="max-height:60px"
                @error="onImgError"
              />
            </td>
            <td class="text-start px-2">{{ c.title }}</td>
            <td>{{ findCategoryName(c.categoryId) }}</td>
            <td>{{ c.teacherName || '—' }}</td>
            <td>{{ c.price }} 元</td>
            <td>
              <span
                :class="[ 'badge', isActive(c.status) ? 'bg-success' : 'bg-secondary' ]"
              >
                {{ isActive(c.status) ? '上架' : '下架' }}
              </span>
            </td>
            <td>{{ formatDate(c.createdAt) }}</td>
            <td>
              <button
                class="btn btn-sm btn-outline-primary me-1"
                @click="openModal(c)"
              >
                編輯
              </button>
              <button
                class="btn btn-sm btn-outline-danger me-1"
                @click="deleteCourse(c.id)"
              >
                刪除
              </button>
              <button
                class="btn btn-sm"
                :class="isActive(c.status) ? 'btn-outline-warning' : 'btn-outline-success'"
                @click="toggleStatus(c)"
              >
                {{ isActive(c.status) ? '下架' : '上架' }}
              </button>
            </td>
          </tr>
          <tr v-if="!loading && courses.length === 0">
            <td colspan="8" class="text-muted py-4">無資料</td>
          </tr>
        </tbody>
      </table>
    </div>

    <div v-if="loading" class="text-center my-4">
      <div class="spinner-border" role="status"></div>
    </div>

    <!-- Modal -->
    <div class="modal fade" id="courseModal" tabindex="-1">
      <div class="modal-dialog modal-lg">
        <div class="modal-content">
          <div class="modal-header">
            <h5 class="modal-title">{{ form.id ? "編輯課程" : "新增課程" }}</h5>
            <button type="button" class="btn-close" data-bs-dismiss="modal"></button>
          </div>
          <div class="modal-body">
            <div class="row g-3">
              <div class="col-md-6">
                <label class="form-label">課程名稱</label>
                <input v-model.trim="form.title" type="text" class="form-control" />
              </div>

              <div class="col-md-6">
                <label class="form-label">分類</label>
                <select v-model.number="form.categoryId" class="form-select">
                  <option disabled value="">請選擇</option>
                  <option v-for="c in categories" :key="c.id" :value="c.id">
                    {{ c.name }}
                  </option>
                </select>
              </div>

              <div class="col-md-6">
                <label class="form-label">老師</label>
                <select v-model.number="form.teacherId" class="form-select">
                  <option disabled value="">請選擇</option>
                  <option v-for="t in teacherOptions" :key="t.id" :value="t.id">
                    {{ t.name }}
                  </option>
                </select>
                <div v-if="!teacherOptions.length" class="form-text text-warning">
                  找不到可用老師（請確認 TeacherProfile 資料且狀態為 ACTIVE/Active）
                </div>
              </div>

              <div class="col-md-6">
                <label class="form-label">價格</label>
                <input v-model.number="form.price" type="number" min="0" class="form-control" />
              </div>

              <div class="col-md-6">
                <label class="form-label">狀態</label>
                <select v-model="form.status" class="form-select">
                  <option value="Active">上架</option>
                  <option value="Inactive">下架</option>
                </select>
              </div>

              <div class="col-12">
                <label class="form-label">描述</label>
                <textarea v-model.trim="form.description" class="form-control" rows="3"></textarea>
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
import { ref, onMounted } from "vue";
import { Modal } from "bootstrap";
import http from "@/api/axios";
import { resolveCoverUrl } from "@/api/courseApi";
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

console.log("✅ CourseAdmin.vue 正在執行中");

onMounted(() => {
  modal = new Modal(document.getElementById("courseModal"));
  fetchCategories();
  fetchTeacherOptions();
  fetchCourses();
});

const isActive = (s) => (s ?? '').toLowerCase() === 'active';

const fetchCategories = async () => {
  try {
    const res = await http.get("/categories");
    categories.value = (Array.isArray(res.data) ? res.data : []).map(c => ({
      id: c.categoryId,
      name: c.categoryName
    }));
  } catch {
    categories.value = [];
  }
};


const fetchTeacherOptions = async () => {
  console.log("⚡ 呼叫 teacher options API");
  try {
    const res = await axios.get("http://localhost:8080/api/teachers/options");
    teacherOptions.value = Array.isArray(res.data)
      ? res.data.map(t => ({
          id: t.teacherId,
          name: t.teacherName
        }))
      : [];
  } catch (err) {
    console.warn("teacher options 請求失敗:", err);
    teacherOptions.value = [];
  }
};

const fetchCourses = async () => {
  loading.value = true;
  try {
    const res = await http.get("/courses/admin/search", {
      params: {
        keyword: keyword.value || null,
        status: filterStatus.value || null,
        categoryId: filterCategory.value || null,
        priceOrder: "desc",
      },
    });

    const arr = Array.isArray(res.data) ? res.data : [];
    courses.value = arr.map(x => ({
      id: x.courseId,
      title: x.courseTitle,
      categoryId: x.categoryId,
      teacherId: x.teacherId,
      teacherName: x.teacherName ?? x.teacher?.name ?? null,
      price: x.price,
      status: x.courseStatus ?? x.status,
      description: x.courseDescription,
      coverUrl: x.coverUrl,
      createdAt: x.createdAt
    }));
  } finally {
    loading.value = false;
  }
};

const openModal = (c = null) => {
  form.value = c
    ? { ...c }
    : {
        title: "",
        categoryId: categories.value[0]?.id ?? null,
        teacherId: teacherOptions.value[0]?.id ?? null,
        price: 0,
        status: "Inactive",
        description: "",
        coverUrl: "default-course.png",
      };
  preview.value = c?.coverUrl ? coverSrc(c.coverUrl) : defaultCourse;
  modal.show();
};

const onFileChange = async (e) => {
  const file = e.target.files[0];
  if (!file) return;
  const fd = new FormData();
  fd.append("file", file);
  const res = await http.post("/upload", fd, {
    headers: { "Content-Type": "multipart/form-data" },
  });
  form.value.coverUrl = res.data.url;
  preview.value = coverSrc(res.data.url);
};

const saveCourse = async () => {
  errorMsg.value = "";

  const title = (form.value.title || "").trim();
  const cId = Number(form.value.categoryId);
  const tId = Number(form.value.teacherId);
  const price = Number(form.value.price);
  const status = (form.value.status || 'Inactive');
  const description = (form.value.description || "").trim();
  const coverUrlRaw =
    form.value.coverUrl && String(form.value.coverUrl).trim() !== ""
      ? String(form.value.coverUrl).trim()
      : "default-course.png";

  if (!title || Number.isNaN(cId) || Number.isNaN(tId) || Number.isNaN(price)) {
    errorMsg.value = "請填寫：課程名稱、分類(數字)、老師(必選)、價格(數字)";
    return;
  }

  const payload = {
    title,
    categoryId: cId,
    teacherId: tId,
    price,
    status,
    description: description || null,
    coverUrl: coverUrlRaw,
  };
  if (payload.description == null) delete payload.description;

  try {
    if (form.value.id) {
      await http.patch(`/courses/${form.value.id}`, payload);
    } else {
      await http.post("/courses/create", payload);
    }
    modal.hide();
    await fetchCourses();
  } catch (e) {
    errorMsg.value = e?.response?.data?.message || "儲存課程失敗";
  }
};

const deleteCourse = async (id) => {
  if (!confirm("確定要刪除此課程嗎？")) return;
  await http.delete(`/courses/${id}`);
  await fetchCourses();
};

const toggleStatus = async (c) => {
  const path = isActive(c.status)
    ? `/courses/${c.id}/unpublish`
    : `/courses/${c.id}/publish`;
  await http.post(path);
  await fetchCourses();
};

const coverSrc = (url) => resolveCoverUrl(url) || defaultCourse;

const onImgError = (e) => {
  if (e.target.dataset.fallbackApplied === "1") return;
  e.target.dataset.fallbackApplied = "1";
  e.target.src = defaultCourse;
};

const findCategoryName = (id) => {
  const list = categories.value;
  if (!Array.isArray(list)) return "未分類";
  return list.find((x) => x.id === id)?.name || "未分類";
};

const formatDate = (d) => (d ? d.replace("T", " ").slice(0, 16) : "-");
</script>
