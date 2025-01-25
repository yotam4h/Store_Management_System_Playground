package com.storeManagement;

import java.util.Objects;

public class ChatSession {
    private ClientThread client1;
    private ClientThread client2;

    public ChatSession(ClientThread client1, ClientThread client2) {
        this.client1 = client1;
        this.client2 = client2;
    }

    // Check if this chat session contains a particular client
    public boolean contains(ClientThread client) {
        return client1.equals(client) || client2.equals(client);
    }

    // Get the other client in the chat session
    public ClientThread getOtherClient(ClientThread client) {
        if (client.equals(client1)) {
            return client2;
        } else if (client.equals(client2)) {
            return client1;
        }
        return null;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ChatSession that = (ChatSession) o;
        return (Objects.equals(client1, that.client1) && Objects.equals(client2, that.client2)) ||
                (Objects.equals(client1, that.client2) && Objects.equals(client2, that.client1));
    }

    @Override
    public int hashCode() {
        return Objects.hash(client1, client2) + Objects.hash(client2, client1);  // To ensure order does not matter
    }
}
