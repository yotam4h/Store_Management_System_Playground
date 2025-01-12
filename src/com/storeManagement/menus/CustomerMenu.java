package com.storeManagement.menus;

import com.storeManagement.dataAccessObject.CustomerDao;
import com.storeManagement.model.Customer;
import com.storeManagement.utils.Constants.CustomerType;

import java.sql.SQLException;
import java.util.List;
import java.util.Scanner;

public class CustomerMenu
{
    CustomerDao cdao = new CustomerDao();

    public void showMenu()
    {
        Scanner s = new Scanner(System.in);
        int choice;

        do
        {
            System.out.println("\n\nCUSTOMER MENU");
            System.out.println("1. Add new customer");
            System.out.println("2. Update existing customer");
            System.out.println("3. Delete existing customer");
            System.out.println("4. Show all customers");
            System.out.println("5. Exit");

            System.out.print("Enter your choice: ");
            choice = s.nextInt();

            try
            {
                switch (choice)
                {
                    case 1:
                        addCustomer();
                        break;
                    case 2:
                        updateCustomer();
                        break;
                    case 3:
                        deleteCustomer();
                        break;
                    case 4:
                        showCustomers();
                        break;
                    case 5:
                        System.out.println("Exiting Customer Menu...");
                        break;
                    default:
                        System.out.println("Invalid choice! Please try again.");
                }
            }
            catch (SQLException e)
            {
                System.out.println("An error occurred: " + e.getMessage());
            }

        } while (choice != 5);
    }

    void updateCustomer() throws SQLException
    {
        Scanner s = new Scanner(System.in);

        System.out.println("Updating EXISTING Customer!");

        System.out.print("Enter customer ID: ");
        int id = s.nextInt();
        s.nextLine();

        Customer c = cdao.get(id);

        System.out.println("Current details: ");
        System.out.println(c);

        System.out.print("Enter new full name: ");
        c.setFullName(s.nextLine());

        System.out.print("Enter new phone number: ");
        c.setPhoneNumber(s.nextLine());

        cdao.update(c);

        System.out.println("Customer updated successfully!");
    }

    void addCustomer() throws SQLException
    {
        Scanner s = new Scanner(System.in);
        Customer c = new Customer();

        System.out.println("Adding NEW Customer!");

        System.out.print("Enter full name: ");
        c.setFullName(s.nextLine());

        System.out.print("Enter phone number: ");
        c.setPhoneNumber(s.nextLine());

        c.setType(CustomerType.NEW);

        cdao.add(c);

        System.out.println("Customer added successfully!");
    }

    void showCustomers() throws SQLException
    {
        List<Customer> customers = cdao.getList();

        System.out.println("All customers: ");
        for (Customer c : customers)
        {
            System.out.println(c);
        }

        System.out.println("Total customers: " + customers.size());
    }

    void deleteCustomer() throws SQLException
    {
        Scanner s = new Scanner(System.in);

        System.out.println("Deleting EXISTING Customer!");

        System.out.print("Enter customer ID: ");
        int id = s.nextInt();

        cdao.delete(id);

        System.out.println("Customer deleted successfully!");
    }


    public static void main(String[] args)
    {
        CustomerMenu cm = new CustomerMenu();
        cm.showMenu();
    }

}
