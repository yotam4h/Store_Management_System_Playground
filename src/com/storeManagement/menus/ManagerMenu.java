package com.storeManagement.menus;

import com.storeManagement.dataAccessObject.BranchDao;
import com.storeManagement.dataAccessObject.EmployeeDao;
import com.storeManagement.model.Employee;
import com.storeManagement.utils.Constants.EmployeeRole;

import java.sql.SQLException;
import java.util.List;
import java.util.Scanner;

public class ManagerMenu
{
    EmployeeDao edao = new EmployeeDao();
    BranchDao bdao = new BranchDao();

    public void showMenu()
    {

    }

    void addEmployee() throws SQLException
    {
        Scanner s = new Scanner(System.in);
        Employee e = new Employee();

        System.out.println("Adding new employee!");

        System.out.print("Enter full name: ");
        e.setFullName(s.nextLine());

        System.out.print("Enter phone number: ");
        e.setPhoneNumber(s.nextLine());

        System.out.print("Choose Role (1-ADMIN,2-MANAGER,3-EMPLOYEE): ");
        switch (s.nextInt())
        {
            case 1: e.setRole(EmployeeRole.ADMIN);
            case 2: e.setRole(EmployeeRole.MANAGER);
            case 3: e.setRole(EmployeeRole.EMPLOYEE);
        }
        s.nextLine(); // clean buffer

        // TODO : show branch id's...

        System.out.println("Enter branch id: ");
        e.setBranchId(s.nextInt());

        if(edao.add(e) > 0)
            System.out.println("Employee added!");

    }

    void deleteEmployee()
    {}

    void showEmployees() throws SQLException
    {
        List<Employee> employees = edao.getList();

        for (Employee e : employees)
        {
            System.out.println(e);
        }
    }

    void updateEmployee()
    {}

}
