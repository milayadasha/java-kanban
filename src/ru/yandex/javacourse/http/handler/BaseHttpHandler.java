package ru.yandex.javacourse.http.handler;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import ru.yandex.javacourse.http.HttpStatusCode;
import ru.yandex.javacourse.service.TaskManager;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.Optional;

abstract class BaseHttpHandler implements HttpHandler {
    private static final Charset DEFAULT_CHARSET = StandardCharsets.UTF_8;
    protected TaskManager taskManager;
    protected Gson gson;

    protected BaseHttpHandler(TaskManager taskManager, Gson gson) {
        this.taskManager = taskManager;
        this.gson = gson;
    }

    /**
     * Основной метод, который обрабатывает входящий запрос
     */
    @Override
    abstract public void handle(HttpExchange exchange) throws IOException;

    /**
     * Отправляет ответ от сервера клиенту с указанным кодом.
     * Используется для отправки 200 и 201 ответов
     */
    protected void sendText(HttpExchange exchange, String response, int responseCode) {
        try (OutputStream outputStream = exchange.getResponseBody()) {
            exchange.getResponseHeaders().add("Content-Type", "application/json;charset=utf-8");
            exchange.sendResponseHeaders(responseCode, 0);
            outputStream.write(response.getBytes(DEFAULT_CHARSET));
        } catch (IOException exception) {
            System.out.println("Возникла проблема при отправке ответа выполнения метода");
        }
    }

    /**
     * Отправляет ответ от сервера клиенту с кодом 404.
     * Используется для отправки сообщения о том, что элемент не найден
     */
    protected void sendNotFound(HttpExchange exchange, String response) {
        try (OutputStream outputStream = exchange.getResponseBody()) {
            exchange.getResponseHeaders().add("Content-Type", "application/json;charset=utf-8");
            exchange.sendResponseHeaders(HttpStatusCode.NOT_FOUND.getCode(), 0);
            outputStream.write(response.getBytes(DEFAULT_CHARSET));
        } catch (IOException exception) {
            System.out.println("Возникла проблема при отправке ответа о том, что элемент не найден");
        }
    }

    /**
     * Отправляет ответ от сервера клиенту с кодом 406.
     * Используется для отправки сообщения о том, что элемент пересекается с уже существующими
     */
    protected void sendHasInteractions(HttpExchange exchange, String response) {
        try (OutputStream outputStream = exchange.getResponseBody()) {
            exchange.getResponseHeaders().add("Content-Type", "application/json;charset=utf-8");
            exchange.sendResponseHeaders(HttpStatusCode.NOT_ACCEPTABLE.getCode(), 0);
            outputStream.write(response.getBytes(DEFAULT_CHARSET));
        } catch (IOException exception) {
            System.out.println("Возникла проблема при отправке ответа о том, что объект пересекается с существующим");
        }
    }

    /**
     * Возвращает Id задачи из запроса
     */
    public Optional<Integer> getIdFromRequestBody(String requestBody) {
        JsonElement jsonElement = JsonParser.parseString(requestBody);

        if (!jsonElement.isJsonObject()) {
            System.out.println("Невозможно получить номер задачи. Проверьте корректность запроса");
            return Optional.empty();
        }

        JsonObject jsonObject = jsonElement.getAsJsonObject();
        JsonElement idElement = jsonObject.get("id");

        if (idElement == null || idElement.getAsInt() == 0) {
            return Optional.empty();
        }

        return Optional.of(idElement.getAsInt());
    }
}