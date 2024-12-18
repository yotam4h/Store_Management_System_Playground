package com.storeManagement.dataAccessObject;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import com.storeManagement.model.Customer;
import com.storeManagement.utils.Constants.CustomerType;
import com.storeManagement.utils.DatabaseConnection;
import com.storeManagement.utils.Logger;

import static com.storeManagement.utils.Constants.OperationType.*;

public class CustomerDao implements Dao<Customer>
{
    static Connection con = DatabaseConnection.getConnection();
    static Logger logger = new Logger();

    @Override
    public int add(Customer customer) throws SQLException
    {
        String query = "INSERT INTO Customers (full_name, phone_number, customer_type) VALUES (?, ?, ?)";
        PreparedStatement ps = con.prepareStatement(query, PreparedStatement.RETURN_GENERATED_KEYS);

        ps.setString(1, customer.getFullName());
        ps.setString(2, customer.getPhoneNumber());
        ps.setString(3, customer.getType());

        int rowsAffected = ps.executeUpdate();
        if (rowsAffected > 0)
        {
            ResultSet rs = ps.getGeneratedKeys();
            if (rs.next())
            {
                customer.setId(rs.getInt(1));
            }
        } else {
            throw new SQLException("Customer not added");
        }

        logger.log(ADD, customer.toString());

        return rowsAffected;
    }

    @Override
    public void delete(int id) throws SQLException
    {
        String query = "DELETE FROM Customers WHERE id = ?";
        PreparedStatement ps = con.prepareStatement(query);
        ps.setInt(1, id);
        int rowsAffected = ps.executeUpdate();
        if(rowsAffected == 0)
        {
            throw new SQLException("Customer not found");
        }

        logger.log(DELETE, "Customer with ID " + id + " deleted");
    }

    @Override
    public Customer get(int id) throws SQLException
    {
        String query = "SELECT * FROM Customers WHERE id = ?";
        PreparedStatement ps = con.prepareStatement(query);
        ps.setInt(1, id);
        ResultSet rs = ps.executeQuery();
        if (rs.next())
        {
            return new Customer(rs.getInt("id"), rs.getString("full_name"), rs.getString("phone_number"), CustomerType.valueOf(rs.getString("customer_type")));
        } else {
            throw new SQLException("Customer not found");
        }
    }

    @Override
    public List<Customer> getList() throws SQLException, Exception
    {
        String query = "SELECT * FROM Customers";
        PreparedStatement ps = con.prepareStatement(query);
        ResultSet rs = ps.executeQuery();
        List<Customer> customers = new ArrayList<>();
        while (rs.next())
        {
            customers.add(new Customer(rs.getInt("id"), rs.getString("full_name"), rs.getString("phone_number"), CustomerType.valueOf(rs.getString("customer_type"))));
        }

        if (customers.isEmpty())
        {
            throw new Exception("No customers found");
        }

        return customers;
    }

    @Override
    public void update(Customer customer) throws SQLException
    {
        String query = "UPDATE Customers SET full_name = ?, phone_number = ?, customer_type = ? WHERE id = ?";
        PreparedStatement ps = con.prepareStatement(query);
        ps.setString(1, customer.getFullName());
        ps.setString(2, customer.getPhoneNumber());
        ps.setString(3, customer.getType());
        ps.setInt(4, customer.getId());
        int rowsAffected = ps.executeUpdate();
        if (rowsAffected == 0)
        {
            throw new SQLException("Customer not updated");
        }

        logger.log(UPDATE, customer.toString());
    }

}
