package com.example.Backend.Repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.example.Backend.Entity.OrderDetail;

@Repository
public interface OrderDetailRepository extends JpaRepository<OrderDetail, Long> {
    @Query("SELECT d FROM OrderDetail d WHERE d.order.orderID = :orderID")
    List<OrderDetail> findDetailsByOrderID(@Param("orderID") Long orderID);

    List<OrderDetail> findByOrder_OrderID(Long orderId);

    @Query(value = "SELECT * FROM dbo.OrderDetail WHERE OrderID = :orderID", nativeQuery = true)
    List<OrderDetail> findDetailsByOrderIDNative(@Param("orderID") Long orderID);
}
