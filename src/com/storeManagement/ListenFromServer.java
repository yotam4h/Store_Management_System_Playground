package com.storeManagement;

import java.io.IOException;
import java.io.ObjectInputStream;

public class ListenFromServer extends Thread {
    private final Client client;

    public ListenFromServer( Client client) {
        this.client = client;
    }

    public void run() {
        boolean keepGoing = true;
        while (keepGoing) {
            try {
                String msg = (String) client.sInput.readObject();
                if (msg.equals("CHAT_ENDED")) {
                    client.display("Chat ended.");
                    keepGoing = false;
                }
                System.out.println(msg);
            } catch (IOException e) {
                client.display("Server has closed the connection: " + e);
                break;
            } catch (ClassNotFoundException e2) {
                client.display("Class not found: " + e2);
            }

            if (Thread.currentThread().isInterrupted()) {
                keepGoing = false;
            }
        }
    }
}