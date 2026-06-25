package com.stockpilot.model;

import com.stockpilot.exception.InvalidInputException;
import java.math.BigDecimal;

public class OrderItem {
    private Long id;
    private Long orderId; // Sẽ được gán sau khi Order cha được lưu vào DB
    private Long productId;
    private int quantity;
    private BigDecimal unitPrice;

    public OrderItem(Long id, Long orderId, Long productId, int quantity, BigDecimal unitPrice) {
        this.id = id;
        this.orderId = orderId;
        this.productId = productId;
        setQuantity(quantity);
        setUnitPrice(unitPrice);
    }

    // Getters
    public Long getId() {
        return id;
    }

    public Long getOrderId() {
        return orderId;
    }

    public Long getProductId() {
        return productId;
    }

    public int getQuantity() {
        return quantity;
    }

    public BigDecimal getUnitPrice() {
        return unitPrice;
    }

    // Setters
    public void setOrderId(Long orderId) {
        this.orderId = orderId;
    }

    public void setQuantity(int quantity) {
        if (quantity <= 0) {
            throw new InvalidInputException("Số lượng mua phải lớn hơn 0.");
        }
        this.quantity = quantity;
    }

    public void setUnitPrice(BigDecimal unitPrice) {
        if (unitPrice == null || unitPrice.compareTo(BigDecimal.ZERO) < 0) {
            throw new InvalidInputException("Đơn giá không được âm.");
        }
        this.unitPrice = unitPrice;
    }
}