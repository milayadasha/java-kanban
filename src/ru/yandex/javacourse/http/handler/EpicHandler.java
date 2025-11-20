package ru.yandex.javacourse.http.handler;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.sun.net.httpserver.HttpExchange;
import ru.yandex.javacourse.exceptions.NotFoundException;
import ru.yandex.javacourse.http.HttpStatusCode;
import ru.yandex.javacourse.model.Epic;
import ru.yandex.javacourse.model.Subtask;
import ru.yandex.javacourse.service.TaskManager;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Optional;

public class EpicHandler extends BaseHttpHandler {
    public EpicHandler(TaskManager taskManager, Gson gson) {
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
                    getAllEpics(exchange);
                    break;
                case "POST":
                    processingRequestedEpic(exchange, requestBody);
                    break;
                default:
                    sendNotFound(exchange, "Некорректный метод");
            }
        } else if (pathSplitValues.length == 3) {
            String epicId = pathSplitValues[2];
            switch (requestMethod) {
                case "GET":
                    getEpicById(exchange, epicId);
                    break;
                case "DELETE":
                    deleteEpicById(exchange, epicId);
                    break;
                default:
                    sendNotFound(exchange, "Некорректный метод");
            }
        } else if (pathSplitValues.length == 4) {
            String epicId = pathSplitValues[2];
            if (requestMethod.equals("GET")) {
                getSubtasksByEpicId(exchange, epicId);
            } else {
                sendNotFound(exchange, "Некорректный метод");
            }
        } else {
            sendNotFound(exchange, "Некорректный метод");
        }
    }

    /**
     * Возвращает все эпики, которые хранятся в менеджере
     */
    public void getAllEpics(HttpExchange exchange) {
        List<Epic> epics = taskManager.getAllEpics();
        String epicsJson = gson.toJson(epics);
        sendText(exchange, epicsJson, HttpStatusCode.OK.getCode());
    }

    /**
     * Добавляет или обновляет эпик в менеджере, который был прислан в запросе
     */
    public void processingRequestedEpic(HttpExchange exchange, String requestBody) {
        try {
            Epic requestEpic = getEpicFromRequestBody(requestBody);

            Optional<Integer> epicId = getIdFromRequestBody(requestBody);

            if (epicId.isPresent()) {
                taskManager.updateEpic(requestEpic);
                String response = "Эпик с id " + requestEpic.getId() + " успешно обновлён";
                sendText(exchange, response, HttpStatusCode.OK.getCode());
            } else {
                Epic createdEpic = taskManager.addEpic(requestEpic);
                String response = "Эпик с id " + createdEpic.getId() + " успешно добавлен";
                sendText(exchange, response, HttpStatusCode.CREATED.getCode());
            }
        } catch (NotFoundException exception) {
            sendNotFound(exchange, "Эпик для обработки не найден");
        }
    }

    /**
     * Возвращает эпик по id, который был прислан в запросе
     */
    public void getEpicById(HttpExchange exchange, String epicIdStr) {
        try {
            int epicId = Integer.parseInt(epicIdStr);
            Epic returnedEpic = taskManager.getEpicById(epicId);
            String returnedEpicJson = gson.toJson(returnedEpic);
            sendText(exchange, returnedEpicJson, HttpStatusCode.OK.getCode());
        } catch (NumberFormatException exception) {
            sendText(exchange, "Ошибка в работе метода", HttpStatusCode.INTERNAL_SERVER_ERROR.getCode());
        } catch (NotFoundException exception) {
            sendNotFound(exchange, "Эпик с id " + epicIdStr + " не найден");
        }
    }

    /**
     * Удаляет эпик по id, который был прислан в запросе
     */
    public void deleteEpicById(HttpExchange exchange, String epicIdStr) {
        try {
            int epicId = Integer.parseInt(epicIdStr);
            taskManager.deleteEpicById(epicId);
            String response = "Эпик с id " + epicId + " успешно удалён";
            sendText(exchange, response, HttpStatusCode.OK.getCode());
        } catch (NumberFormatException exception) {
            sendText(exchange, "Ошибка в работе метода", HttpStatusCode.INTERNAL_SERVER_ERROR.getCode());
        } catch (NotFoundException exception) {
            sendNotFound(exchange, "Эпик с id " + epicIdStr + " не найден");
        }
    }

    /**
     * Возвращает все подзадачи эпика, id которого был прислан в запросе
     */
    public void getSubtasksByEpicId(HttpExchange exchange, String epicIdStr) {
        try {
            int epicId = Integer.parseInt(epicIdStr);
            List<Subtask> subtasksByEpicId = taskManager.getAllSubtasksByEpicId(epicId);
            String subtasksByEpicIdJson = gson.toJson(subtasksByEpicId);
            sendText(exchange, subtasksByEpicIdJson, HttpStatusCode.OK.getCode());
        } catch (NumberFormatException exception) {
            sendText(exchange, "Ошибка в работе метода", HttpStatusCode.INTERNAL_SERVER_ERROR.getCode());
        } catch (NotFoundException exception) {
            sendNotFound(exchange, "Эпик с id " + epicIdStr + " не найден");
        }
    }

    /**
     * Десериализует эпик из запроса
     */
    public Epic getEpicFromRequestBody(String requestBody) {
        JsonElement jsonElement = JsonParser.parseString(requestBody);

        if (!jsonElement.isJsonObject()) {
            System.out.println("Невозможно получить эпик. Проверьте корректность запроса");
            return null;
        }
        JsonObject jsonObject = jsonElement.getAsJsonObject();
        return gson.fromJson(jsonObject, Epic.class);
    }
}