package ru.yandex.javacourse.service;

import ru.yandex.javacourse.model.Epic;
import ru.yandex.javacourse.model.Subtask;
import ru.yandex.javacourse.model.Task;
import java.util.LinkedList;
import java.util.List;

public class InMemoryHistoryManager implements HistoryManager {
    private final List<Task> viewedTasks = new LinkedList<>();
    private static final int MAX_HISTORY_SIZE = 10;

    @Override
    public List<Task> getHistory() {
        return new LinkedList<>(viewedTasks);
    }

    @Override
    public void add(Task task) {
        if (task == null) {
            return;
        }

        if (viewedTasks.size() == MAX_HISTORY_SIZE) {
            viewedTasks.removeFirst();
        }

        if (task instanceof Epic epic) {
            Epic savedEpic = epic.getCopy();
            viewedTasks.add(savedEpic);
        } else if (task instanceof Subtask subtask) {
            Subtask savedSubtask = subtask.getCopy();
            viewedTasks.add(savedSubtask);
        } else {
            Task savedTask = task.getCopy();
            viewedTasks.add(savedTask);
        }
    }

}
