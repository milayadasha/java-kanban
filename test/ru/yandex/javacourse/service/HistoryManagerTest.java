package ru.yandex.javacourse.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.yandex.javacourse.model.Task;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class HistoryManagerTest {
    Task task;
    HistoryManager historyManager = Managers.getDefaultHistory();

    @BeforeEach
    public void create() {
        task = new Task("Задача 1","Новая задача");
    }

    @Test
    void add() {
        historyManager.add(task);
        final List<Task> history = historyManager.getHistory();
        assertNotNull(history, "После добавления задачи, история не должна быть пустой.");
        assertEquals(1, history.size(), "После добавления задачи, история не должна быть пустой.");
    }

    @Test
    public void historyManagerShouldSavePreviousVersion() {
        historyManager.add(task);
        task.setDescription("Обновлённая задача");
        historyManager.add(task);

        List<Task> historyList = historyManager.getHistory();

        assertEquals(2,historyList.size(), "Обновлённая задача не добавлена.");
        assertNotEquals(historyList.get(0).getDescription(), historyList.get(1).getDescription(),
                "Менеджер хранит старую версию задачи");
    }


}