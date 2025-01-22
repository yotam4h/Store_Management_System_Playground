/*
package com.storeManagement;

import java.util.Map;
import java.util.Queue;
import java.util.concurrent.ConcurrentHashMap;

public class ChatManager
{
    private Map<Integer, Queue<Server.ClientThread>> employees = new ConcurrentHashMap<>();
    private boolean manager = false;

    public synchronized void addEmployeeToQueue(Server.ClientThread employee) {
        employees.putIfAbsent(employee.branchId, new java.util.LinkedList<>());
        employees.get(employee.branchId).add(employee);

        Integer otherBranchId = null;

        for(Integer id : employees.keySet()) {
            if(!id.equals(employee.branchId)) {
                otherBranchId = id;
                break;
            }
        }

        if (otherBranchId != null && !employees.get(otherBranchId).isEmpty()) {
            JoinChat(employee.branchId, otherBranchId);
        }
    }

    public synchronized void removeEmployeeFromQueue(Server.ClientThread employee) {
        if (employees.containsKey(employee.branchId)) {
            employees.get(employee.branchId).remove(employee);
        }
    }

    public synchronized void JoinChat(Integer branchId, Integer otherBranchId) {
        while (employees.containsKey(branchId) && !employees.get(branchId).isEmpty() && employees.containsKey(otherBranchId) && !employees.get(otherBranchId).isEmpty()) {
            Server.ClientThread employee1 = employees.get(branchId).poll();
            Server.ClientThread employee2 = employees.get(otherBranchId).poll();

            if (employee1 != null && employee2 != null) {
                startChat(employee1, employee2);
            }
        }
    }

    public void startChat(Server.ClientThread employee1, Server.ClientThread employee2) {
        // Notify both employees
        employee1.sendMessage("You are now talking to " + employee2.username);
        employee2.sendMessage("You are now talking to " + employee1.username);

        // Start bidirectional chat in separate threads
        new Thread(() -> forwardMessages(employee1, employee2)).start();
        new Thread(() -> forwardMessages(employee2, employee1)).start();
    }

    private void forwardMessages(Server.ClientThread sender, Server.ClientThread receiver) {
        try {
            String message;
            while ((message = sender.readMessage()) != null) {
                receiver.sendMessage(message);
            }
        } catch (Exception e) {
            System.err.println("Error forwarding messages: " + e.getMessage());
        } finally {
            removeEmployeeFromQueue(sender);
            sender.sendMessage("CHAT_ENDED");
        }
    }
}
*/

package com.storeManagement;

import java.util.Map;
import java.util.Queue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;

public class ChatManager {
    private Map<Integer, Queue<Server.ClientThread>> employees = new ConcurrentHashMap<>();

    public synchronized void addEmployeeToQueue(Server.ClientThread employee) {
        employees.putIfAbsent(employee.branchId, new ConcurrentLinkedQueue<>());
        employees.get(employee.branchId).add(employee);

        Integer otherBranchId = employees.keySet().stream()
                .filter(id -> !id.equals(employee.branchId) && employees.get(id) != null && !employees.get(id).isEmpty())
                .findFirst()
                .orElse(null);

        if (otherBranchId != null) {
            JoinChat(employee.branchId, otherBranchId);
        }
    }

    public synchronized void removeEmployeeFromQueue(Server.ClientThread employee) {
        Queue<Server.ClientThread> queue = employees.get(employee.branchId);
        if (queue != null) {
            queue.remove(employee);
        }
    }

    public synchronized void JoinChat(Integer branchId, Integer otherBranchId) {
        Queue<Server.ClientThread> queue1 = employees.get(branchId);
        Queue<Server.ClientThread> queue2 = employees.get(otherBranchId);

        if (queue1 == null || queue2 == null) return;

        while (!queue1.isEmpty() && !queue2.isEmpty()) {
            Server.ClientThread employee1 = queue1.poll();
            Server.ClientThread employee2 = queue2.poll();

            if (employee1 != null && employee2 != null) {
                startChat(employee1, employee2);
            }
        }
    }

    public void startChat(Server.ClientThread employee1, Server.ClientThread employee2) {
        employee1.sendMessage("You are now talking to " + employee2.username);
        employee2.sendMessage("You are now talking to " + employee1.username);

        new Thread(() -> forwardMessages(employee1, employee2)).start();
        new Thread(() -> forwardMessages(employee2, employee1)).start();
    }

    private void forwardMessages(Server.ClientThread sender, Server.ClientThread receiver) {
        try {
            String message;
            while ((message = sender.readMessage()) != null) {
                receiver.sendMessage(message);
            }
        } catch (Exception e) {
            System.err.println("Error forwarding messages: " + e.getMessage());
        } finally {
            removeEmployeeFromQueue(sender);
            sender.sendMessage("CHAT_ENDED");
        }
    }
}
