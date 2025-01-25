package com.storeManagement;

import com.storeManagement.dataAccessObject.UserDao;

import java.io.*;
import java.net.Socket;
import java.sql.SQLException;

public class ClientThread extends Thread {
    private Socket socket;
    private ObjectInputStream inputStream;
    private ObjectOutputStream outputStream;
    private String username;
    private int branchId;
    private Server server;
    private boolean loggedIn = false;

    public ClientThread(Socket socket, Server server) {
        this.socket = socket;
        this.server = server;
    }

    @Override
    public void run() {
        try {
            setupStreams();
            authenticateUser();

            String line = "";
            while (!line.equalsIgnoreCase("EXIT")) {
                line = (String) inputStream.readObject();
                server.broadcast(line, this);
            }

        } catch (IOException | ClassNotFoundException e) {
            System.out.println("Error handling client messages: " + e.getMessage());
        } finally {
            close();
        }
    }

    private void setupStreams() throws IOException {
        inputStream = new ObjectInputStream(socket.getInputStream());
        outputStream = new ObjectOutputStream(socket.getOutputStream());
    }

    private void authenticateUser() throws IOException, ClassNotFoundException {
        username = (String) inputStream.readObject();
        String password = (String) inputStream.readObject();

        // if username is already logged in
        if (server.isUsernameLoggedIn(username)) {
            outputStream.writeObject("ALREADY_LOGGED_IN");
            socket.close();
            throw new IOException("User already logged in.");
        }

        if (server.authenticateUser(username, password)) {
            outputStream.writeObject("SUCCESS");
            branchId = server.getBranchIdForUser(username);
            String role = server.getRoleForUser(username);
            outputStream.writeObject(role);
            outputStream.writeObject(String.valueOf(branchId));
            loggedIn = true;
        } else {
            outputStream.writeObject("INVALID");
            socket.close();
        }
    }

    void close() {
        try {
            if (socket != null) socket.close();
            if (inputStream != null) inputStream.close();
            if (outputStream != null) outputStream.close();
            server.removeClient(this); // Remove the client from the server's list of clients
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public String getUsername() {
        return username;
    }

    public int getBranchId() {
        return branchId;
    }

    public void sendMessage(String message) {
        try {
            outputStream.writeObject(message);
        } catch (IOException e) {
            System.out.println("Error sending message to client: " + e.getMessage());
        }
    }

    public boolean isLoggedIn() {
        return loggedIn;
    }

}
