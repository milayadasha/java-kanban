package ru.yandex.javacourse.service;

import ru.yandex.javacourse.model.Task;

import java.util.List;
import java.util.Map;
import java.util.ArrayList;
import java.util.HashMap;

public class InMemoryHistoryManager implements HistoryManager {
    private Node<Task> head;
    private Node<Task> tail;
    private final Map<Integer, Node<Task>> idToNode = new HashMap<>();
    private int size = 0;

    @Override
    public List<Task> getHistory() {
        List<Task> historyList = new ArrayList<>(size);
        Node<Task> current = head;
        while (current != null) {
            historyList.add(current.data);
            current = current.next;
        }
        return historyList;
    }

    @Override
    public void add(Task task) {
        if (task == null) {
            return;
        }
        int taskId = task.getId();
        remove(taskId);
        linkLast(task);
    }

    @Override
    public void remove(int id) {
        Node<Task> node = idToNode.get(id);
        if (node != null) {
            removeNode(node);
        }
    }

    private void linkLast(Task task) {
        if (task == null) {
            return;
        }

        Task taskCopy = task.getCopy();
        if (taskCopy == null) {
            return;
        }

        Node<Task> oldTail = tail;
        Node<Task> createdNode = new Node<>(oldTail, taskCopy, null);
        tail = createdNode;
        if (oldTail == null) {
            head = createdNode;
        } else {
            oldTail.next = createdNode;
        }
        idToNode.put(taskCopy.getId(), createdNode);
        size++;
    }

    public List<Task> getTasks() {
        List<Task> allTasks = new ArrayList<>();
        for (Node<Task> node : idToNode.values()) {
            allTasks.add(node.data);
        }
        return allTasks;
    }

    private void removeNode(Node<Task> node) {
        if (node == null) {
            return;
        }

        Node<Task> prevNode = node.prev;
        Node<Task> nextNode = node.next;

        if (prevNode == null) {
            head = nextNode;
        } else {
            prevNode.next = nextNode;
        }

        if (nextNode == null) {
            tail = prevNode;
        } else {
            nextNode.prev = prevNode;
        }

        node.prev = null;
        node.next = null;
        idToNode.remove(node.data.getId());
        size--;
    }
}
