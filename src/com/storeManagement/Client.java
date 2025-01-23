package com.storeManagement;

import com.storeManagement.utils.Constants;

import java.io.*;
import java.net.Socket;
import java.util.Scanner;

import static java.lang.System.exit;

public class Client implements AutoCloseable {
    ObjectInputStream sInput;
    ObjectOutputStream sOutput;
    Socket socket;
    String server, username;
    Constants.EmployeeRole role;
    int branchId;
    int port;

    public Client(String server, int port) {
        this.server = server;
        this.port = port;
    }

    public void display(String msg) {
        System.out.println(msg);
    }

    public boolean start() {
        try {
            socket = new Socket(server, port);
        } catch (Exception ec) {
            display("Error connecting to server:" + ec);
            return false;
        }
        try {
            sInput = new ObjectInputStream(socket.getInputStream());
            sOutput = new ObjectOutputStream(socket.getOutputStream());
        } catch (IOException eIO) {
            display("Exception creating new Input/output Streams: " + eIO);
            return false;
        }

        try {
            Scanner s = new Scanner(System.in);

            display("\n\nLOGIN");

            display("Enter your username: ");
            username = s.nextLine();
            display("Enter your password: ");
            String password = s.nextLine();

            sOutput.writeObject(username);
            sOutput.writeObject(password);

            String response = (String) sInput.readObject();
            switch (response) {
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

            String role = (String) sInput.readObject();
            switch (role) {
                case "ADMIN":
                    this.role = Constants.EmployeeRole.ADMIN;
                    break;
                case "MANAGER":
                    this.role = Constants.EmployeeRole.MANAGER;
                    break;
                case "EMPLOYEE":
                    this.role = Constants.EmployeeRole.EMPLOYEE;
                    break;
            }

            branchId = Integer.parseInt((String) sInput.readObject());
        } catch (IOException eIO) {
            display("Exception doing login: " + eIO);
            disconnect();
            return false;
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }

        return true;
    }

    public void sendMessage(String msg) {
        try {
            sOutput.writeObject(msg);
        } catch (IOException e) {
            display("Exception writing to server: " + e);
        }
    }

    public String readMessage() {
        try {
            return (String) sInput.readObject();
        } catch (IOException e) {
            display("Exception reading from server: " + e);
        } catch (ClassNotFoundException e) {
            display("Class not found: " + e);
        }
        return null;
    }

    public void disconnect() {
        sendMessage("DISCONNECT");

        try {
            if (sInput != null) sInput.close();
        } catch (Exception e) {
        }
        try {
            if (sOutput != null) sOutput.close();
        } catch (Exception e) {
        }
        try {
            if (socket != null) socket.close();
        } catch (Exception e) {
        }
    }

    @Override
    public void close() throws Exception {
        disconnect();
    }

    public static void main(String[] args) {
        int portNumber = 1500;
        String serverAddress = "localhost";
        Client client = null;
        boolean loggedIn = false;

        while (!loggedIn) {
            client = new Client(serverAddress, portNumber);
            if (client.start())
                loggedIn = true;
        }

        Menus.displayMenu(client);

        client.disconnect();
        exit(0);
    }
}