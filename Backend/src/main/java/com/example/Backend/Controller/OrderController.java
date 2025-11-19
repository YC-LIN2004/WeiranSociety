package com.example.Backend.Controller;

import java.util.List;
import java.util.Map;
import java.util.Set;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.Backend.DTO.Request.CreateOrderRequest;
import com.example.Backend.Entity.Order;
import com.example.Backend.Service.OrderService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/orders")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class OrderController {

    private final OrderService orderService;

    /**
     * 建立新訂單
     */
    @PostMapping("/create")
    public ResponseEntity<?> createOrder(@RequestBody CreateOrderRequest req) {
        try {
            Order order = orderService.createOrder(
                    req.getUserId(),
                    req.getCartDetailIds(),
                    req.getFinalAmount(),
                    req.getDiscountAmount(),
                    req.getPaymentMethod());

            return ResponseEntity.ok(Map.of(
                    "success", true,
                    "message", "訂單建立成功",
                    "orderId", order.getOrderID(),
                    "order", order));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of(
                    "success", false,
                    "error", "訂單建立失敗：" + e.getMessage()));
        }
    }

    /**
     * 獲取所有訂單
     */
    @GetMapping
    public ResponseEntity<List<Order>> getAllOrders() {
        try {
            List<Order> orders = orderService.getAllOrders();
            return ResponseEntity.ok(orders);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * 搜尋訂單（根據關鍵字）
     */
    @GetMapping("/search")
    public ResponseEntity<List<Order>> searchOrders(@RequestParam String keyword) {
        try {
            List<Order> orders = orderService.searchOrders(keyword);
            return ResponseEntity.ok(orders);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * 更新訂單狀態
     */
    @PutMapping("/{orderId}/status")
    public ResponseEntity<?> updateStatus(
            @PathVariable Long orderId,
            @RequestBody Map<String, String> request) {
        try {
            String newStatus = request.get("status");

            if (newStatus == null || newStatus.trim().isEmpty()) {
                return ResponseEntity.badRequest().body(Map.of(
                        "success", false,
                        "error", "狀態不能為空"));
            }

            boolean updated = orderService.updateOrderStatus(orderId, newStatus);

            if (updated) {
                return ResponseEntity.ok(Map.of(
                        "success", true,
                        "message", "狀態更新成功"));
            } else {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of(
                        "success", false,
                        "error", "訂單不存在"));
            }
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Map.of(
                    "success", false,
                    "error", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of(
                    "success", false,
                    "error", "更新失敗：" + e.getMessage()));
        }
    }

    /**
     * 刪除訂單
     */
    @DeleteMapping("/{orderId}")
    public ResponseEntity<?> deleteOrder(@PathVariable Long orderId) {
        try {
            boolean deleted = orderService.deleteOrder(orderId);

            if (deleted) {
                return ResponseEntity.ok(Map.of(
                        "success", true,
                        "message", "訂單刪除成功"));
            } else {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of(
                        "success", false,
                        "error", "訂單不存在"));
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of(
                    "success", false,
                    "error", "刪除失敗：" + e.getMessage()));
        }
    }

    // //刪除訂單
    // @DeleteMapping("/{orderId}")
    // public ResponseEntity<?> deleteOrder(@PathVariable Integer orderId) {
    // boolean deleted = orderService.deleteOrder(orderId);
    // return deleted ?
    // ResponseEntity.ok(Map.of("success", true)) :
    // ResponseEntity.notFound().build();
    // }

    /**
     * 獲取有效狀態列表
     */
    @GetMapping("/statuses")
    public ResponseEntity<Set<String>> getValidStatuses() {
        try {
            Set<String> statuses = orderService.getValidStatuses();
            return ResponseEntity.ok(statuses);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * 獲取特定用戶的訂單（支援狀態篩選和分頁）
     */
    // 刪除訂單
    // @DeleteMapping("/{orderId}")
    // public ResponseEntity<?> deleteOrder(@PathVariable Integer orderId) {
    // boolean deleted = orderService.deleteOrder(orderId);
    // return deleted ?
    // ResponseEntity.ok(Map.of("success", true)) :
    // ResponseEntity.notFound().build();
    // }
    @GetMapping("/user/{userId}")
    public ResponseEntity<?> getUserOrdersPaged(
            @PathVariable Long userId,
            @RequestParam(defaultValue = "all") String status) {
        try {
            var result = orderService.getUserOrderSummaries(userId, status);
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of(
                    "error", "獲取用戶訂單失敗：" + e.getMessage()));
        }
    }
}