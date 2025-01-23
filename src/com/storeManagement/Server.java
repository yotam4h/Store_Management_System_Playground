/*
package com.storeManagement;

import com.storeManagement.dataAccessObject.UserDao;
import com.storeManagement.utils.Constants;

import java.io.*;
import java.net.*;
import java.sql.SQLException;
import java.util.ArrayList;


public class Server
{
    private static int uniqueId;
    private int port;
    private boolean keepGoing;
    private ServerSocket serverSocket;
    private Socket socket;
    private ObjectInputStream sInput;
    private ObjectOutputStream sOutput;
    private ArrayList<ClientThread> clients;
    private UserDao userDao;
    private ChatManager chatManage;

    public Server(int port)
    {
        this.port = port;
        clients = new ArrayList<ClientThread>();
        userDao = new UserDao();
        chatManage = new ChatManager();
    }

    public boolean authenticateUser(String username, String password)
    {
        try {
            return userDao.authenticateUser(username, password);
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    private void display(String msg)
    {
        System.out.println(msg);
    }

    public void start()
    {
        keepGoing = true;
        try
        {
            serverSocket = new ServerSocket(port);
            while(keepGoing)
            {
                display("Server waiting for Clients on port " + port + ".");
                socket = serverSocket.accept();
                if(!keepGoing)
                    break;
                ClientThread t = new ClientThread(socket);
                clients.add(t);
                t.start();
            }
            try
            {
                serverSocket.close();
                for(int i = 0; i < clients.size(); ++i)
                {
                    ClientThread tc = clients.get(i);
                    try
                    {
                        tc.sInput.close();
                        tc.sOutput.close();
                        tc.socket.close();
                    }
                    catch(IOException ioE)
                    {
                        display("Exception closing the server and clients: " + ioE);
                    }
                }
            }
            catch(Exception e)
            {
                display("Exception closing the server and clients: " + e);
            }
        }
        catch (IOException e)
        {
            String msg = "Exception on new ServerSocket: " + e + "\n";
            display(msg);
        }
    }

    protected void stop()
    {
        keepGoing = false;
        try
        {
            new Socket("localhost", port);
        }
        catch(Exception e)
        {
            display("Exception closing the server and clients: " + e);
        }
    }

    private synchronized void broadcast(String message)
    {
        String time = new java.text.SimpleDateFormat("HH:mm:ss").format(new java.util.Date());
        String messageLf = time + " " + message + "\n";
        System.out.print(messageLf);
        for(int i = clients.size(); --i >= 0;)
        {
            ClientThread ct = clients.get(i);
            if(!ct.sendMessage(messageLf))
            {
                clients.remove(i);
                display("Disconnected Client " + ct.username + " removed from list.");
            }
        }
    }

    private synchronized void directMessage(String message, String username)
    {
        String time = new java.text.SimpleDateFormat("HH:mm:ss").format(new java.util.Date());
        String messageLf = time + " " + message + "\n";
        System.out.print(messageLf);
        for(int i = clients.size(); --i >= 0;)
        {
            ClientThread ct = clients.get(i);
            if(ct.username.equals(username))
            {
                if(!ct.sendMessage(messageLf))
                {
                    clients.remove(i);
                    display("Disconnected Client " + ct.username + " removed from list.");
                }
            }
        }
    }

    private synchronized void remove(int id)
    {
        for(int i = 0; i < clients.size(); ++i)
        {
            ClientThread ct = clients.get(i);
            if(ct.id == id)
            {
                clients.remove(i);
                display(ct.username + " just disconnected.");
                return;
            }
        }
    }

    public static void main(String[] args)
    {
        Server server = new Server(1500);
        server.start();
    }

     class ClientThread extends Thread {
        Socket socket;
        ObjectInputStream sInput;
        ObjectOutputStream sOutput;
        int id;
        String username, password;
        Constants.EmployeeRole role;
        int branchId;
        boolean ready = false;

        ClientThread(Socket socket) {
            id = ++uniqueId;
            this.socket = socket;
            try {
                sOutput = new ObjectOutputStream(socket.getOutputStream());
                sInput = new ObjectInputStream(socket.getInputStream());

                username = (String) sInput.readObject();
                password = (String) sInput.readObject();

                // check if the user is already in the list
                for (int i = 0; i < clients.size(); ++i) {
                    ClientThread ct = clients.get(i);
                    if (ct.username.equals(username)) {
                        sendMessage("ALREADY_LOGGED_IN");
                        close();
                        return;
                    }
                }

                // check if username and password are correct through the database
                if (!authenticateUser(username, password)) {
                    sendMessage("INVALID");
                    close();
                    return;
                }
                display(username + " just connected.");
                sendMessage("SUCCESS");

                role = Constants.EmployeeRole.valueOf(userDao.getByUsername(username).getRole());

                sendMessage(role.toString());

                branchId = userDao.getByUsername(username).getBranchId();

                sendMessage(String.valueOf(branchId));
            } catch (IOException e) {
                display("Exception creating new Input/output Streams: " + e);
                return;
            } catch (ClassNotFoundException e) {
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        }

        public void run() {
            boolean keepGoing = true;

            while (keepGoing) {
                // IF SERVER GOT "DISCONNECT" MESSAGE
                try {
                    String msg = (String) sInput.readObject();
                    switch (msg) {
                        case "DISCONNECT": {
                            keepGoing = false;
                            break;
                        }
                        case "READY": {
                            ready = true;
                            chatManage.addEmployeeToQueue(this);
                            break;
                        }
                        case "NOT_READY": {
                            ready = false;
                            chatManage.removeEmployeeFromQueue(this);
                            break;
                        }
                    }
                } catch (IOException e) {
                    display(username + " Exception reading Streams: " + e);
                    break;
                } catch (ClassNotFoundException e) {
                    break;
                }
            }

            remove(id);
            close();
        }

        private void close() {
            try {
                if (sOutput != null) sOutput.close();
            } catch (Exception e) {
            }
            try {
                if (sInput != null) sInput.close();
            } catch (Exception e) {
            }
            ;
            try {
                if (socket != null) socket.close();
            } catch (Exception e) {
            }
        }

        public boolean sendMessage(String msg) {
            if (!socket.isConnected()) {
                close();
                return false;
            }
            try {
                sOutput.writeObject(msg);
            } catch (IOException e) {
                display("Error sending message to " + username);
                display(e.toString());
            }
            return true;
        }

        public String readMessage() {
            try {
                return (String) sInput.readObject();
            } catch (IOException | ClassNotFoundException e) {
                e.printStackTrace();
                return null;
            }
        }
    }
}

*/


package com.storeManagement;

import com.storeManagement.dataAccessObject.UserDao;
import com.storeManagement.utils.Constants;
import com.storeManagement.utils.Constants.EmployeeRole;

import java.io.*;
import java.net.*;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Server {
    private static final Logger logger = Logger.getLogger(Server.class.getName());
    private static int uniqueId;
    private int port;
    private boolean keepGoing;
    private ServerSocket serverSocket;
    private final List<ClientThread> clients;
    private final UserDao userDao;
    private final ChatManager chatManage;

    public Server(int port) {
        this.port = port;
        this.clients = new ArrayList<>();
        this.userDao = new UserDao();
        this.chatManage = new ChatManager();
    }

    public boolean authenticateUser(String username, String password) {
        try {
            return userDao.authenticateUser(username, password);
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Authentication error", e);
            return false;
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

                ClientThread clientThread = new ClientThread(socket);
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

    class ClientThread extends Thread {
        private final Socket socket;
        private ObjectInputStream sInput;
        private ObjectOutputStream sOutput;
        String username;
        int branchId;

        public ClientThread(Socket socket) {
            this.socket = socket;
            try {
                sOutput = new ObjectOutputStream(socket.getOutputStream());
                sInput = new ObjectInputStream(socket.getInputStream());

                this.username = (String) sInput.readObject();
                String password = (String) sInput.readObject();

                for (ClientThread client : clients) {
                    if (client.username.equals(username)) {
                        sendMessage("ALREADY_LOGGED_IN");
                        close();
                        return;
                    }
                }

                if (!authenticateUser(username, password)) {
                    sendMessage("INVALID");
                    close();
                    return;
                }

                this.branchId = userDao.getByUsername(username).getBranchId();
                sendMessage("SUCCESS");

                logger.info(username + " connected.");

                EmployeeRole role = Constants.EmployeeRole.valueOf(userDao.getByUsername(username).getRole());

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
                while (keepGoing) {
                    String msg = (String) sInput.readObject();
                    switch (msg) {
                        case "DISCONNECT":
                            keepGoing = false;
                            break;
                        case "READY":
                            chatManage.addEmployeeToQueue(this);
                            break;
                        case "NOT_READY":
                            chatManage.removeEmployeeFromQueue(this);
                            break;
                        default:
                            sendMessage("Unknown command");
                    }
                }
            } catch (IOException | ClassNotFoundException e) {
                logger.log(Level.WARNING, "Client disconnected unexpectedly", e);
            } finally {
                close();
            }
        }

        private void close() {
            try {
                if (sOutput != null) sOutput.close();
                if (sInput != null) sInput.close();
                if (socket != null) socket.close();
            } catch (IOException e) {
                logger.log(Level.WARNING, "Error closing client resources", e);
            } finally {
                logger.info(username + " disconnected.");
                synchronized (clients) {
                    clients.remove(this);
                }
            }
        }

        public void sendMessage(String msg) {
            try {
                sOutput.writeObject(msg);
            } catch (IOException e) {
                logger.log(Level.WARNING, "Error sending message to client", e);
            }
        }

        public String readMessage() {
            try {
                if (sInput != null)
                    return (String) sInput.readObject();
            } catch (IOException | ClassNotFoundException e) {
                logger.log(Level.WARNING, "Error reading message from client", e);
            }
            return null;
        }
    }

    public static void main(String[] args) {
        Server server = new Server(1500);
        server.start();
    }
}
