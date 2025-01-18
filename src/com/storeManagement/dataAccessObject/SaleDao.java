package com.storeManagement.dataAccessObject;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import com.storeManagement.model.Sale;
import com.storeManagement.utils.DatabaseConnection;
import com.storeManagement.utils.Logger;

import static com.storeManagement.utils.Constants.OperationType.*;

public class SaleDao implements Dao<Sale>
{
    static Connection con = null;
    static Logger logger = null;

    public SaleDao() {
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
    public int add(Sale sale) throws SQLException, IllegalArgumentException
    {
        if (sale == null)
        {
            throw new IllegalArgumentException("Sale cannot be null");
        }

        String query = "INSERT INTO Sales (customer_id, product_id, quantity, branch_id) VALUES (?, ?, ?, ?)";
        PreparedStatement ps = con.prepareStatement(query, PreparedStatement.RETURN_GENERATED_KEYS);
        ps.setInt(1, sale.getCustomerId());
        ps.setInt(2, sale.getProductId());
        ps.setInt(3, sale.getQuantity());
        ps.setInt(4, sale.getBranchId());

        int rowsAffected = ps.executeUpdate();
        if (rowsAffected > 0)
        {
            ResultSet rs = ps.getGeneratedKeys();
            if (rs.next())
            {
                sale.setId(rs.getInt(1));
                sale.setSaleDate(rs.getTimestamp("sale_date"));
            }
        } else {
            throw new SQLException("Sale not added");
        }

        logger.log(ADD, sale.toString());

        return rowsAffected;
    }

    @Override
    public void delete(int id) throws SQLException
    {
        String query = "DELETE FROM Sales WHERE id = ?";
        PreparedStatement ps = con.prepareStatement(query);
        ps.setInt(1, id);

        int rowsAffected = ps.executeUpdate();
        if(rowsAffected == 0)
        {
            throw new SQLException("Sale not deleted");
        }

        logger.log(DELETE, "Sale with ID " + id + " deleted");
    }

    @Override
    public Sale get(int id) throws SQLException
    {
        String query = "SELECT * FROM Sales WHERE id = ?";
        PreparedStatement ps = con.prepareStatement(query);
        ps.setInt(1, id);

        ResultSet rs = ps.executeQuery();
        if (rs.next())
        {
            return new Sale(rs.getInt("id"), rs.getInt("customer_id"), rs.getInt("product_id"), rs.getInt("quantity"), rs.getInt("branch_id"), rs.getTimestamp("date"));
        }

        throw new SQLException("Sale not found");
    }

    @Override
    public List<Sale> getList() throws SQLException
    {
        String query = "SELECT * FROM Sales";
        PreparedStatement ps = con.prepareStatement(query);

        ResultSet rs = ps.executeQuery();
        List<Sale> sales = new ArrayList<>();
        while (rs.next())
        {
            sales.add(new Sale(rs.getInt("id"), rs.getInt("customer_id"), rs.getInt("product_id"), rs.getInt("quantity"), rs.getInt("branch_id")));
        }

        return sales;
    }

    @Override
    public void update(Sale sale) throws SQLException
    {
        String query = "UPDATE Sales SET customer_id = ?, product_id = ?, quantity = ?, branch_id = ? WHERE id = ?";
        PreparedStatement ps = con.prepareStatement(query);
        ps.setInt(1, sale.getCustomerId());
        ps.setInt(2, sale.getProductId());
        ps.setInt(3, sale.getQuantity());
        ps.setInt(4, sale.getBranchId());
        ps.setInt(5, sale.getId());

        int rowsAffected = ps.executeUpdate();
        if(rowsAffected == 0)
        {
            throw new SQLException("Sale not updated");
        }

        logger.log(UPDATE, sale.toString());
    }

    public List<Sale> getSalesByBranch(int branchId) throws SQLException
    {
        String query = "SELECT * FROM Sales WHERE branch_id = ?";
        PreparedStatement ps = con.prepareStatement(query);
        ps.setInt(1, branchId);

        ResultSet rs = ps.executeQuery();
        List<Sale> sales = new ArrayList<>();
        while (rs.next())
        {
            sales.add(new Sale(rs.getInt("id"), rs.getInt("customer_id"), rs.getInt("product_id"), rs.getInt("quantity"), rs.getInt("branch_id")));
        }

        return sales;
    }


    public List<Sale> getSalesByProduct(int productId) throws SQLException
    {
        String query = "SELECT * FROM Sales WHERE product_id = ?";
        PreparedStatement ps = con.prepareStatement(query);
        ps.setInt(1, productId);

        ResultSet rs = ps.executeQuery();
        List<Sale> sales = new ArrayList<>();
        while (rs.next())
        {
            sales.add(new Sale(rs.getInt("id"), rs.getInt("customer_id"), rs.getInt("product_id"), rs.getInt("quantity"), rs.getInt("branch_id")));
        }

        return sales;
    }

    public List<Sale> getSalesByCategory(String category) throws SQLException
    {
        String query = "SELECT * FROM Sales WHERE product_id IN (SELECT id FROM Products WHERE category = ?)";
        PreparedStatement ps = con.prepareStatement(query);
        ps.setString(1, category);

        ResultSet rs = ps.executeQuery();
        List<Sale> sales = new ArrayList<>();
        while (rs.next())
        {
            sales.add(new Sale(rs.getInt("id"), rs.getInt("customer_id"), rs.getInt("product_id"), rs.getInt("quantity"), rs.getInt("branch_id")));
        }

        return sales;
    }
}
