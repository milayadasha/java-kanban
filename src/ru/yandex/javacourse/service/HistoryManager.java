package ru.yandex.javacourse.service;

import ru.yandex.javacourse.model.Task;
import java.util.List;

public interface HistoryManager {

    List<Task> getHistory();

    void add(Task task);
}
