package com.example.Backend.Service;

import com.example.Backend.Entity.Cart;
import com.example.Backend.Entity.CartDetail;
import com.example.Backend.Entity.Course;
import com.example.Backend.Repository.CartRepository;
import com.example.Backend.Repository.CartDetailRepository;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class CartService {

    private final CartRepository cartRepository;
    private final CartDetailRepository cartDetailRepository;

    public CartService(CartRepository cartRepository, CartDetailRepository cartDetailRepository) {
        this.cartRepository = cartRepository;
        this.cartDetailRepository = cartDetailRepository;
    }

    // ✅ 查詢使用者目前使用中的購物車課程列表
    public List<CartDetail> getActiveCartDetails(Long userId) {
        return cartDetailRepository.findActiveDetailsWithCourse(userId, (byte) 1);
    }

    // ✅ 建立購物車（防止重複建立）
    public Cart createCart(Long userId) {
        // 先檢查是否已存在使用中的購物車
        var existingCart = cartRepository.findByUserIdAndCartStatus(userId, (byte) 1);

        if (existingCart.isPresent()) {
            // 如果已有使用中購物車，直接回傳那張，不重複建立
            return existingCart.get();
        }

        // 否則建立新的購物車
        Cart cart = new Cart();
        cart.setUserId(userId);
        cart.setCartStatus((byte) 1); // 1 = 使用中
        return cartRepository.save(cart);
    }

    // ✅ 結帳購物車（狀態改 2）
    public Cart checkoutCart(Long cartId) {
        Cart cart = cartRepository.findById(cartId)
                .orElseThrow(() -> new RuntimeException("找不到購物車 ID: " + cartId));
        cart.setCartStatus((byte) 2);
        return cartRepository.save(cart);
    }

    // ✅ 刪除單一商品（CartDetail）
    public void removeCourseFromCart(Long cartDetailId) {
        if (!cartDetailRepository.existsById(cartDetailId)) {
            throw new RuntimeException("找不到購物車明細 ID: " + cartDetailId);
        }
        cartDetailRepository.deleteById(cartDetailId);
    }

    // 🧾 可選：查歷史紀錄（管理用）
    public List<Cart> getUserCarts(Long userId) {
        return cartRepository.findByUserId(userId);
    }

    // ✅ 加入購物車（使用現有的 createCart 方法）
    public void addCourseToCart(Long userId, Long courseId) {
        // 1️⃣ 確保有使用中的購物車（沒有就建立）
        Cart cart = createCart(userId); // ✅ 直接使用你現成的邏輯

        // 2️⃣ 檢查該課程是否已存在於購物車
        boolean exists = cartDetailRepository
                .findByCart_CartIdAndCourse_CourseId(cart.getCartId(), courseId)
                .isPresent();

        if (exists) {
            throw new RuntimeException("此課程已在購物車中");
        }

        // 3️⃣ 新增購物車明細
        CartDetail detail = new CartDetail();
        detail.setCart(cart);

        // ✅ 改這裡（建立一個 course 物件來指定課程 ID）
        Course course = new Course();
        course.setCourseId(courseId);
        detail.setCourse(course);

        cartDetailRepository.save(detail);
    }

}
