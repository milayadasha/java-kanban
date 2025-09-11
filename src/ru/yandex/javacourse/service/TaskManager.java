package ru.yandex.javacourse.service;

import ru.yandex.javacourse.model.Epic;
import ru.yandex.javacourse.model.Subtask;
import ru.yandex.javacourse.model.Task;
import java.util.ArrayList;
import java.util.List;

public interface TaskManager {

    /**
     * Возвращает все задачи в виде списка объектов
     */
    ArrayList<Task> getAllTasks();

    /**
     * Удаляет все задачи
     */
    void deleteAllTasks();

    /**
     * Возвращает задачу по её id
     */
    Task getTaskById(int id);

    /**
     * Добавляет новую задачу.
     */
    Task addTask(Task task);

    /**
     * Обновляет задачу.
     */
    void updateTask(Task task);

    /**
     * Удаляет задачу по её id
     */
    void deleteTaskById(int id);

    /**
     * Возвращает все эпики в виде списка объектов
     */
    ArrayList<Epic> getAllEpics();

    /**
     * Удаляет все эпики вместе с подзадачами
     */
    void deleteAllEpics();

    /**
     * Возвращает эпик по его id
     */
    Epic getEpicById(int id);

    /**
     * Добавляет новый эпик.
     */
    Epic addEpic(Epic epic);

    /**
     * Обновляет эпик.
     */
    void updateEpic(Epic epic);

    /**
     * Удаляет эпик по его id.
     */
    void deleteEpicById(int id);

    /**
     * Возвращает все подзадачи в виде списка объектов
     */
    ArrayList<Subtask> getAllSubtasks();

    /**
     * Удаляет все подзадачи:
     */
    void deleteAllSubtasks();

    /**
     * Возвращает подзадачу по её id
     */
    Subtask getSubtaskById(int id);

    /**
     * Добавляет новую подзадачу.
     */
    Subtask addSubtask(Subtask subtask);

    /**
     * Обновляет подзадачу.
     */
    void updateSubtask(Subtask subtask);

    /**
     * Удаляет подзадачу по её id:
     */
    void deleteSubtaskById(int id);

    /**
     * Возвращает список подзадач указанного эпика.
     */
    ArrayList<Subtask> getAllSubtasksByEpicId(int epicId);

    /**
     * Обновляет статус эпика.
     */
    void updateEpicStatus(Epic epic);

    /**
     * Возвращает список просмотренных задач
     */
    List<Task> getHistory();
}


