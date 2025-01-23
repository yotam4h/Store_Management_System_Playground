package com.storeManagement.model;

import com.storeManagement.utils.Constants.CustomerType;

import java.util.InputMismatchException;
import java.util.Scanner;

public class Customer
{
    int id;
    String full_name;
    String phone_number;
    CustomerType type;

    public Customer()
    {
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

    public void setId()
    {
        Scanner s = new Scanner(System.in);
        boolean validId = false;
        System.out.println("Enter the customer ID: ");

        while(!validId)
        {
            try {
                this.id = s.nextInt();
                validId = true;
            } catch (InputMismatchException e) {
                System.out.println("Invalid ID.");
            } finally
            {
                s.nextLine();
            }
        }
    }

    public String getFullName()
    {
        return full_name;
    }

    public void setFullName(String full_name)
    {
        this.full_name = full_name;
    }

    public void setFullName()
    {
        Scanner s = new Scanner(System.in);
        System.out.println("Enter the customer full name: ");
        this.full_name = s.nextLine();
    }

    public String getPhoneNumber()
    {
        return phone_number;
    }

    public void setPhoneNumber(String phone_number)
    {
        this.phone_number = phone_number;
    }

    public void setPhoneNumber()
    {
        Scanner s = new Scanner(System.in);
        System.out.println("Enter the customer phone number: ");
        this.phone_number = s.nextLine();
    }

    public String getType()
    {
        return type.toString();
    }

    public void setType(CustomerType type)
    {
        this.type = type;
    }

    public void setType()
    {
        Scanner s = new Scanner(System.in);
        boolean validType = false;
        while (!validType)
        {
            System.out.println("Choose type: ");
            System.out.println("1. NEW\n2. RETURNING\n3. VIP");
            System.out.println("Enter role: ");
            int type = s.nextInt();

            switch (type)
            {
                case 1:
                    this.type = CustomerType.NEW;
                    validType = true;
                    break;
                case 2:
                    this.type = CustomerType.RETURNING;
                    validType = true;
                    break;
                case 3:
                    this.type = CustomerType.VIP;
                    validType = true;
                    break;
                default:
                    System.out.println("Invalid type.");
                    break;
            }
        }
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
