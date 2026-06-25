package com.stockpilot.repository;

import java.util.List;
import java.util.Optional;

/**
 * Interface Generic đại diện cho lớp truy cập dữ liệu (Data Access Object -
 * DAO)
 * 
 * @param <T>  Loại đối tượng (Ví dụ: Product, Customer)
 * @param <ID> Loại dữ liệu của khóa chính (Ví dụ: Long, String)
 */
public interface Repository<T, ID> {

    // Thêm mới một thực thể vào Database
    T save(T entity);

    // Tìm kiếm thực thể theo Khóa chính (ID)
    // Dùng Optional để tránh lỗi NullPointerException (Best Practice từ Java 8+)
    Optional<T> findById(ID id);

    // Lấy toàn bộ danh sách thực thể từ Database
    List<T> findAll();

    // Cập nhật thông tin thực thể đã tồn tại
    void update(T entity);

    // Xóa thực thể theo Khóa chính (ID)
    void deleteById(ID id);
}