package com.stockpilot.model;

import com.stockpilot.exception.InvalidInputException;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class Order {
    private Long id;
    private Long customerId;
    private LocalDateTime orderDate;
    private BigDecimal totalAmount;
    private List<OrderItem> items; // Quan hệ 1 - Nhiều

    public Order(Long id, Long customerId, LocalDateTime orderDate, BigDecimal totalAmount) {
        this.id = id;
        this.customerId = customerId;
        this.orderDate = (orderDate != null) ? orderDate : LocalDateTime.now();
        setTotalAmount(totalAmount);
        this.items = new ArrayList<>();
    }

    // Getters
    public Long getId() {
        return id;
    }

    public Long getCustomerId() {
        return customerId;
    }

    public LocalDateTime getOrderDate() {
        return orderDate;
    }

    public BigDecimal getTotalAmount() {
        return totalAmount;
    }

    public List<OrderItem> getItems() {
        return items;
    }

    // Setters
    public void setTotalAmount(BigDecimal totalAmount) {
        if (totalAmount == null || totalAmount.compareTo(BigDecimal.ZERO) < 0) {
            throw new InvalidInputException("Tổng tiền đơn hàng không hợp lệ.");
        }
        this.totalAmount = totalAmount;
    }

    // Hàm tiện ích để thêm sản phẩm vào đơn
    public void addItem(OrderItem item) {
        if (item != null) {
            this.items.add(item);
        }
    }
}