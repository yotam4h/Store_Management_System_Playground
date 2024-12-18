package com.storeManagement.model;

public class Branch
{
    int id;
    String name;
    String address;
    String phone;

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

    public String getName()
    {
        return name;
    }

    public void setName(String name)
    {
        this.name = name;
    }

    public String getAddress()
    {
        return address;
    }

    public void setAddress(String address)
    {
        this.address = address;
    }

    public String getPhone()
    {
        return phone;
    }

    public void setPhone(String phone)
    {
        this.phone = phone;
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
