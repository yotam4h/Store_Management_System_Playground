package com.storeManagement.utils;

import com.storeManagement.model.Log;
import com.storeManagement.utils.Constants.OperationType;

import java.io.File;

public class Logger
{
    String path = "log.txt";

    public Logger() {
        if(System.getProperty("os.name").startsWith("Windows")) {
            setPath("C:\\tmp\\log.txt");
        } else if (System.getProperty("os.name").startsWith("Linux")) {
            setPath("/logs/log.txt");
        } else {
            setPath("./log.txt");
        }
    }

    public Logger(String path)
    {
        setPath(path);
    }

    public String getPath()
    {
        return path;
    }

    public void setPath(String path)
    {
        this.path = path;
    }

    public void log(Log log)
    {
        File file = new File(getPath());
        if (!file.exists())
        {
            System.out.println("File does not exist. Creating file...");
            try
            {
                file.createNewFile();
            }
            catch (Exception e)
            {
                System.out.println("Error creating file: " + e.getMessage());
            }
        }
        try
        {
            java.io.FileWriter writer = new java.io.FileWriter(file, true);
            writer.write(log.toString() + "\n");
            writer.close();
        }
        catch (Exception e)
        {
            System.out.println("Error writing to file: " + e.getMessage());
        }
    }

    public void log(OperationType operation, String details)
    {
        Log log = new Log(operation, details);
        log(log);
    }

}
