package com.storeManagement.model;

import com.storeManagement.dataAccessObject.ProductDao;
import com.storeManagement.utils.Constants;

import java.sql.SQLException;
import java.util.InputMismatchException;
import java.util.Scanner;

public class Sale
{
    int id;
    int customer_id;
    int product_id;
    int quantity;
    int branch_id;
    double total_price;
    java.sql.Timestamp sale_date = null;

    public Sale() {}

    public Sale(int id, int customer_id, int product_id, int quantity, int branch_id,double total_price)
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
        setTotalPrice(total_price);
    }

    public void setId(int id)
    {
        this.id = id;
    }

    public void setId()
    {
        Scanner s = new Scanner(System.in);
        boolean validId = false;
        System.out.println("Enter the sale ID: ");

        while(!validId)
        {
            try {
                this.id = s.nextInt();
                validId = true;
            } catch (InputMismatchException e) {
                System.out.println("Invalid ID.");
            } finally {
                s.nextLine();
            }
        }
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

    public void setTotalPrice(double total_price)
    {
        this.total_price = total_price;
    }

    public void setTotalPrice(Constants.CustomerType type)
    {
        ProductDao productDao = new ProductDao();
        try {
            Product product = productDao.get(product_id);
            double price = product.getPrice();

            switch(type)
            {
                case NEW:
                    setTotalPrice(price * quantity);
                    break;
                case RETURNING:
                    setTotalPrice(price * quantity * 0.9);
                    break;
                case VIP:
                    setTotalPrice(price * quantity * 0.8);
                    break;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public double getTotalPrice()
    {
        return total_price;
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
        String sale_date = this.sale_date == null ? "" : "sale_date=" + this.sale_date.toString();

        return "Sale{" +
                "id=" + id +
                "customer_id=" + customer_id +
                "product_id=" + product_id +
                "quantity=" + quantity +
                "branch_id=" + branch_id +
                "total_price=" + total_price +
                sale_date +
                '}';
    }

}
