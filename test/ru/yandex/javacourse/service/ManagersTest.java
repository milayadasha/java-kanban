package ru.yandex.javacourse.service;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ManagersTest {

    @Test
    @DisplayName("Должен возвращать не-null менеджер задач при вызове метода создания")
    public void test_getDefault_WhenCreateNewTaskManager_ShouldReturnNotNull() {
        //given & when
        TaskManager taskManager = Managers.getDefault();

        //then
        assertNotNull(taskManager);
    }

    @Test
    @DisplayName("Должен возвращать не-null менеджер истории при вызове при вызове метода создания")
    public void test_getDefaultHistory_WhenCreateNewHistoryManager_ShouldReturnNotNull() {
        //given & when
        HistoryManager historyManager = Managers.getDefaultHistory();

        //then
        assertNotNull(historyManager);
    }

}