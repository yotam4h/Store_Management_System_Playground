package com.storeManagement.dataAccessObject;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import com.storeManagement.model.User;
import com.storeManagement.utils.DatabaseConnection;
import com.storeManagement.utils.Constants.EmployeeRole;
import com.storeManagement.utils.Logger;

import static com.storeManagement.utils.Constants.OperationType.*;

public class UserDao implements Dao<User>
{
    static Connection con = null;
    static Logger logger = null;

    public UserDao() {
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
    public int add(User user) throws SQLException, IllegalArgumentException
    {
        if (user == null)
        {
            throw new IllegalArgumentException("User cannot be null");
        }

        String query = "INSERT INTO Users (username, password_hash, role, branch_id) VALUES (?, ?, ?, ?)";
        PreparedStatement ps = con.prepareStatement(query, PreparedStatement.RETURN_GENERATED_KEYS);
        ps.setString(1, user.getUsername());
        ps.setString(2, user.getPasswordHash());
        ps.setString(3, user.getRole());
        ps.setInt(4, user.getBranchId());

        int rowsAffected = ps.executeUpdate();
        if (rowsAffected > 0)
        {
            ResultSet rs = ps.getGeneratedKeys();
            if (rs.next())
            {
                user.setId(rs.getInt(1));
            }
        }

        logger.log(ADD, user.toString());

        return rowsAffected;
    }

    @Override
    public void delete(int id) throws SQLException
    {
        String query = "DELETE FROM Users WHERE id = ?";
        PreparedStatement ps = con.prepareStatement(query);
        ps.setInt(1, id);
        int rowsAffected = ps.executeUpdate();
        if (rowsAffected == 0)
        {
            throw new SQLException("User not found");
        }

        logger.log(DELETE, "User with id " + id);
    }

    @Override
    public User get(int id) throws SQLException
    {
        String query = "SELECT * FROM Users WHERE id = ?";
        PreparedStatement ps = con.prepareStatement(query);
        ps.setInt(1, id);
        ResultSet rs = ps.executeQuery();
        if (rs.next())
        {
            return new User(rs.getInt("id"), rs.getString("username"), rs.getString("password_hash"), EmployeeRole.valueOf(rs.getString("role")), rs.getInt("branch_id"));
        }
        return null;
    }

    @Override
    public List<User> getList() throws SQLException
    {
        List<User> users = new ArrayList<>();
        String query = "SELECT * FROM Users";
        PreparedStatement ps = con.prepareStatement(query);
        ResultSet rs = ps.executeQuery();
        while (rs.next())
        {
            users.add(new User(rs.getInt("id"), rs.getString("username"), rs.getString("password_hash"), EmployeeRole.valueOf(rs.getString("role")), rs.getInt("branch_id")));
        }
        return users;
    }

    @Override
    public void update(User user) throws SQLException
    {
        String query = "UPDATE Users SET username = ?, password_hash = ?, role = ?, branch_id = ? WHERE id = ?";
        PreparedStatement ps = con.prepareStatement(query);
        ps.setString(1, user.getUsername());
        ps.setString(2, user.getPasswordHash());
        ps.setString(3, user.getRole());
        ps.setInt(4, user.getBranchId());
        ps.setInt(5, user.getId());
        int rowsAffected = ps.executeUpdate();
        if (rowsAffected == 0)
        {
            throw new SQLException("User not found");
        }

        logger.log(UPDATE, user.toString());
    }

    public User getByUsername(String username) throws SQLException
    {
        String query = "SELECT * FROM Users WHERE username = ?";
        PreparedStatement ps = con.prepareStatement(query);
        ps.setString(1, username);
        ResultSet rs = ps.executeQuery();
        if (rs.next())
        {
            return new User(rs.getInt("id"), rs.getString("username"), rs.getString("password_hash"), EmployeeRole.valueOf(rs.getString("role")), rs.getInt("branch_id"));
        }
        return null;
    }

    public boolean authenticateUser(String username, String password) throws SQLException
    {
        String query = "SELECT * FROM Users WHERE username = ? AND password_hash = ?";
        PreparedStatement ps = con.prepareStatement(query);
        ps.setString(1, username);
        ps.setString(2, password);
        ResultSet rs = ps.executeQuery();
        return rs.next();
    }
}
