package com.stockpilot.repository;

import com.stockpilot.exception.DataAccessException;
import com.stockpilot.model.Order;
import com.stockpilot.model.OrderItem;
import com.stockpilot.util.DbConnectionHelper;

import java.sql.*;
import java.util.List;
import java.util.Optional;

public class OrderRepository implements Repository<Order, Long> {

    @Override
    public Order save(Order order) {
        String insertOrderSql = "INSERT INTO orders (customer_id, order_date, total_amount) VALUES (?, ?, ?)";
        String insertItemSql = "INSERT INTO order_items (order_id, product_id, quantity, unit_price) VALUES (?, ?, ?, ?)";

        try (Connection conn = DbConnectionHelper.getConnection()) {

            // 1. LƯU ĐƠN HÀNG CHÍNH VÀ LẤY ID
            try (PreparedStatement orderStmt = conn.prepareStatement(insertOrderSql, Statement.RETURN_GENERATED_KEYS)) {
                orderStmt.setLong(1, order.getCustomerId());
                orderStmt.setTimestamp(2, Timestamp.valueOf(order.getOrderDate()));
                orderStmt.setBigDecimal(3, order.getTotalAmount());

                orderStmt.executeUpdate();

                // Lấy order_id vừa được sinh ra
                try (ResultSet generatedKeys = orderStmt.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        long newOrderId = generatedKeys.getLong(1);
                        // Tạo đối tượng Order mới có chứa ID
                        order = new Order(newOrderId, order.getCustomerId(), order.getOrderDate(),
                                order.getTotalAmount());

                        // 2. LƯU DANH SÁCH CHI TIẾT ĐƠN HÀNG
                        try (PreparedStatement itemStmt = conn.prepareStatement(insertItemSql)) {
                            for (OrderItem item : order.getItems()) {
                                itemStmt.setLong(1, newOrderId); // Gán ID của đơn hàng cha
                                itemStmt.setLong(2, item.getProductId());
                                itemStmt.setInt(3, item.getQuantity());
                                itemStmt.setBigDecimal(4, item.getUnitPrice());

                                // Dùng addBatch để tối ưu hóa việc insert nhiều dòng cùng lúc
                                itemStmt.addBatch();
                            }
                            // Thực thi lưu toàn bộ items
                            itemStmt.executeBatch();
                        }
                    }
                }
            }
        } catch (SQLException e) {
            throw new DataAccessException("Lỗi khi lưu đơn hàng vào DB", e);
        }
        return order;
    }

    // --- Các hàm bên dưới tạo khung trống trước, bạn có thể implement sau nếu cần
    // ---

    @Override
    public Optional<Order> findById(Long id) {
        // Mở rộng sau: Khi SELECT Order phải JOIN để lấy thêm List<OrderItem>
        throw new UnsupportedOperationException("Chưa implement hàm findById cho Order");
    }

    @Override
    public List<Order> findAll() {
        throw new UnsupportedOperationException("Chưa implement hàm findAll cho Order");
    }

    @Override
    public void update(Order entity) {
        throw new UnsupportedOperationException("Thường thì Hóa đơn đã xuất sẽ không cho Update nội dung");
    }

    @Override
    public void deleteById(Long id) {
        throw new UnsupportedOperationException(
                "Thường thì Hóa đơn đã xuất sẽ được đánh dấu Cancelled thay vì xóa cứng");
    }
}