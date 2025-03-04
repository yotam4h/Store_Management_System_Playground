package com.storeManagement.dataAccessObject;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import com.storeManagement.model.Employee;
import com.storeManagement.utils.DatabaseConnection;
import com.storeManagement.utils.Constants.EmployeeRole;
import com.storeManagement.utils.Logger;

import static com.storeManagement.utils.Constants.OperationType.*;

public class EmployeeDao implements Dao<Employee>
{
    static Connection con = null;
    static Logger logger = null;

    public EmployeeDao() {
        try {
            con = DatabaseConnection.getConnection();
        } catch (IllegalStateException e) {
            System.out.println("IllegalStateException: " + e.getMessage());
        }
        try {
            if(System.getProperty("os.name").startsWith("Windows")) {
                logger = new Logger("C:\\tmp\\log.txt");
            } else {
                logger = new Logger("/logs/log.txt");
            }
        } catch (IllegalStateException e) {
            System.out.println("IllegalStateException: " + e.getMessage());
        }
    }

    @Override
    public int add(Employee emp) throws SQLException, IllegalArgumentException
    {
        if (emp == null)
        {
            throw new IllegalArgumentException("Employee cannot be null");
        }

        String query = "INSERT INTO Employees (id, full_name, phone_number, role, branch_id) VALUES (?, ?, ?, ?, ?)";
        PreparedStatement ps = con.prepareStatement(query, PreparedStatement.RETURN_GENERATED_KEYS);

        ps.setInt(1, emp.getId());
        ps.setString(2, emp.getFullName());
        ps.setString(3, emp.getPhoneNumber());
        ps.setString(4, emp.getRole());
        ps.setInt(5, emp.getBranchId());

        int rowsAffected = ps.executeUpdate();
        if (rowsAffected > 0)
        {
            ResultSet rs = ps.getGeneratedKeys();
            if (rs.next())
            {
                emp.setId(rs.getInt(1));
            }
        } else {
            throw new SQLException("Employee not added");
        }

        logger.log(ADD, emp.toString());

        return rowsAffected;
    }

    @Override
    public void delete(int id) throws SQLException
    {
        String query = "DELETE FROM Employees WHERE id = ?";
        PreparedStatement ps = con.prepareStatement(query);
        ps.setInt(1, id);
        int rowsAffected = ps.executeUpdate();
        if(rowsAffected == 0)
        {
            throw new SQLException("Employee not found");
        }

        logger.log(DELETE, "Employee with ID " + id + " deleted");
    }

    @Override
    public Employee get(int id) throws SQLException
    {
        String query = "SELECT * FROM Employees WHERE id = ?";
        PreparedStatement ps = con.prepareStatement(query);
        ps.setInt(1, id);
        ResultSet rs = ps.executeQuery();

        if (rs.next())
        {
            return new Employee(rs.getInt("id"), rs.getString("full_name"), rs.getString("phone_number"), EmployeeRole.valueOf(rs.getString("role")), rs.getInt("branch_id"));
        } else {
            throw new SQLException("Employee not found");
        }
    }

    @Override
    public List<Employee> getList() throws SQLException
    {
        String query = "SELECT * FROM Employees";
        PreparedStatement ps = con.prepareStatement(query);
        ResultSet rs = ps.executeQuery();
        List<Employee> employees = new ArrayList<>();

        while (rs.next())
        {
            employees.add(new Employee(rs.getInt("id"), rs.getString("full_name"), rs.getString("phone_number"), EmployeeRole.valueOf(rs.getString("role")), rs.getInt("branch_id")));
        }

        if (employees.isEmpty())
        {
            throw new SQLException("No employees found");
        }

        return employees;
    }

    @Override
    public void update(Employee emp) throws SQLException, IllegalArgumentException
    {
        if(emp == null)
        {
            throw new IllegalArgumentException("Employee cannot be null");
        }

        String query = "UPDATE Employees SET full_name = ?, phone_number = ?, role = ?, branch_id = ? WHERE id = ?";
        PreparedStatement ps = con.prepareStatement(query);
        ps.setString(1, emp.getFullName());
        ps.setString(2, emp.getPhoneNumber());
        ps.setString(3, emp.getRole());
        ps.setInt(4, emp.getBranchId());
        ps.setInt(5, emp.getId());

        int rowsAffected = ps.executeUpdate();
        if (rowsAffected == 0)
        {
            throw new SQLException("Employee not updated");
        }

        logger.log(UPDATE, emp.toString());
    }

    public List<Employee> getEmployeesByBranch(int branchId) throws SQLException
    {
        String query = "SELECT * FROM Employees WHERE branch_id = ?";
        PreparedStatement ps = con.prepareStatement(query);
        ps.setInt(1, branchId);
        ResultSet rs = ps.executeQuery();
        List<Employee> employees = new ArrayList<>();

        while (rs.next())
        {
            employees.add(new Employee(rs.getInt("id"), rs.getString("full_name"), rs.getString("phone_number"), EmployeeRole.valueOf(rs.getString("role")), rs.getInt("branch_id")));
        }

        if (employees.isEmpty())
        {
            throw new SQLException("No employees found");
        }

        return employees;
    }
}
