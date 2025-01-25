package com.storeManagement;

import java.util.HashSet;
import java.util.Set;

public class ChatManager {
    private Set<String> loggedInUsers = new HashSet<>();  // Track logged-in usernames
    private Set<ChatSession> activeChats = new HashSet<>();  // Active chat sessions

    // Method to check if a username is already logged in
    public boolean isUsernameLoggedIn(String username) {
        return loggedInUsers.contains(username);
    }

    // Method to add a user to the logged-in users list
    public void addUserToLogin(String username) {
        loggedInUsers.add(username);
    }

    // Method to remove a user from the logged-in users list
    public void removeUserFromLogin(String username) {
        loggedInUsers.remove(username);
    }

    // Check if a chat is active between two clients
    public boolean isChatActive(ClientThread client1, ClientThread client2) {
        ChatSession session = new ChatSession(client1, client2);
        return activeChats.contains(session);
    }

    // Start a new chat session between two clients
    public void startChat(ClientThread client1, ClientThread client2) {
        if (!isChatActive(client1, client2)) {
            activeChats.add(new ChatSession(client1, client2));
            // Notify both clients that the chat has started
            client1.sendMessage("Chat started with " + client2.getUsername());
            client2.sendMessage("Chat started with " + client1.getUsername());
        } else {
            client1.sendMessage("Chat is already active between you and " + client2.getUsername());
        }
    }

    // End the chat session between two clients
    public void endChat(ClientThread client1, ClientThread client2) {
        activeChats.remove(new ChatSession(client1, client2));  // Remove the chat session
        // Notify both clients that the chat has ended
        client1.sendMessage("Chat ended with " + client2.getUsername());
        client2.sendMessage("Chat ended with " + client1.getUsername());
    }

    // Method to retrieve a client by their username
    public ClientThread getClientByUsername(String username) {
        // This assumes you have some way of mapping usernames to ClientThreads
        // For example, a Map<String, ClientThread> could be used
        // Placeholder logic:
        return null;  // Replace with actual logic for retrieving the client
    }

    // Method to handle a new message from a client
    public void handleMessage(ClientThread sender, String message) {
        // Find the recipient client
        String targetUsername = message.split(" ")[0];  // Assuming format: "username message"
        ClientThread targetClient = getClientByUsername(targetUsername);

        if (targetClient != null) {
            // If the target client exists, send the message
            targetClient.sendMessage("Message from " + sender.getUsername() + ": " + message);
        } else {
            sender.sendMessage("Target user not available.");
        }
    }
}
