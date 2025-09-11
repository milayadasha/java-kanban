package ru.yandex.javacourse.model;

import org.junit.jupiter.api.Test;
import ru.yandex.javacourse.service.Managers;
import ru.yandex.javacourse.service.TaskManager;

import static org.junit.jupiter.api.Assertions.*;

class EpicTest {
    TaskManager taskManager = Managers.getDefault();

    @Test
    public void returnTrueIfTEpicsEquals() {
        Epic epic1 = new Epic("Эпик 1","Первый");
        epic1.setId(1);
        Epic epic2 = new Epic("Эпик 2","Второй");
        epic2.setId(1);

        assertEquals(epic1,epic2, "Эпики не равны");
    }

    @Test
    public void returnNullWhenAddedEpicLikeItsOwnSubtask() {
        Epic epic = taskManager.addEpic(new Epic("Эпик","Новый эпик"));

        Subtask subtask = new Subtask(epic.getName(), epic.getDescription(), epic.getId());
        subtask.setId(epic.getId());

        assertNull(taskManager.addSubtask(subtask), "Смогли добавить эпик в качестве собственной подзадачи");
    }
}