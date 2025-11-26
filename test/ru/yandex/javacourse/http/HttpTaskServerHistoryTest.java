package ru.yandex.javacourse.http;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import ru.yandex.javacourse.model.Epic;
import ru.yandex.javacourse.model.Subtask;
import ru.yandex.javacourse.model.Task;
import ru.yandex.javacourse.service.InMemoryTaskManager;
import ru.yandex.javacourse.service.TaskManager;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

public class HttpTaskServerHistoryTest {
    private static final String EPIC_NAME = "Эпик";
    private static final String EPIC_DESCRIPTION = "Новый эпик";
    private static final String SUBTASK_NAME_1 = "Подзадача 1";
    private static final String SUBTASK_DESCRIPTION_1 = "Первая подзадача";
    private static final String SUBTASK_NAME_2 = "Подзадача 2";
    private static final String SUBTASK_DESCRIPTION_2 = "Вторая подзадача";

    TaskManager manager = new InMemoryTaskManager();
    HttpTaskServer taskServer = new HttpTaskServer(manager);
    HttpClient client = HttpClient.newHttpClient();

    @BeforeEach
    public void setUp() {
        manager.deleteAllTasks();
        manager.deleteAllSubtasks();
        manager.deleteAllEpics();

        try {
            taskServer.start();
        } catch (IOException exception) {
            System.out.println("Возникла ошибка во время старта тестов");
        }
    }

    @AfterEach
    public void shutDown() {
        taskServer.stop();
    }

    @Test
    @DisplayName("Должен возвращать историю вызовов задач при отправке запроса в HttpServer при корректном URL")
    public void test_getHistory_WhenCorrectURL_ShouldReturnHistory() throws IOException, InterruptedException {
        //given
        URI url = URI.create("http://localhost:8080/history");
        Epic epic = manager.addEpic(new Epic(EPIC_NAME, EPIC_DESCRIPTION));
        Subtask subtask1 = manager.addSubtask(new Subtask(SUBTASK_NAME_1, SUBTASK_DESCRIPTION_1, epic.getId()));
        Subtask subtask2 = manager.addSubtask(new Subtask(SUBTASK_NAME_2, SUBTASK_DESCRIPTION_2, epic.getId()));
        manager.getSubtaskById(subtask1.getId());
        manager.getSubtaskById(subtask2.getId());
        HttpRequest request = HttpRequest.newBuilder()
                .uri(url)
                .GET()
                .build();

        //when
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        List<Task> historyFromManager = manager.getHistory();

        // then
        assertEquals(HttpStatusCode.OK.getCode(), response.statusCode());
        assertNotNull(historyFromManager, "История не возвращаются");
        assertEquals(2, historyFromManager.size(), "Некорректное количество задач в истории");
        assertEquals(SUBTASK_NAME_1, historyFromManager.get(0).getName(), "Некорректное имя задачи в истории");
        assertEquals(SUBTASK_NAME_2, historyFromManager.get(1).getName(), "Некорректное имя задачи в истории");
    }

    @Test
    @DisplayName("Не должен возвращать историю вызовов задач при отправке запроса в HttpServer при некорректном URL")
    public void test_getHistory_WhenNotCorrectURL_ShouldReturn404() throws IOException, InterruptedException {
        //given
        URI url = URI.create("http://localhost:8080/getHistory");
        Epic epic = manager.addEpic(new Epic(EPIC_NAME, EPIC_DESCRIPTION));
        Subtask subtask1 = manager.addSubtask(new Subtask(SUBTASK_NAME_1, SUBTASK_DESCRIPTION_1, epic.getId()));
        Subtask subtask2 = manager.addSubtask(new Subtask(SUBTASK_NAME_2, SUBTASK_DESCRIPTION_2, epic.getId()));
        manager.getSubtaskById(subtask1.getId());
        manager.getSubtaskById(subtask2.getId());
        HttpRequest request = HttpRequest.newBuilder()
                .uri(url)
                .GET()
                .build();

        //when
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        // then
        assertEquals(HttpStatusCode.NOT_FOUND.getCode(), response.statusCode());
    }
}
