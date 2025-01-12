package com.storeManagement.model;

import com.storeManagement.utils.Constants.EmployeeRole;

public class User
{
    int id;
    String username;
    String password_hash;
    EmployeeRole role;
    int branch_id;

    public User() {
    }

    public User(int id, String username, String password_hash, EmployeeRole role, int branch_id) {
        setId(id);
        setUsername(username);
        setPasswordHash(password_hash);
        setRole(role);
        setBranchId(branch_id);
    }

    public User(String username, String password_hash, EmployeeRole role, int branch_id) {
        setUsername(username);
        setPasswordHash(password_hash);
        setRole(role);
        setBranchId(branch_id);
    }

    public int getId() {
        return id;
    }

    public String getUsername() {
        return username;
    }

    public String getPasswordHash() {
        return password_hash;
    }

    public String getRole() {
        return role.toString();
    }

    public int getBranchId() {
        return branch_id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public void setPasswordHash(String passwordHash) {
        this.password_hash = passwordHash;
    }

    public void setRole(EmployeeRole role) {
        this.role = role;
    }

    public void setBranchId(int branchId) {
        this.branch_id = branchId;
    }

    @Override
    public String toString()
    {
        return "User{" +
                "id=" + id +
                ", username='" + username + '\'' +
                ", password_hash='" + password_hash + '\'' +
                ", role='" + role + '\'' +
                ", branch_id=" + branch_id +
                '}';
    }
}
