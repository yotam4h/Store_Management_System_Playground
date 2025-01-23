package com.storeManagement.model;

import com.storeManagement.utils.Constants.EmployeeRole;

import java.util.InputMismatchException;
import java.util.Scanner;

public class Employee {
    int id;
    String fullName;
    String phoneNumber;
    EmployeeRole role;
    int branchId;

    public Employee() {}

    public Employee(int id, String fullName, String phoneNumber, EmployeeRole role, int branchId) {
        setId(id);
        setFullName(fullName);
        setPhoneNumber(phoneNumber);
        setRole(role);
        setBranchId(branchId);
    }

    // Overloaded Constructor without ID (for new employees)
    public Employee(String fullName, String phoneNumber, EmployeeRole role, int branchId) {
        setFullName(fullName);
        setPhoneNumber(phoneNumber);
        setRole(role);
        setBranchId(branchId);
    }

    // Getters
    public int getId() {
        return id;
    }

    public String getFullName() {
        return fullName;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public String getRole() {
        return role.toString();
    }

    public int getBranchId() {
        return branchId;
    }

    // Setters (optional, for future updates)
    public void setId(int id) {
        this.id = id;
    }

    public void setId()
    {
        Scanner s = new Scanner(System.in);
        boolean validId = false;
        System.out.println("Enter the employee ID: ");

        while(!validId)
        {
            try {
                this.id = s.nextInt();
                validId = true;
            } catch (InputMismatchException e) {
                System.out.println(e.getMessage());
            } finally {
                s.nextLine();
            }
        }
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public void setFullName()
    {
        Scanner s = new Scanner(System.in);
        System.out.println("Enter the employee full name: ");
        this.fullName = s.nextLine();
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public void setPhoneNumber()
    {
        Scanner s = new Scanner(System.in);
        System.out.println("Enter the employee phone number: ");
        this.phoneNumber = s.nextLine();
    }

    public void setRole(EmployeeRole role) {
        this.role = role;
    }

    public void setRole()
    {
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
        this.branchId = branchId;
    }

    public void setBranchId()
    {
        Scanner s = new Scanner(System.in);
        System.out.println("Enter the employee branch id: ");
        this.branchId = s.nextInt();
    }

    @Override
    public String toString()
    {
        return "Employee{" +
                "id=" + id +
                ", fullName='" + getFullName() + '\'' +
                ", phoneNumber='" + getPhoneNumber() + '\'' +
                ", employeeRole=" + getRole() +
                ", branchId=" + getBranchId() +
                '}';
    }

}