package com.storeManagement.dataAccessObject;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import com.storeManagement.dataAccessObject.exceptions.DaoException;
import com.storeManagement.model.Branch;
import com.storeManagement.utils.DatabaseConnection;
import com.storeManagement.utils.Logger;

import static com.storeManagement.utils.Constants.OperationType.*;

public class BranchDao implements Dao<Branch>
{
    static Connection con = DatabaseConnection.getConnection();
    static Logger logger = new Logger();

    @Override
    public int add(Branch obj) throws SQLException, DaoException
    {
        String query = "INSERT INTO Branches (name, address, phone_number) VALUES (?, ?, ?)";
        PreparedStatement ps = con.prepareStatement(query, PreparedStatement.RETURN_GENERATED_KEYS);
        ps.setString(1, obj.getName());
        ps.setString(2, obj.getAddress());
        ps.setString(3, obj.getPhone());

        int rowsAffected = ps.executeUpdate();
        if (rowsAffected > 0)
        {
            ResultSet rs = ps.getGeneratedKeys();
            if (rs.next())
            {
                obj.setId(rs.getInt(1));
            }
        } else {
            throw new DaoException("Branch not added");
        }

        logger.log(ADD, obj.toString());

        return rowsAffected;
    }

    @Override
    public void delete(int id) throws SQLException, DaoException
    {
        String query = "DELETE FROM Branches WHERE id = ?";
        PreparedStatement ps = con.prepareStatement(query);
        ps.setInt(1, id);
        int rowsAffected = ps.executeUpdate();
        if(rowsAffected == 0)
        {
            throw new DaoException("Branch not found");
        }

        logger.log(DELETE, "Branch with id " + id + " deleted");
    }

    @Override
    public Branch get(int id) throws SQLException, DaoException
    {
        String query = "SELECT * FROM Branches WHERE id = ?";
        PreparedStatement ps = con.prepareStatement(query);
        ps.setInt(1, id);
        ResultSet rs = ps.executeQuery();

        if (rs.next())
        {
            return new Branch(rs.getInt("id"), rs.getString("name"), rs.getString("address"), rs.getString("phone_number"));
        } else {
            throw new DaoException("Branch not found");
        }
    }

    @Override
    public List<Branch> getList() throws SQLException, DaoException
    {
        String query = "SELECT * FROM Branches";
        PreparedStatement ps = con.prepareStatement(query);
        ResultSet rs = ps.executeQuery();

        List<Branch> branches = new ArrayList<>();
        while (rs.next())
        {
            branches.add(new Branch(rs.getInt("id"), rs.getString("name"), rs.getString("address"), rs.getString("phone_number")));
        }

        if (branches.isEmpty())
        {
            throw new DaoException("No branches found");
        }

        return branches;
    }

    @Override
    public void update(Branch obj) throws SQLException, DaoException
    {
        String query = "UPDATE Branches SET name = ?, address = ?, phone_number = ? WHERE id = ?";
        PreparedStatement ps = con.prepareStatement(query);
        ps.setString(1, obj.getName());
        ps.setString(2, obj.getAddress());
        ps.setString(3, obj.getPhone());
        ps.setInt(4, obj.getId());

        int rowsAffected = ps.executeUpdate();
        if (rowsAffected == 0)
        {
            throw new DaoException("Branch not updated");
        }

        logger.log(UPDATE, obj.toString());
    }
}
