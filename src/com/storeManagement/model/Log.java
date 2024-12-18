package com.storeManagement.model;

import com.storeManagement.utils.Constants.OperationType;

import java.util.Date;

public class Log
{
    OperationType operation;
    String details;
    Date date;

    public Log(OperationType operation, String details, Date date)
    {
        setOperation(operation);
        setDetails(details);
        setDate(date);
    }

    public Log(OperationType operation, String details)
    {
        this(operation, details, new Date());
    }

    public String getOperation()
    {
        return operation.toString();
    }

    public void setOperation(OperationType operation)
    {
        this.operation = operation;
    }

    public String getDetails()
    {
        return details;
    }

    public void setDetails(String details)
    {
        this.details = details;
    }

    public Date getDate()
    {
        return date;
    }

    public void setDate(Date date)
    {
        this.date = date;
    }

    @Override
    public String toString()
    {
        String tab = getOperation().length() > OperationType.ADD.toString().length() ? "\t" : "\t\t";
        return "[" + getDate().toString() + "] " + getOperation() + ":" + tab + getDetails();
    }
}
