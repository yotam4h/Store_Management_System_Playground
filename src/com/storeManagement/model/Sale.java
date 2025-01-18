package com.storeManagement.model;

import com.storeManagement.dataAccessObject.ProductDao;

import java.sql.SQLException;
import java.util.Scanner;

public class Sale
{
    int id;
    int customer_id;
    int product_id;
    int quantity;
    int branch_id;
    java.sql.Timestamp sale_date;

    public Sale() {}

    public Sale(int customer_id, int product_id, int quantity, int branch_id)
    {
        setCustomerId(customer_id);
        setProductId(product_id);
        try {
            setQuantity(quantity);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        setBranchId(branch_id);
    }

    public Sale(int id, int customer_id, int product_id, int quantity, int branch_id)
    {
        setId(id);
        setCustomerId(customer_id);
        setProductId(product_id);
        try {
            setQuantity(quantity);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        setBranchId(branch_id);
    }

    public Sale(int id, int customer_id, int product_id, int quantity, int branch_id, java.sql.Timestamp sale_date)
    {
        setId(id);
        setCustomerId(customer_id);
        setProductId(product_id);
        try {
            setQuantity(quantity);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        setBranchId(branch_id);
        setSaleDate(sale_date);
    }

    public void setId(int id)
    {
        this.id = id;
    }

    public void setId()
    {
        Scanner s = new Scanner(System.in);
        System.out.println("Enter the sale id: ");
        this.id = s.nextInt();
    }

    public void setCustomerId(int customer_id)
    {
        this.customer_id = customer_id;
    }

    public void setCustomerId()
    {
        Scanner s = new Scanner(System.in);
        System.out.println("Enter the customer id: ");
        this.customer_id = s.nextInt();
    }

    public void setProductId(int product_id)
    {
        this.product_id = product_id;
    }

    public void setProductId()
    {
        Scanner s = new Scanner(System.in);
        System.out.println("Enter the product id: ");
        this.product_id = s.nextInt();
    }

    public boolean setQuantity(int quantity) throws SQLException {
        ProductDao productDao = new ProductDao();
        Product product = productDao.get(product_id);
        if (product.getStockQuantity() < quantity)
        {
            System.out.println("Not enough stock available.");
            System.out.println("Available stock: " + product.getStockQuantity());
            return false;
        }
        this.quantity = quantity;
        return true;
    }

    public void setQuantity() throws SQLException {
        Scanner s = new Scanner(System.in);
        System.out.println("Enter the quantity: ");
        int quantity = s.nextInt();
        while (!setQuantity(quantity))
        {
            System.out.println("Enter the quantity: ");
            quantity = s.nextInt();
        }
    }

    public void setBranchId(int branch_id)
    {
        this.branch_id = branch_id;
    }

    public void setBranchId()
    {
        Scanner s = new Scanner(System.in);
        System.out.println("Enter the branch id: ");
        this.branch_id = s.nextInt();
    }

    public void setSaleDate(java.sql.Timestamp sale_date)
    {
        this.sale_date = sale_date;
    }

    public java.sql.Timestamp getSaleDate()
    {
        return sale_date;
    }

    public int getId() {return id;}

    public int getCustomerId() { return customer_id;}

    public int getProductId() {return product_id;}

    public int getQuantity() {return quantity;}

    public int getBranchId() {return branch_id;}

    @Override
    public String toString()
    {
        return "Sale{" +
                "id=" + id +
                "customer_id=" + customer_id +
                "product_id=" + product_id +
                "quantity=" + quantity +
                "branch_id=" + branch_id +
                '}';
    }

}
