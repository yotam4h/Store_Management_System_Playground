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
    private ChatManager chatManager;  // Reference to ChatManager for handling chats
    private UserDao userDao;  // Reference to UserDao for user authentication

    public ClientThread(Socket socket, ChatManager chatManager) {
        this.socket = socket;
        this.chatManager = chatManager;
        this.userDao = new UserDao();
    }

    @Override
    public void run() {
        try {
            setupStreams();
            authenticateUser();
            handleClientMessages();
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
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
        if (chatManager.isUsernameLoggedIn(username)) {
            outputStream.writeObject("ALREADY_LOGGED_IN");
            socket.close();
            throw new IOException("User already logged in.");
        }

        if (isValidCredentials(username, password)) {
            outputStream.writeObject("SUCCESS");
            branchId = getBranchIdForUser(username);
            String role = getRoleForUser(username);
            outputStream.writeObject(role);
            outputStream.writeObject(String.valueOf(branchId));
            chatManager.addUserToLogin(username);  // Notify ChatManager that the user is logged in
        } else {
            outputStream.writeObject("INVALID");
            socket.close();
        }
    }

    private boolean isValidCredentials(String username, String password) {
        try {
            return userDao.authenticateUser(username, password);
        } catch (SQLException e) {
            System.out.println("Error during authentication: " + e.getMessage());
            return false;
        }
    }

    private int getBranchIdForUser(String username) {
        try {
            return userDao.getByUsername(username).getBranchId();
        } catch (SQLException e) {
            System.out.println("Error getting branch ID: " + e.getMessage());
            return -1;
        }
    }

    private String getRoleForUser(String username) {
        try {
            return userDao.getByUsername(username).getRole();
        } catch (SQLException e) {
            System.out.println("Error getting role: " + e.getMessage());
            return null;
        }
    }

    private void handleClientMessages() throws IOException, ClassNotFoundException {
        while (true) {
            String message = (String) inputStream.readObject();

            if (message.equalsIgnoreCase("EXIT")) {
                break;
            } else if (message.startsWith("ENDCHAT")) {
                handleEndChat();
            } else {
                handleChatMessages(message);
            }
        }
    }

    private void handleChatMessages(String message) {
        if (message.startsWith("CHAT")) {
            String targetUsername = message.split(" ")[1];
            ClientThread targetClient = chatManager.getClientByUsername(targetUsername);

            if (targetClient != null) {
                chatManager.startChat(this, targetClient);
            } else {
                try {
                    outputStream.writeObject("Target user not available.");
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        } else {
            chatManager.handleMessage(this, message);  // Use handleMessage to send the message to the target user
        }
    }

    private void handleEndChat() {
        // Ending the chat with the other client
        // In this example, it assumes that you want to end the chat with the user who started the chat
        // If you want a more specific logic, you'll need to track the target client during the chat session
        ClientThread targetClient = chatManager.getClientByUsername(username);
        if (targetClient != null) {
            chatManager.endChat(this, targetClient);
        } else {
            try {
                outputStream.writeObject("No active chat to end.");
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    void close() {
        try {
            if (socket != null) socket.close();
            if (inputStream != null) inputStream.close();
            if (outputStream != null) outputStream.close();
            chatManager.removeUserFromLogin(username);  // Remove the user from logged-in list when they disconnect
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
            e.printStackTrace();
        }
    }

    public Socket getSocket() {
        return socket;
    }
}
