package ru.yandex.javacourse.service;

import ru.yandex.javacourse.model.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class InMemoryTaskManager implements TaskManager {
    private static int idCount = 0;
    private final HashMap<Integer, Task> tasks = new HashMap<>();
    private final HashMap<Integer, Epic> epics = new HashMap<>();
    private final HashMap<Integer, Subtask> subtasks = new HashMap<>();
    private final HistoryManager historyManager = Managers.getDefaultHistory();

    /**
     * Возвращает ID для новой задачи.
     * Увеличивает счётчик на 1.
     */
    private int generateId() {
        idCount += 1;
        return idCount;
    }

    /**
     * Возвращает все задачи в виде списка объектов
     */
    @Override
    public ArrayList<Task> getAllTasks() {
        return new ArrayList<>(tasks.values());
    }

    /**
     * Удаляет все задачи
     */
    @Override
    public void deleteAllTasks() {
        tasks.clear();
    }

    /**
     * Возвращает задачу по её id.
     * Добавляет её в историю просмотра задач.
     */
    @Override
    public Task getTaskById(int id) {
        Task returnedTask = tasks.get(id);
        historyManager.add(returnedTask);
        return returnedTask;
    }

    /**
     * Добавляет новую задачу.
     * Создаёт копию переданной задачи, присваивает уникальный ID и сохраняет в хранилище.
     *
     * @param task объект задачи, которую нужно добавить (может быть null)
     * @return созданная копия задачи с присвоенным ID или null, если передан null
     */
    @Override
    public Task addTask(Task task) {
        if (task == null) {
            return null;
        }
        Task createdTask = task.getCopy();
        createdTask.setId(generateId());
        tasks.put(createdTask.getId(), createdTask);
        return createdTask;
    }

    /**
     * Обновляет задачу.
     * Создаёт копию переданной задачи и обновляет по её ID уже существующую версию в хранилище.
     *
     * @param task объект задачи, которую нужно обновить (может быть null)
     */
    @Override
    public void updateTask(Task task) {
        if (task == null) {
            return;
        }
        boolean isTaskExists = tasks.containsKey(task.getId());
        if (isTaskExists) {
            Task updatedTask = task.getCopy();
            tasks.put(updatedTask.getId(), updatedTask);
        }
    }

    /**
     * Удаляет задачу по её id
     */
    @Override
    public void deleteTaskById(int id) {
        tasks.remove(id);
    }

    /**
     * Возвращает все эпики в виде списка объектов
     */
    @Override
    public ArrayList<Epic> getAllEpics() {
        return new ArrayList<>(epics.values());
    }

    /**
     * Удаляет все эпики вместе с подзадачами
     */
    @Override
    public void deleteAllEpics() {
        epics.clear();
        subtasks.clear();
    }

    /**
     * Возвращает эпик по его id.
     * Добавляет его в историю просмотра задач.
     */
    @Override
    public Epic getEpicById(int id) {
        Epic returnEpic = epics.get(id);
        historyManager.add(returnEpic);
        return returnEpic;
    }

    /**
     * Добавляет новый эпик.
     * Создаёт копию переданного эпика, присваивает уникальный ID и сохраняет в хранилище.
     *
     * @param epic объект эпика, который нужно добавить (может быть null)
     * @return созданная копия эпика с присвоенным ID или null, если передан null
     */
    @Override
    public Epic addEpic(Epic epic) {
        if (epic == null) {
            return null;
        }
        Epic createdEpic = epic.getCopy();
        createdEpic.setId(generateId());
        epics.put(createdEpic.getId(), createdEpic);
        return createdEpic;
    }

    /**
     * Обновляет эпик.
     * Создаёт копию переданного эпика и обновляет по его ID уже существующую версию в хранилище.
     *
     * @param epic объект задачи, которую нужно обновить (может быть null)
     */
    @Override
    public void updateEpic(Epic epic) {
        if (epic == null) {
            return;
        }
        boolean isEpicExists = epics.containsKey(epic.getId());
        if (isEpicExists) {
            Epic currentEpic = epics.get(epic.getId());
            ArrayList<Integer> currentEpicSubtasksIdList = currentEpic.getSubtasksIdList();
            Epic updatedEpic = epic.getCopy();
            updatedEpic.setSubtasksIdList(currentEpicSubtasksIdList);
            updateEpicStatus(updatedEpic);
            epics.put(updatedEpic.getId(), updatedEpic);
        }
    }

    /**
     * Удаляет эпик по его id.
     * Вместе с ним удаляет подзадачи из хранилища, которые относились к этому эпику
     */
    @Override
    public void deleteEpicById(int id) {
        epics.remove(id);
        ArrayList<Integer> subtasksToRemove = new ArrayList<>();
        for (Subtask subtask : subtasks.values()) {
            if (subtask.getEpicId() == id) {
                subtasksToRemove.add(subtask.getId());
            }
        }
        for (Integer subtaskId : subtasksToRemove) {
            subtasks.remove(subtaskId);
        }
    }

    /**
     * Возвращает все подзадачи в виде списка объектов
     */
    @Override
    public ArrayList<Subtask> getAllSubtasks() {
        return new ArrayList<>(subtasks.values());
    }

    /**
     * Удаляет все подзадачи:
     * - из хранилища
     * - из эпиков
     */
    @Override
    public void deleteAllSubtasks() {
        subtasks.clear();
        for (Epic epic : epics.values()) {
            epic.deleteSubtasksIdList();
            updateEpicStatus(epic);
        }
    }

    /**
     * Возвращает подзадачу по её id.
     * Добавляет её в историю просмотра задач.
     */
    @Override
    public Subtask getSubtaskById(int id) {
        Subtask returnSubtask = subtasks.get(id);
        historyManager.add(returnSubtask);
        return returnSubtask;
    }

    /**
     * Добавляет новую подзадачу.
     * Создаёт копию переданной подзадачи, присваивает уникальный ID и сохраняет в хранилище.
     * Сохраняет ID переданной подзадачи в список ID подзадач внутри соответствующего эпика.
     * Обновляет статус эпика.
     *
     * @param subtask объект подзадачи, которую нужно добавить (может быть null)
     * @return созданная копия подзадачи с присвоенным ID или null, если передан null
     */
    @Override
    public Subtask addSubtask(Subtask subtask) {
        if (subtask == null || subtask.getEpicId() == subtask.getId()) {
            return null;
        }

        Subtask createdSubtask = subtask.getCopy();
        createdSubtask.setId(generateId());
        subtasks.put(createdSubtask.getId(), createdSubtask);

        Epic epic = epics.get(createdSubtask.getEpicId());
        epic.addSubtaskId(createdSubtask.getId());
        updateEpicStatus(epic);

        return createdSubtask;
    }

    /**
     * Обновляет подзадачу.
     * Создаёт копию переданной подзадачи и обновляет по её ID уже существующую версию в хранилище.
     * Обновляет статус эпика.
     *
     * @param subtask объект задачи, которую нужно обновить (может быть null)
     */
    @Override
    public void updateSubtask(Subtask subtask) {
        if (subtask == null || subtask.getEpicId() == subtask.getId()) {
            return;
        }

        boolean isSubtaskExists = subtasks.containsKey(subtask.getId());
        if (isSubtaskExists) {
            Subtask updatedSubtask = subtask.getCopy();
            subtasks.put(updatedSubtask.getId(), updatedSubtask);
            Epic epic = epics.get(updatedSubtask.getEpicId());
            updateEpicStatus(epic);
        }
    }

    /**
     * Удаляет подзадачу по её id:
     * - из списка ID подзадач соответствующего эпика
     * - из хранилища
     */
    @Override
    public void deleteSubtaskById(int id) {
        Subtask subtask = subtasks.get(id);
        if (subtask == null) {
            return;
        }
        Epic epic = epics.get(subtask.getEpicId());
        epic.deleteSubtaskId(id);
        updateEpicStatus(epic);
        subtasks.remove(id);
    }

    /**
     * Возвращает список подзадач указанного эпика.
     * Создаёт и наполняет список объектов-подзадач из хранилища по ID эпика.
     *
     * @param epicId это ID эпика
     * @return список объектов-подзадач
     */
    @Override
    public ArrayList<Subtask> getAllSubtasksByEpicId(int epicId) {
        ArrayList<Subtask> subtasksByEpic = new ArrayList<>();
        for (Subtask subtask : subtasks.values()) {
            if (subtask.getEpicId() == epicId) {
                subtasksByEpic.add(subtask);
            }
        }
        return subtasksByEpic;
    }

    /**
     * Обновляет статус эпика.
     * Получает список его подзадач и проверяет их статус. По их статусам проставляет актуальный статус эпика.
     *
     * @param epic объект эпика, статус которого нужно обновить.
     */
    @Override
    public void updateEpicStatus(Epic epic) {
        if (epic == null) {
            return;
        }
        ArrayList<Integer> epicSubtasksId = epic.getSubtasksIdList();
        int subtasksCount = epicSubtasksId.size();
        int newCount = 0;
        int doneCount = 0;

        for (Integer epicSubtaskId : epicSubtasksId) {
            Subtask subtask = subtasks.get(epicSubtaskId);
            if (subtask.getStatus() == TaskStatus.NEW) {
                newCount++;
            }
            if (subtask.getStatus() == TaskStatus.DONE) {
                doneCount++;
            }
        }

        if (subtasksCount == 0 || (subtasksCount == newCount)) {
            epic.setStatus(TaskStatus.NEW);
        } else if (subtasksCount == doneCount) {
            epic.setStatus(TaskStatus.DONE);
        } else {
            epic.setStatus(TaskStatus.IN_PROGRESS);
        }
    }

    /**
     * Возвращает список просмотренных задач.
     * Вызывает метод просмотра истории в менеджере истории задач.
     */
    @Override
    public List<Task> getHistory() {
        return historyManager.getHistory();
    }

}
