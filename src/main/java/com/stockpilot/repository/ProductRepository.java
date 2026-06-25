package com.stockpilot.repository;

import com.stockpilot.exception.DataAccessException;
import com.stockpilot.model.Product;
import com.stockpilot.util.DbConnectionHelper;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class ProductRepository implements Repository<Product, Long> {

    // Helper map ResultSet sang Object (Code sạch)
    private Product mapResultSetToProduct(ResultSet rs) throws SQLException {
        return new Product(
                rs.getLong("id"),
                rs.getString("sku"),
                rs.getString("name"),
                rs.getString("category"),
                rs.getBigDecimal("price"),
                rs.getInt("stock_quantity"));
    }

    @Override
    public Product save(Product product) {
        String sql = "INSERT INTO products (sku, name, category, price, stock_quantity) VALUES (?, ?, ?, ?, ?)";

        // try-with-resources: Tự động đóng connection và statement sau khi chạy xong
        try (Connection conn = DbConnectionHelper.getConnection();
                PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            pstmt.setString(1, product.getSku());
            pstmt.setString(2, product.getName());
            pstmt.setString(3, product.getCategory());
            pstmt.setBigDecimal(4, product.getPrice());
            pstmt.setInt(5, product.getStockQuantity());

            pstmt.executeUpdate();

            // Lấy ID vừa được database tự động sinh ra
            try (ResultSet generatedKeys = pstmt.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    // Tạo một object mới copy dữ liệu cũ nhưng có thêm ID
                    return new Product(
                            generatedKeys.getLong(1),
                            product.getSku(),
                            product.getName(),
                            product.getCategory(),
                            product.getPrice(),
                            product.getStockQuantity());
                }
            }
        } catch (SQLException e) {
            throw new DataAccessException("Lỗi khi lưu sản phẩm vào DB", e);
        }
        return product;
    }

    @Override
    public Optional<Product> findById(Long id) {
        String sql = "SELECT * FROM products WHERE id = ?";
        try (Connection conn = DbConnectionHelper.getConnection();
                PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setLong(1, id);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapResultSetToProduct(rs));
                }
            }
        } catch (SQLException e) {
            throw new DataAccessException("Lỗi khi tìm sản phẩm theo ID: " + id, e);
        }
        return Optional.empty(); // Trả về hộp rỗng nếu không tìm thấy
    }

    @Override
    public List<Product> findAll() {
        List<Product> products = new ArrayList<>();
        String sql = "SELECT * FROM products";
        try (Connection conn = DbConnectionHelper.getConnection();
                Statement stmt = conn.createStatement();
                ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                products.add(mapResultSetToProduct(rs));
            }
        } catch (SQLException e) {
            throw new DataAccessException("Lỗi khi lấy danh sách sản phẩm", e);
        }
        return products;
    }

    @Override
    public void update(Product product) {
        String sql = "UPDATE products SET sku = ?, name = ?, category = ?, price = ?, stock_quantity = ? WHERE id = ?";
        try (Connection conn = DbConnectionHelper.getConnection();
                PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, product.getSku());
            pstmt.setString(2, product.getName());
            pstmt.setString(3, product.getCategory());
            pstmt.setBigDecimal(4, product.getPrice());
            pstmt.setInt(5, product.getStockQuantity());
            pstmt.setLong(6, product.getId()); // Tham số WHERE id = ?

            pstmt.executeUpdate();
        } catch (SQLException e) {
            throw new DataAccessException("Lỗi khi cập nhật sản phẩm", e);
        }
    }

    @Override
    public void deleteById(Long id) {
        String sql = "DELETE FROM products WHERE id = ?";
        try (Connection conn = DbConnectionHelper.getConnection();
                PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setLong(1, id);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            throw new DataAccessException("Lỗi khi xóa sản phẩm", e);
        }
    }
}