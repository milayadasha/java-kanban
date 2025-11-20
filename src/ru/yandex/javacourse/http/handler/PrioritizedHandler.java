package ru.yandex.javacourse.http.handler;

import com.google.gson.Gson;
import com.sun.net.httpserver.HttpExchange;
import ru.yandex.javacourse.http.HttpStatusCode;
import ru.yandex.javacourse.model.Task;
import ru.yandex.javacourse.service.TaskManager;

import java.io.IOException;
import java.util.TreeSet;

public class PrioritizedHandler extends BaseHttpHandler {
    public PrioritizedHandler(TaskManager taskManager, Gson gson) {
        super(taskManager, gson);
    }

    /**
     * Основной метод, который обрабатывает входящий запрос
     */
    @Override
    public void handle(HttpExchange exchange) throws IOException {
        String[] pathSplitValues = exchange.getRequestURI().getPath().split("/");
        String requestMethod = exchange.getRequestMethod();

        if (pathSplitValues.length == 2 && requestMethod.equals("GET")) {
            getPrioritizedTasks(exchange);
        } else {
            sendNotFound(exchange, "Некорректный метод");
        }
    }

    /**
     * Возвращает приоритизированные задач, которые хранятся в менеджере
     */
    public void getPrioritizedTasks(HttpExchange exchange) {
        TreeSet<Task> prioritizedTasks = taskManager.getPrioritizedTasks();
        String response = gson.toJson(prioritizedTasks);
        sendText(exchange, response, HttpStatusCode.OK.getCode());
    }
}
