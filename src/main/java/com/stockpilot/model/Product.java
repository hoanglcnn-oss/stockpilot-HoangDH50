package com.stockpilot.model;

import com.stockpilot.exception.InvalidInputException;
import java.math.BigDecimal;

public class Product {
    private Long id; // Có thể null khi mới tạo chưa lưu DB
    private String sku;
    private String name;
    private String category;
    private BigDecimal price; // Dùng BigDecimal cho tiền tệ (Good/Excellent tier)
    private int stockQuantity;

    // Constructor
    public Product(Long id, String sku, String name, String category, BigDecimal price, int stockQuantity) {
        this.id = id;
        setSku(sku); // Gọi hàm setter để dùng chung logic validate
        this.name = name;
        this.category = category;
        setPrice(price);
        setStockQuantity(stockQuantity);
    }

    // Getters
    public Long getId() {
        return id;
    }

    public String getSku() {
        return sku;
    }

    public String getName() {
        return name;
    }

    public String getCategory() {
        return category;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public int getStockQuantity() {
        return stockQuantity;
    }

    // Setters kèm Validation (Encapsulation)
    public void setSku(String sku) {
        // Regex: 3 chữ cái IN HOA, theo sau là dấu gạch ngang, kết thúc bằng 4 chữ số
        if (sku == null || !sku.matches("^[A-Z]{3}-\\d{4}$")) {
            throw new InvalidInputException(
                    "Mã SKU không hợp lệ! Định dạng chuẩn: 3 chữ HOA - 4 chữ số (VD: ABC-1234)");
        }
        this.sku = sku;
    }

    public void setPrice(BigDecimal price) {
        if (price == null || price.compareTo(BigDecimal.ZERO) < 0) {
            throw new InvalidInputException("Giá sản phẩm không được nhỏ hơn 0.");
        }
        this.price = price;
    }

    public void setStockQuantity(int stockQuantity) {
        if (stockQuantity < 0) {
            throw new InvalidInputException("Số lượng tồn kho không được âm.");
        }
        this.stockQuantity = stockQuantity;
    }

    // Bắt buộc override toString để in ra console đẹp hơn
    @Override
    public String toString() {
        return String.format("Product[ID=%d, SKU=%s, Name=%s, Price=%s, Stock=%d]",
                id, sku, name, price.toString(), stockQuantity);
    }
}