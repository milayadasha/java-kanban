package ru.yandex.javacourse.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import ru.yandex.javacourse.model.Task;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class HistoryManagerTest {
    private static final String TASK_NAME = "Задача 1";
    private static final String TASK_DESCRIPTION = "Первая задача";
    private static final String TASK_DESCRIPTION_UPDATED = "Обновлённая задача";

    Task task;
    HistoryManager historyManager = Managers.getDefaultHistory();

    @BeforeEach
    @DisplayName("Cоздаёт перед каждым тестом новую задачу")
    public void create() {
        task = new Task(TASK_NAME,TASK_DESCRIPTION);
    }

    @Test
    @DisplayName("Должен возвращать true, если удалось добавить задачу в менеджер")
    void test_Add_WhenTaskAddedToHistoryManager_ShouldReturnTrue() {
        //given
        historyManager.add(task);

        //when
        final List<Task> history = historyManager.getHistory();

        //then
        assertNotNull(history, "После добавления задачи, история не должна быть пустой.");
        assertEquals(1, history.size(), "После добавления задачи, история не должна быть пустой.");
    }

    @Test
    @DisplayName("Должен возвращать true, если менеджер хранит и старую версию задачи, и новую")
    public void test_Add_AfterUpdatedTaskAddToHistoryManager_ShouldStoreBothVersions() {
        //given
        historyManager.add(task);
        Task updatedTask = new Task(task.getName(), TASK_DESCRIPTION_UPDATED);

        //when
        historyManager.add(updatedTask);
        List<Task> historyList = historyManager.getHistory();

        //then
        assertEquals(2,historyList.size(), "Обновлённая задача не добавлена.");
        assertNotEquals(historyList.get(0).getDescription(), historyList.get(1).getDescription(),
                "Менеджер хранит старую версию задачи");
    }

}