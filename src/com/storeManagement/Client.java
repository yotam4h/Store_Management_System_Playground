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

    Client(String server, int port)
    {
        this.server = server;
        this.port = port;
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
//        display("Connection accepted " + socket.getInetAddress() + ":" + socket.getPort());
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
            Scanner s = new Scanner(System.in);

            display("\n\nLOGIN");

            display("Enter your username: ");
            username = s.nextLine();
            display("Enter your password: ");
            password = s.nextLine();

            sOutput.writeObject(username);
            sOutput.writeObject(password);

            String response = (String) sInput.readObject();
            switch (response)
            {
                case "INVALID":
                    display("Invalid username or password. Please try again.");
                    disconnect();
                    return false;
                case "ALREADY_LOGGED_IN":
                    display("User is already logged in. Please try again later.");
                    disconnect();
                    return false;
                case "SUCCESS":
                    display("Login successful.");
                    break;
                default:
                    display("Unknown response from server: " + response);
                    disconnect();
                    return false;
            }
        }
        catch (IOException eIO)
        {
            display("Exception doing login: " + eIO);
            disconnect();
            return false;
        } catch (ClassNotFoundException e)
        {
            e.printStackTrace();
        }

        new ListenFromServer().start();

        return true;
    }

    private void sendMessage(String msg)
    {
        try
        {
            sOutput.writeObject(msg);
        }
        catch(IOException e)
        {
            display("Exception writing to server: " + e);
        }
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
        Client client = null;
        boolean loggedIn = false;

        while(!loggedIn)
        {
            client = new Client(serverAddress, portNumber);
            if(client.start())
                loggedIn = true;
        }

        Scanner s = new Scanner(System.in);

        while(true)
        {
            String msg = s.nextLine();
            client.sendMessage(msg);
        }


    }

    class ListenFromServer extends Thread
    {
        public void run()
        {
            while(true)
            {
                try
                {
                    String msg = (String) sInput.readObject();
                    System.out.println(msg);
                }
                catch(IOException e)
                {
//                    display("Server has closed the connection: " + e);
                    break;
                }
                catch(ClassNotFoundException e2)
                {
                    display("Class not found: " + e2);
                }
            }
        }
    }
}