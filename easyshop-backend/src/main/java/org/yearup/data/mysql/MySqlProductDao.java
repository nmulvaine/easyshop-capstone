package org.yearup.data.mysql;

import org.springframework.stereotype.Component;
import org.yearup.models.Product;
import org.yearup.data.ProductDao;

import javax.sql.DataSource;
import java.math.BigDecimal;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

@Component
public class MySqlProductDao extends MySqlDaoBase implements ProductDao {

    public MySqlProductDao(DataSource dataSource) {
        super(dataSource);
    }

    @Override
    public List<Product> search(Integer categoryId, BigDecimal minPrice, BigDecimal maxPrice, String color) {
        List<Product> products = new ArrayList<>();

        String sql = "SELECT * FROM products " +
                     "WHERE (category_id = ? OR ? = -1) " +
                     "  AND (price BETWEEN ? AND ?) " +
                     "  AND (color = ? OR ? = '')";

        categoryId = categoryId == null ? -1 : categoryId;
        minPrice = minPrice == null ? BigDecimal.ZERO : minPrice;
        maxPrice = maxPrice == null ? BigDecimal.valueOf(Double.MAX_VALUE) : maxPrice;
        color = color == null ? "" : color;

        try (Connection connection = getConnection();
             PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, categoryId);
            stmt.setInt(2, categoryId);
            stmt.setBigDecimal(3, minPrice);
            stmt.setBigDecimal(4, maxPrice);
            stmt.setString(5, color);
            stmt.setString(6, color);

            try (ResultSet row = stmt.executeQuery()) {
                while (row.next()) {
                    products.add(mapRow(row));
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error executing search query", e);
        }

        return products;
    }

    @Override
    public List<Product> listByCategoryId(int categoryId)
    {
        return List.of();
    }

    @Override
    public Product getById(int productId) {
        String sql = "SELECT * FROM products WHERE product_id = ?";
        try (Connection connection = getConnection();
             PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, productId);

            try (ResultSet row = stmt.executeQuery()) {
                if (row.next()) {
                    return mapRow(row);
                } else {
                    throw new RuntimeException("Product with ID " + productId + " not found.");
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error fetching product by ID", e);
        }
    }

    @Override
    public Product create(Product product) {
        String sql = "INSERT INTO products(name, price, category_id, description, color, image_url, stock, featured) " +
                     "VALUES (?, ?, ?, ?, ?, ?, ?, ?)";

        try (Connection connection = getConnection();
             PreparedStatement stmt = connection.prepareStatement(sql, PreparedStatement.RETURN_GENERATED_KEYS)) {
            stmt.setString(1, product.getName());
            stmt.setBigDecimal(2, product.getPrice());
            stmt.setInt(3, product.getCategoryId());
            stmt.setString(4, product.getDescription());
            stmt.setString(5, product.getColor());
            stmt.setString(6, product.getImageUrl());
            stmt.setInt(7, product.getStock());
            stmt.setBoolean(8, product.isFeatured());

            int rowsAffected = stmt.executeUpdate();
            if (rowsAffected > 0) {
                try (ResultSet generatedKeys = stmt.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        int productId = generatedKeys.getInt(1);
                        return getById(productId);
                    }
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error creating product", e);
        }
        return null;
    }

    @Override
    public void update(int productId, Product product) {
        String sql = "UPDATE products SET name = ?, price = ?, category_id = ?, description = ?, " +
                     "color = ?, image_url = ?, stock = ?, featured = ? WHERE product_id = ?";

        try (Connection connection = getConnection();
             PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, product.getName());
            stmt.setBigDecimal(2, product.getPrice());
            stmt.setInt(3, product.getCategoryId());
            stmt.setString(4, product.getDescription());
            stmt.setString(5, product.getColor());
            stmt.setString(6, product.getImageUrl());
            stmt.setInt(7, product.getStock());
            stmt.setBoolean(8, product.isFeatured());
            stmt.setInt(9, productId);

            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Error updating product", e);
        }
    }

    @Override
    public void delete(int productId) {
        String sql = "DELETE FROM products WHERE product_id = ?";

        try (Connection connection = getConnection();
             PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, productId);
            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Error deleting product", e);
        }
    }

    private static Product mapRow(ResultSet row) throws SQLException {
        return new Product(
                row.getInt("product_id"),
                row.getString("name"),
                row.getBigDecimal("price"),
                row.getInt("category_id"),
                row.getString("description"),
                row.getString("color"),
                row.getInt("stock"),
                row.getBoolean("featured"),
                row.getString("image_url")
        );
    }
}
