package persistence;

import entity.Product;
import exception.ProductException;
import org.postgresql.util.PSQLException;
import utils.ConnectionDB;

import java.sql.*;
import java.sql.Date;
import java.util.*;

public class ProductDAO {

    // get all products
    public List<Product> getAll() {
        List<Product> products = new ArrayList<>();
        try (
                Connection conn = ConnectionDB.getConnection();
                PreparedStatement pstmt = conn
                        .prepareStatement("SELECT * FROM fn_get_all_product()")
        ) {
            return getProducts(products, pstmt);
        } catch (SQLException e) {
            throw handleSQLException(e);
        }
    }

    // get product by id
    public Product getById(int id) {
        try (
                Connection conn = ConnectionDB.getConnection();
                PreparedStatement pstmt = conn
                        .prepareStatement("SELECT * FROM fn_get_product_by_id(?)")
        ) {
            pstmt.setInt(1, id);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return new Product(
                            rs.getInt("product_id"),
                            rs.getString("product_name"),
                            rs.getDouble("product_price"),
                            rs.getString("product_title"),
                            rs.getDate("product_created").toLocalDate(),
                            rs.getString("product_catalog"),
                            rs.getBoolean("product_status")
                    );
                }
            }
            return null;
        } catch (SQLException e) {
            throw handleSQLException(e);
        }
    }

    // check catalog exist
    public boolean isCatalogExist(String catalog) {
        try (
                Connection conn = ConnectionDB.getConnection();
                PreparedStatement pstmt = conn
                        .prepareStatement("SELECT fn_check_exist_catalog(?)")
        ) {
            pstmt.setString(1, catalog);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getBoolean(1);
                }
            }
            return false;
        } catch (SQLException e) {
            throw handleSQLException(e);
        }
    }

    // add new product
    public void addProduct(Product product) {
        try (
                Connection conn = ConnectionDB.getConnection();
                CallableStatement callableStatement = conn
                        .prepareCall("CALL proc_add_product(?, ?, ?, ?, ?)")
        ) {
            callableStatement.setString(1, product.getName());
            callableStatement.setFloat(2, (float) product.getPrice());
            callableStatement.setString(3, product.getTitle());
            callableStatement.setDate(4, Date.valueOf(product.getCreatedDate()));
            callableStatement.setString(5, product.getCatalog());
            callableStatement.executeUpdate();
        } catch (SQLException e) {
            throw handleSQLException(e);
        }
    }

    // update product by id
    public void updateById(int id, Product newProduct) {
        try (
                Connection conn = ConnectionDB.getConnection();
                CallableStatement callableStatement = conn
                        .prepareCall("CALL proc_update_product_by_id(?, ?, ?, ?, ?, ?)")
        ) {
            callableStatement.setInt(1, id);
            callableStatement.setString(2, newProduct.getName());
            callableStatement.setDouble(3, newProduct.getPrice());
            callableStatement.setString(4, newProduct.getTitle());
            callableStatement.setDate(5, Date.valueOf(newProduct.getCreatedDate()));
            callableStatement.setString(6, newProduct.getCatalog());
            callableStatement.executeUpdate();
        } catch (SQLException e) {
            throw handleSQLException(e);
        }
    }

    // delete product by id
    public void deleteById(int id) {
        try (
                Connection conn = ConnectionDB.getConnection();
                CallableStatement callableStatement = conn
                        .prepareCall("CALL proc_delete_product_by_id(?)")
        ) {
            callableStatement.setInt(1, id);
            callableStatement.executeUpdate();
        } catch (SQLException e) {
            throw handleSQLException(e);
        }
    }

    // search by product name
    public List<Product> searchByName(String name) {
        List<Product> products = new ArrayList<>();
        try (
                Connection conn = ConnectionDB.getConnection();
                PreparedStatement pstmt = conn
                        .prepareStatement("SELECT * FROM fn_get_product_by_name_keyword(?)")
        ) {
            pstmt.setString(1, name);
            return getProducts(products, pstmt);
        } catch (SQLException e) {
            throw handleSQLException(e);
        }
    }

    // get Catalog count
    public Map<String, Integer> getProductCatalog() {
        Map<String, Integer> productCatalog = new HashMap<>();
        try (
                Connection conn = ConnectionDB.getConnection();
                PreparedStatement pstmt = conn
                        .prepareStatement("SELECT * FROM fn_count_product_by_catalog()")
        ) {
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    productCatalog.put(rs.getString("catalog"), rs.getInt("product_count"));
                }
            }
            return productCatalog;
        } catch (SQLException e) {
            throw handleSQLException(e);
        }
    }

    public boolean isNameExist(String name, Integer excludeId) {
        String sql = "SELECT fn_check_exist_product_name(?, ?)";

        try (
                Connection conn = ConnectionDB.getConnection();
                PreparedStatement pstmt = conn.prepareStatement(sql)
        ) {
            pstmt.setString(1, name);

            if (excludeId == null) {
                pstmt.setNull(2, Types.INTEGER);
            } else {
                pstmt.setInt(2, excludeId);
            }

            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getBoolean(1);
                }
            }

            return false;

        } catch (SQLException e) {
            throw handleSQLException(e);
        }
    }

    private List<Product> getProducts(List<Product> products, PreparedStatement pstmt) throws SQLException {
        try (ResultSet rs = pstmt.executeQuery()) {
            while (rs.next()) {
                Product product = new Product(
                        rs.getInt("product_id"),
                        rs.getString("product_name"),
                        rs.getDouble("product_price"),
                        rs.getString("product_title"),
                        rs.getDate("product_created").toLocalDate(),
                        rs.getString("product_catalog"),
                        rs.getBoolean("product_status")
                );
                products.add(product);
            }
            return products;
        }
    }

    private ProductException handleSQLException(SQLException e) {
        if (e instanceof PSQLException ex
                && ex.getServerErrorMessage() != null) {

            return new ProductException(
                    ex.getServerErrorMessage().getMessage()
            );
        }

        return new ProductException("Lỗi hệ thống.");
    }
}
