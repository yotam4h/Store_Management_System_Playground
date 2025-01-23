package com.storeManagement;

import com.storeManagement.dataAccessObject.UserDao;
import com.storeManagement.utils.Constants.EmployeeRole;

import java.io.*;
import java.net.Socket;
import java.sql.SQLException;
import java.util.Iterator;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ClientThread extends Thread {
    private static final Logger logger = Logger.getLogger(ClientThread.class.getName());
    private final Socket socket;
    private final Server server;
    private final UserDao userDao;
    private final ChatManager chatManage;
    private ObjectInputStream sInput;
    private ObjectOutputStream sOutput;
    private String username;
    private int branchId;
    private boolean inChat = false;

    public ClientThread(Socket socket, Server server, UserDao userDao, ChatManager chatManage) {
        this.socket = socket;
        this.server = server;
        this.userDao = userDao;
        this.chatManage = chatManage;
        initialize();
    }

    private void initialize() {
        try {
            sOutput = new ObjectOutputStream(socket.getOutputStream());
            sInput = new ObjectInputStream(socket.getInputStream());

            this.username = (String) sInput.readObject();
            String password = (String) sInput.readObject();

            if(server.isUsernameLoggedIn(username)){
                sendMessage("ALREADY_LOGGED_IN");
                close();
                return;
            }

            if (!server.authenticateUser(username, password)) {
                sendMessage("INVALID");
                close();
                return;
            }

            this.branchId = userDao.getByUsername(username).getBranchId();
            sendMessage("SUCCESS");

            logger.info(username + " connected.");

            EmployeeRole role = EmployeeRole.valueOf(userDao.getByUsername(username).getRole());
            sendMessage(role.toString());

            branchId = userDao.getByUsername(username).getBranchId();
            sendMessage(String.valueOf(branchId));

        } catch (IOException | ClassNotFoundException | SQLException e) {
            logger.log(Level.SEVERE, "Client connection error", e);
            close();
        }
    }

    @Override
    public void run() {
        try {
            boolean keepGoing = true;
            String msg = "";
            while (keepGoing) {
                if(!inChat)
                    msg = readMessage();
                switch (msg) {
                    case "DISCONNECT":
                        keepGoing = false;
                        break;
                    case "READY":
                        chatManage.addEmployeeToQueue(this);
                        break;
                }


            }
        } catch (IOException | ClassNotFoundException e) {
            logger.log(Level.WARNING, "Client disconnected unexpectedly", e);
        } finally {
            disconnect();
        }
    }

    public void setInChat(boolean inChat) {
        this.inChat = inChat;
    }

    public void close() {
        try {
            if (sOutput != null) sOutput.close();
            if (sInput != null) sInput.close();
            if (socket != null) socket.close();
        } catch (IOException e) {
            logger.log(Level.WARNING, "Error closing client resources", e);
        } finally {
            server.removeClient(this);
        }
    }

    public void disconnect() {
        close();
        logger.info(username + " disconnected.");
    }

    public void sendMessage(String msg) {
        try {
            sOutput.writeObject(msg);
        } catch (IOException e) {
            logger.log(Level.WARNING, "Error sending message to client", e);
        }
    }

    public String readMessage() throws IOException, ClassNotFoundException {
        return (String) sInput.readObject();
    }

    public int getBranchId() {
        return branchId;
    }

    public String getUsername() {
        return username;
    }

}
