package com.example.Backend.Controller;

import com.example.Backend.Entity.CartDetail;
import com.example.Backend.Service.CartDetailService;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/cartDetail")
@CrossOrigin(origins = "*")
public class CartDetailController {

    private final CartDetailService cartDetailService;

    public CartDetailController(CartDetailService cartDetailService) {
        this.cartDetailService = cartDetailService;
    }

    // ✅ 查詢單筆明細（測試用）
    @GetMapping("/{cartDetailId}")
    public CartDetail getCartDetail(@PathVariable Long cartDetailId) {
        return cartDetailService.getCartDetailById(cartDetailId);
    }

    // ✅ 刪除單筆明細
    @DeleteMapping("/{cartDetailId}")
    public String deleteCartDetail(@PathVariable Long cartDetailId) {
        cartDetailService.deleteCartDetail(cartDetailId);
        return "購物車明細已刪除 (ID: " + cartDetailId + ")";
    }
}
