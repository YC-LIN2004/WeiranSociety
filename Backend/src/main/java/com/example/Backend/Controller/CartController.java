package com.example.Backend.Controller;

import com.example.Backend.DTO.DTOS.CartDetailDTO;
import com.example.Backend.Entity.Cart;
import com.example.Backend.Entity.CartDetail;
import com.example.Backend.Service.CartService;
import lombok.RequiredArgsConstructor;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/cart")
@RequiredArgsConstructor
@CrossOrigin(origins = "*") // 允許前端跨域請求
public class CartController {

    private final CartService cartService;

    // ✅ 查詢使用中購物車課程清單
    @GetMapping("/user/{userId}/details")
    public ResponseEntity<?> getCartDetails(@PathVariable Long userId) {
        try {
            List<CartDetail> details = cartService.getActiveCartDetails(userId);

            if (details == null || details.isEmpty()) {
                return ResponseEntity.ok(List.of());
            }

            String baseUrl = "http://localhost:8080";

            List<CartDetailDTO> dtoList = details.stream()
                    .filter(d -> d.getCourse() != null)
                    .map(detail -> {
                        var dto = new CartDetailDTO();
                        dto.setCartDetailId(detail.getCartDetailId());
                        dto.setCourseId(detail.getCourse().getCourseId());
                        dto.setTitle(detail.getCourse().getCourseTitle());
                        dto.setPrice(detail.getCourse().getPrice());

                        String cover = detail.getCourse().getCoverUrl();
                        if (cover != null && !cover.startsWith("http")) {
                            cover = baseUrl + cover;
                        }
                        dto.setCoverUrl(cover);
                        return dto;
                    })
                    .toList();

            return ResponseEntity.ok(dtoList);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", e.getMessage()));
        }
    }
    // public Object getCartDetails(@PathVariable Integer userId) {
    // List<CartDetail> details = cartService.getActiveCartDetails(userId);
    // if (details == null)
    // return List.of(); // 沒資料 → 空陣列
    // if (details.size() == 1)
    // return List.of(details.get(0)); // 單筆也包成陣列
    // return details;
    // }

    // 建立購物車（ex: /api/cart/create?userId=100）
    @PostMapping("/create")
    public Cart createCart(@RequestParam Long userId) {
        return cartService.createCart(userId);
    }

    // 關閉購物車
    // @PutMapping("/{cartId}/close")
    // public Cart closeCart(@PathVariable Integer cartId) {
    // return cartService.closeCart(cartId);
    // }

    // @DeleteMapping("/detail/{detailId}")
    // public ResponseEntity<String> removeCartItem(@PathVariable Integer detailId)
    // {
    // cartService.removeCartItem(detailId);
    // return ResponseEntity.ok("已刪除該商品");
    // }

    // 刪除單一商品
    @DeleteMapping("/cart-detail/{cartDetailId}")
    public ResponseEntity<String> removeCourseFromCart(@PathVariable Long cartDetailId) {
        cartService.removeCourseFromCart(cartDetailId);
        return ResponseEntity.ok("課程已從購物車移除");
    }

    // 結帳購物車
    @PutMapping("/{cartId}/checkout")
    public Cart checkoutCart(@PathVariable Long cartId) {
        return cartService.checkoutCart(cartId);
    }

    @GetMapping("/test")
    public String testConnection() {
        return "後端 API 正常運作中 🚀";
    }

    // ✅ 加入購物車
    @PostMapping("/add")
    public ResponseEntity<Map<String, Object>> addCourseToCart(
            @RequestParam Long userId,
            @RequestParam Long courseId) {

        try {
            cartService.addCourseToCart(userId, courseId);
            return ResponseEntity.ok(Map.of(
                    "success", true,
                    "message", "課程已加入購物車"));
        } catch (RuntimeException e) {
            return ResponseEntity.ok(Map.of(
                    "success", false,
                    "message", e.getMessage())); // 不用 409，保持 200，讓前端用 success 判斷
        }
    }
}