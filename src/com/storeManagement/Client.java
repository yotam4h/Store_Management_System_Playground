package com.storeManagement;

import com.storeManagement.dataAccessObject.UserDao;
import com.storeManagement.dataAccessObject.BranchDao;
import com.storeManagement.dataAccessObject.ProductDao;
import com.storeManagement.dataAccessObject.CustomerDao;
import com.storeManagement.dataAccessObject.EmployeeDao;
import com.storeManagement.model.Branch;
import com.storeManagement.model.User;
import com.storeManagement.model.Product;
import com.storeManagement.model.Customer;
import com.storeManagement.model.Employee;
import com.storeManagement.model.Sale;
import com.storeManagement.utils.Constants;

import java.io.*;
import java.net.*;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import static java.lang.System.exit;

public class Client
{
    ObjectInputStream sInput;
    ObjectOutputStream sOutput;
    Socket socket;
    String server, username, password;
    Constants.EmployeeRole role;
    int port;

    Client(String server, int port)
    {
        this.server = server;
        this.port = port;
    }

    private void display(String msg)
    {
        System.out.println(msg);
    }

    public boolean start()
    {
        try
        {
            socket = new Socket(server, port);
        }
        catch(Exception ec)
        {
            display("Error connecting to server:" + ec);
            return false;
        }
//        display("Connection accepted " + socket.getInetAddress() + ":" + socket.getPort());
        try
        {
            sInput = new ObjectInputStream(socket.getInputStream());
            sOutput = new ObjectOutputStream(socket.getOutputStream());
        }
        catch (IOException eIO)
        {
            display("Exception creating new Input/output Streams: " + eIO);
            return false;
        }

        try
        {
            Scanner s = new Scanner(System.in);

            display("\n\nLOGIN");

            display("Enter your username: ");
            username = s.nextLine();
            display("Enter your password: ");
            password = s.nextLine();

            sOutput.writeObject(username);
            sOutput.writeObject(password);

            String response = (String) sInput.readObject();
            switch (response)
            {
                case "INVALID":
                    display("Invalid username or password. Please try again.");
                    disconnect();
                    return false;
                case "ALREADY_LOGGED_IN":
                    display("User is already logged in. Please try again later.");
                    disconnect();
                    return false;
                case "SUCCESS":
                    display("Login successful.");
                    break;
                default:
                    display("Unknown response from server: " + response);
                    disconnect();
                    return false;
            }

            String role = (String) sInput.readObject();
            switch (role)
            {
                case "ADMIN":
                    this.role = Constants.EmployeeRole.ADMIN;
                    break;
                case "MANAGER":
                    this.role = Constants.EmployeeRole.MANAGER;
                    break;
                case "EMPLOYEE":
                    this.role = Constants.EmployeeRole.EMPLOYEE;
                    break;
            }

//            s.close();
        }
        catch (IOException eIO)
        {
            display("Exception doing login: " + eIO);
            disconnect();
            return false;
        } catch (ClassNotFoundException e)
        {
            e.printStackTrace();
        }

        new ListenFromServer().start();

        return true;
    }

    private void sendMessage(String msg)
    {
        try
        {
            sOutput.writeObject(msg);
        }
        catch(IOException e)
        {
            display("Exception writing to server: " + e);
        }
    }

    public void disconnect()
    {
        // SEND TO SERVER THAT USER IS DISCONNECTING
        sendMessage("DISCONNECT");

        try
        {
            if(sInput != null) sInput.close();
        }
        catch(Exception e) {}
        try
        {
            if(sOutput != null) sOutput.close();
        }
        catch(Exception e) {};
        try
        {
            if(socket != null) socket.close();
        }
        catch(Exception e) {}
    }

    public static void main(String[] args)
    {
        int portNumber = 1500;
        String serverAddress = "localhost";
        Client client = null;
        boolean loggedIn = false;

        while(!loggedIn)
        {
            client = new Client(serverAddress, portNumber);
            if(client.start())
                loggedIn = true;
        }

        switch (client.role.toString())
        {
            case "ADMIN":
                adminMenu();
                break;
            case "MANAGER":
                managerMenu();
                break;
            case "EMPLOYEE":
                employeeMenu();
                break;
            default:
                System.out.println("Unknown role.");
                break;
        }



        client.disconnect();
        exit(0);
    }

    class ListenFromServer extends Thread
    {
        public void run()
        {
            while(true)
            {
                try
                {
                    String msg = (String) sInput.readObject();
                    System.out.println(msg);
                }
                catch(IOException e)
                {
                    display("Server has closed the connection: " + e);
                    break;
                }
                catch(ClassNotFoundException e2)
                {
                    display("Class not found: " + e2);
                }
            }
        }
    }


    static void adminMenu()
    {
        Scanner s = new Scanner(System.in);
        int choice = 0;

        while(choice != 9)
        {
            System.out.println("\n\nADMIN MENU");
            System.out.println("1. User");
            System.out.println("2. Product");
            System.out.println("3. Customer");
            System.out.println("4. Report");
            System.out.println("5. Sale");
            System.out.println("6. Chat");
            System.out.println("7. Branch");
            System.out.println("8. Exit");

            System.out.print("Enter your choice: ");
            choice = s.nextInt();

            switch (choice)
            {
                case 1:
                    userMenu();
                    break;
                case 2:
                    productMenu();
                    break;
                case 3:
                    customerMenu();
                    break;
                case 4:
                    reportMenu();
                    break;
                case 5:
                    saleMenu();
                    break;
                case 6:
                    chatMenu();
                    break;
                case 7:
                    branchMenu();
                    break;
                case 8:
                    System.out.println("\n\nEXITING...");
                    choice = 9;
                    break;
                default:
                    System.out.println("Invalid choice.");
                    break;
            }
        }
    }

    static void managerMenu()
    {}

    static void employeeMenu()
    {}

    static void productMenu()
    {}

    static void customerMenu()
    {}

    static void reportMenu()
    {}

    static void saleMenu()
    {}

    static void chatMenu()
    {}

    static void branchMenu()
    {
        BranchDao branchDao = new BranchDao();
        Scanner s = new Scanner(System.in);
        int choice = 0;

        while (choice != 5)
        {
            System.out.println("\n\nADMIN MENU");
            System.out.println("1. Add branch");
            System.out.println("2. Update branch");
            System.out.println("3. Delete branch");
            System.out.println("4. View branches");
            System.out.println("5. Exit");

            System.out.print("Enter your choice: ");
            choice = s.nextInt();

            switch (choice)
            {
                case 1:
                    System.out.println("\n\nADD BRANCH");
                    Branch newBranch = new Branch();
                    newBranch.setName();
                    newBranch.setAddress();
                    newBranch.setPhone();
                    break;
                case 2:
                    System.out.println("\n\nUPDATE BRANCH");
                    System.out.println("Enter the branch id: ");
                    int id = s.nextInt();
                    s.nextLine();
                    Branch branch = null;
                    try
                    {
                        branch = branchDao.get(id);
                    }
                    catch (SQLException e)
                    {
                        e.printStackTrace();
                    }
                    if (branch == null)
                    {
                        System.out.println("Branch not found.");
                        break;
                    }
                    System.out.println("Current branch details: " + branch.toString());
                    branch.setName();
                    branch.setAddress();
                    branch.setPhone();
                    try
                    {
                        branchDao.update(branch);
                    }
                    catch (SQLException e)
                    {
                        e.printStackTrace();
                    }
                    break;
                case 3:
                    System.out.println("\n\nDELETE BRANCH");
                    System.out.println("Enter the branch id: ");
                    id = s.nextInt();
                    try
                    {
                        branchDao.delete(id);
                    }
                    catch (SQLException e)
                    {
                        e.printStackTrace();
                    }
                    break;
                case 4:
                    System.out.println("\n\nVIEW BRANCHES");
                    List<Branch> branches = null;
                    try
                    {
                        branches = branchDao.getList();
                    }
                    catch (SQLException e)
                    {
                        e.printStackTrace();
                    }
                    if (branches == null)
                    {
                        System.out.println("No branches found.");
                        break;
                    }
                    for (Branch b : branches)
                    {
                        System.out.println(b.toString());
                    }
                    break;
                case 5:
                    System.out.println("\n\nEXITING...");
                    choice = 5;
                    break;
                default:
                    System.out.println("Invalid choice.");
                    break;
            }
        }
    }

    static void userMenu()
    {
        UserDao userDao = new UserDao();
        Scanner s = new Scanner(System.in);
        int choice = 0;

        while(choice != 5)
        {
            System.out.println("\n\nADMIN MENU");
            System.out.println("1. Add user");
            System.out.println("2. Update user");
            System.out.println("3. Delete user");
            System.out.println("4. View users");
            System.out.println("5. Exit");

            System.out.print("Enter your choice: ");
            choice = s.nextInt();


            switch (choice)
            {
                case 1:
                    System.out.println("\n\nADD USER");
                    User newUser = new User();
                    newUser.setUsername();
                    newUser.setPasswordHash();
                    newUser.setRole();
                    newUser.setBranchId();
                    try
                    {
                        userDao.add(newUser);
                    }
                    catch (SQLException e)
                    {
                        e.printStackTrace();
                    }
                    break;
                case 2:
                    System.out.println("\n\nUPDATE USER");
                    System.out.println("Enter the user id: ");
                    int id = s.nextInt();
                    s.nextLine();
                    User user = null;
                    try
                    {
                        user = userDao.get(id);
                    } catch (SQLException e)
                    {
                        e.printStackTrace();
                    }
                    if (user == null)
                    {
                        System.out.println("User not found.");
                        break;
                    }
                    System.out.println("Current user details: " + user.toString());
                    user.setUsername();
                    user.setPasswordHash();
                    user.setRole();
                    user.setBranchId();
                    try
                    {
                        userDao.update(user);
                    }
                    catch (SQLException e)
                    {
                        e.printStackTrace();
                    }
                    break;
                case 3:
                    System.out.println("\n\nDELETE USER");
                    System.out.println("Enter the user id: ");
                    id = s.nextInt();
                    try
                    {
                        userDao.delete(id);
                    }
                    catch (SQLException e)
                    {
                        e.printStackTrace();
                    }
                    break;
                case 4:
                    System.out.println("\n\nVIEW USERS");
                    List<User> users = null;
                    try
                    {
                        users = userDao.getList();
                    }
                    catch (SQLException e)
                    {
                        e.printStackTrace();
                    }
                    if (users == null)
                    {
                        System.out.println("No users found.");
                        break;
                    }
                    for (User u : users)
                    {
                        System.out.println(u.toString());
                    }
                    break;
                case 5:
                    System.out.println("\n\nEXITING...");
                    choice = 5;
                    break;
                default:
                    System.out.println("Invalid choice.");
                    break;
            }
        }
    }
}