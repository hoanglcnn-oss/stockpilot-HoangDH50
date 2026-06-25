package com.stockpilot.repository;

import com.stockpilot.exception.DataAccessException;
import com.stockpilot.model.Customer;
import com.stockpilot.util.DbConnectionHelper;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class CustomerRepository implements Repository<Customer, Long> {

    // Helper map ResultSet sang Object
    private Customer mapResultSetToCustomer(ResultSet rs) throws SQLException {
        return new Customer(
                rs.getLong("id"),
                rs.getString("name"),
                rs.getString("email"),
                rs.getString("phone"));
    }

    @Override
    public Customer save(Customer customer) {
        // Cập nhật SQL: INSERT vào bảng customers
        String sql = "INSERT INTO customers (name, email, phone) VALUES (?, ?, ?)";

        try (Connection conn = DbConnectionHelper.getConnection();
                PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            pstmt.setString(1, customer.getName());
            pstmt.setString(2, customer.getEmail());
            pstmt.setString(3, customer.getPhone());

            pstmt.executeUpdate();

            // Lấy ID tự động sinh ra
            try (ResultSet generatedKeys = pstmt.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    return new Customer(
                            generatedKeys.getLong(1),
                            customer.getName(),
                            customer.getEmail(),
                            customer.getPhone());
                }
            }
        } catch (SQLException e) {
            throw new DataAccessException("Lỗi khi lưu khách hàng vào DB", e);
        }
        return customer;
    }

    @Override
    public Optional<Customer> findById(Long id) {
        // Cập nhật SQL: Tìm trong bảng customers
        String sql = "SELECT * FROM customers WHERE id = ?";
        try (Connection conn = DbConnectionHelper.getConnection();
                PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setLong(1, id);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapResultSetToCustomer(rs));
                }
            }
        } catch (SQLException e) {
            throw new DataAccessException("Lỗi khi tìm khách hàng theo ID: " + id, e);
        }
        return Optional.empty();
    }

    @Override
    public List<Customer> findAll() {
        List<Customer> customers = new ArrayList<>();
        // Cập nhật SQL: Lấy từ bảng customers
        String sql = "SELECT * FROM customers";
        try (Connection conn = DbConnectionHelper.getConnection();
                Statement stmt = conn.createStatement();
                ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                customers.add(mapResultSetToCustomer(rs));
            }
        } catch (SQLException e) {
            throw new DataAccessException("Lỗi khi lấy danh sách khách hàng", e);
        }
        return customers;
    }

    @Override
    public void update(Customer customer) {
        // Cập nhật SQL: UPDATE bảng customers
        String sql = "UPDATE customers SET name = ?, email = ?, phone = ? WHERE id = ?";
        try (Connection conn = DbConnectionHelper.getConnection();
                PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, customer.getName());
            pstmt.setString(2, customer.getEmail());
            pstmt.setString(3, customer.getPhone());
            pstmt.setLong(4, customer.getId());

            pstmt.executeUpdate();
        } catch (SQLException e) {
            throw new DataAccessException("Lỗi khi cập nhật khách hàng", e);
        }
    }

    @Override
    public void deleteById(Long id) {
        // Cập nhật SQL: DELETE từ bảng customers
        String sql = "DELETE FROM customers WHERE id = ?";
        try (Connection conn = DbConnectionHelper.getConnection();
                PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setLong(1, id);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            throw new DataAccessException("Lỗi khi xóa khách hàng", e);
        }
    }
}