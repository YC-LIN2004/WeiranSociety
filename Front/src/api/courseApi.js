import axios from '@/api/axios'

// 🧱 取得課程列表（支援搜尋、分類、排序）
export async function fetchCourses({ keyword, categoryId, priceOrder }) {
  const params = {}
  if (keyword) params.keyword = keyword
  if (categoryId) params.categoryId = categoryId
  if (priceOrder) params.priceOrder = priceOrder

  const { data } = await axios.get('/courses', { params })
  return data
}

// 🧱 取得分類列表
export async function fetchCategories() {
  const { data } = await axios.get('/categories')
  return data
}

// 🧱 處理封面 / 頭像 URL
export function resolveCoverUrl(url) {
  if (!url) return ''
  if (url.startsWith('http')) return url
  return `${import.meta.env.VITE_API_BASE_URL || 'http://localhost:8080/api'}${url}`
}

// 🧱 取得單一課程詳細資料
export async function fetchCourseById(id) {
  const base = import.meta.env.VITE_API_BASE_URL || 'http://localhost:8080/api'
  const { data } = await axios.get(`${base}/courses/detail/${id}`)

  return {
    id: data.courseId,
    title: data.courseTitle,
    price: data.price,
    description: data.courseDescription,
    coverUrl: data.coverUrl,
    categoryName: data.categoryName,
    teacher: {
      teacherId: data.teacher?.teacherId ?? null,  // ✅ 加這行！
      name: data.teacher?.name ?? '未知老師',
      avatarUrl: data.teacher?.avatarUrl ?? null,
      rating: data.teacher?.rating ?? null,
      bio: data.teacher?.bio ?? '',
      expertise: data.teacher?.expertise ?? ''
    },
    sections: data.sections ?? []
  }
}
