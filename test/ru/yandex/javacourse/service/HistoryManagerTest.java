package ru.yandex.javacourse.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import ru.yandex.javacourse.model.Task;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class HistoryManagerTest {
    private static final String TASK_NAME = "Задача";
    private static final String TASK_DESCRIPTION = "Описание задачи";
    private static final String TASK_DESCRIPTION_UPDATED = "Обновлённое описание задачи";
    private static final int TASK_ID = 1;

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
    @DisplayName("Должен возвращать true, если менеджер хранит только последнюю версию задачи")
    public void test_Add_AfterUpdatedTaskAddToHistoryManager_ShouldStoreLastVersion() {
        //given
        historyManager.add(task);
        Task updatedTask = task.getCopy();
        updatedTask.setDescription(TASK_DESCRIPTION_UPDATED);

        //when
        historyManager.add(updatedTask);
        List<Task> historyList = historyManager.getHistory();

        //then
        assertEquals(1,historyList.size(), "История хранит больше одной версии задачи");
        assertNotEquals(task.getDescription(), historyList.get(0).getDescription(),
                "Менеджер хранит старую версию задачи");
    }

    @Test
    @DisplayName("Должен возвращать true, если менеджер хранит только последнюю версию задачи")
    public void test_Remove_AfterRemoveMiddleTask_ShouldRemainOrder() {
        //given
        task.setId(TASK_ID);
        Task task2 = new Task(TASK_NAME + " " + (TASK_ID + 1), TASK_DESCRIPTION + " " + (TASK_ID + 1));
        task2.setId(TASK_ID + 1);

        Task task3 = new Task(TASK_NAME + " " + (TASK_ID + 2), TASK_DESCRIPTION + " " + (TASK_ID + 2));
        task3.setId(TASK_ID + 2);

        //when
        historyManager.add(task);
        historyManager.add(task2);
        historyManager.add(task3);
        historyManager.remove(task2.getId());
        List<Task> historyList = historyManager.getHistory();

        //then
        assertEquals(2,historyList.size(), "Из истории должна быть удалена одна задача");
        assertEquals(task3.getId(), historyList.get(1).getId(), "Вторая из середины не удалена");
    }

}