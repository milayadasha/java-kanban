package ru.yandex.javacourse.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import ru.yandex.javacourse.model.Task;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class InMemoryHistoryManagerTest {
    private static final String TASK_NAME = "Задача 1";
    private static final String TASK_DESCRIPTION = "Первая задача";
    private static final String TASK_NAME_2 = "Задача 2";
    private static final String TASK_DESCRIPTION_2 = "Вторая задача";
    private static final Integer TASK_ID = 1;
    private static final Integer TASK_ID_2 = 2;

    Task task;
    HistoryManager historyManager = Managers.getDefaultHistory();

    @BeforeEach
    @DisplayName("Cоздаёт перед каждым тестом новую задачу")
    public void create() {
        task = new Task(TASK_NAME,TASK_DESCRIPTION);
    }

    @Test
    @DisplayName("Должен возвращать true, если удалось удалить задачу из истории")
    void test_Remove_WhenTaskRemoveInHistoryManager_HistoryShouldBeEmpty() {
        //given
        historyManager.add(task);

        //when
        historyManager.remove(task.getId());
        final List<Task> history = historyManager.getHistory();

        //then
        assertTrue(history.isEmpty(), "После удаления задачи история должна быть пустой.");
    }

    @Test
    @DisplayName("Должен возвращать true, если при повторном добавлении задача переместилась в конец истории")
    void test_Add_TaskAddedTwiceToHistoryManager_ShouldBeLast() {
        //given
        task.setId(TASK_ID);
        Task task2 = new Task(TASK_NAME_2,TASK_DESCRIPTION_2);
        task2.setId(TASK_ID_2);

        //when
        historyManager.add(task);
        historyManager.add(task2);
        historyManager.add(task);
        final List<Task> history = historyManager.getHistory();

        //then
        assertEquals(2, history.size(), "После добавления задач история должна быть без дублей.");
        assertEquals(task.getId(),history.get(history.size() - 1).getId(), "При повторном добавлении задачи " +
                "она должна оказаться в конце истории");
    }
}
