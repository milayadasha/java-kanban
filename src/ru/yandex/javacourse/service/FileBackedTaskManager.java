package ru.yandex.javacourse.service;

import ru.yandex.javacourse.exceptions.ManagerSaveException;
import ru.yandex.javacourse.model.*;

import java.io.*;
import java.util.ArrayList;

public class FileBackedTaskManager extends InMemoryTaskManager {
    private final File file;

    public static void main(String[] args) throws IOException {
        File file = new File("src/tasks.csv");
        System.out.println("Файл создан: " + file.getAbsolutePath());
        FileBackedTaskManager fileBackedTaskManager = Managers.getDefaultFileBacked(file);

        Task task = fileBackedTaskManager.addTask(new Task("Задача","Описание задачи"));
        Epic epic = fileBackedTaskManager.addEpic(new Epic("Эпик","Описание эпика"));
        Subtask subtask = fileBackedTaskManager.addSubtask(new Subtask("Подзадача",
                "Описание подзадачи",epic.getId()));

        FileBackedTaskManager newFileBackedTaskManager = loadFromFile(file);
        Task newTask = newFileBackedTaskManager.addTask(new Task("Новая задача",
                "Описание новой задачи"));

        Subtask updatedSubtask = new Subtask("Подзадача", "Описание подзадачи", epic.getId());
        updatedSubtask.setId(subtask.getId());
        updatedSubtask.setStatus(TaskStatus.DONE);
        newFileBackedTaskManager.updateSubtask(updatedSubtask);
    }

    public FileBackedTaskManager(File file) {
        this.file = file;
    }

    /**
     * Сохраняет состояния менеджера в файл.
     * Каждая задача становится отдельной строчкой
     */
    public void save() throws ManagerSaveException {
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(file))) {
            bw.write("id,type,name,status,description,epic");
            bw.newLine();

            ArrayList<Task> tasks = getAllTasks();
            for (Task task : tasks) {
                String taskString = toString(task);
                bw.write(taskString);
                bw.newLine();
            }

            ArrayList<Epic> epics = getAllEpics();
            for (Epic epic : epics) {
                String epicString = toString(epic);
                bw.write(epicString);
                bw.newLine();
            }

            ArrayList<Subtask> subtasks = getAllSubtasks();
            for (Subtask subtask : subtasks) {
                String subtaskString = toString(subtask);
                bw.write(subtaskString);
                bw.newLine();
            }
        } catch (IOException exception) {
            throw new ManagerSaveException("Ошибка во время работы метода автосохранения");
        }

    }

    /**
     * Вызывает родительский метод удаления всех задач
     * Сохраняет состояние в файл
     */
    @Override
    public void deleteAllTasks() {
        super.deleteAllTasks();
        save();
    }

    /**
     * Вызывает родительский метод добавления задачи
     * Сохраняет состояние в файл
     * @param task объект задачи, которую нужно добавить (может быть null)
     * @return копия созданной задачи с присвоенным ID или null, если передан null
     */
    @Override
    public Task addTask(Task task) {
        Task addedTask = super.addTask(task);
        save();
        return addedTask;
    }

    /**
     * Вызывает родительский метод обновления задачи
     * Сохраняет состояние в файл
     * @param task объект задачи, которую нужно обновить (может быть null)
     */
    @Override
    public void updateTask(Task task) {
        super.updateTask(task);
        save();
    }

    /**
     * Вызывает родительский метод удаления задачи по id
     * Сохраняет состояние в файл
     */
    @Override
    public void deleteTaskById(int id) {
        super.deleteTaskById(id);
        save();
    }

    /**
     * Вызывает родительский метод удаления всех эпиков вместе с подзадачами
     * Сохраняет состояние в файл
     */
    @Override
    public void deleteAllEpics() {
        super.deleteAllEpics();
        save();
    }

    /**
     * Вызывает родительский метод добавления эпика
     * Сохраняет состояние в файл
     * @param epic объект эпика, который нужно добавить (может быть null)
     * @return копия созданного эпика с присвоенным ID или null, если передан null
     */
    @Override
    public Epic addEpic(Epic epic) {
        Epic addedEpic = super.addEpic(epic);
        save();
        return addedEpic;
    }

    /**
     * Вызывает родительский метод обновления эпика
     * Сохраняет состояние в файл
     * @param epic объект эпика, который нужно обновить (может быть null)
     */
    @Override
    public void updateEpic(Epic epic) {
        super.updateEpic(epic);
        save();
    }

    /**
     * Вызывает родительский метод удаления эпика по id
     * Сохраняет состояние в файл
     */
    @Override
    public void deleteEpicById(int id) {
        super.deleteEpicById(id);
        save();
    }

    /**
     * Вызывает родительский метод удаления всех подзадач
     * Сохраняет состояние в файл
     */
    @Override
    public void deleteAllSubtasks() {
        super.deleteAllSubtasks();
        save();
    }

    /**
     * Вызывает родительский метод добавления подзадачи
     * Сохраняет состояние в файл
     * @param subtask объект подзадачи, которую нужно добавить (может быть null)
     * @return копия созданной подзадачи с присвоенным ID или null, если передан null
     */
    @Override
    public Subtask addSubtask(Subtask subtask) {
        Subtask addedSubtask = super.addSubtask(subtask);
        save();
        return addedSubtask;
    }

    /**
     * Вызывает родительский метод обновления подзадачи
     * Сохраняет состояние в файл
     * @param subtask объект подзадачи, которую нужно обновить (может быть null)
     */
    @Override
    public void updateSubtask(Subtask subtask) {
        super.updateSubtask(subtask);
        save();
    }

    /**
     * Вызывает родительский метод удаления подзадачи по id
     * Сохраняет состояние в файл
     */
    @Override
    public void deleteSubtaskById(int id) {
        super.deleteSubtaskById(id);
        save();
    }

    /**
     * Преобразует входную задачу в строку в зависимости от типа задачи
     * Сохраняет состояние в файл
     */
    public String toString(Task task) {
        if (task == null) {
            return "";
        }

        TaskType taskType;
        String epicId = "";
        if (task instanceof Subtask subtask) {
            taskType = TaskType.SUBTASK;
            epicId = Integer.toString(subtask.getEpicId());
        } else if (task instanceof Epic) {
            taskType = TaskType.EPIC;
        } else {
            taskType = TaskType.TASK;
        }
        return String.format("%s,%s,%s,%s,%s,%s",
                task.getId(),taskType, task.getName(), task.getStatus(), task.getDescription(), epicId);
    }

    /**
     * Преобразует строку в конкретный тип задачи в зависимости от типа
     */
    public Task fromString(String value) {
        String[] splitValues = value.split(",");
        int taskId = Integer.parseInt(splitValues[0]);
        TaskType taskType = TaskType.valueOf(splitValues[1]);
        String taskName = splitValues[2];
        TaskStatus taskStatus = TaskStatus.valueOf(splitValues[3]);
        String taskDescription = splitValues[4];

        if (taskType.equals(TaskType.SUBTASK)) {
            int epicId = Integer.parseInt(splitValues[5]);
            Subtask subTask = new Subtask(taskName, taskDescription, taskStatus, epicId);
            subTask.setId(taskId);
            return subTask;
        } else if (taskType.equals(TaskType.EPIC)) {
            Epic epic = new Epic(taskName, taskDescription);
            epic.setId(taskId);
            return epic;
        } else {
            Task task = new Task(taskName, taskDescription);
            task.setId(taskId);
            task.setStatus(taskStatus);
            return task;
        }
    }

    /**
     * Заполняет менеджер задач информацией из файла
     * @param file файл, из которого надо получить все задачи, подзадачи и эпики
     * @return созданный новый менеджер, который заполнен
     */
    public static FileBackedTaskManager loadFromFile(File file) throws IOException {
        FileBackedTaskManager fileBackedTaskManager = Managers.getDefaultFileBacked(file);
        int lastId = 0;
        if (!file.exists() || !file.isFile()) {
            throw new IOException("Указанный файл не существует");
        }

        try (BufferedReader br = new BufferedReader(new FileReader(file))){
            br.readLine();
            while (br.ready()) {
                String fileString = br.readLine();
                if (!fileString.isBlank()) {
                    Task task = fileBackedTaskManager.fromString(fileString);
                    lastId = Integer.max(lastId, task.getId());
                    if (task instanceof Subtask subtask) {
                        fileBackedTaskManager.subtasks.put(subtask.getId(),subtask);
                    } else if (task instanceof Epic epic) {
                        fileBackedTaskManager.epics.put(epic.getId(),epic);
                    } else {
                        fileBackedTaskManager.tasks.put(task.getId(),task);
                    }
                }
            }
        } catch (IOException exception) {
            System.out.println("Возникла проблема при восстановлении менеджера из файла");
        }

        for (Subtask subtask : fileBackedTaskManager.getAllSubtasks()) {
            Epic epic = fileBackedTaskManager.epics.get(subtask.getEpicId());
            epic.addSubtaskId(subtask.getId());
        }

        for (Epic epic : fileBackedTaskManager.getAllEpics()) {
            fileBackedTaskManager.updateEpic(epic);
        }

        setIdCount(lastId);
        return fileBackedTaskManager;
    }
}
