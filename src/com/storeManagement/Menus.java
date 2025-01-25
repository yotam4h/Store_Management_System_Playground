package com.storeManagement;

import com.storeManagement.dataAccessObject.*;
import com.storeManagement.model.*;
import com.storeManagement.utils.Constants;

import java.io.FileWriter;
import java.io.IOException;
import java.sql.SQLException;
import java.util.InputMismatchException;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Scanner;

public class Menus {
    public  void displayMenu(Client client) {
        switch (client.getRole()) {
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
    }

    void adminMenu(Client client)
    {
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
            choice = getChoice();

            switch (choice)
            {
                case 1:
                    userDaoMenu();
                    break;
                case 2:
                    branchDaoMenu();
                    break;
                case 3:
                    employeeDaoMenu(Constants.EmployeeRole.ADMIN, 0);
                    break;
                case 4:
                    productDaoMenu(0);
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

    void managerMenu(Client client)
    {
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
            choice = getChoice();

            switch (choice)
            {
                case 1:
                    employeeDaoMenu(Constants.EmployeeRole.EMPLOYEE, client.getBranchId());
                    break;
                case 2:
                    productDaoMenu(client.getBranchId());
                    break;
                case 3:
                    customerDaoMenu();
                    break;
                case 4:
                    saleDaoMenu(client.getBranchId());
                    break;
                case 5:
                    reportMenu(client.getBranchId());
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

    void employeeMenu(Client client)
    {
        int choice = 0;

        while(choice != 4)
        {
            System.out.println("\n\nEMPLOYEE MENU");
            System.out.println("1. Customer");
            System.out.println("2. Sale");
            System.out.println("3. Chat");
            System.out.println("4. Exit");

            System.out.print("Enter your choice: ");
            choice = getChoice();

            switch (choice)
            {
                case 1:
                    customerDaoMenu();
                    break;
                case 2:
                    saleDaoMenu(client.getBranchId());
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

    void chatMenu(Client client)
    {
        // send to server "READY"
        client.sendMessage("READY");
        Scanner s = new Scanner(System.in);
        String line = "";

        System.out.println("Waiting for chat to start...");
        System.out.println("Type 'CHAT <username>' to start a chat with a user.");
        System.out.println("Type 'EXIT' to exit chat.");

        ListenFromServer listenFromServer = new ListenFromServer(client);
        listenFromServer.start();

        while (!line.equalsIgnoreCase("EXIT"))
        {
            try
            {
                line = s.nextLine();
                client.sendMessage(line);
            }
            catch (Exception e)
            {
                System.out.println("Error sending message.");
                break;
            }

            if (!listenFromServer.isAlive())
                break;
        }

        if (listenFromServer.isAlive())
            listenFromServer.interrupt();

        try {
            listenFromServer.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

    }

    void reportMenu(int branchId)
    {
        SaleDao saleDao = new SaleDao();
        ProductDao productDao = new ProductDao();
        Scanner s = new Scanner(System.in);
        int choice = 0;

        while(choice != 7)
        {
            System.out.println("\n\nREPORT MENU");
            System.out.println("1. Sales by branch");
            System.out.println("2. Sales by product");
            System.out.println("3. Sales by category");
            System.out.println("4. Export Branch Report");
            System.out.println("5. Export Product Report");
            System.out.println("6. Export Category Report");
            System.out.println("7. Exit");

            System.out.print("Enter your choice: ");
            choice = getChoice();

            switch(choice) {
                case 1: {
                    System.out.println("\n\nSALES BY BRANCH");
                    List<Sale> sales;
                    try {
                        if (branchId == 0)
                        {
                            Branch branch = new Branch();
                            branch.setId();
                            sales = saleDao.getSalesByBranch(branch.getId());
                        }
                        else
                            sales = saleDao.getSalesByBranch(branchId);
                    } catch (SQLException e) {
                        System.out.println(e.getMessage());
                        break;
                    }
                    double totalSales = 0;
                    double totalSum = 0;
                    for (Sale sale : sales) {
                        Product product;
                        try {
                            product = productDao.get(sale.getProductId());
                        } catch (SQLException e) {
                            System.out.println(e.getMessage());
                            System.out.println("Error getting product.");
                            break;
                        }
                        totalSales += sale.getQuantity();
                        totalSum += sale.getTotalPrice();

                        System.out.println("************************");
                        System.out.println("Product: " + product.getName());
                        System.out.println("Quantity: " + sale.getQuantity());
                        System.out.println("Sum: " + sale.getTotalPrice());

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
                    List<Sale> sales;
                    try {
                        sales = saleDao.getSalesByProduct(productId);
                    } catch (SQLException e) {
                        System.out.println(e.getMessage());
                        break;
                    }
                    double totalSales = 0;
                    double totalSum = 0;
                    for (Sale sale : sales) {
                        Product product;
                        try {
                            product = productDao.get(sale.getProductId());
                        } catch (SQLException e) {
                            System.out.println(e.getMessage());
                            break;
                        }

                        totalSales += sale.getQuantity();
                        totalSum += sale.getTotalPrice();

                        System.out.println("************************");
                        System.out.println("Product: " + product.getName());
                        System.out.println("Quantity: " + sale.getQuantity());
                        System.out.println("Sum: " + sale.getTotalPrice());
                    }

                    System.out.println("************************");
                    System.out.println("Total sales: " + totalSales);
                    System.out.println("Total sum: " + totalSum);
                }
                break;
                case 3: {
                    System.out.println("\n\nSALES BY CATEGORY");
                    Product product = new Product();
                    product.setCategory();

                    List<Sale> sales;
                    try {
                        sales = saleDao.getSalesByCategory(product.getCategory());
                    } catch (SQLException e) {
                        System.out.println(e.getMessage());
                        break;
                    }
                    double totalSales = 0;
                    double totalSum = 0;
                    for (Sale sale : sales) {
                        try {
                            product = productDao.get(sale.getProductId());
                        } catch (SQLException e) {
                            System.out.println(e.getMessage());
                            break;
                        }
                        totalSales += sale.getQuantity();
                        totalSum += sale.getTotalPrice();

                        System.out.println("************************");
                        System.out.println("Product: " + product.getName());
                        System.out.println("Quantity: " + sale.getQuantity());
                        System.out.println("Sum: " + sale.getTotalPrice());
                    }

                    System.out.println("************************");
                    System.out.println("Total sales: " + totalSales);
                    System.out.println("Total sum: " + totalSum);

                    break;
                }
                case 4: { // write to a text file a report by branch id
                    List<Sale> sales = null;
                    try {
                        if (branchId == 0) {
                            Branch branch = new Branch();
                            branch.setId();
                            sales = saleDao.getSalesByBranch(branch.getId());
                        } else {
                            sales = saleDao.getSalesByBranch(branchId);
                        }
                    } catch (SQLException e) {
                        e.printStackTrace();
                    }
                    double totalSales = 0;
                    double totalSum = 0;
                    try (FileWriter writer = new FileWriter("./branch_sales_report.txt")) {
                        writer.write("SALES BY BRANCH:\n");
                        for (Sale sale : sales) {
                            Product product = null;
                            try {
                                product = productDao.get(sale.getProductId());
                            } catch (SQLException e) {
                                System.out.println(e.getMessage());
                                System.out.println("Error getting product.");
                                break;
                            }
                            totalSales += sale.getQuantity();
                            totalSum += sale.getTotalPrice();

                            writer.write("************************\n");
                            writer.write("Product: " + product.getName() + "\n");
                            writer.write("Quantity: " + sale.getQuantity() + "\n");
                            writer.write("Sum: " + sale.getTotalPrice() + "\n");
                        }
                        writer.write("************************\n");
                        writer.write("Total sales: " + totalSales + "\n");
                        writer.write("Total sum: " + totalSum + "\n");
                        System.out.println("Text report created successfully.");
                    } catch (IOException e) {
                        System.out.println("Error writing text report: " + e.getMessage());
                    }
                    break;
                }
                case 5: { // write to a text file a report by product id
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
                    try (FileWriter writer = new FileWriter("./product_" + productId + "_sales_report.txt")) {
                        writer.write("SALES BY PRODUCT " + productId + ":\n");
                        for (Sale sale : sales) {
                            Product product = null;
                            try {
                                product = productDao.get(sale.getProductId());
                            } catch (SQLException e) {
                                System.out.println(e.getMessage());
                                System.out.println("Error getting product.");
                                break;
                            }
                            totalSales += sale.getQuantity();
                            totalSum += sale.getTotalPrice();

                            writer.write("************************\n");
                            writer.write("Product: " + product.getName() + "\n");
                            writer.write("Quantity: " + sale.getQuantity() + "\n");
                            writer.write("Sum: " + sale.getTotalPrice() + "\n");
                        }
                        writer.write("************************\n");
                        writer.write("Total sales: " + totalSales + "\n");
                        writer.write("Total sum: " + totalSum + "\n");
                        System.out.println("Text report created successfully.");
                    } catch (IOException e) {
                        System.out.println("Error writing text report: " + e.getMessage());
                    }
                    break;
                }
                case 6: { // write to a text file a report by category
                    System.out.println("\n\nSALES BY CATEGORY");
                    Product product = new Product();
                    product.setCategory();

                    List<Sale> sales = null;
                    try {
                        sales = saleDao.getSalesByCategory(product.getCategory());
                    } catch (SQLException e) {
                        System.out.println(e.getMessage());
                        break;
                    }
                    double totalSales = 0;
                    double totalSum = 0;
                    try (FileWriter writer = new FileWriter("./category_" + product.getCategory() + "_sales_report.txt")) {
                        writer.write("SALES BY " + product.getCategory() + " CATEGORY:\n");
                        for (Sale sale : sales) {
                            try {
                                product = productDao.get(sale.getProductId());
                            } catch (SQLException e) {
                                System.out.println(e.getMessage());
                                System.out.println("Error getting product.");
                                break;
                            }
                            totalSales += sale.getQuantity();
                            totalSum += sale.getTotalPrice();

                            writer.write("************************\n");
                            writer.write("Product: " + product.getName() + "\n");
                            writer.write("Quantity: " + sale.getQuantity() + "\n");
                            writer.write("Sum: " + sale.getTotalPrice() + "\n");
                        }
                        writer.write("************************\n");
                        writer.write("Total sales: " + totalSales + "\n");
                        writer.write("Total sum: " + totalSum + "\n");
                        System.out.println("Text report created successfully.");
                    } catch (IOException e) {
                        System.out.println("Error writing text report: " + e.getMessage());
                    }
                    break;
                }
                case 7: {
                    System.out.println("\n\nEXITING...");
                    break;
                }
                default:
                    System.out.println("Invalid choice.");
                    break;
            }
        }
    }

    void saleDaoMenu(int branchId)
    {
        SaleDao saleDao = new SaleDao();
        ProductDao productDao = new ProductDao();
        CustomerDao customerDao = new CustomerDao();
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
            choice = getChoice();

            switch (choice) {
                case 1:
                {
                    System.out.println("\n\nADD SALE");
                    Sale newSale = new Sale();
                    newSale.setCustomerId();
                    newSale.setProductId();
                    try
                    {
                        newSale.setQuantity();
                        newSale.setBranchId(branchId);
                        Constants.CustomerType type = Constants.CustomerType.valueOf(customerDao.get(newSale.getCustomerId()).getType());
                        newSale.setTotalPrice(type);

                        // Add sale
                        saleDao.add(newSale);

                        // Update stock quantity
                        Product product = productDao.get(newSale.getProductId());
                        product.setStockQuantity(product.getStockQuantity() - newSale.getQuantity());
                        productDao.update(product);

                        // Update customer type if necessary
                        Customer customer = customerDao.get(newSale.getCustomerId());
                        if (customer.getType().equals(Constants.CustomerType.NEW.toString()))
                        {
                            customer.setType(Constants.CustomerType.RETURNING);
                            customerDao.update(customer);
                        }
                    } catch (SQLException e)
                    {
                        e.printStackTrace();
                    }
                    break;
                }
                case 2:
                {
                    System.out.println("\n\nUPDATE SALE");
                    Sale sale = new Sale();
                    sale.setId();
                    try
                    {
                        sale = saleDao.get(sale.getId());
                    } catch (SQLException e)
                    {
                        System.out.println(e.getMessage());

                    }
                    System.out.println("Current sale details: " + sale.toString());

                    try
                    {
                        int oldQuantity = sale.getQuantity();
                        sale.setCustomerId();
                        sale.setProductId();
                        sale.setQuantity();
                        sale.setBranchId(branchId);

                        Constants.CustomerType type = Constants.CustomerType.valueOf(customerDao.get(sale.getCustomerId()).getType());
                        sale.setTotalPrice(type);

                        saleDao.update(sale);

                        // Update stock quantity
                        Product product = productDao.get(sale.getProductId());
                        product.setStockQuantity(product.getStockQuantity() + oldQuantity - sale.getQuantity());
                        productDao.update(product);

                    } catch (SQLException e)
                    {
                        e.printStackTrace();
                    }
                }
                case 3:
                {
                    System.out.println("\n\nDELETE SALE");
                    Sale sale = new Sale();
                    sale.setId();
                    try
                    {
                        saleDao.delete(sale.getId());
                    } catch (SQLException e)
                    {
                        System.out.println(e.getMessage());
                    }
                    break;
                }
                case 4:
                {
                    System.out.println("\n\nVIEW SALES");
                    List<Sale> sales = null;
                    try
                    {
                        sales = saleDao.getSalesByBranch(branchId);
                    } catch (SQLException e)
                    {
                        System.out.println(e.getMessage());
                        break;
                    }
                    for (Sale sa : sales)
                    {
                        System.out.println(sa.toString());
                    }
                    break;
                }
                case 5:
                {
                    System.out.println("\n\nVIEW PRODUCTS");
                    List<Product> products = null;
                    try
                    {
                        products = productDao.getProductsByBranch(branchId);
                    } catch (SQLException e)
                    {
                        System.out.println(e.getMessage());
                        break;
                    }
                    for (Product p : products)
                    {
                        System.out.println(p.toString());
                    }
                    break;
                }
                case 6:
                    System.out.println("\n\nEXITING...");
                    break;
                default:
                    System.out.println("Invalid choice.");
                    break;
            }
        }
    }

    void employeeDaoMenu(Constants.EmployeeRole role, int branchId)
    {
        EmployeeDao employeeDao = new EmployeeDao();
        int choice = 0;

        while(choice != 5) {
            System.out.println("\n\nEMPLOYEE MENU");
            System.out.println("1. Add employee");
            System.out.println("2. Update employee");
            System.out.println("3. Delete employee");
            System.out.println("4. View employees");
            System.out.println("5. Exit");

            System.out.print("Enter your choice: ");
            choice = getChoice();

            switch (choice) {
                case 1:
                {
                    System.out.println("\n\nADD EMPLOYEE");
                    Employee newEmployee = new Employee();
                    newEmployee.setId();
                    newEmployee.setFullName();
                    newEmployee.setPhoneNumber();

                    if (role == Constants.EmployeeRole.ADMIN)
                    {
                        newEmployee.setRole();
                        newEmployee.setBranchId();
                    }
                    else
                    {
                        newEmployee.setRole(Constants.EmployeeRole.EMPLOYEE);
                        newEmployee.setBranchId(branchId);
                    }

                    try
                    {
                        employeeDao.add(newEmployee);
                    } catch (SQLException e)
                    {
                        e.printStackTrace();
                    }
                    break;
                }
                case 2:
                {
                    System.out.println("\n\nUPDATE EMPLOYEE");
                    Employee employee = new Employee();
                    employee.setId();
                    try {
                        employee = employeeDao.get(employee.getId());
                    } catch (SQLException e) {
                        System.out.println(e.getMessage());
                    }
                    System.out.println("Current employee details: " + employee.toString());
                    employee.setFullName();
                    employee.setPhoneNumber();
                    if (role == Constants.EmployeeRole.ADMIN)
                    {
                        employee.setRole();
                        employee.setBranchId();
                    }
                    else
                    {
                        employee.setRole(Constants.EmployeeRole.EMPLOYEE);
                        employee.setBranchId(branchId);
                    }
                    try {
                        employeeDao.update(employee);
                    } catch (SQLException e) {
                        e.printStackTrace();
                    }
                    break;
                }
                case 3:
                {
                    System.out.println("\n\nDELETE EMPLOYEE");
                    Employee employee = new Employee();
                    employee.setId();
                    try {
                        employeeDao.delete(employee.getId());
                    } catch (SQLException e) {
                        e.printStackTrace();
                    }
                    break;
                }
                case 4:
                {
                    System.out.println("\n\nVIEW EMPLOYEES");
                    List<Employee> employees = null;
                    try {
                        if (role == Constants.EmployeeRole.ADMIN)
                            employees = employeeDao.getList();
                        else
                            employees = employeeDao.getEmployeesByBranch(branchId);
                    } catch (SQLException e) {
                        System.out.println(e.getMessage());
                        break;
                    }
                    for (Employee e : employees) {
                        System.out.println(e.toString());
                    }
                    break;
                }
                case 5:
                    System.out.println("\n\nEXITING...");
                    break;
                default:
                    System.out.println("Invalid choice.");
                    break;
            }
        }
    }

    void productDaoMenu(int branchId)
    {
        ProductDao productDao = new ProductDao();
        int choice = 0;

        while(choice != 5) {
            System.out.println("\n\nPRODUCT MENU");
            System.out.println("1. Add product");
            System.out.println("2. Update product");
            System.out.println("3. Delete product");
            System.out.println("4. View products");
            System.out.println("5. Exit");

            System.out.print("Enter your choice: ");
            choice = getChoice();

            switch (choice)
            {
                case 1:
                {
                    System.out.println("\n\nADD PRODUCT");
                    Product newProduct = new Product();
                    newProduct.setName();
                    newProduct.setCategory();
                    newProduct.setPrice();
                    newProduct.setStockQuantity();

                    if (branchId == 0)
                    {
                        newProduct.setBranchId();
                    } else
                        newProduct.setBranchId(branchId);
                    try
                    {
                        productDao.add(newProduct);
                    } catch (SQLException e)
                    {
                        e.printStackTrace();
                    }
                    break;
                }
                case 2:
                {
                    System.out.println("\n\nUPDATE PRODUCT");
                    Product product = new Product();
                    product.setId();
                    try
                    {
                        product = productDao.get(product.getId());
                    } catch (SQLException e)
                    {
                        System.out.println(e.getMessage());
                    }
                    System.out.println("Current product details: " + product.toString());
                    product.setName();
                    product.setCategory();
                    product.setPrice();
                    product.setStockQuantity();

                    if (branchId == 0)
                    {
                        product.setBranchId();
                    } else
                        product.setBranchId(branchId);

                    try
                    {
                        productDao.update(product);
                    } catch (SQLException e)
                    {
                        e.printStackTrace();
                    }
                    break;
                }
                case 3:
                {
                    System.out.println("\n\nDELETE PRODUCT");
                    Product product = new Product();
                    product.setId();

                    try
                    {
                        productDao.delete(product.getId());
                    } catch (SQLException e)
                    {
                        e.printStackTrace();
                    }
                    break;
                }
                case 4:
                {
                    System.out.println("\n\nVIEW PRODUCTS");
                    List<Product> products = null;
                    try
                    {
                        if (branchId == 0)
                        {
                            products = productDao.getList();
                        } else
                        {
                            products = productDao.getProductsByBranch(branchId);
                        }
                    } catch (SQLException e)
                    {
                        System.out.println(e.getMessage());
                        break;
                    }
                    for (Product p : products)
                    {
                        System.out.println(p.toString());
                    }
                    break;
                }
                case 5:
                    System.out.println("\n\nEXITING...");
                    break;
                default:
                    System.out.println("Invalid choice.");
                    break;
            }
        }
    }

    void customerDaoMenu()
    {
        CustomerDao customerDao = new CustomerDao();
        int choice = 0;

        while (choice != 5) {
            System.out.println("\n\nCUSTOMER MENU");
            System.out.println("1. Add customer");
            System.out.println("2. Update customer");
            System.out.println("3. Delete customer");
            System.out.println("4. View customers");
            System.out.println("5. Exit");

            System.out.print("Enter your choice: ");
            choice = getChoice();

            switch (choice) {
                case 1:
                {
                    System.out.println("\n\nADD CUSTOMER");
                    Customer newCustomer = new Customer();
                    newCustomer.setId();
                    newCustomer.setFullName();
                    newCustomer.setPhoneNumber();
                    newCustomer.setType();
                    try
                    {
                        customerDao.add(newCustomer);
                    } catch (SQLException e)
                    {
                        e.printStackTrace();
                    }
                    break;
                }
                case 2:
                {
                    System.out.println("\n\nUPDATE CUSTOMER");
                    Customer customer = new Customer();
                    customer.setId();
                    try
                    {
                        customer = customerDao.get(customer.getId());
                    } catch (SQLException e)
                    {
                        System.out.println(e.getMessage());
                    }
                    System.out.println("Current customer details: " + customer.toString());
                    customer.setFullName();
                    customer.setPhoneNumber();
                    customer.setType();
                    try
                    {
                        customerDao.update(customer);
                    } catch (SQLException e)
                    {
                        e.printStackTrace();
                    }
                    break;
                }
                case 3:
                {
                    System.out.println("\n\nDELETE CUSTOMER");
                    Customer customer = new Customer();
                    customer.setId();

                    try
                    {
                        customerDao.delete(customer.getId());
                    } catch (SQLException e)
                    {
                        e.printStackTrace();
                    }
                    break;
                }
                case 4:
                {
                    System.out.println("\n\nVIEW CUSTOMERS");
                    List<Customer> customers = null;
                    try
                    {
                        customers = customerDao.getList();
                    } catch (SQLException e)
                    {
                        System.out.println(e.getMessage());
                        break;
                    }
                    for (Customer c : customers)
                    {
                        System.out.println(c.toString());
                    }
                    break;
                }
                case 5:
                    System.out.println("\n\nEXITING...");
                    break;
                default:
                    System.out.println("Invalid choice.");
                    break;
            }
        }
    }

    void branchDaoMenu()
    {
        BranchDao branchDao = new BranchDao();
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
            choice = getChoice();

            switch (choice)
            {
                case 1:
                {
                    System.out.println("\n\nADD BRANCH");
                    Branch newBranch = new Branch();
                    newBranch.setName();
                    newBranch.setAddress();
                    newBranch.setPhone();
                    break;
                }
                case 2:
                {
                    System.out.println("\n\nUPDATE BRANCH");
                    Branch branch = new Branch();
                    branch.setId();

                    try
                    {
                        branch = branchDao.get(branch.getId());
                    } catch (SQLException e)
                    {
                        System.out.println(e.getMessage());
                        break;
                    }
                    System.out.println("Current branch details: " + branch.toString());
                    branch.setName();
                    branch.setAddress();
                    branch.setPhone();
                    try
                    {
                        branchDao.update(branch);
                    } catch (SQLException e)
                    {
                        e.printStackTrace();
                    }
                    break;
                }
                case 3:
                {
                    System.out.println("\n\nDELETE BRANCH");
                    Branch branch = new Branch();
                    branch.setId();
                    try
                    {
                        branchDao.delete(branch.getId());
                    } catch (SQLException e)
                    {
                        e.printStackTrace();
                    }
                    break;
                }
                case 4:
                {
                    System.out.println("\n\nVIEW BRANCHES");
                    List<Branch> branches = null;
                    try
                    {
                        branches = branchDao.getList();
                    } catch (SQLException e)
                    {
                        System.out.println(e.getMessage());
                        break;
                    }
                    for (Branch b : branches)
                    {
                        System.out.println(b.toString());
                    }
                    break;
                }
                case 5:
                    System.out.println("\n\nEXITING...");
                    break;
                default:
                    System.out.println("Invalid choice.");
                    break;
            }
        }
    }

    void userDaoMenu()
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
            choice = getChoice();


            switch (choice)
            {
                case 1:
                {
                    System.out.println("\n\nADD USER");
                    User newUser = new User();
                    newUser.setUsername();
                    newUser.setPasswordHash();
                    newUser.setRole();
                    newUser.setBranchId();
                    try
                    {
                        userDao.add(newUser);
                    } catch (SQLException e)
                    {
                        e.printStackTrace();
                    }
                    break;
                }
                case 2:
                {
                    System.out.println("\n\nUPDATE USER");
                    User user = new User();
                    user.setId();
                    try
                    {
                        user = userDao.get(user.getId());
                    } catch (SQLException e)
                    {
                        System.out.println(e.getMessage());
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
                    } catch (SQLException e)
                    {
                        e.printStackTrace();
                    }
                    break;
                }
                case 3:
                {
                    System.out.println("\n\nDELETE USER");
                    User user = new User();
                    user.setId();

                    try
                    {
                        userDao.delete(user.getId());
                    } catch (SQLException e)
                    {
                        e.printStackTrace();
                    }
                    break;
                }
                case 4:
                {
                    System.out.println("\n\nVIEW USERS");
                    List<User> users = null;
                    try
                    {
                        users = userDao.getList();
                    } catch (SQLException e)
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
                }
                case 5:
                    System.out.println("\n\nEXITING...");
                    break;
                default:
                    System.out.println("Invalid choice.");
                    break;
            }
        }
    }

    int getChoice() {
        boolean valid = false;
        int choice = 0;
        Scanner s = new Scanner(System.in);
        while (!valid) {
            try {
                if (s.hasNextInt()) {
                    choice = s.nextInt();
                    s.nextLine(); // Consume newline
                    valid = true;
                } else {
                    System.out.println("Invalid input. Please enter a number.");
                    s.next(); // Consume the invalid input
                }
            } catch (InputMismatchException e) {
                System.out.println("Must be an integer.");
                s.next(); // Consume the invalid input
            } catch (NoSuchElementException e) {
                System.out.println("No more input available.");
                break;
            }
        }
        return choice;
    }
}