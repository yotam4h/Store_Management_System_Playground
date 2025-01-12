package com.storeManagement.menus;

import com.storeManagement.dataAccessObject.UserDao;

import java.sql.SQLException;
import java.util.Scanner;

public class LoginMenu
{
    UserDao udao = new UserDao();

    public void showMenu()
    {
        Scanner s = new Scanner(System.in);
        int choice;

        do
        {
            System.out.println("\n\nLOGIN MENU");
            System.out.println("1. Login");
            System.out.println("2. Exit");

            System.out.print("Enter choice: ");
            choice = s.nextInt();

            switch (choice)
            {
                case 1:
                    login();
                    break;
                case 2:
                    System.out.println("Exiting...");
                    break;
                default:
                    System.out.println("Invalid choice");
            }
        } while (choice != 2);
    }

    public void login()
    {
        Scanner s = new Scanner(System.in);
        String username, password;

        System.out.println("\n\nLOGIN");
        System.out.print("Enter username: ");
        username = s.nextLine();
        System.out.print("Enter password: ");
        password = s.nextLine();

        try
        {
            if (udao.authenticateUser(username, password))
            {
                System.out.println("Login successful");
            } else
            {
                System.out.println("Login failed");
            }
        } catch (SQLException e)
        {
            System.out.println("An error occurred while logging in");
        }
    }

    public static void main(String[] args)
    {
        LoginMenu lm = new LoginMenu();
        lm.showMenu();
    }
}
