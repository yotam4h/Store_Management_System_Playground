package com.storeManagement;

import java.io.*;
import java.net.*;
import java.util.Scanner;

public class Client
{
    ObjectInputStream sInput;
    ObjectOutputStream sOutput;
    Socket socket;
    String server, username, password;
    int port;

    Client(String server, int port, String username, String password)
    {
        this.server = server;
        this.port = port;
        this.username = username;
        this.password = password;
    }

    private void display(String msg)
    {
        System.out.println(msg);
    }

    public boolean start()
    {
        try
        {
            socket = new Socket(server, port);
        }
        catch(Exception ec)
        {
            display("Error connecting to server:" + ec);
            return false;
        }

        display("Connection accepted " + socket.getInetAddress() + ":" + socket.getPort());

        try
        {
            sInput = new ObjectInputStream(socket.getInputStream());
            sOutput = new ObjectOutputStream(socket.getOutputStream());
        }
        catch (IOException eIO)
        {
            display("Exception creating new Input/output Streams: " + eIO);
            return false;
        }

        try
        {
            sOutput.writeObject(username);
            sOutput.writeObject(password);
        }
        catch (IOException eIO)
        {
            System.out.println("Exception doing login: " + eIO);
            disconnect();
            return false;
        }

        return true;
    }

    public void disconnect()
    {
        try
        {
            if(sInput != null) sInput.close();
        }
        catch(Exception e) {}
        try
        {
            if(sOutput != null) sOutput.close();
        }
        catch(Exception e) {};
        try
        {
            if(socket != null) socket.close();
        }
        catch(Exception e) {}
    }


    public static void main(String[] args)
    {
        int portNumber = 1500;
        String serverAddress = "localhost";
        String username;
        String password;
        Scanner s = new Scanner(System.in);
        Client client;

        System.out.println("Enter username: ");
        username = s.nextLine();
        System.out.println("Enter password: ");
        password = s.nextLine();

        client = new Client(serverAddress, portNumber, username, password);

        if(!client.start())
        {
            return;
        }

    }
}
