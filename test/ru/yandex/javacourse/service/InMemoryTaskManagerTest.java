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
    private static final String TASK_NAME_UPDATED = "Обновлённая задача";
    private static final String TASK_DESCRIPTION_UPDATED = "Новая задача";
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

    @Test
    @DisplayName("Должен сохранять задачу в неизменном виде после добавления в менеджер")
    public void test_AddTask_WhenTaskUpdatedAfterAddToTaskManager_TaskShouldRemainUnchanged() {
        //given
        Task task = taskManager.addTask(new Task(TASK_NAME,TASK_DESCRIPTION));

        //when
        task.setName(TASK_NAME_UPDATED);
        task.setDescription(TASK_DESCRIPTION_UPDATED);

        //then
        Task findTask = taskManager.getTaskById(task.getId());
        assertEquals(TASK_NAME, findTask.getName(), "Задачи не совпадают по name.");
        assertEquals(TASK_DESCRIPTION, findTask.getDescription(), "Задачи не совпадают по description.");
    }

    @Test
    @DisplayName("При удалении задачи из менеджера она должна быть удалена из истории")
    public void test_DeleteTaskById_WhenTaskRemoved_HistoryShouldBeEmpty() {
        //given
        Task task = taskManager.addTask(new Task(TASK_NAME,TASK_DESCRIPTION));
        taskManager.getTaskById(task.getId());

        //when
        taskManager.deleteTaskById(task.getId());

        //then
        final List<Task> history = taskManager.getHistory();
        assertEquals(0,history.size(), "История после удаления задачи должна быть пустой");
    }

    @Test
    @DisplayName("При удалении эпика из менеджера он должен быть удален из истории")
    public void test_DeleteEpicById_WhenEpicRemoved_HistoryShouldBeEmpty() {
        //given
        Epic epic  = taskManager.addEpic(new Epic(EPIC_NAME,EPIC_DESCRIPTION));
        taskManager.getEpicById(epic.getId());

        //when
        taskManager.deleteEpicById(epic.getId());

        //then
        final List<Task> history = taskManager.getHistory();
        assertEquals(0,history.size(), "История после удаления эпика должна быть пустой");
    }

    @Test
    @DisplayName("При удалении подзадачи из менеджера она должна быть удален из истории")
    public void test_DeleteSubtaskById_WhenSubtaskRemoved_HistoryShouldBeEmpty() {
        //given
        Epic epic = taskManager.addEpic(new Epic(EPIC_NAME,EPIC_DESCRIPTION));
        Subtask subtask = taskManager.addSubtask(new Subtask(SUBTASK_NAME,SUBTASK_DESCRIPTION,
                epic.getId()));
        taskManager.getSubtaskById(subtask.getId());


        //when
        taskManager.deleteSubtaskById(subtask.getId());

        //then
        final List<Task> history = taskManager.getHistory();
        assertEquals(0,history.size(), "История после удаления подзадачи должна быть пустой");
    }

    @Test
    @DisplayName("При удалении подзадач из менеджера она должна быть удален из списка id подзадач эпика")
    public void test_DeleteAllSubtask_WhenSubtasksRemoved_EpicSubtasksIdListShouldBeEmpty() {
        //given
        Epic epic = taskManager.addEpic(new Epic(EPIC_NAME,EPIC_DESCRIPTION));
        Subtask subtask1 = taskManager.addSubtask(new Subtask(SUBTASK_NAME,SUBTASK_DESCRIPTION,
                epic.getId()));
        Subtask subtask2 = taskManager.addSubtask(new Subtask(SUBTASK_NAME,SUBTASK_DESCRIPTION,
                epic.getId()));


        //when
        taskManager.deleteAllSubtasks();

        //then
        assertEquals(0,epic.getSubtasksIdList().size(), "После удаления подзадач внутри эпиков" +
                " не должно оставаться неактуальных id подзадач");
    }

    @Test
    @DisplayName("При удалении эпика из менеджера его подзадачи должны быть удалены из списка подзадач")
    public void test_DeleteEpicById_WhenSEpicRemoved_SubtasksShouldBeEmpty() {
        //given
        Epic epic = taskManager.addEpic(new Epic(EPIC_NAME,EPIC_DESCRIPTION));
        Subtask subtask1 = taskManager.addSubtask(new Subtask(SUBTASK_NAME,SUBTASK_DESCRIPTION,
                epic.getId()));
        Subtask subtask2 = taskManager.addSubtask(new Subtask(SUBTASK_NAME,SUBTASK_DESCRIPTION,
                epic.getId()));


        //when
        taskManager.deleteEpicById(epic.getId());
        List<Subtask> subtasks = taskManager.getAllSubtasks();

        //then
        assertEquals(0,subtasks.size(), "После удаления эпика список подзадач должен быть очищен");
    }
}