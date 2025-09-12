package ru.yandex.javacourse.model;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class TaskTest {
    private static final String TASK_NAME_1 = "Задача 1";
    private static final String TASK_DESCRIPTION_1 = "Первая задача";
    private static final String TASK_NAME_2 = "Задача 2";
    private static final String TASK_DESCRIPTION_2 = "Вторая задача";

    @Test
    @DisplayName("Должен возвращать true при сравнении двух задач с одинаковым ID")
    public void test_Equals_WhenTasksHaveSameId_ShouldReturnTrue() {
        //given
        Task task1 = new Task(TASK_NAME_1,TASK_DESCRIPTION_1);
        task1.setId(1);

        //when
        Task task2 = new Task(TASK_NAME_2,TASK_DESCRIPTION_2 );
        task2.setId(1);

        //then
        assertEquals(task1,task2, "Задачи не равны");
    }

}