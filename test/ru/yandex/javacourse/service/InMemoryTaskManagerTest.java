package ru.yandex.javacourse.service;

import org.junit.jupiter.api.Test;
import ru.yandex.javacourse.model.Epic;
import ru.yandex.javacourse.model.Subtask;
import ru.yandex.javacourse.model.Task;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class InMemoryTaskManagerTest {
    TaskManager taskManager = Managers.getDefault();

    @Test
    public void canInMemoryTaskManagerAddTask() {
        Task task = taskManager.addTask(new Task("Задача 1","Новая задача"));
        Task findTask = taskManager.getTaskById(task.getId());

        assertNotNull(findTask, "Задача не найдена.");
        assertEquals(task, findTask, "Задачи не совпадают.");

        List<Task> tasksList = taskManager.getAllTasks();

        assertNotNull(tasksList, "Задачи не возвращаются");
        assertEquals(1, tasksList.size(), "Неверное количество задач.");
        assertEquals(task, tasksList.get(0), "Задачи не совпадают.");
    }

    @Test
    public void canInMemoryTaskManagerAddEpic() {
        Epic epic = taskManager.addEpic(new Epic("Эпик 1","Новый эпик"));
        Epic findEpic = taskManager.getEpicById(epic.getId());

        assertNotNull(findEpic, "Эпик не найден.");
        assertEquals(epic, findEpic, "Эпики не совпадают.");

        List<Epic> epicsList = taskManager.getAllEpics();

        assertNotNull(epicsList, "Эпики не возвращаются");
        assertEquals(1, epicsList.size(), "Неверное количество эпиков.");
        assertEquals(epic, epicsList.get(0), "Эпики не совпадают.");
    }

    @Test
    public void canInMemoryTaskManagerAddSubtask() {
        Epic epic = taskManager.addEpic(new Epic("Эпик 1","Новый эпик"));
        Subtask subtask = taskManager.addSubtask(new Subtask("Подзадача 1","Новая подзадча",
                epic.getId() ));
        Subtask findSubtask = taskManager.getSubtaskById(subtask.getId());

        assertNotNull(findSubtask,"Подзадача не найдена.");
        assertEquals(subtask, findSubtask, "Подзадачи не совпадают.");

        List<Subtask> subtasksList = taskManager.getAllSubtasks();

        assertNotNull(subtasksList, "Подзадачи не возвращаются");
        assertEquals(1, subtasksList.size(), "Неверное количество подзадач.");
        assertEquals(subtask, subtasksList.get(0), "Подзадачи не совпадают.");
    }

    @Test
    public void addTaskOnlyWithGeneratingId() {
        Task task = new Task("Задача", "Новая задача");
        task.setId(999);
        Task createdTask = taskManager.addTask(task);

        assertNotEquals(task.getId(),createdTask.getId(), "менеджер не генерит уникальный id во время" +
                "добавления задачи");

    }

}