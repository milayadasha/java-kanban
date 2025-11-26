package ru.yandex.javacourse.http.handler;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.sun.net.httpserver.HttpExchange;
import ru.yandex.javacourse.exceptions.NotFoundException;
import ru.yandex.javacourse.http.HttpStatusCode;
import ru.yandex.javacourse.model.Subtask;
import ru.yandex.javacourse.service.TaskManager;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Optional;

public class SubtaskHandler extends BaseHttpHandler {
    public SubtaskHandler(TaskManager taskManager, Gson gson) {
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
                    getAllSubtasks(exchange);
                    break;
                case "POST":
                    processingRequestedSubtask(exchange, requestBody);
                    break;
                default:
                    sendNotFound(exchange, "Некорректный метод");
            }
        } else if (pathSplitValues.length == 3) {
            String subtaskId = pathSplitValues[2];
            switch (requestMethod) {
                case "GET":
                    getSubtaskById(exchange, subtaskId);
                    break;
                case "DELETE":
                    deleteSubtaskById(exchange, subtaskId);
                    break;
                default:
                    sendNotFound(exchange, "Некорректный метод");
            }
        } else {
            sendNotFound(exchange, "Некорректный метод");
        }
    }

    /**
     * Возвращает все подзадачи, которые хранятся в менеджере
     */
    public void getAllSubtasks(HttpExchange exchange) {
        List<Subtask> subtasks = taskManager.getAllSubtasks();
        String subtasksJson = gson.toJson(subtasks);
        sendText(exchange, subtasksJson, HttpStatusCode.OK.getCode());
    }

    /**
     * Добавляет или обновляет подзадачу в менеджере, которая была прислана в запросе
     */
    public void processingRequestedSubtask(HttpExchange exchange, String requestBody) {
        try {
            Subtask requestSubtask = getSubtaskFromRequestBody(requestBody);
            Optional<Integer> subtaskId = getIdFromRequestBody(requestBody);

            if (subtaskId.isPresent()) {
                taskManager.updateSubtask(requestSubtask);
                String response = "Подзадача с id " + requestSubtask.getId() + " успешно обновлена";
                sendText(exchange, response, HttpStatusCode.OK.getCode());
            } else {
                try {
                    Subtask createdSubtask = taskManager.addSubtask(requestSubtask);
                    String response = "Подзадача с id " + createdSubtask.getId() + " успешно добавлена";
                    sendText(exchange, response, HttpStatusCode.CREATED.getCode());
                } catch (NotFoundException exception) {
                    sendHasInteractions(exchange,
                            "Добавление подзадачи невозможно. Она пересекается с уже существующими задачами");
                }
            }
        } catch (NotFoundException exception) {
            sendNotFound(exchange, "Подзадача для обработки не найдена");
        }
    }

    /**
     * Возвращает подзадачу по id, который был прислан в запросе
     */
    public void getSubtaskById(HttpExchange exchange, String subtaskIdStr) {
        try {
            int subtaskId = Integer.parseInt(subtaskIdStr);
            Subtask returnedSubtask = taskManager.getSubtaskById(subtaskId);
            String returnedSubtaskJson = gson.toJson(returnedSubtask);
            sendText(exchange, returnedSubtaskJson, HttpStatusCode.OK.getCode());
        } catch (NumberFormatException e) {
            sendText(exchange, "Ошибка в работе метода", HttpStatusCode.INTERNAL_SERVER_ERROR.getCode());
        } catch (NotFoundException exception) {
            sendNotFound(exchange, "Подзадача с id " + subtaskIdStr + " не найдена");
        }
    }

    /**
     * Удаляет подзадачу по id, который был прислан в запросе
     */
    public void deleteSubtaskById(HttpExchange exchange, String subtaskIdStr) {
        try {
            int subtaskId = Integer.parseInt(subtaskIdStr);
            taskManager.deleteSubtaskById(subtaskId);
            String response = "Подзадача с id " + subtaskId + " успешно удалена";
            sendText(exchange, response, HttpStatusCode.OK.getCode());
        } catch (NumberFormatException exception) {
            sendText(exchange, "Ошибка в работе метода", HttpStatusCode.INTERNAL_SERVER_ERROR.getCode());
        } catch (NotFoundException exception) {
            sendNotFound(exchange, "Подзадача с id " + subtaskIdStr + " не найдена");
        }
    }

    /**
     * Десериализует подзадачу из запроса
     */
    public Subtask getSubtaskFromRequestBody(String requestBody) {
        JsonElement jsonElement = JsonParser.parseString(requestBody);

        if (!jsonElement.isJsonObject()) {
            System.out.println("Невозможно получить подзадачу. Проверьте корректность запроса");
            return null;
        }
        JsonObject jsonObject = jsonElement.getAsJsonObject();
        return gson.fromJson(jsonObject, Subtask.class);
    }
}