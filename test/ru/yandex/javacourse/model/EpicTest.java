package ru.yandex.javacourse.model;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import ru.yandex.javacourse.service.Managers;
import ru.yandex.javacourse.service.TaskManager;

import static org.junit.jupiter.api.Assertions.*;

class EpicTest {
    private static final String EPIC_NAME_1 = "Эпик 1";
    private static final String EPIC_DESCRIPTION_1 = "Первый эпик";
    private static final String EPIC_NAME_2 = "Эпик 2";
    private static final String EPIC_DESCRIPTION_2 = "Второй эпик";
    private static final int EPIC_ID = 1;

    TaskManager taskManager = Managers.getDefault();

    @Test
    @DisplayName("Должен возвращать true при сравнении двух эпиков с одинаковым ID")
    public void test_Equals_WhenEpicsHaveSameId_ShouldReturnTrue() {
        //given
        Epic epic1 = new Epic(EPIC_NAME_1,EPIC_DESCRIPTION_1);
        epic1.setId(EPIC_ID);

        //when
        Epic epic2 = new Epic(EPIC_NAME_2,EPIC_DESCRIPTION_2);
        epic2.setId(EPIC_ID);

        //then
        assertEquals(epic1,epic2, "Эпики не равны");
    }

    @Test
    @DisplayName("Должен возвращать null при попытке добавить эпик в качестве собственной подзадачи")
    public void test_AddSubtask_WhenEpicAddedAsOwnSubtask_ShouldReturnNull() {
        //given
        Epic epic = taskManager.addEpic(new Epic(EPIC_NAME_1,EPIC_DESCRIPTION_1));
        Subtask subtask = new Subtask(epic.getName(), epic.getDescription(), epic.getId());
        subtask.setId(epic.getId());

        //when
        Subtask resultSubtask = taskManager.addSubtask(subtask);

        //then
        assertNull(resultSubtask, "Смогли добавить эпик в качестве собственной подзадачи");
    }
}