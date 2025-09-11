package ru.yandex.javacourse.model;

import org.junit.jupiter.api.Test;
import ru.yandex.javacourse.service.Managers;
import ru.yandex.javacourse.service.TaskManager;

import static org.junit.jupiter.api.Assertions.*;

class SubtaskTest {
    TaskManager taskManager = Managers.getDefault();

    @Test
    public void returnTrueIfSubtasksEquals() {
        Subtask subtask1 = new Subtask("Подзадача 1","Первая подзадача", 44);
        subtask1.setId(1);
        Subtask subtask2 = new Subtask("Подзадача 2","Вторая подзадача", 44);
        subtask2.setId(1);

        assertEquals(subtask1,subtask2, "Подзадачи не равны");
    }

    @Test
    public void returnNullWhenAddedSubtaskLikeItsOwnEpic() {
        //Epic epic = taskManager.addEpic(new Epic("Эпик","Новый эпик"));

        Subtask subtask = new Subtask("Подзадача","Новая подзадача", 1);
        subtask.setId(subtask.getEpicId());

        assertNull(taskManager.addSubtask(subtask), "Удалось добавить задачу в качестве собственного эпика");
    }
}