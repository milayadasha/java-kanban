package ru.yandex.javacourse.service;

import ru.yandex.javacourse.model.*;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

public class InMemoryTaskManager implements TaskManager {
    protected int idCount = 0;
    protected final HashMap<Integer, Task> tasks = new HashMap<>();
    protected final HashMap<Integer, Epic> epics = new HashMap<>();
    protected final HashMap<Integer, Subtask> subtasks = new HashMap<>();
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
     * Удаляет все задачи в менеджере и истории
     */
    @Override
    public void deleteAllTasks() {
        tasks.keySet().forEach(historyManager::remove);
        tasks.clear();
    }

    /**
     * Возвращает копию задачи по её id.
     * Добавляет её в историю просмотра задач.
     */
    @Override
    public Task getTaskById(int id) {
        Task returnedTask = tasks.get(id);
        if (returnedTask == null) {
            return null;
        }
        historyManager.add(returnedTask);
        return returnedTask.getCopy();
    }

    /**
     * Добавляет новую задачу.
     * Создаёт копию переданной задачи, присваивает уникальный ID и сохраняет в хранилище.
     *
     * @param task объект задачи, которую нужно добавить (может быть null)
     * @return копия созданной задачи с присвоенным ID или null, если передан null
     */
    @Override
    public Task addTask(Task task) {
        if (task == null) {
            return null;
        }

        boolean hasCross = hasCrossInTimeWithManagerTasks(task);
        if (hasCross) {
            return null;
        }

        Task createdTask = task.getCopy();
        createdTask.setId(generateId());
        tasks.put(createdTask.getId(), createdTask);
        return createdTask.getCopy();
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

        boolean hasCross = hasCrossInTimeWithManagerTasks(task);
        if (hasCross) {
            return;
        }

        boolean isTaskExists = tasks.containsKey(task.getId());
        if (isTaskExists) {
            Task updatedTask = task.getCopy();
            tasks.put(updatedTask.getId(), updatedTask);
        }
    }

    /**
     * Удаляет задачу по её id из менеджера и истории
     */
    @Override
    public void deleteTaskById(int id) {
        historyManager.remove(id);
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
     * Удаляет все эпики вместе с подзадачами из менеджера и истории
     */
    @Override
    public void deleteAllEpics() {
        subtasks.keySet().forEach(historyManager::remove);
        epics.keySet().forEach(historyManager::remove);
        epics.clear();
        subtasks.clear();
    }

    /**
     * Возвращает копию эпика по его id.
     * Добавляет его в историю просмотра задач.
     */
    @Override
    public Epic getEpicById(int id) {
        Epic returnedEpic = epics.get(id);
        if (returnedEpic == null) {
            return null;
        }
        historyManager.add(returnedEpic);
        return returnedEpic.getCopy();
    }

    /**
     * Добавляет новый эпик.
     * Создаёт копию переданного эпика, присваивает уникальный ID и сохраняет в хранилище.
     *
     * @param epic объект эпика, который нужно добавить (может быть null)
     * @return копия созданного эпика с присвоенным ID или null, если передан null
     */
    @Override
    public Epic addEpic(Epic epic) {
        if (epic == null) {
            return null;
        }
        Epic createdEpic = epic.getCopy();
        createdEpic.setId(generateId());
        createdEpic.deleteSubtasksIdList();
        epics.put(createdEpic.getId(), createdEpic);
        return createdEpic.getCopy();
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
            updateEpicDates(updatedEpic);
            epics.put(updatedEpic.getId(), updatedEpic);
        }
    }

    /**
     * Удаляет эпик по его id из менеджера и истории.
     * Вместе с ним удаляет подзадачи из менеджера и истории, которые относились к этому эпику
     */
    @Override
    public void deleteEpicById(int id) {
        historyManager.remove(id);
        epics.remove(id);
        ArrayList<Integer> subtasksToRemove = subtasks.values().stream()
                .filter(subtask -> subtask.getEpicId() == id)
                .map(Subtask::getId)
                .collect(Collectors.toCollection(ArrayList::new));

        subtasksToRemove.forEach(subtaskId -> {
            historyManager.remove(subtaskId);
            subtasks.remove(subtaskId);
        });
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
     * - из менеджера и истории
     * - из эпиков
     */
    @Override
    public void deleteAllSubtasks() {
        subtasks.keySet().forEach(historyManager::remove);
        subtasks.clear();
        epics.keySet().forEach(epicId -> {
            Epic epic = epics.get(epicId);
            epic.deleteSubtasksIdList();
            updateEpicStatus(epic);
            updateEpicDates(epic);
        });
    }

    /**
     * Возвращает копию подзадачи по её id.
     * Добавляет её в историю просмотра задач.
     */
    @Override
    public Subtask getSubtaskById(int id) {
        Subtask returnedSubtask = subtasks.get(id);
        if (returnedSubtask == null) {
            return null;
        }
        historyManager.add(returnedSubtask);
        return returnedSubtask.getCopy();
    }

    /**
     * Добавляет новую подзадачу.
     * Создаёт копию переданной подзадачи, присваивает уникальный ID и сохраняет в хранилище.
     * Сохраняет ID переданной подзадачи в список ID подзадач внутри соответствующего эпика.
     * Обновляет статус эпика.
     *
     * @param subtask объект подзадачи, которую нужно добавить (может быть null)
     * @return копия созданной подзадачи с присвоенным ID или null, если передан null
     */
    @Override
    public Subtask addSubtask(Subtask subtask) {
        if (subtask == null) {
            return null;
        }

        boolean hasCross = hasCrossInTimeWithManagerTasks(subtask);
        if (hasCross) {
            return null;
        }

        Epic epic = epics.get(subtask.getEpicId());
        if (epic == null || subtask.getEpicId() == subtask.getId()) {
            return null;
        }
        Subtask createdSubtask = subtask.getCopy();
        createdSubtask.setId(generateId());
        subtasks.put(createdSubtask.getId(), createdSubtask);

        epic.addSubtaskId(createdSubtask.getId());
        updateEpicStatus(epic);
        updateEpicDates(epic);
        return createdSubtask.getCopy();
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

        boolean hasCross = hasCrossInTimeWithManagerTasks(subtask);
        if (hasCross) {
            return;
        }

        boolean isSubtaskExists = subtasks.containsKey(subtask.getId());
        if (isSubtaskExists) {
            Subtask updatedSubtask = subtask.getCopy();
            subtasks.put(updatedSubtask.getId(), updatedSubtask);
            Epic epic = epics.get(updatedSubtask.getEpicId());
            updateEpicStatus(epic);
            updateEpicDates(epic);
        }
    }

    /**
     * Удаляет подзадачу по её id:
     * - из списка ID подзадач соответствующего эпика
     * - из менеджера и истории
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
        updateEpicDates(epic);

        historyManager.remove(id);
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
        return subtasks.values().stream()
                .filter(subtask -> subtask.getEpicId() == epicId)
                .collect(Collectors.toCollection(ArrayList::new));
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

    /**
     * Пересчитывает все даты внутри эпика
     */
    @Override
    public void updateEpicDates(Epic epic) {
        if (epic == null) {
            return;
        }

        Optional<LocalDateTime> epicStartTime = epic.getSubtasksIdList().stream()
                .map(subtasks::get)
                .filter(Objects::nonNull)
                .filter(subtask -> subtask.getStartTime() != null)
                .min(Comparator.comparing(Subtask::getStartTime))
                .map(Subtask::getStartTime);

        Optional<LocalDateTime> epicEndTime = epic.getSubtasksIdList().stream()
                .map(subtasks::get)
                .filter(Objects::nonNull)
                .filter(subtask -> subtask.getEndTime() != null)
                .max(Comparator.comparing(Subtask::getEndTime))
                .map(Subtask::getEndTime);

        epic.setStartTime(epicStartTime.orElse(null));
        epic.setEndTime(epicEndTime.orElse(null));

        if (epic.getStartTime() != null && epic.getEndTime() != null) {
            Optional<Duration> epicDuration = epic.getSubtasksIdList().stream()
                    .map(subtasks::get)
                    .filter(Objects::nonNull)
                    .map(Subtask::getDuration)
                    .filter(Objects::nonNull)
                    .reduce(Duration::plus);
            epic.setDuration(epicDuration.orElse(null));
        } else {
            epic.setDuration(null);
        }
    }

    /**
     * Выводит список задач в порядке приоритета
     */
    @Override
    public TreeSet<Task> getPrioritizedTasks() {
        List<Task> allTasks = new ArrayList<>();
        allTasks.addAll(getAllTasks());
        allTasks.addAll(getAllSubtasks());

        return allTasks.stream()
                .filter(task -> task.getStartTime() != null)
                .collect(Collectors.toCollection(() -> new TreeSet<>(Comparator.comparing(Task::getStartTime))));
    }

    /**
     * Проверяет, пересекаются ли две задачи по времени
     */
    @Override
    public boolean ifTasksCrossInTime(Task firstTask, Task secondTask) {
        if (firstTask == null || secondTask == null || firstTask.equals(secondTask)) {
            return false;
        }

        LocalDateTime firstTaskStartTime = firstTask.getStartTime();
        LocalDateTime firstTaskEndTime = firstTask.getEndTime();

        LocalDateTime secondTaskStartTime = secondTask.getStartTime();
        LocalDateTime secondTaskEndTime = secondTask.getEndTime();

        if (firstTaskStartTime == null || firstTaskEndTime == null || secondTaskStartTime == null
                || secondTaskEndTime == null) {
            return false;
        }

        if (firstTaskEndTime.isBefore(secondTaskStartTime) ||
                secondTaskEndTime.isBefore(firstTaskStartTime)) {
            return false;
        }

        return true;
    }

    /**
     * Проверяет, пересекается ли заданная задача с любой задаче из менеджера
     *
     * @param task задача для проверки
     * @return true если есть пересечение по времени, false если пересечений нет или задача не имеет временных меток
     */
    @Override
    public boolean hasCrossInTimeWithManagerTasks(Task task) {
        if (task == null || task.getStartTime() == null || task.getEndTime() == null) {
            return false;
        }

        Optional<Task> overlapTask = getPrioritizedTasks().stream()
                .filter(currentTask -> ifTasksCrossInTime(task, currentTask))
                .findFirst();

        return overlapTask.isPresent();
    }
}
