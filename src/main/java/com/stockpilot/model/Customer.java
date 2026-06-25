package com.stockpilot.model;

import com.stockpilot.exception.InvalidInputException;

public class Customer {
    private Long id;
    private String name;
    private String email;
    private String phone;

    public Customer(Long id, String name, String email, String phone) {
        this.id = id;
        this.name = name;
        setEmail(email);
        setPhone(phone);
    }

    // Getters
    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getEmail() {
        return email;
    }

    public String getPhone() {
        return phone;
    }

    // Setters kèm Validation
    public void setEmail(String email) {
        // Regex kiểm tra email chuẩn cơ bản
        if (email == null || !email.matches("^[A-Za-z0-9+_.-]+@(.+)$")) {
            throw new InvalidInputException("Email không đúng định dạng!");
        }
        this.email = email;
    }

    public void setPhone(String phone) {
        // Regex kiểm tra số điện thoại: Bắt đầu từ 0, có từ 9 đến 10 chữ số
        if (phone == null || !phone.matches("^0\\d{8,9}$")) {
            throw new InvalidInputException("Số điện thoại không hợp lệ (Phải bắt đầu bằng số 0, dài 9-10 số).");
        }
        this.phone = phone;
    }

    @Override
    public String toString() {
        return String.format("Customer[ID=%d, Name=%s, Email=%s]", id, name, email);
    }
}