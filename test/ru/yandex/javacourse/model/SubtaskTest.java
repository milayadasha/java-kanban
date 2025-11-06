package ru.yandex.javacourse.model;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import ru.yandex.javacourse.service.Managers;
import ru.yandex.javacourse.service.TaskManager;

import static org.junit.jupiter.api.Assertions.*;

class SubtaskTest {
    private static final String SUBTASK_NAME_1 = "Подзадача 1";
    private static final String SUBTASK_DESCRIPTION_1 = "Первая подзадача";
    private static final String SUBTASK_NAME_2 = "Подзадача 2";
    private static final String SUBTASK_DESCRIPTION_2 = "Вторая подзадача";
    private static final int EPIC_ID = 44;
    private static final int SUBTASK_ID = 1;

    TaskManager taskManager = Managers.getDefault();

    @Test
    @DisplayName("Должен возвращать true при сравнении двух подзадач с одинаковым ID")
    public void test_Equals_WhenSubtasksHaveSameId_ShouldReturnTrue() {
        //given
        Subtask subtask1 = new Subtask(SUBTASK_NAME_1,SUBTASK_DESCRIPTION_1, EPIC_ID);
        subtask1.setId(SUBTASK_ID);

        //when
        Subtask subtask2 = new Subtask(SUBTASK_NAME_2,SUBTASK_DESCRIPTION_2, EPIC_ID);
        subtask2.setId(SUBTASK_ID);

        //then
        assertEquals(subtask1,subtask2, "Подзадачи не равны");
    }

    @Test
    @DisplayName("Должен возвращать null при попытке добавить подзадачу с ID равным ID эпика")
    public void test_AddSubtask_WhenSubtaskAddedLikeOwnEpic_ShouldReturnNull() {
        //given
        Subtask subtask = new Subtask(SUBTASK_NAME_1,SUBTASK_DESCRIPTION_1 , EPIC_ID);
        subtask.setId(subtask.getEpicId());

        //when
        Subtask resultSubtask = taskManager.addSubtask(subtask);

        //then
        assertNull(resultSubtask, "Удалось добавить подзадачу в качестве собственного эпика");
    }

    @Test
    @DisplayName("Должен возвращать не null EpicId при попытке добавить подзадачу")
    public void test_AddSubtask_WhenSubtaskAdded_ShouldHasEpicId() {
        //given
        Subtask subtask = new Subtask(SUBTASK_NAME_1,SUBTASK_DESCRIPTION_1 , EPIC_ID);

        //when
        Integer resultSubtaskEpicId = subtask.getEpicId();

        //then
        assertNotNull(resultSubtaskEpicId, "При добавлении задачи у неё нет id эпика");
    }
}