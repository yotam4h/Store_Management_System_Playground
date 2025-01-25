package com.storeManagement;

import java.io.IOException;
import java.io.ObjectInputStream;

class ListenFromServer extends Thread {
    private final Client client;

    public ListenFromServer(Client client) {
        this.client = client;
    }

    public void run() {
        boolean keepGoing = true;
        while (keepGoing) {
            if (Thread.currentThread().isInterrupted()) {
                keepGoing = false;
            }

//            String message = client.readMessage();
//            if ("CHAT_ENDED".equalsIgnoreCase(message)) {
//                keepGoing = false;
//                System.out.println("Chat ended by the server.");
//            } else if (message == null)
//            {
//                keepGoing = false;
//            } else
//            {
//                System.out.println(message);
//            }
            String message = "";
            try {
                message = client.readMessage();
            } catch (IOException | ClassNotFoundException e) {
                if (e.getMessage() != null)
                    System.out.println("Error reading message from server: " + e.getMessage());
            } finally {
                if ("CHAT_ENDED".equalsIgnoreCase(message)) {
                    keepGoing = false;
                    System.out.println("Chat ended by the server.");
                } else if (message == null) {
                    keepGoing = false;
                } else {
                    System.out.println(message);
                }
            }

        }
    }

}