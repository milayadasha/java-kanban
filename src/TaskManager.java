import model.*;
import java.util.ArrayList;
import java.util.HashMap;

public class TaskManager {
    private static int idCount = 0;
    HashMap<Integer, Task> tasks = new HashMap();
    HashMap<Integer, Epic> epics = new HashMap();
    HashMap<Integer, Subtask> subtasks = new HashMap();

    private int generateId() {
        idCount += 1;
        return idCount;
    }

    //Методы для TASKS (обычные задачи)
    public ArrayList<Task> getAllTasks() {
        return new ArrayList<>(tasks.values());
    }

    public void deleteAllTasks() {
        tasks.clear();
    }

    public Task getTaskById(int id) {
        return tasks.get(id);
    }

    public Task addTask(Task task) {
        if (task == null) {
            return null;
        }
        Task createdTask = new Task(task.getName(), task.getDescription());
        createdTask.setId(generateId());
        tasks.put(createdTask.getId(), createdTask);
        return createdTask;
    }

    public void updateTask(Task task) {
        if (task == null) {
            return;
        }
        boolean isTaskExists = tasks.containsKey(task.getId());
        if (isTaskExists) {
            Task updatedTask = new Task(task.getName(),task.getDescription(), task.getStatus());
            updatedTask.setId(task.getId());
            tasks.put(updatedTask.getId(), updatedTask);
        }
    }

    public void deleteTaskById (int id) {
        tasks.remove(id);
    }

    //Методы для EPICS (эпики)
    public ArrayList<Epic> getAllEpics() {
        return new ArrayList<>(epics.values());
    }

    public void deleteAllEpics() {
        epics.clear();
        subtasks.clear();
    }

    public Epic getEpicById(int id) {
        return epics.get(id);
    }

    public Epic addEpic(Epic epic) {
        if (epic == null) {
            return null;
        }
        Epic createdEpic = new Epic(epic.getName(),epic.getDescription());
        createdEpic.setId(generateId());
        epics.put(createdEpic.getId(), createdEpic);
        return createdEpic;
    }

    public void updateEpic(Epic epic) {
        if (epic == null) {
            return;
        }
        boolean isEpicExists = epics.containsKey(epic.getId());
        if (isEpicExists) {
            Epic currentEpic = epics.get(epic.getId());
            ArrayList<Integer> currentEpicSubtasksIdList = currentEpic.getSubtasksIdList();
            Epic updatedEpic = new Epic(epic.getName(), epic.getDescription());
            updatedEpic.setId(epic.getId());
            updatedEpic.setSubtasksIdList(currentEpicSubtasksIdList);
            updateEpicStatus(updatedEpic);
            epics.put(updatedEpic.getId(), updatedEpic);
        }
    }

    public void deleteEpicById (int id) {
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

    //Методы для SUBTASKS (подзадачи)
    public ArrayList<Subtask> getAllSubtasks() {
        return new ArrayList<>(subtasks.values());
    }

    public void deleteAllSubtasks() {
        subtasks.clear();
        for (Epic epic : epics.values()) {
            epic.deleteSubtasksIdList();
            updateEpicStatus(epic);
        }
    }

    public Task getSubtaskById(int id) {
        return subtasks.get(id);
    }

    public Subtask addSubtask(Subtask subtask) {
        if (subtask == null) {
            return null;
        }
        Subtask createdSubtask = new Subtask(subtask.getName(), subtask.getDescription(), subtask.getEpicId());
        createdSubtask.setId(generateId());
        subtasks.put(createdSubtask.getId(), createdSubtask);

        Epic epic = epics.get(createdSubtask.getEpicId());
        epic.addSubtaskId(createdSubtask.getId());
        updateEpicStatus(epic);

        return createdSubtask;
    }

    public void updateSubtask(Subtask subtask) {
        if (subtask == null) {
            return;
        }
        boolean isSubtaskExists = subtasks.containsKey(subtask.getId());
        if (isSubtaskExists) {
            Subtask updatedSubtask = new Subtask(subtask.getName(), subtask.getDescription(), subtask.getStatus(),
                    subtask.getEpicId());
            updatedSubtask.setId(subtask.getId());
            subtasks.put(updatedSubtask.getId(), updatedSubtask);
            Epic epic = epics.get(updatedSubtask.getEpicId());
            updateEpicStatus(epic);
        }
    }

    public void deleteSubtaskById (Integer id) {
        Subtask subtask = subtasks.get(id);
        Epic epic = epics.get(subtask.getEpicId());
        epic.deleteSubtaskId(id);
        updateEpicStatus(epic);
        subtasks.remove(id);
    }

    public ArrayList<Subtask>  getAllSubtasksByEpicId(int epicId) {
        ArrayList<Subtask> subtasksByEpic = new ArrayList<>();
        for (Subtask subtask : subtasks.values()) {
            if (subtask.getEpicId() == epicId) {
                subtasksByEpic.add(subtask);
            }
        }
        return subtasksByEpic;
    }

    //Дополнительный метод для получения статуса эпика
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
}
