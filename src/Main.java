import ru.yandex.javacourse.model.*;
import ru.yandex.javacourse.service.TaskManager;

public class Main {

    public static void main(String[] args) {
        TaskManager taskManager = new TaskManager();
        //Создание задач
        Task task1 = taskManager.addTask(new Task("Звонок бабушке", "Позвонить, чтобы узнать новости"));
        Task task2 = taskManager.addTask(new Task("Стирка", "Постирать белые вещи"));

        //Создание эпика b добавление подзадач
        Epic epic1 = taskManager.addEpic(new Epic("Сделать домашнее задание","Задание для 4 спринта"));
        Subtask subtask1 = taskManager.addSubtask(new Subtask("Изучить теорию",
                "Прочитать теорию на сайте", epic1.getId()));
        Subtask subtask2 = taskManager.addSubtask(new Subtask("Освоить тренажёр",
                "Выполнить все задания",  epic1.getId()));


        Epic epic2 = taskManager.addEpic(new Epic("Переезд","Подготовиться к переезду"));
        Subtask subtask3 = taskManager.addSubtask(new Subtask("Собрать вещи",
                "Собрать вещи в коробки", epic2.getId()));

        printAllTasks(taskManager);

        //Проверка методов обновления
        Task updatedTask1 = new Task("Звонок маме", "Позвонить, чтобы узнать новости");
        updatedTask1.setId(task1.getId());
        updatedTask1.setStatus(TaskStatus.IN_PROGRESS);
        taskManager.updateTask(updatedTask1);
        updatedTask1.setStatus(TaskStatus.DONE); //нужно, чтобы проверить, что статус останется IN_PROGRESS

        Subtask updatedSubtask2 = new Subtask(subtask2.getName(), subtask2.getDescription(), epic1.getId());
        updatedSubtask2.setId(subtask2.getId());
        updatedSubtask2.setStatus(TaskStatus.DONE);
        taskManager.updateSubtask(updatedSubtask2);

        Subtask updatedSubtask3 = new Subtask("Расхламиться", "Выкинуть старые вещи",
                epic2.getId());
        updatedSubtask3.setId(subtask3.getId());
        updatedSubtask3.setStatus(TaskStatus.DONE);
        taskManager.updateSubtask(updatedSubtask3);
        updatedSubtask3.setName("Поиграть в компьютер"); //нужно, чтобы проверить, что название не изменится

        printAllTasks(taskManager);

        //Проверка методов удаления
        taskManager.deleteTaskById(task2.getId());
        taskManager.deleteEpicById(epic1.getId());
        taskManager.deleteSubtaskById(subtask3.getId());

        printAllTasks(taskManager);
    }

    public static void printAllTasks(TaskManager taskManager) {
        //Вывод всех типов задач
        System.out.println("Список всех задач: ");
        for (Task task : taskManager.getAllTasks()) {
            System.out.println(task);
        }
        System.out.println();

        System.out.println("Cписок всех подзадач: ");
        for (Subtask subtask : taskManager.getAllSubtasks()) {
            System.out.println(subtask);
        }
        System.out.println();

        System.out.println("Cписок всех эпиков: ");
        for (Epic epic : taskManager.getAllEpics()) {
            System.out.println(epic);
        }
        System.out.println("-".repeat(20));
    }
}
