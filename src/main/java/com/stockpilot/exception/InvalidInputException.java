package com.stockpilot.exception;

// Kế thừa RuntimeException để không bắt buộc phải dùng try-catch ở mọi nơi khi khởi tạo object
public class InvalidInputException extends RuntimeException {
    public InvalidInputException(String message) {
        super(message);
    }
}