package com.storeManagement.model;

import com.storeManagement.utils.Constants.EmployeeRole;

import java.util.Scanner;

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

    public void setId() {
        Scanner s = new Scanner(System.in);
        System.out.println("Enter the user id: ");
        this.id = s.nextInt();
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public void setUsername() {
        Scanner s = new Scanner(System.in);
        System.out.println("Enter the username: ");
        this.username = s.next();
    }

    public void setPasswordHash(String passwordHash) {
        this.password_hash = passwordHash;
    }

    public void setPasswordHash() {
        Scanner s = new Scanner(System.in);
        System.out.println("Enter the password: ");
        this.password_hash = s.next();
    }

    public void setRole(EmployeeRole role) {
        this.role = role;
    }

    public void setRole() {
        Scanner s = new Scanner(System.in);
        boolean validRole = false;

        while (!validRole)
        {
            System.out.println("Choose role: ");
            System.out.println("1. ADMIN\n2. MANAGER\n3. EMPLOYEE");
            System.out.println("Enter role: ");
            int role = s.nextInt();
            switch (role)
            {
                case 1:
                    this.role = EmployeeRole.ADMIN;
                    validRole = true;
                    break;
                case 2:
                    this.role = EmployeeRole.MANAGER;
                    validRole = true;
                    break;
                case 3:
                    this.role = EmployeeRole.EMPLOYEE;
                    validRole = true;
                    break;
                default:
                    System.out.println("Invalid role.");
                    break;
            }
        }
    }

    public void setBranchId(int branchId) {
        this.branch_id = branchId;
    }

    public void setBranchId() {
        Scanner s = new Scanner(System.in);
        System.out.println("Enter the branch id: ");
        this.branch_id = s.nextInt();
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
