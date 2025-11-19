package com.example.Backend.Repository;

import com.example.Backend.Entity.Cart;
import com.example.Backend.Entity.CartDetail;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface CartRepository extends JpaRepository<Cart, Long> {
    List<Cart> findByUserId(Long userId);

    Optional<Cart> findByUserIdAndCartStatus(Long userId, byte cartStatus);

    @Query("SELECT c FROM Cart c WHERE c.userId = :userId AND c.cartStatus = 1")
    Optional<Cart> findActiveCartByUserId(@Param("userId") Long userId);
}