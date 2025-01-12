import com.storeManagement.dataAccessObject.*;
import com.storeManagement.model.User;

import java.util.Scanner;

import static java.lang.System.exit;

public class Main
{

    public static void main(String[] args)
    {
        UserDao udao = new UserDao();
        BranchDao bdao = new BranchDao();
        CustomerDao cdao = new CustomerDao();
        EmployeeDao edao = new EmployeeDao();
        ProductDao pdao = new ProductDao();
        SaleDao sdao = new SaleDao();

        User loggedUser = new User();

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
                    System.out.println("\n\nLOGIN");
                    System.out.print("Enter username: ");
                    String username = s.next();
                    System.out.print("Enter password: ");
                    String password = s.next();

                    try
                    {
                        if (udao.authenticateUser(username, password))
                        {
                            loggedUser = udao.getByUsername(username);
                            System.out.println("Login successful");
                            choice = -1;
                        } else
                        {
                            System.out.println("Invalid username or password");
                        }
                    } catch (Exception e)
                    {
                        System.out.println("An error occurred while logging in");
                    }

                    break;
                case 2:
                    System.out.println("Exiting...");
                    exit(0);
                    break;
                default:
                    System.out.println("Invalid choice");
            }
        } while (choice != -1);


    }
}