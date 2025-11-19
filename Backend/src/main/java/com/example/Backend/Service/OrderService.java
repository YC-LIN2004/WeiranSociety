package com.example.Backend.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.Backend.DTO.DTOS.OrderItemDTO;
import com.example.Backend.DTO.DTOS.OrderlogDTO;
import com.example.Backend.Entity.Cart;
import com.example.Backend.Entity.CartDetail;
import com.example.Backend.Entity.Order;
import com.example.Backend.Entity.OrderDetail;
import com.example.Backend.Entity.UserOrder;
import com.example.Backend.Repository.CartDetailRepository;
import com.example.Backend.Repository.CartRepository;
import com.example.Backend.Repository.OrderDetailRepository;
import com.example.Backend.Repository.OrderRepository;
import com.example.Backend.Repository.UserOrderRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class OrderService {
    private final OrderDetailRepository orderDetailRepository;
    private final OrderRepository orderRepository;
    private final UserOrderRepository userOrderRepository;
    private final CartRepository cartRepository;
    private final CartDetailRepository cartDetailRepository;
    private final CartService cartService;

    // ✅ 定義有效的訂單狀態常數
    public static final String STATUS_UNPAID = "未付款";
    public static final String STATUS_PENDING = "付款中";
    public static final String STATUS_PAID = "完成";
    public static final String STATUS_CANCELLED = "已取消";
    public static final String STATUS_REFUNDED = "已退款";

    private static final Set<String> VALID_STATUSES = Set.of(
            STATUS_UNPAID,
            STATUS_PENDING,
            STATUS_PAID,
            STATUS_CANCELLED,
            STATUS_REFUNDED);

    /*
     * ===========================================================
     * 🔹 建立訂單
     * ===========================================================
     */
    @Transactional
    public Order createOrder(Long userId, List<Long> cartDetailIds, BigDecimal finalAmount,
            BigDecimal discountAmount, String paymentMethod) {
        log.info("🛒 建立訂單中：userId={}, cartDetailIds={}, finalAmount={}",
                userId, cartDetailIds, finalAmount);

        Cart cart = cartRepository.findActiveCartByUserId(userId)
                .orElseThrow(() -> new IllegalArgumentException("找不到使用者的購物車"));

        List<CartDetail> details = cartDetailRepository.findAllById(cartDetailIds);
        if (details.isEmpty()) {
            throw new IllegalArgumentException("購物車明細為空，無法建立訂單");
        }

        BigDecimal total = details.stream()
                .map(d -> {
                    BigDecimal p = d.getCourse().getPrice();
                    return p != null ? p : BigDecimal.ZERO;
                })
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        // ✅ 折扣金額處理
        BigDecimal disc = (discountAmount != null) ? discountAmount : BigDecimal.ZERO;
        BigDecimal finalPay = (finalAmount != null) ? finalAmount : total.subtract(disc);
        // 3️⃣ 建立訂單實體
        Order order = new Order();
        order.setUserID(userId);
        order.setTotalAmount(total);
        order.setDiscountAmount(disc);
        order.setNetAmount(finalPay); // ✅ 計算實付金額
        order.setOrderStatus(STATUS_UNPAID);
        order.setPaymentMethod(paymentMethod != null ? paymentMethod : "未選擇");
        order.setCreatedAt(LocalDateTime.now());
        order.setUpdatedAt(LocalDateTime.now());
        // 4️⃣ 儲存訂單
        Order saved = orderRepository.save(order);
        log.info("✅ 訂單已建立：orderId={}", saved.getOrderID());

        // 5️⃣ 建立訂單明細
        List<OrderDetail> orderDetails = new ArrayList<>();
        for (CartDetail d : details) {
            OrderDetail od = new OrderDetail();
            od.setOrder(saved); // 關聯 Order
            od.setCourse(d.getCourse());
            od.setQuantity(1);
            od.setUnitPrice(d.getCourse().getPrice());
            od.setCreatedAt(LocalDateTime.now());
            orderDetails.add(od);
        }
        orderDetailRepository.saveAll(orderDetails);

        // 5️⃣ 更新購物車狀態為「已結帳」
        cart.setCartStatus((byte) 2); // 1=使用中, 2=已結帳
        cartRepository.save(cart);
        log.info("🛒 購物車 {} 狀態更新為已結帳", cart.getCartId());

        cartService.createCart(userId);

        return saved;
    }

    /*
     * ===========================================================
     * 🔹 查詢 / 更新 / 統計
     * ===========================================================
     */

    public List<Order> getAllOrders() {
        return orderRepository.findAll();
    }

    public List<Order> searchOrders(String keyword) {
        if (keyword == null || keyword.trim().isEmpty()) {
            return getAllOrders();
        }

        keyword = keyword.trim();

        try {
            Long orderId = Long.parseLong(keyword);

            // 1️⃣ 嘗試根據 OrderID 搜尋
            Optional<Order> order = orderRepository.findById(orderId);
            if (order.isPresent()) {
                log.info("✅ Found order by OrderID: {}", orderId);
                return List.of(order.get());
            }

            // 2️⃣ 根據 UserID 搜尋
            List<Order> userOrders = orderRepository.findByUserID(orderId);
            if (!userOrders.isEmpty()) {
                log.info("✅ Found {} orders for UserID: {}", userOrders.size(), orderId);
                return userOrders;
            }

            log.warn("⚠️ No orders found for ID: {}", orderId);
            return new ArrayList<>();

        } catch (NumberFormatException e) {
            log.warn("❌ Invalid search keyword (not a number): {}", keyword);
            return new ArrayList<>();
        }
    }

    public Optional<Order> getOrderById(Long orderID) {
        return orderRepository.findById(orderID);
    }

    @Transactional
    public boolean deleteOrder(Long orderID) {
        if (orderRepository.existsById(orderID)) {
            orderRepository.deleteById(orderID);
            log.info("✅ Deleted order: {}", orderID);
            return true;
        }
        log.warn("⚠️ Order not found for deletion: {}", orderID);
        return false;
    }

    @Transactional
    public boolean updateOrderStatus(Long orderID, String status) {
        log.info("🔄 Updating order status: orderId={}, status={}", orderID, status);

        if (!isValidStatus(status)) {
            log.error("❌ Invalid status: {}", status);
            throw new IllegalArgumentException("無效的訂單狀態: " + status + "。有效狀態: " + VALID_STATUSES);
        }

        return orderRepository.findById(orderID)
                .map(order -> {
                    order.setOrderStatus(status);
                    orderRepository.save(order);
                    log.info("✅ Updated order {} status to {}", orderID, status);
                    return true;
                })
                .orElseThrow(() -> new IllegalArgumentException("訂單不存在: " + orderID));
    }

    // 前台「訂單紀錄」：依 userId + 狀態（all / 完成 / 未付款 / 已取消 …）回傳 DTO 列表
    public List<OrderlogDTO> getUserOrderSummaries(Long userId, String status) {
        List<UserOrder> userOrders;
        List<OrderlogDTO> result = new ArrayList<>();

        // 查詢條件
        if (status == null || status.equalsIgnoreCase("all")) {
            userOrders = userOrderRepository.findByUserIdOrderByCreatedAtDesc(userId);
        } else {
            userOrders = userOrderRepository.findByUserIdAndOrderStatus(userId, status);
        }

        // 轉 DTO
        result = userOrders.stream()
                .map(this::userOrderToDTO)
                .collect(Collectors.toList());

        // 排序（依建立時間由新到舊）
        result.sort((a, b) -> {
            if (a.createdAt() == null && b.createdAt() == null)
                return 0;
            if (a.createdAt() == null)
                return 1;
            if (b.createdAt() == null)
                return -1;
            return b.createdAt().compareTo(a.createdAt());
        });

        log.info("✅ 查詢用戶 {} 訂單：UserOrder={} 筆, 總共={} 筆",
                userId, userOrders.size(), result.size());

        return result;
    }

    // 將 Order 轉成 DTO
    private OrderlogDTO toDTO(Order order) {
        List<OrderItemDTO> items = new ArrayList<>();

        if (order.getOrderID() != null) {
            List<OrderDetail> details = orderDetailRepository.findByOrder_OrderID(order.getOrderID());
            items = details.stream()
                    .map(d -> new OrderItemDTO(
                            d.getCourse().getCourseId(),
                            d.getCourse().getCourseTitle(),
                            d.getUnitPrice(),
                            d.getQuantity(),
                            d.getCourse().getCoverUrl()))
                    .collect(Collectors.toList());
        }

        return new OrderlogDTO(
                order.getOrderID(),
                order.getUserID(),
                order.getTotalAmount(),
                order.getDiscountAmount(),
                order.getNetAmount(),
                order.getOrderStatus(),
                order.getPaymentMethod(),
                order.getCreatedAt(),
                items);
    }

    // ✅ 將 UserOrder 轉成 DTO
    private OrderlogDTO userOrderToDTO(UserOrder order) {
        List<OrderItemDTO> items = new ArrayList<>();

        items.add(new OrderItemDTO(
                null,
                "綠界支付訂單",
                order.getNetAmount(),
                1,
                null));

        return new OrderlogDTO(
                order.getOrderId(),
                order.getUserId(),
                order.getTotalAmount(),
                order.getDiscountAmount() != null ? order.getDiscountAmount() : BigDecimal.ZERO,
                order.getNetAmount(),
                order.getOrderStatus(),
                order.getPaymentMethod(),
                order.getCreatedAt(),
                items);
    }

    private boolean isValidStatus(String status) {
        return status != null && VALID_STATUSES.contains(status);
    }

    public Set<String> getValidStatuses() {
        return VALID_STATUSES;
    }

    public long countUnpaidOrders() {
        return orderRepository.countByOrderStatus(STATUS_UNPAID) +
                userOrderRepository.countByOrderStatus(STATUS_UNPAID);
    }

    public long countPendingOrders() {
        return orderRepository.countByOrderStatus(STATUS_PENDING) +
                userOrderRepository.countByOrderStatus(STATUS_PENDING);
    }

    public long countPaidOrders() {
        return orderRepository.countByOrderStatus(STATUS_PAID) +
                userOrderRepository.countByOrderStatus(STATUS_PAID);
    }

    public long countCancelledOrders() {
        return orderRepository.countByOrderStatus(STATUS_CANCELLED) +
                userOrderRepository.countByOrderStatus(STATUS_CANCELLED);
    }

    public List<Order> getUnpaidOrders() {
        return orderRepository.findByOrderStatus(STATUS_UNPAID);
    }

    public List<Order> getPendingOrders() {
        return orderRepository.findByOrderStatus(STATUS_PENDING);
    }

    public List<Order> getPaidOrders() {
        return orderRepository.findByOrderStatus(STATUS_PAID);
    }

    public List<Order> getCancelledOrders() {
        return orderRepository.findByOrderStatus(STATUS_CANCELLED);
    }

    public List<Order> getOrdersByStatus(String status) {
        if (!isValidStatus(status)) {
            throw new IllegalArgumentException("無效的訂單狀態: " + status);
        }
        return orderRepository.findByOrderStatus(status);
    }

    public List<Order> getUserOrders(Long userID) {
        return orderRepository.findByUserIDOrderByCreatedAtDesc(userID);
    }

    public List<Order> getUserOrdersByStatus(Long userID, String status) {
        if (!isValidStatus(status)) {
            throw new IllegalArgumentException("無效的訂單狀態: " + status);
        }
        return orderRepository.findByUserIDAndOrderStatus(userID, status);
    }

    // public Order createOrder(Long userId, List<Long> cartDetailIds, BigDecimal
    // finalAmount,
    // BigDecimal discountAmount) {
    // // TODO Auto-generated method stub
    // throw new UnsupportedOperationException("Unimplemented method
    // 'createOrder'");
    // }
}
