package com.storeManagement.model;

import java.util.Scanner;

public class Branch
{
    int id;
    String name;
    String address;
    String phone;

    public Branch()
    {
    }

    public Branch(int id, String name, String address, String phone)
    {
        setId(id);
        setName(name);
        setAddress(address);
        setPhone(phone);
    }

    // Constructor for adding a new branch
    public Branch(String name, String address, String phone)
    {
        setName(name);
        setAddress(address);
        setPhone(phone);
    }

    public int getId()
    {
        return id;
    }

    public void setId(int id)
    {
        this.id = id;
    }

    public void setId()
    {
        Scanner s = new Scanner(System.in);
        System.out.println("Enter the branch ID: ");
        this.id = s.nextInt();
    }

    public String getName()
    {
        return name;
    }

    public void setName(String name)
    {
        this.name = name;
    }

    public void setName()
    {
        Scanner s = new Scanner(System.in);
        System.out.println("Enter the branch name: ");
        this.name = s.nextLine();
    }

    public String getAddress()
    {
        return address;
    }

    public void setAddress(String address)
    {
        this.address = address;
    }

    public void setAddress()
    {
        Scanner s = new Scanner(System.in);
        System.out.println("Enter the branch address: ");
        this.address = s.nextLine();
    }

    public String getPhone()
    {
        return phone;
    }

    public void setPhone(String phone)
    {
        this.phone = phone;
    }

    public void setPhone()
    {
        Scanner s = new Scanner(System.in);
        System.out.println("Enter the branch phone number: ");
        this.phone = s.nextLine();
    }

    @Override
    public String toString()
    {
        return "Branch{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", address='" + address + '\'' +
                ", phone='" + phone + '\'' +
                '}';
    }
}
