package ru.yandex.javacourse.http;

import com.google.gson.Gson;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import ru.yandex.javacourse.model.Epic;
import ru.yandex.javacourse.model.Subtask;
import ru.yandex.javacourse.service.InMemoryTaskManager;
import ru.yandex.javacourse.service.TaskManager;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

public class HttpTaskServerSubtasksTest {
    private static final String EPIC_NAME = "Эпик";
    private static final String EPIC_DESCRIPTION = "Новый эпик";
    private static final String SUBTASK_NAME_1 = "Подзадача 1";
    private static final String SUBTASK_DESCRIPTION_1 = "Первая подзадача";
    private static final String SUBTASK_NAME_2 = "Подзадача 2";
    private static final String SUBTASK_DESCRIPTION_2 = "Вторая подзадача";

    TaskManager manager = new InMemoryTaskManager();
    HttpTaskServer taskServer = new HttpTaskServer(manager);
    HttpClient client = HttpClient.newHttpClient();
    Gson gson = HttpTaskServer.getGson();

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
    @DisplayName("Должен сохранять подзадачу в менеджер при отправке запроса в HttpServer")
    public void test_AddSubtask_WhenSendSubtask_ManagerShouldStore() throws IOException, InterruptedException {
        //given
        URI url = URI.create("http://localhost:8080/subtasks");
        Epic epic = manager.addEpic(new Epic(EPIC_NAME, EPIC_DESCRIPTION));
        Subtask subtask = new Subtask(SUBTASK_NAME_1, SUBTASK_DESCRIPTION_1, epic.getId());

        epic.setStartTime(LocalDateTime.now());
        epic.setDuration(Duration.ofMinutes(5));
        String subtaskJson = gson.toJson(subtask);
        HttpRequest request = HttpRequest.newBuilder()
                .uri(url)
                .POST(HttpRequest.BodyPublishers.ofString(subtaskJson))
                .build();

        //when
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        List<Subtask> subtasksFromManager = manager.getAllSubtasks();

        // then
        assertEquals(HttpStatusCode.CREATED.getCode(), response.statusCode());
        assertNotNull(subtasksFromManager, "Подзадачи не возвращаются");
        assertEquals(1, subtasksFromManager.size(), "Некорректное количество подзадач");
        assertEquals(SUBTASK_NAME_1, subtasksFromManager.get(0).getName(), "Некорректное имя подзадачи");
    }

    @Test
    @DisplayName("Должен обновлять подзадачу в менеджере при отправке запроса в HttpServer")
    public void test_UpdateSubtask_WhenSendSubtaskWithId_ManagerShouldStoreUpdate()
            throws IOException, InterruptedException {
        //given
        URI url = URI.create("http://localhost:8080/subtasks");
        Epic epic = manager.addEpic(new Epic(EPIC_NAME, EPIC_DESCRIPTION));
        Subtask subtask = manager.addSubtask(new Subtask(SUBTASK_NAME_1, SUBTASK_DESCRIPTION_1, epic.getId()));
        Subtask updatedSubtask = new Subtask(SUBTASK_NAME_2, SUBTASK_DESCRIPTION_2, epic.getId());
        updatedSubtask.setId(subtask.getId());
        String subtaskJson = gson.toJson(updatedSubtask);
        HttpRequest request = HttpRequest.newBuilder()
                .uri(url)
                .POST(HttpRequest.BodyPublishers.ofString(subtaskJson))
                .build();

        //when
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        List<Subtask> subtasksFromManager = manager.getAllSubtasks();

        // then
        assertEquals(HttpStatusCode.OK.getCode(), response.statusCode());
        assertNotNull(subtasksFromManager, "Подзадачи не возвращаются");
        assertEquals(1, subtasksFromManager.size(), "Некорректное количество подзадач");
        assertEquals(SUBTASK_NAME_2, subtasksFromManager.get(0).getName(), "Некорректное имя подзадачи");
    }

    @Test
    @DisplayName("Должен возвращать подзадачу по id из менеджера при отправке запроса в HttpServer")
    public void test_GetSubtaskById_WhenSendRequest_ShouldReturnById() throws IOException, InterruptedException {
        //given
        Epic epic = manager.addEpic(new Epic(EPIC_NAME, EPIC_DESCRIPTION));
        Subtask subtask = manager.addSubtask(new Subtask(SUBTASK_NAME_1, SUBTASK_DESCRIPTION_1, epic.getId()));
        URI url = URI.create("http://localhost:8080/subtasks/" + subtask.getId());
        HttpRequest request = HttpRequest.newBuilder()
                .uri(url)
                .GET()
                .build();

        //when
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        List<Subtask> subtasksFromManager = manager.getAllSubtasks();

        // then
        assertEquals(HttpStatusCode.OK.getCode(), response.statusCode());
        assertNotNull(subtasksFromManager, "Эпики не возвращаются");
        assertEquals(1, subtasksFromManager.size(), "Некорректное количество подзадач");
        assertEquals(SUBTASK_NAME_1, subtasksFromManager.get(0).getName(), "Некорректное имя подзадачи");
    }

    @Test
    @DisplayName("Должен удалять подзадачу по id в менеджере при отправке запроса в HttpServer")
    public void test_DeleteSubtaskById_WhenSendCorrectId_ShouldReturnNull() throws IOException, InterruptedException {
        //given
        Epic epic = manager.addEpic(new Epic(EPIC_NAME, EPIC_DESCRIPTION));
        Subtask subtask = manager.addSubtask(new Subtask(SUBTASK_NAME_1, SUBTASK_DESCRIPTION_1, epic.getId()));
        URI url = URI.create("http://localhost:8080/subtasks/" + subtask.getId());
        HttpRequest request = HttpRequest.newBuilder()
                .uri(url)
                .DELETE()
                .build();

        //when
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        List<Subtask> subtasksFromManager = manager.getAllSubtasks();

        // then
        assertEquals(HttpStatusCode.OK.getCode(), response.statusCode());
        assertNotNull(subtasksFromManager, "Подзадачи не возвращаются");
        assertEquals(0, subtasksFromManager.size(), "Некорректное количество подзадач");
    }

    @Test
    @DisplayName("Должен возвращать все подзадачи из менеджера при отправке запроса в HttpServer")
    public void test_GetAllSubtasks_WhenSendRequest_ShouldReturnAll() throws IOException, InterruptedException {
        //given
        Epic epic = manager.addEpic(new Epic(EPIC_NAME, EPIC_DESCRIPTION));
        manager.addSubtask(new Subtask(SUBTASK_NAME_1, SUBTASK_DESCRIPTION_1, epic.getId()));
        manager.addSubtask(new Subtask(SUBTASK_NAME_2, SUBTASK_DESCRIPTION_2, epic.getId()));
        URI url = URI.create("http://localhost:8080/subtasks");
        HttpRequest request = HttpRequest.newBuilder()
                .uri(url)
                .GET()
                .build();

        //when
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        List<Subtask> subtasksFromManager = manager.getAllSubtasks();

        // then
        assertEquals(HttpStatusCode.OK.getCode(), response.statusCode());
        assertNotNull(subtasksFromManager, "Подзадачи не возвращаются");
        assertEquals(2, subtasksFromManager.size(), "Некорректное количество подзадач");
        assertEquals(SUBTASK_NAME_1, subtasksFromManager.get(0).getName(), "Некорректное имя подзадачи");
        assertEquals(SUBTASK_NAME_2, subtasksFromManager.get(1).getName(), "Некорректное имя подзадачи");
    }

    @Test
    @DisplayName("Не должен сохранять подзадачу в менеджер при отправке запроса по некорректному адресу")
    public void test_AddSubtask_WhenIncorrectURL_ShouldReturn404() throws IOException, InterruptedException {
        //given
        URI incorrectUrl = URI.create("http://localhost:8080/addSubtask");
        Epic epic = manager.addEpic(new Epic(EPIC_NAME, EPIC_DESCRIPTION));
        Subtask subtask = new Subtask(SUBTASK_NAME_1, SUBTASK_DESCRIPTION_1, epic.getId());
        String subtaskJson = gson.toJson(subtask);
        HttpRequest request = HttpRequest.newBuilder()
                .uri(incorrectUrl)
                .POST(HttpRequest.BodyPublishers.ofString(subtaskJson))
                .build();

        //when
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        // then
        assertEquals(HttpStatusCode.NOT_FOUND.getCode(), response.statusCode(),
                "Не должен добавлять подзадачу при некорректном URL");
    }

    @Test
    @DisplayName("Не должен обновлять подзадачу в менеджере при отправке запроса с несуществующим id")
    public void test_UpdateSubtask_WhenIdNotExist_ShouldReturn404() throws IOException, InterruptedException {
        //given
        URI url = URI.create("http://localhost:8080/subtasks");
        Epic epic = manager.addEpic(new Epic(EPIC_NAME, EPIC_DESCRIPTION));
        Subtask subtask = manager.addSubtask(new Subtask(SUBTASK_NAME_1, SUBTASK_DESCRIPTION_1, epic.getId()));
        Subtask updatedSubtask = new Subtask(SUBTASK_NAME_2, SUBTASK_DESCRIPTION_2, epic.getId());
        updatedSubtask.setId(subtask.getId() + 1);
        String subtaskJson = gson.toJson(updatedSubtask);
        HttpRequest request = HttpRequest.newBuilder()
                .uri(url)
                .POST(HttpRequest.BodyPublishers.ofString(subtaskJson))
                .build();

        //when
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        // then
        assertEquals(HttpStatusCode.NOT_FOUND.getCode(), response.statusCode(),
                "Не должен обновлять подзадачу по несуществующему id");
    }

    @Test
    @DisplayName("Не должен возвращать подзадачу из менеджера при отправке запроса с несуществующим id ")
    public void test_GetSubtaskById_WhenIdNotExist_ShouldReturn404() throws IOException, InterruptedException {
        //given
        Epic epic = manager.addEpic(new Epic(EPIC_NAME, EPIC_DESCRIPTION));
        Subtask subtask = manager.addSubtask(new Subtask(SUBTASK_NAME_1, SUBTASK_DESCRIPTION_1, epic.getId()));
        URI url = URI.create("http://localhost:8080/subtasks/" + (subtask.getId() + 1));
        HttpRequest request = HttpRequest.newBuilder()
                .uri(url)
                .GET()
                .build();

        //when
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        // then
        assertEquals(HttpStatusCode.NOT_FOUND.getCode(), response.statusCode(),
                "Не должен возвращать подзадачу по несуществующему id");
    }

    @Test
    @DisplayName("Не должен возвращать подзадачу из менеджера при отправке запроса с некорректным id ")
    public void test_GetSubtaskById_WhenIdNotAcceptable_ShouldReturn500() throws IOException, InterruptedException {
        //given
        Epic epic = manager.addEpic(new Epic(EPIC_NAME, EPIC_DESCRIPTION));
        Subtask subtask = manager.addSubtask(new Subtask(SUBTASK_NAME_1, SUBTASK_DESCRIPTION_1, epic.getId()));
        URI url = URI.create("http://localhost:8080/subtasks/" + subtask.getId() + "test");
        HttpRequest request = HttpRequest.newBuilder()
                .uri(url)
                .GET()
                .build();

        //when
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        // then
        assertEquals(HttpStatusCode.INTERNAL_SERVER_ERROR.getCode(), response.statusCode(),
                "Не должен возвращать подзадачу по некорректному id");
    }

    @Test
    @DisplayName("Не должен удалять подзадачу в менеджере при отправке запроса с несуществующим id ")
    public void test_DeleteSubtaskById_WhenIdNotExist_ShouldReturn404() throws IOException, InterruptedException {
        //given
        Epic epic = manager.addEpic(new Epic(EPIC_NAME, EPIC_DESCRIPTION));
        Subtask subtask = manager.addSubtask(new Subtask(SUBTASK_NAME_1, SUBTASK_DESCRIPTION_1, epic.getId()));
        URI url = URI.create("http://localhost:8080/subtasks/" + (subtask.getId() + 1));
        HttpRequest request = HttpRequest.newBuilder()
                .uri(url)
                .DELETE()
                .build();

        //when
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        // then
        assertEquals(HttpStatusCode.NOT_FOUND.getCode(), response.statusCode(),
                "Не должен удалять подзадачу по несуществующему id");
    }

    @Test
    @DisplayName("Не должен удалять подзадачу в менеджере при отправке запроса с некорректным id ")
    public void test_DeleteSubtaskById_WhenIdNotAcceptable_ShouldReturn500() throws IOException, InterruptedException {
        //given
        Epic epic = manager.addEpic(new Epic(EPIC_NAME, EPIC_DESCRIPTION));
        Subtask subtask = manager.addSubtask(new Subtask(SUBTASK_NAME_1, SUBTASK_DESCRIPTION_1, epic.getId()));
        URI url = URI.create("http://localhost:8080/subtasks/" + subtask.getId() + "test");
        HttpRequest request = HttpRequest.newBuilder()
                .uri(url)
                .DELETE()
                .build();

        //when
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        // then
        assertEquals(HttpStatusCode.INTERNAL_SERVER_ERROR.getCode(), response.statusCode(),
                "Не должен удалять подзадачу по некорректному id");
    }
}