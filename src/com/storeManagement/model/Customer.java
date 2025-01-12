package com.storeManagement.model;

import com.storeManagement.utils.Constants.CustomerType;

public class Customer
{
    int id;
    String full_name;
    String phone_number;
    CustomerType type;

    public Customer()
    {
    }

    public Customer(String full_name, String phone_number, CustomerType type)
    {
        setFullName(full_name);
        setPhoneNumber(phone_number);
        setType(type);
    }

    public Customer(int id, String full_name, String phone_number, CustomerType type)
    {
        setId(id);
        setFullName(full_name);
        setPhoneNumber(phone_number);
        setType(type);
    }

    public int getId()
    {
        return id;
    }

    public void setId(int id)
    {
        this.id = id;
    }

    public String getFullName()
    {
        return full_name;
    }

    public void setFullName(String full_name)
    {
        this.full_name = full_name;
    }

    public String getPhoneNumber()
    {
        return phone_number;
    }

    public void setPhoneNumber(String phone_number)
    {
        this.phone_number = phone_number;
    }

    public String getType()
    {
        return type.toString();
    }

    public void setType(CustomerType type)
    {
        this.type = type;
    }

    @Override
    public String toString()
    {
        return "Customer{" +
                "id=" + id +
                ", full_name='" + full_name + '\'' +
                ", phone_number='" + phone_number + '\'' +
                ", type=" + type +
                '}';
    }
}
