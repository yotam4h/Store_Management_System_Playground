package com.storeManagement;

import java.util.Map;
import java.util.Queue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;

public class ChatManager {
    private final Map<Integer, Queue<ClientThread>> employees = new ConcurrentHashMap<>();

    public synchronized void addEmployeeToQueue(ClientThread employee) {
        employees.putIfAbsent(employee.getBranchId(), new ConcurrentLinkedQueue<>());
        employees.get(employee.getBranchId()).add(employee);

        Integer otherBranchId = employees.keySet().stream()
                .filter(id -> !id.equals(employee.getBranchId()) && employees.get(id) != null && !employees.get(id).isEmpty())
                .findFirst()
                .orElse(null);

        if (otherBranchId != null) {
            JoinChat(employee.getBranchId(), otherBranchId);
        }
    }

    public synchronized void removeEmployeeFromQueue(ClientThread employee) {
        Queue<ClientThread> queue = employees.get(employee.getBranchId());
        if (queue != null) {
            queue.remove(employee);
        }
    }

    public synchronized void JoinChat(Integer branchId, Integer otherBranchId) {
        Queue<ClientThread> queue1 = employees.get(branchId);
        Queue<ClientThread> queue2 = employees.get(otherBranchId);

        if (queue1 == null || queue2 == null) return;

        while (!queue1.isEmpty() && !queue2.isEmpty()) {
            ClientThread employee1 = queue1.poll();
            ClientThread employee2 = queue2.poll();

            if (employee1 != null && employee2 != null) {
                startChat(employee1, employee2);
            }
        }
    }

    public void startChat(ClientThread employee1, ClientThread employee2) {
        employee1.setInChat(true);
        employee2.setInChat(true);
        employee1.sendMessage("You are now talking to " + employee2.getUsername());
        employee2.sendMessage("You are now talking to " + employee1.getUsername());

        new Thread(() -> forwardMessages(employee1, employee2)).start();
        new Thread(() -> forwardMessages(employee2, employee1)).start();
    }

    private void forwardMessages(ClientThread sender, ClientThread receiver) {
        try {
            String message;
            while ((message = sender.readMessage()) != null) {
                receiver.sendMessage(message);
            }
        } catch (Exception e) {
            System.err.println("Error forwarding messages: " + e.getMessage());
        } finally {
            sender.setInChat(false);
            receiver.setInChat(false);
        }
    }
}
