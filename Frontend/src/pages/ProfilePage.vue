<template>
  <div class="container mt-5 profile-page">
    <h2 class="text-center mb-4 fw-bold">個人資料設定</h2>

    <!-- 頭貼 -->
    <div class="avatar-wrapper mx-auto mb-4" @click="triggerFile">
      <img :src="user.avatar || defaultAvatar" class="avatar-img shadow" />
      <div class="avatar-overlay"><i class="bi bi-camera"></i></div>
      <input type="file" ref="fileInput" accept="image/*" @change="onFileSelect" hidden />
    </div>

    <!-- ✅ 彈出式裁切視窗 -->
    <div v-if="showCropper" class="modal-mask">
      <div class="modal-container">
        <h5 class="fw-semibold mb-3">調整頭貼</h5>

        <div class="cropper-wrapper">
          <img ref="cropperImage" :src="rawImageUrl" class="cropper-image" />
        </div>

        <div class="mt-3 d-flex justify-content-center gap-3">
          <button class="btn btn-outline-secondary px-4" @click="cancelCrop">取消</button>
          <button class="btn btn-success px-4" @click="confirmCrop">確認</button>
        </div>
      </div>
    </div>

    <!-- 表單 -->
    <div class="profile-form mt-4">
      <div class="form-group">
        <label>帳號</label>
        <input v-model="user.account" class="form-control" disabled />
      </div>
      <div class="form-group">
        <label>Email</label>
        <input v-model="form.email" type="email" class="form-control" />
      </div>
      <div class="form-group">
        <label>暱稱</label>
        <input v-model="form.username" class="form-control" />
      </div>
      <div class="form-group">
        <label>電話</label>
        <input v-model="form.phone" class="form-control" />
      </div>

      <button class="btn btn-primary w-100 mt-3" @click="updateProfile">更新資料</button>
    </div>
  </div>
</template>

<script setup>
import { ref, nextTick, onMounted } from "vue";
import axios from "@/api/axios.js";
import useUserStore from "@/stores/user.js";
import Cropper from "cropperjs";
import "cropperjs/dist/cropper.min.css";

const userStore = useUserStore();
const API_BASE = "/user";
const baseUrl = import.meta.env.VITE_API_BASE_URL || "http://localhost:8080";
const defaultAvatar = new URL("@/assets/logo.png", import.meta.url).href;

const user = ref({ account: "", email: "", avatar: "" });
const form = ref({ username: "", phone: "", email: "" });
const rawImageUrl = ref(null);
const cropper = ref(null);
const showCropper = ref(false);
const fileInput = ref(null);

async function loadProfile() {
  const res = await axios.get(`${API_BASE}/profile/${userStore.account}`);
  const u = res.data;
  user.value = {
    account: u.account,
    email: u.email,
    avatar: u.avatar ? `${baseUrl}${u.avatar}` : defaultAvatar
  };
  form.value.username = u.username;
  form.value.phone = u.phone;
  form.value.email = u.email;
}
onMounted(loadProfile);

function triggerFile() {
  fileInput.value.click();
}

async function onFileSelect(e) {
  const file = e.target.files[0];
  if (!file) return;

  rawImageUrl.value = URL.createObjectURL(file);
  showCropper.value = true;

  await nextTick();
  const image = document.querySelector(".cropper-image");

  cropper.value = new Cropper(image, {
    aspectRatio: 1,
    viewMode: 1,
    dragMode: "move",
    background: false,
    movable: true,
    zoomable: true,
    zoomOnWheel: true,
    autoCropArea: 1,
    ready() {
      // 強制圓形遮罩
      const box = document.querySelector(".cropper-view-box");
      const face = document.querySelector(".cropper-face");
      box.style.borderRadius = "50%";
      face.style.borderRadius = "50%";
    }
  });
}

function cancelCrop() {
  cropper.value?.destroy();
  cropper.value = null;
  showCropper.value = false;
}

async function confirmCrop() {
  const canvas = cropper.value.getCroppedCanvas({ width: 400, height: 400 });
  const blob = await new Promise((r) => canvas.toBlob(r, "image/png"));
  const file = new File([blob], "avatar.png", { type: "image/png" });
  cropper.value.destroy();
  showCropper.value = false;
  await uploadAvatar(file);
}

async function uploadAvatar(file) {
  const formData = new FormData();
  formData.append("file", file);
  formData.append("userId", userStore.userId);

  const res = await axios.post(`${API_BASE}/upload-avatar`, formData, {
    headers: { "Content-Type": "multipart/form-data" }
  });
  const avatarUrl = res.data.avatarUrl.startsWith("http")
    ? res.data.avatarUrl
    : `${baseUrl}${res.data.avatarUrl}`;
  user.value.avatar = avatarUrl;
  userStore.avatar = avatarUrl;
  alert("✅ 頭貼更新成功！");
}

async function updateProfile() {
  const res = await axios.put(`${API_BASE}/update/${userStore.account}`, form.value);
  alert(res.data.message || "✅ 資料更新成功");
  await loadProfile();
}
</script>

<style scoped>
.profile-page {
  max-width: 480px;
  background: #fff;
  border-radius: 12px;
  box-shadow: 0 0 10px rgba(0, 0, 0, 0.1);
  padding: 30px;
}

/* 頭貼 */
.avatar-wrapper {
  position: relative;
  width: 150px;
  height: 150px;
  cursor: pointer;
}
.avatar-img {
  width: 150px;
  height: 150px;
  border-radius: 50%;
  object-fit: cover;
}
.avatar-overlay {
  position: absolute;
  inset: 0;
  background: rgba(0, 0, 0, 0.4);
  color: white;
  display: flex;
  align-items: center;
  justify-content: center;
  opacity: 0;
  transition: 0.3s;
}
.avatar-wrapper:hover .avatar-overlay {
  opacity: 1;
}

/* ✅ 彈出式 Modal */
.modal-mask {
  position: fixed;
  inset: 0;
  background: rgba(255, 255, 255, 0.98);
  display: flex;
  align-items: center;
  justify-content: center;
  z-index: 9999;
}

/* 小視窗 */
.modal-container {
  background: #fff;
  border-radius: 20px;
  padding: 30px;
  box-shadow: 0 0 25px rgba(0, 0, 0, 0.3);
  text-align: center;
}

/* ✅ 裁切區 */
.cropper-wrapper {
  width: 320px;
  height: 320px;
  overflow: hidden;
  border-radius: 50%;
  border: 2px solid #888;
  background: #fff;
  margin: 0 auto;
}
.cropper-image {
  width: 100%;
  height: 100%;
  object-fit: cover;
}

/* ✅ 去除灰層、顯示正常 */
:deep(.cropper-bg),
:deep(.cropper-drag-box),
:deep(.cropper-face) {
  background: none !important;
  opacity: 1 !important;
}
:deep(.cropper-view-box) {
  border-radius: 50% !important;
  border: 2px solid #222 !important;
}
</style>
