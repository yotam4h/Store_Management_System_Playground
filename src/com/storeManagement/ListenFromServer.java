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
            String message = client.readMessage();
            if (message == null || "CHAT_ENDED".equalsIgnoreCase(message)) {
                keepGoing = false;
                System.out.println("Chat ended by the server.");
            } else {
                System.out.println(message);
            }

            if (Thread.currentThread().isInterrupted()) {
                keepGoing = false;
            }
        }
    }

}