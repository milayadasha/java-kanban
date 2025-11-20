package ru.yandex.javacourse.model;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import ru.yandex.javacourse.exceptions.NotFoundException;
import ru.yandex.javacourse.service.Managers;
import ru.yandex.javacourse.service.TaskManager;
import static org.junit.jupiter.api.Assertions.*;

class EpicTest {
    private static final String EPIC_NAME_1 = "Эпик 1";
    private static final String EPIC_DESCRIPTION_1 = "Первый эпик";
    private static final String EPIC_NAME_2 = "Эпик 2";
    private static final String EPIC_DESCRIPTION_2 = "Второй эпик";
    private static final int EPIC_ID = 1;
    private static final String SUBTASK_NAME = "Подзадача";
    private static final String SUBTASK_DESCRIPTION = "Новая подзадача";

    TaskManager taskManager = Managers.getDefault();

    @Test
    @DisplayName("Должен возвращать true при сравнении двух эпиков с одинаковым ID")
    public void test_Equals_WhenEpicsHaveSameId_ShouldReturnTrue() {
        //given
        Epic epic1 = new Epic(EPIC_NAME_1, EPIC_DESCRIPTION_1);
        epic1.setId(EPIC_ID);

        //when
        Epic epic2 = new Epic(EPIC_NAME_2, EPIC_DESCRIPTION_2);
        epic2.setId(EPIC_ID);

        //then
        assertEquals(epic1, epic2, "Эпики не равны");
    }

    @Test
    @DisplayName("Должен возвращать NotFoundException при попытке добавить эпик в качестве собственной подзадачи")
    public void test_AddSubtask_WhenEpicAddedAsOwnSubtask_ShouldReturnNotFoundException() {
        //given && when
        Epic epic = taskManager.addEpic(new Epic(EPIC_NAME_1, EPIC_DESCRIPTION_1));
        Subtask subtask = new Subtask(epic.getName(), epic.getDescription(), epic.getId());
        subtask.setId(epic.getId());

        //then
        assertThrows(NotFoundException.class, () -> taskManager.addSubtask(subtask),
                "Должно быть исключение при попытке добавить эпик в качестве собственной подзадачи");
    }

    @Test
    @DisplayName("При добавлении подзадач со статусом NEW у эпика тоже должен быть статус NEW")
    public void test_AddSubtask_WhenAllSubtasksNew_EpicStatusNew() {
        //given
        Epic epic = taskManager.addEpic(new Epic(EPIC_NAME_1, EPIC_DESCRIPTION_1));

        //when
        taskManager.addSubtask(new Subtask(SUBTASK_NAME, SUBTASK_DESCRIPTION,
                epic.getId()));
        taskManager.addSubtask(new Subtask(SUBTASK_NAME, SUBTASK_DESCRIPTION,
                epic.getId()));
        Epic returnedEpic = taskManager.getEpicById(epic.getId());

        //then
        assertEquals("NEW", returnedEpic.getStatus().toString(), "Статус эпика должен быть NEW");
    }

    @Test
    @DisplayName("При добавлении подзадач со статусом DONE у эпика тоже должен быть статус DONE")
    public void test_AddSubtask_WhenAllSubtasksDone_EpicStatusDone() {
        //given
        Epic epic = taskManager.addEpic(new Epic(EPIC_NAME_1, EPIC_DESCRIPTION_1));

        //when
        taskManager.addSubtask(new Subtask(SUBTASK_NAME, SUBTASK_DESCRIPTION, TaskStatus.DONE,
                epic.getId()));
        taskManager.addSubtask(new Subtask(SUBTASK_NAME, SUBTASK_DESCRIPTION, TaskStatus.DONE,
                epic.getId()));
        Epic returnedEpic = taskManager.getEpicById(epic.getId());

        //then
        assertEquals("DONE", returnedEpic.getStatus().toString(), "Статус эпика должен быть DONE");
    }

    @Test
    @DisplayName("При добавлении подзадач со статусом IN_PROGRESS у эпика тоже должен быть статус IN_PROGRESS")
    public void test_AddSubtask_WhenAllSubtasksInProgress_EpicStatusInProgress() {
        //given
        Epic epic = taskManager.addEpic(new Epic(EPIC_NAME_1, EPIC_DESCRIPTION_1));

        //when
        taskManager.addSubtask(new Subtask(SUBTASK_NAME, SUBTASK_DESCRIPTION, TaskStatus.IN_PROGRESS,
                epic.getId()));
        taskManager.addSubtask(new Subtask(SUBTASK_NAME, SUBTASK_DESCRIPTION, TaskStatus.IN_PROGRESS,
                epic.getId()));
        Epic returnedEpic = taskManager.getEpicById(epic.getId());

        //then
        assertEquals("IN_PROGRESS", returnedEpic.getStatus().toString(), "Статус эпика должен быть DONE");
    }

    @Test
    @DisplayName("При добавлении подзадач с разными статусами у эпика  должен быть статус IN_PROGRESS")
    public void test_AddSubtask_WhenSubtasksInProgressDone_EpicStatusInProgress() {
        //given
        Epic epic = taskManager.addEpic(new Epic(EPIC_NAME_1, EPIC_DESCRIPTION_1));

        //when
        taskManager.addSubtask(new Subtask(SUBTASK_NAME, SUBTASK_DESCRIPTION, TaskStatus.IN_PROGRESS,
                epic.getId()));
        taskManager.addSubtask(new Subtask(SUBTASK_NAME, SUBTASK_DESCRIPTION, TaskStatus.DONE,
                epic.getId()));
        Epic returnedEpic = taskManager.getEpicById(epic.getId());

        //then
        assertEquals("IN_PROGRESS", returnedEpic.getStatus().toString(), "Статус эпика должен быть DONE");
    }
}