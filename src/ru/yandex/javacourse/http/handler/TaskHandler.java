package ru.yandex.javacourse.http.handler;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.sun.net.httpserver.HttpExchange;
import ru.yandex.javacourse.exceptions.NotFoundException;
import ru.yandex.javacourse.http.HttpStatusCode;
import ru.yandex.javacourse.model.Task;
import ru.yandex.javacourse.service.TaskManager;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Optional;

public class TaskHandler extends BaseHttpHandler {
    public TaskHandler(TaskManager taskManager, Gson gson) {
        super(taskManager, gson);
    }

    /**
     * Основной метод, который обрабатывает входящий запрос
     */
    @Override
    public void handle(HttpExchange exchange) throws IOException {
        String[] pathSplitValues = exchange.getRequestURI().getPath().split("/");
        String requestMethod = exchange.getRequestMethod();
        String requestBody = new String(exchange.getRequestBody().readAllBytes(), StandardCharsets.UTF_8);

        if (pathSplitValues.length == 2) {
            switch (requestMethod) {
                case "GET":
                    getAllTasks(exchange);
                    break;
                case "POST":
                    processingRequestedTask(exchange, requestBody);
                    break;
                default:
                    sendNotFound(exchange, "Некорректный метод");
            }
        } else if (pathSplitValues.length == 3) {
            String taskId = pathSplitValues[2];
            switch (requestMethod) {
                case "GET":
                    getTaskById(exchange, taskId);
                    break;
                case "DELETE":
                    deleteTaskById(exchange, taskId);
                    break;
                default:
                    sendNotFound(exchange, "Некорректный метод");
            }
        } else {
            sendNotFound(exchange, "Некорректный метод");
        }
    }

    /**
     * Возвращает все задачи, которые хранятся в менеджере
     */
    public void getAllTasks(HttpExchange exchange) {
        List<Task> tasks = taskManager.getAllTasks();
        String tasksJson = gson.toJson(tasks);
        sendText(exchange, tasksJson, HttpStatusCode.OK.getCode());
    }

    /**
     * Добавляет или обновляет задачу в менеджере, которая была прислана в запросе
     */
    public void processingRequestedTask(HttpExchange exchange, String requestBody) {
        try {
            Task requestTask = getTaskFromRequestBody(requestBody);
            Optional<Integer> taskId = getIdFromRequestBody(requestBody);

            if (taskId.isPresent()) {
                taskManager.updateTask(requestTask);
                String response = "Задача с id " + requestTask.getId() + " успешно обновлена";
                sendText(exchange, response, HttpStatusCode.OK.getCode());
            } else {
                try {
                    Task createdTask = taskManager.addTask(requestTask);
                    String response = "Задача с id " + createdTask.getId() + " успешно добавлена";
                    sendText(exchange, response, HttpStatusCode.CREATED.getCode());
                } catch (NotFoundException exception) {
                    sendHasInteractions(exchange,
                            "Добавление задачи невозможно. Она пересекается с уже существующими задачами");
                }
            }
        } catch (NotFoundException exception) {
            sendNotFound(exchange, "Задача для обработки не найдена");
        }
    }

    /**
     * Возвращает задачу по id, который был прислан в запросе
     */
    public void getTaskById(HttpExchange exchange, String taskIdStr) {
        try {
            int taskId = Integer.parseInt(taskIdStr);
            Task returnedTask = taskManager.getTaskById(taskId);
            String returnedTaskJson = gson.toJson(returnedTask);
            sendText(exchange, returnedTaskJson, HttpStatusCode.OK.getCode());
        } catch (NumberFormatException exception) {
            sendText(exchange, "Ошибка в работе метода", HttpStatusCode.INTERNAL_SERVER_ERROR.getCode());
        } catch (NotFoundException exception) {
            sendNotFound(exchange, "Задача с id " + taskIdStr + " не найдена");
        }
    }

    /**
     * Удаляет задачу по id, который был прислан в запросе
     */
    public void deleteTaskById(HttpExchange exchange, String taskIdStr) {
        try {
            int taskId = Integer.parseInt(taskIdStr);
            taskManager.deleteTaskById(taskId);
            String response = "Задача с id " + taskId + " успешно удалена";
            sendText(exchange, response, HttpStatusCode.OK.getCode());
        } catch (NumberFormatException exception) {
            sendText(exchange, "Ошибка в работе метода", HttpStatusCode.INTERNAL_SERVER_ERROR.getCode());
        } catch (NotFoundException exception) {
            sendNotFound(exchange, "Задача с id " + taskIdStr + " не найдена");
        }
    }

    /**
     * Десериализует задачу из запроса
     */
    public Task getTaskFromRequestBody(String requestBody) {
        JsonElement jsonElement = JsonParser.parseString(requestBody);

        if (!jsonElement.isJsonObject()) {
            System.out.println("Невозможно получить задачу. Проверьте корректность запроса");
            return null;
        }
        JsonObject jsonObject = jsonElement.getAsJsonObject();
        return gson.fromJson(jsonObject, Task.class);
    }
}