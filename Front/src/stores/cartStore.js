import { defineStore } from 'pinia'
import { ref, computed } from 'vue'
import axios from 'axios'
import useUserStore from '@/stores/user.js'

axios.defaults.withCredentials = true

export const useCartStore = defineStore('cart', () => {
  const baseUrl = 'http://localhost:8080/api/cart'
  const userStore = useUserStore()
  const userId = ref(userStore.userId || 0)

  const items = ref([])
  const selectedCoupons = ref([])
  const discountAmount = ref(0)
  const finalAmount = ref(0)

  function syncUserId() {
    userId.value = userStore.userId || 0
  }

  async function fetchCarts() {
    try {
      syncUserId()
      const res = await axios.get(`${baseUrl}/user/${userId.value}/details`)
      items.value = res.data.map(item => ({
        cartDetailId: item.cartDetailId,
        id: item.courseId,
        title: item.title,
        image: item.coverUrl || '/images/spring-boot.jpg',
        price: item.price,
        selected: false
      }))
    } catch (err) {
      console.error('載入購物車失敗', err)
    }
  }

  async function addCourseToCart(course) {
    try {
      syncUserId()
      const id = typeof course === 'object' ? (course.courseId || course.id) : course
      if (!id) throw new Error('無效的課程資料')

      await axios.post(`${baseUrl}/add?userId=${userId.value}&courseId=${id}`)
      await fetchCarts()
    } catch (err) {
      console.error('加入購物車失敗', err)
      throw err
    }
  }

  async function removeItem(cartDetailId) {
    try {
      syncUserId()
      await axios.delete(`${baseUrl}/cart-detail/${cartDetailId}`)
      items.value = items.value.filter(i => i.cartDetailId !== cartDetailId)
    } catch (err) {
      console.error('刪除課程失敗', err)
    }
  }

  const selectedItems = computed(() => items.value.filter(i => i.selected))
  const selectedCount = computed(() => selectedItems.value.length)
  const allSelected = computed(() => items.value.length > 0 && items.value.every(i => i.selected))
  const totalPrice = computed(() =>
    items.value.filter(i => i.selected).reduce((sum, i) => sum + (i.price || 0), 0)
  )

  function toggleSelectAll(checked) {
    items.value.forEach(i => (i.selected = checked))
  }

  function clearCart() {
    items.value = []
  }

  return {
    userId,
    items,
    selectedCoupons,
    discountAmount,
    finalAmount,
    selectedItems,
    selectedCount,
    allSelected,
    totalPrice,
    fetchCarts,
    addCourseToCart,
    removeItem,
    toggleSelectAll,
    clearCart
  }
}, { persist: true })
