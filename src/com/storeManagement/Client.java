
package com.storeManagement;

import java.io.*;
import java.net.Socket;
import java.util.Scanner;

public class Client implements AutoCloseable {
    private ObjectInputStream sInput;
    private ObjectOutputStream sOutput;
    private Socket socket;
    private String server, username, password;
    private int branchId;
    private boolean loggedIn = false;
    private String role;

    public Client(String server, int port) {
        this.server = server;
        connectToServer(port);
    }

    private void connectToServer(int port) {
        try {
            socket = new Socket(server, port);
            sOutput = new ObjectOutputStream(socket.getOutputStream());
            sInput = new ObjectInputStream(socket.getInputStream());
            System.out.println("Connected to server.");
        } catch (IOException e) {
            System.err.println("Connection error: " + e.getMessage());
        }
    }

    public boolean login() {
        try (Scanner scanner = new Scanner(System.in)) {
            System.out.print("Enter username: ");
            username = scanner.nextLine();

            System.out.print("Enter password: ");
            password = scanner.nextLine();

            sOutput.writeObject(username);
            sOutput.writeObject(password);

            String response = (String) sInput.readObject();
            if ("SUCCESS".equals(response)) {
                System.out.println("Login successful.");
                role = (String) sInput.readObject(); // Receive role from server
                branchId = Integer.parseInt((String) sInput.readObject()); // Receive branch ID
                loggedIn = true;
                Menus.displayMenu(this);
                return true;
            } else {
                System.out.println("Login failed: " + response);
                return false;
            }
        } catch (IOException | ClassNotFoundException e) {
            System.err.println("Error during login: " + e.getMessage());
        }
        return false;
    }

    public String getRole() {
        return role;
    }

    public int getBranchId() {
        return branchId;
    }

    public void sendMessage(String msg) {
        try {
            sOutput.writeObject(msg);
        } catch (IOException e) {
            System.err.println("Error sending message: " + e.getMessage());
        }
    }

    public String readMessage() throws IOException, ClassNotFoundException
    {
        return (String) sInput.readObject();
    }

    @Override
    public void close() {
        try {
            sendMessage("DISCONNECT");
            if (sInput != null) sInput.close();
            if (sOutput != null) sOutput.close();
            if (socket != null) socket.close();
            System.out.println("Disconnected from server.");
        } catch (IOException e) {
            System.err.println("Error closing resources: " + e.getMessage());
        }
    }

    public static void main(String[] args) {
        String serverAddress = "localhost";
        int portNumber = 1500;

        try (Client client = new Client(serverAddress, portNumber)) {
            client.login();
        } catch (Exception e) {
            System.err.println("Client error: " + e.getMessage());
        }
    }
}