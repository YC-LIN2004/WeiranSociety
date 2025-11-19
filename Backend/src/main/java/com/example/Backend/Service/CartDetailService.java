package com.example.Backend.Service;

import com.example.Backend.Entity.CartDetail;
import com.example.Backend.Repository.CartDetailRepository;
import org.springframework.stereotype.Service;

@Service
public class CartDetailService {

    private final CartDetailRepository cartDetailRepository;

    public CartDetailService(CartDetailRepository cartDetailRepository) {
        this.cartDetailRepository = cartDetailRepository;
    }

    // 🔍 查詢單筆明細
    public CartDetail getCartDetailById(Long cartDetailId) {
        return cartDetailRepository.findById(cartDetailId)
                .orElseThrow(() -> new RuntimeException("找不到購物車明細 ID: " + cartDetailId));
    }

    // ❌ 刪除單筆明細
    public void deleteCartDetail(Long cartDetailId) {
        if (!cartDetailRepository.existsById(cartDetailId)) {
            throw new RuntimeException("找不到要刪除的購物車明細 ID: " + cartDetailId);
        }
        cartDetailRepository.deleteById(cartDetailId);
    }
}
