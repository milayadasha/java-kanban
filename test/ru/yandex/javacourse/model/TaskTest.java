package ru.yandex.javacourse.model;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class TaskTest {

    @Test
    public void returnTrueIfTasksEquals() {
        Task task1 = new Task("Задача 1","Первая задача");
        task1.setId(1);
        Task task2 = new Task("Задача 2","Вторая задача");
        task2.setId(1);

        assertEquals(task1,task2, "Задачи не равны");
    }

}