package com.storeManagement;

import com.storeManagement.dataAccessObject.UserDao;
import com.storeManagement.model.User;
import com.storeManagement.utils.Constants;

import java.io.*;
import java.net.*;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

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

    public Server(int port)
    {
        this.port = port;
        clients = new ArrayList<ClientThread>();
        userDao = new UserDao();
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
            if(!ct.writeMsg(messageLf))
            {
                clients.remove(i);
                display("Disconnected Client " + ct.username + " removed from list.");
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

    class ClientThread extends Thread
    {
        Socket socket;
        ObjectInputStream sInput;
        ObjectOutputStream sOutput;
        int id;
        String username, password;
        Constants.EmployeeRole role;

        ClientThread(Socket socket)
        {
            id = ++uniqueId;
            this.socket = socket;
            try
            {
                sOutput = new ObjectOutputStream(socket.getOutputStream());
                sInput = new ObjectInputStream(socket.getInputStream());

                username = (String) sInput.readObject();
                password = (String) sInput.readObject();

                // check if the user is already in the list
                for(int i = 0; i < clients.size(); ++i)
                {
                    ClientThread ct = clients.get(i);
                    if(ct.username.equals(username))
                    {
                        writeMsg("ALREADY_LOGGED_IN");
                        close();
                        return;
                    }
                }

                // check if username and password are correct through the database
                if(!authenticateUser(username, password))
                {
                    writeMsg("INVALID");
                    close();
                    return;
                }
                display(username + " just connected.");
                writeMsg("SUCCESS");

                role = Constants.EmployeeRole.valueOf(userDao.getByUsername(username).getRole());

                writeMsg(role.toString());
            }
            catch (IOException e)
            {
                display("Exception creating new Input/output Streams: " + e);
                return;
            }
            catch (ClassNotFoundException e)
            {
            } catch (SQLException e)
            {
                throw new RuntimeException(e);
            }
        }

        public void run()
        {
            boolean keepGoing = true;

            while(keepGoing)
            {
                // IF SERVER GOT "DISCONNECT" MESSAGE
                try
                {
                    String msg = (String) sInput.readObject();
                    if (msg.equalsIgnoreCase("DISCONNECT"))
                    {
                        keepGoing = false;
                    }
                } catch (IOException e)
                {
                    display(username + " Exception reading Streams: " + e);
                    break;
                } catch (ClassNotFoundException e)
                {
                    break;
                }
            }

            remove(id);
            close();
        }

        private void close()
        {
            try
            {
                if(sOutput != null) sOutput.close();
            }
            catch(Exception e) {}
            try
            {
                if(sInput != null) sInput.close();
            }
            catch(Exception e) {};
            try
            {
                if(socket != null) socket.close();
            }
            catch (Exception e) {}
        }

        private boolean writeMsg(String msg)
        {
            if(!socket.isConnected())
            {
                close();
                return false;
            }
            try
            {
                sOutput.writeObject(msg);
            }
            catch(IOException e)
            {
                display("Error sending message to " + username);
                display(e.toString());
            }
            return true;
        }

    }
}