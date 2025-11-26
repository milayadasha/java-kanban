package ru.yandex.javacourse.http;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.sun.net.httpserver.HttpServer;
import ru.yandex.javacourse.http.adapter.DurationTypeAdapter;
import ru.yandex.javacourse.http.adapter.LocalDateTimeTypeAdapter;
import ru.yandex.javacourse.http.handler.*;
import ru.yandex.javacourse.model.Epic;
import ru.yandex.javacourse.model.Subtask;
import ru.yandex.javacourse.model.Task;
import ru.yandex.javacourse.service.Managers;
import ru.yandex.javacourse.service.TaskManager;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.time.Duration;
import java.time.LocalDateTime;

public class HttpTaskServer {
    private final int port;
    private final TaskManager taskManager;
    private HttpServer httpServer;
    private static Gson gson = new GsonBuilder()
            .setPrettyPrinting()
            .serializeNulls()
            .registerTypeAdapter(LocalDateTime.class, new LocalDateTimeTypeAdapter())
            .registerTypeAdapter(Duration.class, new DurationTypeAdapter())
            .create();

    public HttpTaskServer() {
        taskManager = Managers.getDefault();
        port = 8080;
        gson = new GsonBuilder()
                .setPrettyPrinting()
                .serializeNulls()
                .registerTypeAdapter(LocalDateTime.class, new LocalDateTimeTypeAdapter())
                .registerTypeAdapter(Duration.class, new DurationTypeAdapter())
                .create();
    }

    public HttpTaskServer(TaskManager taskManager) {
        this.taskManager = taskManager;
        port = 8080;
    }

    public HttpTaskServer(TaskManager taskManager, int port) {
        this.taskManager = taskManager;
        this.port = port;
    }

    public static Gson getGson() {
        return gson;
    }

    public static void main(String[] args) throws IOException {
        HttpTaskServer httpTaskServer = new HttpTaskServer();
        httpTaskServer.start();
        httpTaskServer.taskManager.addTask(new Task("Звонок бабушке",
                "Позвонить, чтобы узнать новости"));
        httpTaskServer.taskManager.addTask(new Task("Стирка",
                "Постирать белые вещи"));

        Epic epic1 = httpTaskServer.taskManager.addEpic(new Epic("Сделать домашнее задание",
                "Задание для 6 спринта"));
        httpTaskServer.taskManager.addSubtask(new Subtask("Изучить теорию",
                "Прочитать теорию на сайте", epic1.getId()));
        httpTaskServer.taskManager.addSubtask(new Subtask("Освоить тренажёр",
                "Выполнить все задания", epic1.getId()));
        httpTaskServer.taskManager.addSubtask(new Subtask("Прослушать вебинар",
                "Подключиться на вебинар в среду", epic1.getId()));

        //httpTaskServer.stop();

    }

    public void start() throws IOException {
        httpServer = HttpServer.create(new InetSocketAddress(port), 0);
        httpServer.createContext("/tasks", new TaskHandler(taskManager, gson));
        httpServer.createContext("/subtasks", new SubtaskHandler(taskManager, gson));
        httpServer.createContext("/epics", new EpicHandler(taskManager, gson));
        httpServer.createContext("/history", new HistoryHandler(taskManager, gson));
        httpServer.createContext("/prioritized", new PrioritizedHandler(taskManager, gson));
        httpServer.start();
        System.out.println("HTTP-сервер запущен на " + port + " порту!");
    }

    public void stop() {
        httpServer.stop(1);
        System.out.println("HTTP-сервер остановлен на " + port + " порту!");
    }
}