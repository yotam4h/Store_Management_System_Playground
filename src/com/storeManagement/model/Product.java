package com.storeManagement.model;

import com.storeManagement.utils.Constants.Category;

public class Product
{
    int id;
    String name;
    Category category;
    double price;
    int stock_quantity;
    int branch_id;

    public Product(int id, String name, Category category, double price, int stock_quantity, int branch_id)
    {
        setId(id);
        setName(name);
        setCategory(category);
        setPrice(price);
        setStockQuantity(stock_quantity);
        setBranchId(branch_id);
    }

    // Constructor for adding a new Product
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

    public void setName(String name)
    {
        this.name = name;
    }

    public void setCategory(Category category)
    {
        this.category = category;
    }

    public void setPrice(double price)
    {
        this.price = price;
    }

    public void setStockQuantity(int stock_quantity)
    {
        this.stock_quantity = stock_quantity;
    }

    public void setBranchId(int branch_id)
    {
        this.branch_id = branch_id;
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
