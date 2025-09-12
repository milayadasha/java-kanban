package ru.yandex.javacourse.service;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import ru.yandex.javacourse.model.Epic;
import ru.yandex.javacourse.model.Subtask;
import ru.yandex.javacourse.model.Task;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class InMemoryTaskManagerTest {
    private static final String TASK_NAME = "Задача";
    private static final String TASK_DESCRIPTION = "Новая задача";
    private static final int PREDEFINED_TASK_ID = 99;
    private static final String EPIC_NAME = "Эпик";
    private static final String EPIC_DESCRIPTION = "Новый эпик";
    private static final String SUBTASK_NAME = "Подзадача";
    private static final String SUBTASK_DESCRIPTION = "Новая подзадача";

    TaskManager taskManager = Managers.getDefault();

    @Test
    @DisplayName("Должен успешно добавлять задачу и возвращать ее по ID")
    public void test_AddTask_WhenTaskAddedToManager_ShouldBeRetrievableById() {
        //given
        Task task = taskManager.addTask(new Task(TASK_NAME,TASK_DESCRIPTION));

        //when
        Task findTask = taskManager.getTaskById(task.getId());

        //then
        assertNotNull(findTask, "Задача не найдена.");
        assertEquals(task, findTask, "Задачи не совпадают.");

        List<Task> tasksList = taskManager.getAllTasks();

        assertNotNull(tasksList, "Задачи не возвращаются");
        assertEquals(1, tasksList.size(), "Неверное количество задач.");
        assertEquals(task, tasksList.get(0), "Задачи не совпадают.");
    }

    @Test
    @DisplayName("Должен успешно добавлять эпик и возвращать его  по ID")
    public void test_AddEpic_WhenEpicAddedToManager_ShouldBeRetrievableById() {
        //given
        Epic epic = taskManager.addEpic(new Epic(EPIC_NAME,EPIC_DESCRIPTION));

        //when
        Epic findEpic = taskManager.getEpicById(epic.getId());

        //then
        assertNotNull(findEpic, "Эпик не найден.");
        assertEquals(epic, findEpic, "Эпики не совпадают.");

        List<Epic> epicsList = taskManager.getAllEpics();

        assertNotNull(epicsList, "Эпики не возвращаются");
        assertEquals(1, epicsList.size(), "Неверное количество эпиков.");
        assertEquals(epic, epicsList.get(0), "Эпики не совпадают.");
    }

    @Test
    @DisplayName("Должен успешно добавлять подзадачу и возвращать ее по ID")
    public void test_AddSubtask_WhenSubtaskAddedToManager_ShouldBeRetrievableById() {
        //given
        Epic epic = taskManager.addEpic(new Epic(EPIC_NAME,EPIC_DESCRIPTION));
        Subtask subtask = taskManager.addSubtask(new Subtask(SUBTASK_NAME,SUBTASK_DESCRIPTION,
                epic.getId()));

        //when
        Subtask findSubtask = taskManager.getSubtaskById(subtask.getId());

        //then
        assertNotNull(findSubtask,"Подзадача не найдена.");
        assertEquals(subtask, findSubtask, "Подзадачи не совпадают.");

        List<Subtask> subtasksList = taskManager.getAllSubtasks();

        assertNotNull(subtasksList, "Подзадачи не возвращаются");
        assertEquals(1, subtasksList.size(), "Неверное количество подзадач.");
        assertEquals(subtask, subtasksList.get(0), "Подзадачи не совпадают.");
    }

    @Test
    @DisplayName("Должен генерировать уникальный ID при добавлении задачи с установленным ID")
    public void test_AddTask_WhenTaskHasPredefinedId_ShouldGenerateNewId() {
        //given
        Task task = new Task(TASK_NAME, TASK_DESCRIPTION);
        task.setId(PREDEFINED_TASK_ID);

        //when
        Task createdTask = taskManager.addTask(task);

        //then
        assertNotEquals(task.getId(),createdTask.getId(), "Менеджер не генерит уникальный id во время" +
                "добавления задачи");

    }

}