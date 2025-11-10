package ru.yandex.javacourse.service;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import ru.yandex.javacourse.model.Task;
import java.time.Duration;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

class InMemoryTaskManagerTest extends TaskManagerTest<TaskManager> {
    private static final String TASK_NAME = "Задача";
    private static final String TASK_DESCRIPTION = "Новая задача";
    private static final Integer TASK_ID = 1;

    @Override
    protected TaskManager createTaskManager() {
        return Managers.getDefault();
    }

    @Test
    @DisplayName("Должен возвращать false, если при поиске пересечений для сравнения переданы две одинаковые задачи")
    public void test_ifTasksCrossInTime_WhenTaskEquals_ShouldReturnFalse() {
        //given
        LocalDateTime now = LocalDateTime.now();
        Duration taskDuration = Duration.ofMinutes(30);
        Task task = taskManager.addTask(new Task(TASK_NAME, TASK_DESCRIPTION,taskDuration, now));
        task.setId(TASK_ID);

        //when
        Task firstTask = taskManager.getTaskById(task.getId());
        Task secondTask = taskManager.getTaskById(task.getId());
        boolean hasCross = taskManager.ifTasksCrossInTime(firstTask,secondTask);

        //then
        assertFalse(hasCross, "Пересечение не должно считаться, если задача сравнивается сама с собой.");
    }

    @Test
    @DisplayName("Должен возвращать true, если вторая задача начинается до конца первой")
    public void test_ifTasksCrossInTime_WhenTaskStartsBeforeOtherFinish_ShouldReturnTrue() {
        //given
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime nowPlus = now.plusHours(1);
        Duration taskDuration = Duration.ofHours(10);
        Task firstTask = new Task(TASK_NAME, TASK_DESCRIPTION,taskDuration, now);
        firstTask.setId(TASK_ID);
        Task secondTask = new Task(TASK_NAME, TASK_DESCRIPTION,taskDuration, nowPlus);
        secondTask.setId(TASK_ID + 1);

        //when
        boolean hasCross = taskManager.ifTasksCrossInTime(firstTask,secondTask);

        //then
        assertTrue(hasCross, "Задача не может начаться пока другая задача не закончится.");
    }

    @Test
    @DisplayName("Должен возвращать true, если первая задача начинается до конца второй задачи")
    public void test_ifTasksCrossInTime_WhenTaskStartsBeforeOtherFinishSwitch_ShouldReturnTrue() {
        //given
        LocalDateTime secondStart = LocalDateTime.now();
        LocalDateTime firstStart = secondStart.plusHours(3);
        Duration firstDuration = Duration.ofHours(10);
        Duration secondDuration = Duration.ofHours(4);

        Task firstTask = new Task(TASK_NAME, TASK_DESCRIPTION,firstDuration, firstStart);
        firstTask.setId(TASK_ID);
        Task secondTask = new Task(TASK_NAME, TASK_DESCRIPTION,secondDuration, secondStart);
        secondTask.setId(TASK_ID + 1);

        //when
        boolean hasCross = taskManager.ifTasksCrossInTime(firstTask,secondTask);

        //then
        assertTrue(hasCross, "Задача не может начаться пока другая задача не закончится.");
    }

    @Test
    @DisplayName("Должен возвращать true, если две разные задачи начинаются в одинаковое время")
    public void test_ifTasksCrossInTime_WhenTaskHasSameStart_ShouldReturnTrue() {
        //given
        LocalDateTime now = LocalDateTime.now();
        Duration taskDuration = Duration.ofMinutes(30);
        Task firstTask = new Task(TASK_NAME, TASK_DESCRIPTION,taskDuration, now);
        firstTask.setId(TASK_ID);
        Task secondTask = new Task(TASK_NAME, TASK_DESCRIPTION,taskDuration, now);
        firstTask.setId(TASK_ID+1);

        //when
        boolean hasCross = taskManager.ifTasksCrossInTime(firstTask,secondTask);

        //then
        assertTrue(hasCross, "Задача не должна пересекаться по времени сама с собой.");
    }

    @Test
    @DisplayName("Должен возвращать null при попытке добавить в менеджер задачу, пересекающуюся по времени" +
            " с уже добавленной")
    public void test_hasCrossInTimeWithManagerTasks_WhenTaskHasSameStart_ShouldReturnNull() {
        //given
        LocalDateTime now = LocalDateTime.now();
        Duration taskDuration = Duration.ofDays(30);
        taskManager.addTask(new Task(TASK_NAME, TASK_DESCRIPTION,taskDuration, now));

        //when
        Task secondTask = taskManager.addTask(new Task(TASK_NAME, TASK_DESCRIPTION,taskDuration, now));

        //then
        assertNull(secondTask, "Задача не может быть добавлена в менеджер, если есть пересечение по времени");
    }
}