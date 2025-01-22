package com.storeManagement.model;

import com.storeManagement.utils.Constants;
import com.storeManagement.utils.Constants.Category;

import java.util.Scanner;

public class Product
{
    int id;
    String name;
    Category category;
    double price;
    int stock_quantity;
    int branch_id;

    public Product() {}

    public Product(int id, String name, Category category, double price, int stock_quantity, int branch_id)
    {
        setId(id);
        setName(name);
        setCategory(category);
        setPrice(price);
        setStockQuantity(stock_quantity);
        setBranchId(branch_id);
    }

    public Product(String name, Category category, double price, int stock_quantity, int branch_id)
    {
        setName(name);
        setCategory(category);
        setPrice(price);
        setStockQuantity(stock_quantity);
        setBranchId(branch_id);
    }

    public void setId(int id)
    {
        this.id = id;
    }

    public void setId()
    {
        Scanner s = new Scanner(System.in);
        System.out.println("Enter the product id: ");
        this.id = s.nextInt();
    }

    public void setName(String name)
    {
        this.name = name;
    }

    public void setName()
    {
        Scanner s = new Scanner(System.in);
        System.out.println("Enter the product name: ");
        this.name = s.nextLine();
    }

    public void setCategory(Category category)
    {
        this.category = category;
    }

    public void setCategory()
    {
        Scanner s = new Scanner(System.in);
        boolean validCategory = false;

        while (!validCategory)
        {
            System.out.println("Choose category: ");
            for (int i = 0; i < Category.values().length; i++)
            {
                System.out.println((i + 1) + ". " + Category.values()[i]);
            }
            System.out.println("Enter category: ");
            int catChoice = s.nextInt();
            s.nextLine();
            switch (catChoice)
            {
                case 1:
                    this.category = Category.MEN;
                    validCategory = true;
                    break;
                case 2:
                    this.category = Category.WOMEN;
                    validCategory = true;
                    break;
                case 3:
                    this.category = Category.FOOTWEAR;
                    validCategory = true;
                    break;
                case 4:
                    this.category = Category.ACCESSORIES;
                    validCategory = true;
                    break;
                case 5:
                    this.category = Category.KIDS;
                    validCategory = true;
                    break;
                case 6:
                    this.category = Category.SEASONAL;
                    validCategory = true;
                    break;
                default:
                    System.out.println("Invalid category.");
                    break;
            }
        }
    }

    public void setPrice(double price)
    {
        this.price = price;
    }

    public void setPrice()
    {
        Scanner s = new Scanner(System.in);
        System.out.println("Enter the product price: ");
        this.price = s.nextDouble();
    }

    public void setStockQuantity(int stock_quantity)
    {
        this.stock_quantity = stock_quantity;
    }

    public void setStockQuantity()
    {
        Scanner s = new Scanner(System.in);
        System.out.println("Enter the product stock quantity: ");
        this.stock_quantity = s.nextInt();
    }

    public void setBranchId(int branch_id)
    {
        this.branch_id = branch_id;
    }

    public void setBranchId()
    {
        Scanner s = new Scanner(System.in);
        System.out.println("Enter the product branch id: ");
        this.branch_id = s.nextInt();
    }

    public int getId()
    {
        return id;
    }

    public String getName()
    {
        return name;
    }

    public String getCategory() {return category.toString();}

    public double getPrice() {return price;}

    public int getStockQuantity() {return stock_quantity;}

    public int getBranchId() {return branch_id;}

    @Override
    public String toString()
    {
        return "Product{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", category='" + category + '\'' +
                ", price=" + price +
                ", stock_quantity=" + stock_quantity +
                ", branch_id=" + branch_id +
                '}';
    }



}
