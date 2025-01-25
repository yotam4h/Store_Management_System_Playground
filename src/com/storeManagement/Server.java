package com.storeManagement;

import com.storeManagement.dataAccessObject.UserDao;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Server {
    private static final Logger logger = Logger.getLogger(Server.class.getName());
    private int port;
    private boolean keepGoing;
    private ServerSocket serverSocket;
    private final List<ClientThread> clients;
    private final UserDao userDao;

    public Server(int port) {
        this.port = port;
        this.clients = new ArrayList<>();
        this.userDao = new UserDao();
    }

    public boolean authenticateUser(String username, String password) {
        try {
            return userDao.authenticateUser(username, password);
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Authentication error", e);
            return false;
        }
    }

    public int getBranchIdForUser(String username) {
        try {
            return userDao.getByUsername(username).getBranchId();
        } catch (SQLException e) {
            System.out.println("Error getting branch ID: " + e.getMessage());
            return -1;
        }
    }

    public String getRoleForUser(String username) {
        try {
            return userDao.getByUsername(username).getRole();
        } catch (SQLException e) {
            System.out.println("Error getting role: " + e.getMessage());
            return null;
        }
    }

    public void start() {
        keepGoing = true;
        try {
            serverSocket = new ServerSocket(port);
            logger.info("Server started on port " + port);

            while (keepGoing) {
                logger.info("Waiting for clients...");
                Socket socket = serverSocket.accept();
                if (!keepGoing) break;

                ClientThread clientThread = new ClientThread(socket, this);
                synchronized (clients) {
                    clients.add(clientThread);
                }
                clientThread.start();
            }
        } catch (IOException e) {
            logger.log(Level.SEVERE, "Server error", e);
        } finally {
            stop();
        }
    }

    public void stop() {
        keepGoing = false;
        try {
            if (serverSocket != null) serverSocket.close();
            synchronized (clients) {
                for (ClientThread client : clients) {
                    client.close();
                }
            }
        } catch (IOException e) {
            logger.log(Level.SEVERE, "Error closing server", e);
        }
    }

    public void removeClient(ClientThread client) {
        synchronized (clients) {
            clients.remove(client);
        }
    }

    public boolean isUsernameLoggedIn(String username) {
        synchronized (clients) {
            for (ClientThread client : clients) {
                if (client.getUsername().equals(username) && client.isLoggedIn()) {
                    return true;
                }
            }
        }
        return false;
    }

    public static void main(String[] args) {
        Server server = new Server(1500);
        server.start();
    }

    public void broadcast(String message, ClientThread excludeClient) {
        synchronized (clients) {
            for (ClientThread client : clients) {
                if (client != excludeClient) {
                    client.sendMessage(excludeClient.getUsername() + ": " + message);
                }
            }
        }
    }
}