package ru.yandex.javacourse.service;

import org.junit.jupiter.api.Test;
import ru.yandex.javacourse.model.Task;
import static org.junit.jupiter.api.Assertions.*;

class TaskManagerTest {
    TaskManager taskManager = Managers.getDefault();

    @Test
    public void taskRemainedUnchangedAfterAdding() {
        Task task = taskManager.addTask(new Task("Задача 1","Новая задача"));
        Task findTask = taskManager.getTaskById(task.getId());

        assertEquals(task.getId(), findTask.getId(), "Задачи не совпадают по id.");
        assertEquals(task.getName(), findTask.getName(), "Задачи не совпадают по name.");
        assertEquals(task.getDescription(), findTask.getDescription(), "Задачи не совпадают по description.");
    }

}