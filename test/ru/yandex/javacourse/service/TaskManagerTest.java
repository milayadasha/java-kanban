package ru.yandex.javacourse.service;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import ru.yandex.javacourse.model.Task;
import static org.junit.jupiter.api.Assertions.*;

class TaskManagerTest {
    private static final String TASK_NAME = "Задача";
    private static final String TASK_DESCRIPTION = "Новая задача";
    TaskManager taskManager = Managers.getDefault();

    @Test
    @DisplayName("Должен сохранять задачу в неизменном виде после добавления в менеджер")
    public void test_AddTask_WhenTaskAddedToTaskManager_TaskShouldRemainUnchanged() {
        //given
        Task task = taskManager.addTask(new Task(TASK_NAME,TASK_DESCRIPTION));

        //when
        Task findTask = taskManager.getTaskById(task.getId());

        //then
        assertEquals(task.getId(), findTask.getId(), "Задачи не совпадают по id.");
        assertEquals(task.getName(), findTask.getName(), "Задачи не совпадают по name.");
        assertEquals(task.getDescription(), findTask.getDescription(), "Задачи не совпадают по description.");
    }

}