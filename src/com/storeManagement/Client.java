package com.storeManagement;

import com.storeManagement.dataAccessObject.*;
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
    int branchId;
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

            branchId = Integer.parseInt((String) sInput.readObject());
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

//        new ListenFromServer().start();

        return true;
    }

    public void sendMessage(String msg)
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

    public String readMessage()
    {
        try
        {
            return (String) sInput.readObject();
        }
        catch(IOException e)
        {
            display("Exception reading from server: " + e);
        }
        catch(ClassNotFoundException e)
        {
            display("Class not found: " + e);
        }
        return null;
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

    class ListenFromServer extends Thread
    {
        public void run()
        {
            while(true)
            {
                try
                {
                    String msg = (String) sInput.readObject();
                    if(msg.equals("CHAT_ENDED"))
                    {
                        display("Chat ended.");
                        break;
                    }
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
                adminMenu(client);
                break;
            case "MANAGER":
                managerMenu(client);
                break;
            case "EMPLOYEE":
                employeeMenu(client);
                break;
            default:
                System.out.println("Unknown role.");
                break;
        }

        client.disconnect();
        exit(0);
    }

    static void adminMenu(Client client)
    {
        Scanner s = new Scanner(System.in);
        int choice = 0;

        while(choice != 7)
        {
            System.out.println("\n\nADMIN MENU");
            System.out.println("1. User");
            System.out.println("2. Branch");
            System.out.println("3. Employee");
            System.out.println("4. Product");
            System.out.println("5. Customer");
            System.out.println("6. Chat");
            System.out.println("7. Exit");

            System.out.print("Enter your choice: ");
            choice = s.nextInt();

            switch (choice)
            {
                case 1:
                    userDaoMenu();
                    break;
                case 2:
                    branchDaoMenu();
                    break;
                case 3:
                    employeeDaoMenu();
                    break;
                case 4:
                    productDaoMenu();
                    break;
                case 5:
                    customerDaoMenu();
                    break;
                case 6:
                    chatMenu(client);
                    break;
                case 7:
                    System.out.println("\n\nEXITING...");
                    break;
                default:
                    System.out.println("Invalid choice.");
                    break;
            }
        }
    }

    static void managerMenu(Client client)
    {
        Scanner s = new Scanner(System.in);
        int choice = 0;

        while(choice != 7)
        {
            System.out.println("\n\nMANAGER MENU");
            System.out.println("1. Employee");
            System.out.println("2. Product");
            System.out.println("3. Customer");
            System.out.println("4. Sale");
            System.out.println("5. Report");
            System.out.println("6. Chat");
            System.out.println("7. Exit");

            System.out.print("Enter your choice: ");
            choice = s.nextInt();

            switch (choice)
            {
                case 1:
                    employeeDaoMenu(Constants.EmployeeRole.EMPLOYEE, client.branchId);
                    break;
                case 2:
                    productDaoMenu(client.branchId);
                    break;
                case 3:
                    customerDaoMenu();
                    break;
                case 4:
                    saleDaoMenu(client.branchId);
                    break;
                case 5:
                    reportMenu(client.branchId);
                    break;
                case 6:
                    chatMenu(client);
                    break;
                case 7:
                    System.out.println("\n\nEXITING...");
                    break;
                default:
                    System.out.println("Invalid choice.");
                    break;
            }
        }
    }

    static void employeeMenu(Client client)
    {
        Scanner s = new Scanner(System.in);
        int choice = 0;

        while(choice != 5)
        {
            System.out.println("\n\nEMPLOYEE MENU");
            System.out.println("1. Customer");
            System.out.println("2. Sale");
            System.out.println("3. Chat");
            System.out.println("4. Exit");

            System.out.print("Enter your choice: ");
            choice = s.nextInt();

            switch (choice)
            {
                case 1:
                    customerDaoMenu();
                    break;
                case 2:
                    saleDaoMenu(client.branchId);
                    break;
                case 3:
                    chatMenu(client);
                    break;
                case 4:
                    System.out.println("\n\nEXITING...");
                    break;
                default:
                    System.out.println("Invalid choice.");
                    break;
            }
        }
    }

    static void chatMenu(Client client)
    {
        // send to server "READY"
        client.sendMessage("READY");
        DataInputStream console = new DataInputStream(System.in);
        String line = "";

        System.out.println("Waiting for chat to start...");

        System.out.println("Type 'EXIT' to exit.");

        ListenFromServer listenFromServer = client.new ListenFromServer();
        listenFromServer.start();

        while (!line.equalsIgnoreCase("EXIT"))
        {
            try
            {
                line = console.readLine();
                client.sendMessage(line);
            }
            catch (IOException e)
            {
                e.printStackTrace();
            }

            if (!listenFromServer.isAlive())
                break;
        }

        client.sendMessage("NOT_READY");
    }

    static void reportMenu(int branchId)
    {
        SaleDao saleDao = new SaleDao();
        ProductDao productDao = new ProductDao();
        Scanner s = new Scanner(System.in);
        int choice = 0;

        while(choice != 4)
        {
            System.out.println("\n\nREPORT MENU");
            System.out.println("1. Sales by branch");
            System.out.println("2. Sales by product");
            System.out.println("3. Sales by category");
            System.out.println("4. Exit");

            System.out.print("Enter your choice: ");
            choice = s.nextInt();
            s.nextLine();

            switch(choice) {
                case 1: {
                    System.out.println("\n\nSALES BY BRANCH");
                    List<Sale> sales = null;
                    try {
                        sales = saleDao.getSalesByBranch(branchId);
                    } catch (SQLException e) {
                        e.printStackTrace();
                    }
                    double totalSales = 0;
                    double totalSum = 0;
                    for (Sale sale : sales) {
                        Product product = null;
                        try {
                            product = productDao.get(sale.getProductId());
                        } catch (SQLException e) {
                            e.printStackTrace();
                        }
                        totalSales += sale.getQuantity();
                        totalSum += sale.getQuantity() * product.getPrice();

                        System.out.println("************************");
                        System.out.println("Product: " + product.getName());
                        System.out.println("Quantity: " + sale.getQuantity());
                        System.out.println("Sum: " + sale.getQuantity() * product.getPrice());

                    }

                    System.out.println("************************");
                    System.out.println("Total sales: " + totalSales);
                    System.out.println("Total sum: " + totalSum);
                }
                    break;
                case 2: {
                    System.out.println("\n\nSALES BY PRODUCT");
                    System.out.println("Enter the product id: ");
                    int productId = s.nextInt();
                    s.nextLine();
                    List<Sale> sales = null;
                    try {
                        sales = saleDao.getSalesByProduct(productId);
                    } catch (SQLException e) {
                        e.printStackTrace();
                    }
                    double totalSales = 0;
                    double totalSum = 0;
                    for (Sale sale : sales) {
                        Product product = null;
                        try {
                            product = productDao.get(sale.getProductId());
                        } catch (SQLException e) {
                            e.printStackTrace();
                        }
                        totalSales += sale.getQuantity();
                        totalSum += sale.getQuantity() * product.getPrice();

                        System.out.println("************************");
                        System.out.println("Product: " + product.getName());
                        System.out.println("Quantity: " + sale.getQuantity());
                        System.out.println("Sum: " + sale.getQuantity() * product.getPrice());
                    }

                    System.out.println("************************");
                    System.out.println("Total sales: " + totalSales);
                    System.out.println("Total sum: " + totalSum);
                }
                    break;
                case 3: {
                    System.out.println("\n\nSALES BY CATEGORY");
                    boolean validCategory = false;
                    Constants.Category category = null;

                    while (!validCategory)
                    {
                        System.out.println("Choose category: ");
                        for (int i = 0; i < Constants.Category.values().length; i++)
                        {
                            System.out.println((i + 1) + ". " + Constants.Category.values()[i]);
                        }
                        System.out.println("Enter category: ");
                        int catChoice = s.nextInt();
                        s.nextLine();
                        switch (catChoice)
                        {
                            case 1:
                                category = Constants.Category.MEN;
                                validCategory = true;
                                break;
                            case 2:
                                category = Constants.Category.WOMEN;
                                validCategory = true;
                                break;
                            case 3:
                                category = Constants.Category.FOOTWEAR;
                                validCategory = true;
                                break;
                            case 4:
                                category = Constants.Category.ACCESSORIES;
                                validCategory = true;
                                break;
                            case 5:
                                category = Constants.Category.KIDS;
                                validCategory = true;
                                break;
                            case 6:
                                category = Constants.Category.SEASONAL;
                                validCategory = true;
                                break;
                            default:
                                System.out.println("Invalid category.");
                                break;
                        }
                    }
                    List<Sale> sales = null;
                    try {
                        sales = saleDao.getSalesByCategory(category.toString());
                    } catch (SQLException e) {
                        e.printStackTrace();
                    }
                    double totalSales = 0;
                    double totalSum = 0;
                    for (Sale sale : sales) {
                        Product product = null;
                        try {
                            product = productDao.get(sale.getProductId());
                        } catch (SQLException e) {
                            e.printStackTrace();
                        }
                        totalSales += sale.getQuantity();
                        totalSum += sale.getQuantity() * product.getPrice();

                        System.out.println("************************");
                        System.out.println("Product: " + product.getName());
                        System.out.println("Quantity: " + sale.getQuantity());
                        System.out.println("Sum: " + sale.getQuantity() * product.getPrice());
                    }

                    System.out.println("************************");
                    System.out.println("Total sales: " + totalSales);
                    System.out.println("Total sum: " + totalSum);

                    break;
                }
                case 4:
                    System.out.println("\n\nEXITING...");
                    break;
                default:
                    System.out.println("Invalid choice.");
                    break;
            }
        }
    }

    static void saleDaoMenu(int branchId)
    {
        SaleDao saleDao = new SaleDao();
        ProductDao productDao = new ProductDao();
        Scanner s = new Scanner(System.in);
        int choice = 0;

        while (choice != 6)
        {
            System.out.println("\n\nSALE MENU");
            System.out.println("1. Add sale");
            System.out.println("2. Update sale");
            System.out.println("3. Delete sale");
            System.out.println("4. View sales");
            System.out.println("5. View Products");
            System.out.println("6. Exit");

            System.out.print("Enter your choice: ");
            choice = s.nextInt();

            switch (choice) {
                case 1:
                    System.out.println("\n\nADD SALE");
                    Sale newSale = new Sale();
                    newSale.setCustomerId();
                    newSale.setProductId();
                    try {
                        newSale.setQuantity();
                    } catch (SQLException e) {
                        e.printStackTrace();
                    }
                    newSale.setBranchId(branchId);
                    try {
                        saleDao.add(newSale);
                    } catch (SQLException e) {
                        e.printStackTrace();
                    }
                    break;
                case 2:
                    System.out.println("\n\nUPDATE SALE");
                    System.out.println("Enter the sale id: ");
                    int id = s.nextInt();
                    s.nextLine();
                    Sale sale = null;
                    try {
                        sale = saleDao.get(id);
                    } catch (SQLException e) {
                        e.printStackTrace();
                    }
                    if (sale == null) {
                        System.out.println("Sale not found.");
                        break;
                    }
                    System.out.println("Current sale details: " + sale.toString());
                    sale.setCustomerId();
                    sale.setProductId();
                    try {
                        sale.setQuantity();
                    } catch (SQLException e) {
                        e.printStackTrace();
                    }
                    sale.setBranchId(branchId);
                    try {
                        saleDao.update(sale);
                    } catch (SQLException e) {
                        e.printStackTrace();
                    }
                    break;
                case 3:
                    System.out.println("\n\nDELETE SALE");
                    System.out.println("Enter the sale id: ");
                    id = s.nextInt();
                    try {
                        saleDao.delete(id);
                    } catch (SQLException e) {
                        e.printStackTrace();
                    }
                    break;
                case 4:
                    System.out.println("\n\nVIEW SALES");
                    List<Sale> sales = null;
                    try {
                        sales = saleDao.getList();
                    } catch (SQLException e) {
                        e.printStackTrace();
                    }
                    if (sales == null) {
                        System.out.println("No sales found.");
                        break;
                    }
                    for (Sale sa : sales) {
                        System.out.println(sa.toString());
                    }
                    break;
                case 5:
                    System.out.println("\n\nVIEW PRODUCTS");
                    List<Product> products = null;
                    try {
                        products = productDao.getList();
                    } catch (SQLException e) {
                        e.printStackTrace();
                    }
                    if (products == null) {
                        System.out.println("No products found.");
                        break;
                    }
                    for (Product p : products) {
                        if(p.getBranchId() == branchId)
                            System.out.println(p.toString());
                    }
                    break;
                case 6:
                    System.out.println("\n\nEXITING...");
                    break;
                default:
                    System.out.println("Invalid choice.");
                    break;
            }
        }
    }

    static void employeeDaoMenu(Constants.EmployeeRole role, int branchId)
    {
        EmployeeDao employeeDao = new EmployeeDao();
        Scanner s = new Scanner(System.in);
        int choice = 0;

        while(choice != 5) {
            System.out.println("\n\nEMPLOYEE MENU");
            System.out.println("1. Add employee");
            System.out.println("2. Update employee");
            System.out.println("3. Delete employee");
            System.out.println("4. View employees");
            System.out.println("5. Exit");

            System.out.print("Enter your choice: ");
            choice = s.nextInt();

            switch (choice) {
                case 1:
                    System.out.println("\n\nADD EMPLOYEE");
                    Employee newEmployee = new Employee();
                    newEmployee.setFullName();
                    newEmployee.setPhoneNumber();
                    newEmployee.setRole(role);
                    newEmployee.setBranchId(branchId);
                    try {
                        employeeDao.add(newEmployee);
                    } catch (SQLException e) {
                        e.printStackTrace();
                    }
                    break;
                case 2:
                    System.out.println("\n\nUPDATE EMPLOYEE");
                    System.out.println("Enter the employee id: ");
                    int id = s.nextInt();
                    s.nextLine();
                    Employee employee = null;
                    try {
                        employee = employeeDao.get(id);
                    } catch (SQLException e) {
                        e.printStackTrace();
                    }
                    if (employee == null) {
                        System.out.println("Employee not found.");
                        break;
                    }
                    System.out.println("Current employee details: " + employee.toString());
                    employee.setFullName();
                    employee.setPhoneNumber();
                    employee.setRole(role);
                    employee.setBranchId(branchId);
                    try {
                        employeeDao.update(employee);
                    } catch (SQLException e) {
                        e.printStackTrace();
                    }
                    break;
                case 3:
                    System.out.println("\n\nDELETE EMPLOYEE");
                    System.out.println("Enter the employee id: ");
                    id = s.nextInt();
                    try {
                        employeeDao.delete(id);
                    } catch (SQLException e) {
                        e.printStackTrace();
                    }
                    break;
                case 4:
                    System.out.println("\n\nVIEW EMPLOYEES");
                    List<Employee> employees = null;
                    try {
                        employees = employeeDao.getList();
                    } catch (SQLException e) {
                        e.printStackTrace();
                    }
                    if (employees == null) {
                        System.out.println("No employees found.");
                        break;
                    }
                    for (Employee e : employees) {
                        System.out.println(e.toString());
                    }
                    break;
                case 5:
                    System.out.println("\n\nEXITING...");
                    break;
                default:
                    System.out.println("Invalid choice.");
                    break;
            }
        }
    }

    static void employeeDaoMenu()
    {
        EmployeeDao employeeDao = new EmployeeDao();
        Scanner s = new Scanner(System.in);
        int choice = 0;

        while(choice != 5) {
            System.out.println("\n\nEMPLOYEE MENU");
            System.out.println("1. Add employee");
            System.out.println("2. Update employee");
            System.out.println("3. Delete employee");
            System.out.println("4. View employees");
            System.out.println("5. Exit");

            System.out.print("Enter your choice: ");
            choice = s.nextInt();

            switch (choice) {
                case 1:
                    System.out.println("\n\nADD EMPLOYEE");
                    Employee newEmployee = new Employee();
                    newEmployee.setFullName();
                    newEmployee.setPhoneNumber();
                    newEmployee.setRole();
                    newEmployee.setBranchId();
                    try {
                        employeeDao.add(newEmployee);
                    } catch (SQLException e) {
                        e.printStackTrace();
                    }
                    break;
                case 2:
                    System.out.println("\n\nUPDATE EMPLOYEE");
                    System.out.println("Enter the employee id: ");
                    int id = s.nextInt();
                    s.nextLine();
                    Employee employee = null;
                    try {
                        employee = employeeDao.get(id);
                    } catch (SQLException e) {
                        e.printStackTrace();
                    }
                    if (employee == null) {
                        System.out.println("Employee not found.");
                        break;
                    }
                    System.out.println("Current employee details: " + employee.toString());
                    employee.setFullName();
                    employee.setPhoneNumber();
                    employee.setRole();
                    employee.setBranchId();
                    try {
                        employeeDao.update(employee);
                    } catch (SQLException e) {
                        e.printStackTrace();
                    }
                    break;
                case 3:
                    System.out.println("\n\nDELETE EMPLOYEE");
                    System.out.println("Enter the employee id: ");
                    id = s.nextInt();
                    try {
                        employeeDao.delete(id);
                    } catch (SQLException e) {
                        e.printStackTrace();
                    }
                    break;
                case 4:
                    System.out.println("\n\nVIEW EMPLOYEES");
                    List<Employee> employees = null;
                    try {
                        employees = employeeDao.getList();
                    } catch (SQLException e) {
                        e.printStackTrace();
                    }
                    if (employees == null) {
                        System.out.println("No employees found.");
                        break;
                    }
                    for (Employee e : employees) {
                        System.out.println(e.toString());
                    }
                    break;
                case 5:
                    System.out.println("\n\nEXITING...");
                    break;
                default:
                    System.out.println("Invalid choice.");
                    break;
            }
        }
    }

    static void productDaoMenu(int branchId)
    {
        ProductDao productDao = new ProductDao();
        Scanner s = new Scanner(System.in);
        int choice = 0;

        while(choice != 5) {
            System.out.println("\n\nPRODUCT MENU");
            System.out.println("1. Add product");
            System.out.println("2. Update product");
            System.out.println("3. Delete product");
            System.out.println("4. View products");
            System.out.println("5. Exit");

            System.out.print("Enter your choice: ");
            choice = s.nextInt();

            switch (choice) {
                case 1:
                    System.out.println("\n\nADD PRODUCT");
                    Product newProduct = new Product();
                    newProduct.setName();
                    newProduct.setCategory();
                    newProduct.setPrice();
                    newProduct.setStockQuantity();
                    newProduct.setBranchId(branchId);
                    try {
                        productDao.add(newProduct);
                    } catch (SQLException e) {
                        e.printStackTrace();
                    }
                    break;
                case 2:
                    System.out.println("\n\nUPDATE PRODUCT");
                    System.out.println("Enter the product id: ");
                    int id = s.nextInt();
                    s.nextLine();
                    Product product = null;
                    try {
                        product = productDao.get(id);
                    } catch (SQLException e) {
                        e.printStackTrace();
                    }
                    if (product == null) {
                        System.out.println("Product not found.");
                        break;
                    }
                    System.out.println("Current product details: " + product.toString());
                    product.setName();
                    product.setCategory();
                    product.setPrice();
                    product.setStockQuantity();
                    product.setBranchId(branchId);
                    try {
                        productDao.update(product);
                    } catch (SQLException e) {
                        e.printStackTrace();
                    }
                    break;
                case 3:
                    System.out.println("\n\nDELETE PRODUCT");
                    System.out.println("Enter the product id: ");
                    id = s.nextInt();
                    try {
                        productDao.delete(id);
                    } catch (SQLException e) {
                        e.printStackTrace();
                    }
                    break;
                case 4:
                    System.out.println("\n\nVIEW PRODUCTS");
                    List<Product> products = null;
                    try {
                        products = productDao.getList();
                    } catch (SQLException e) {
                        e.printStackTrace();
                    }
                    if (products == null) {
                        System.out.println("No products found.");
                        break;
                    }
                    for (Product p : products) {
                        System.out.println(p.toString());
                    }
                    break;
                case 5:
                    System.out.println("\n\nEXITING...");
                    break;
                default:
                    System.out.println("Invalid choice.");
                    break;
            }
        }
    }

    static void productDaoMenu()
    {
        ProductDao productDao = new ProductDao();
        Scanner s = new Scanner(System.in);
        int choice = 0;

        while(choice != 5) {
            System.out.println("\n\nPRODUCT MENU");
            System.out.println("1. Add product");
            System.out.println("2. Update product");
            System.out.println("3. Delete product");
            System.out.println("4. View products");
            System.out.println("5. Exit");

            System.out.print("Enter your choice: ");
            choice = s.nextInt();

            switch (choice) {
                case 1:
                    System.out.println("\n\nADD PRODUCT");
                    Product newProduct = new Product();
                    newProduct.setName();
                    newProduct.setCategory();
                    newProduct.setPrice();
                    newProduct.setStockQuantity();
                    newProduct.setBranchId();
                    try {
                        productDao.add(newProduct);
                    } catch (SQLException e) {
                        e.printStackTrace();
                    }
                    break;
                case 2:
                    System.out.println("\n\nUPDATE PRODUCT");
                    System.out.println("Enter the product id: ");
                    int id = s.nextInt();
                    s.nextLine();
                    Product product = null;
                    try {
                        product = productDao.get(id);
                    } catch (SQLException e) {
                        e.printStackTrace();
                    }
                    if (product == null) {
                        System.out.println("Product not found.");
                        break;
                    }
                    System.out.println("Current product details: " + product.toString());
                    product.setName();
                    product.setCategory();
                    product.setPrice();
                    product.setStockQuantity();
                    product.setBranchId();
                    try {
                        productDao.update(product);
                    } catch (SQLException e) {
                        e.printStackTrace();
                    }
                    break;
                case 3:
                    System.out.println("\n\nDELETE PRODUCT");
                    System.out.println("Enter the product id: ");
                    id = s.nextInt();
                    try {
                        productDao.delete(id);
                    } catch (SQLException e) {
                        e.printStackTrace();
                    }
                    break;
                case 4:
                    System.out.println("\n\nVIEW PRODUCTS");
                    List<Product> products = null;
                    try {
                        products = productDao.getList();
                    } catch (SQLException e) {
                        e.printStackTrace();
                    }
                    if (products == null) {
                        System.out.println("No products found.");
                        break;
                    }
                    for (Product p : products) {
                        System.out.println(p.toString());
                    }
                    break;
                case 5:
                    System.out.println("\n\nEXITING...");
                    break;
                default:
                    System.out.println("Invalid choice.");
                    break;
            }
        }
    }

    static void customerDaoMenu()
    {
        CustomerDao customerDao = new CustomerDao();
        Scanner s = new Scanner(System.in);
        int choice = 0;

        while (choice != 5) {
            System.out.println("\n\nCUSTOMER MENU");
            System.out.println("1. Add customer");
            System.out.println("2. Update customer");
            System.out.println("3. Delete customer");
            System.out.println("4. View customers");
            System.out.println("5. Exit");

            System.out.print("Enter your choice: ");
            choice = s.nextInt();

            switch (choice) {
                case 1:
                    System.out.println("\n\nADD CUSTOMER");
                    Customer newCustomer = new Customer();
                    newCustomer.setFullName();
                    newCustomer.setPhoneNumber();
                    newCustomer.setType();
                    try {
                        customerDao.add(newCustomer);
                    } catch (SQLException e) {
                        e.printStackTrace();
                    }
                    break;
                case 2:
                    System.out.println("\n\nUPDATE CUSTOMER");
                    System.out.println("Enter the customer id: ");
                    int id = s.nextInt();
                    s.nextLine();
                    Customer customer = null;
                    try {
                        customer = customerDao.get(id);
                    } catch (SQLException e) {
                        e.printStackTrace();
                    }
                    if (customer == null) {
                        System.out.println("Customer not found.");
                        break;
                    }
                    System.out.println("Current customer details: " + customer.toString());
                    customer.setFullName();
                    customer.setPhoneNumber();
                    customer.setType();
                    try {
                        customerDao.update(customer);
                    } catch (SQLException e) {
                        e.printStackTrace();
                    }
                    break;
                case 3:
                    System.out.println("\n\nDELETE CUSTOMER");
                    System.out.println("Enter the customer id: ");
                    id = s.nextInt();
                    try {
                        customerDao.delete(id);
                    } catch (SQLException e) {
                        e.printStackTrace();
                    }
                    break;
                case 4:
                    System.out.println("\n\nVIEW CUSTOMERS");
                    List<Customer> customers = null;
                    try {
                        customers = customerDao.getList();
                    } catch (SQLException e) {
                        e.printStackTrace();
                    }
                    if (customers == null) {
                        System.out.println("No customers found.");
                        break;
                    }
                    for (Customer c : customers) {
                        System.out.println(c.toString());
                    }
                    break;
                case 5:
                    System.out.println("\n\nEXITING...");
                    break;
                default:
                    System.out.println("Invalid choice.");
                    break;
            }
        }
    }

    static void branchDaoMenu()
    {
        BranchDao branchDao = new BranchDao();
        Scanner s = new Scanner(System.in);
        int choice = 0;

        while (choice != 5)
        {
            System.out.println("\n\nBRANCH MENU");
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
                    break;
                default:
                    System.out.println("Invalid choice.");
                    break;
            }
        }
    }

    static void userDaoMenu()
    {
        UserDao userDao = new UserDao();
        Scanner s = new Scanner(System.in);
        int choice = 0;

        while(choice != 5)
        {
            System.out.println("\n\nUSER MENU");
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
                    break;
                default:
                    System.out.println("Invalid choice.");
                    break;
            }
        }
    }
}