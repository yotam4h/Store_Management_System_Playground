package com.storeManagement.dataAccessObject;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import com.storeManagement.model.Product;
import com.storeManagement.utils.DatabaseConnection;
import com.storeManagement.utils.Constants.Category;
import com.storeManagement.utils.Logger;

import static com.storeManagement.utils.Constants.OperationType.*;

public class ProductDao implements Dao<Product>
{
    static Connection con = null;
    static Logger logger = null;

    public ProductDao() {
        try {
            con = DatabaseConnection.getConnection();
        } catch (IllegalStateException e) {
            e.printStackTrace();
        }
        try {
            if(System.getProperty("os.name").startsWith("Windows")) {
                logger = new Logger("C:\\tmp\\log.txt");
            } else {
                logger = new Logger("/logs/log.txt");
            }
        } catch (IllegalStateException e) {
            e.printStackTrace();
        }
    }

    @Override
    public int add(Product product) throws SQLException, IllegalArgumentException
    {
        if (product == null)
        {
            throw new IllegalArgumentException("Product cannot be null");
        }

        String query = "INSERT INTO Products (name, category, price, stock_quantity, branch_id) VALUES (?, ?, ?, ?, ?)";
        PreparedStatement ps = con.prepareStatement(query, PreparedStatement.RETURN_GENERATED_KEYS);
        ps.setString(1, product.getName());
        ps.setString(2, product.getCategory().toString());
        ps.setDouble(3, product.getPrice());
        ps.setInt(4, product.getStockQuantity());
        ps.setInt(5, product.getBranchId());

        int rowsAffected = ps.executeUpdate();
        if (rowsAffected > 0)
        {
            ResultSet rs = ps.getGeneratedKeys();
            if (rs.next())
            {
                product.setId(rs.getInt(1));
            }
        }

        logger.log(ADD, product.toString());

        return rowsAffected;
    }

    @Override
    public void delete(int id) throws SQLException
    {
        String query = "DELETE FROM Products WHERE id = ?";
        PreparedStatement ps = con.prepareStatement(query);
        ps.setInt(1, id);
        int rowsAffected = ps.executeUpdate();
        if(rowsAffected == 0)
        {
            throw new SQLException("Product not found");
        }

        logger.log(DELETE, "Product with ID " + id + " deleted");
    }

    @Override
    public Product get(int id) throws SQLException
    {
        String query = "SELECT * FROM Products WHERE id = ?";
        PreparedStatement ps = con.prepareStatement(query);
        ps.setInt(1, id);
        ResultSet rs = ps.executeQuery();
        if (rs.next())
        {
            return new Product(rs.getInt("id"), rs.getString("name"), Category.valueOf(rs.getString("category")), rs.getDouble("price"), rs.getInt("stock_quantity"), rs.getInt("branch_id"));
        } else {
            throw new SQLException("Product not found");
        }
    }

    @Override
    public List<Product> getList() throws SQLException
    {
        String query = "SELECT * FROM Products";
        PreparedStatement ps = con.prepareStatement(query);
        ResultSet rs = ps.executeQuery();
        List<Product> products = new ArrayList<>();
        while (rs.next())
        {
            products.add(new Product(rs.getInt("id"), rs.getString("name"), Category.valueOf(rs.getString("category")), rs.getDouble("price"), rs.getInt("stock_quantity"), rs.getInt("branch_id")));
        }
        return products;
    }

    @Override
    public void update(Product product) throws SQLException, IllegalArgumentException
    {
        if (product == null)
        {
            throw new IllegalArgumentException("Product cannot be null");
        }

        String query = "UPDATE Products SET name = ?, category = ?, price = ?, stock_quantity = ?, branch_id = ? WHERE id = ?";
        PreparedStatement ps = con.prepareStatement(query);
        ps.setString(1, product.getName());
        ps.setString(2, product.getCategory().toString());
        ps.setDouble(3, product.getPrice());
        ps.setInt(4, product.getStockQuantity());
        ps.setInt(5, product.getBranchId());
        ps.setInt(6, product.getId());

        int rowsAffected = ps.executeUpdate();
        if (rowsAffected == 0)
        {
            throw new SQLException("Product not found");
        }

        logger.log(UPDATE, product.toString());
    }

    public List<Product> getProductsByBranch(int branchId) throws SQLException
    {
        String query = "SELECT * FROM Products WHERE branch_id = ?";
        PreparedStatement ps = con.prepareStatement(query);
        ps.setInt(1, branchId);
        ResultSet rs = ps.executeQuery();
        List<Product> products = new ArrayList<>();
        while (rs.next())
        {
            products.add(new Product(rs.getInt("id"), rs.getString("name"), Category.valueOf(rs.getString("category")), rs.getDouble("price"), rs.getInt("stock_quantity"), rs.getInt("branch_id")));
        }
        return products;
    }

    public List<Product> getProductsByCategory(Category category) throws SQLException
    {
        String query = "SELECT * FROM Products WHERE category = ?";
        PreparedStatement ps = con.prepareStatement(query);
        ps.setString(1, category.toString());
        ResultSet rs = ps.executeQuery();
        List<Product> products = new ArrayList<>();
        while (rs.next())
        {
            products.add(new Product(rs.getInt("id"), rs.getString("name"), Category.valueOf(rs.getString("category")), rs.getDouble("price"), rs.getInt("stock_quantity"), rs.getInt("branch_id")));
        }
        return products;
    }
}
