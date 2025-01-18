package com.storeManagement.model;

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
        setQuantity(quantity);
        setBranchId(branch_id);
    }

    public Sale(int id, int customer_id, int product_id, int quantity, int branch_id)
    {
        setId(id);
        setCustomerId(customer_id);
        setProductId(product_id);
        setQuantity(quantity);
        setBranchId(branch_id);
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

    public void setQuantity(int quantity)
    {
        this.quantity = quantity;
    }

    public void setQuantity()
    {
        Scanner s = new Scanner(System.in);
        System.out.println("Enter the quantity: ");
        this.quantity = s.nextInt();
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
