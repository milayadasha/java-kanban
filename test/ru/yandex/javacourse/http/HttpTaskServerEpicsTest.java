package ru.yandex.javacourse.http;

import com.google.gson.Gson;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import ru.yandex.javacourse.model.Epic;
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

public class HttpTaskServerEpicsTest {
    private static final String EPIC_NAME = "Эпик";
    private static final String EPIC_DESCRIPTION = "Новый эпик";
    private static final String EPIC_NAME_UPDATED = "Обновлённый эпик";
    private static final String EPIC_DESCRIPTION_UPDATED = "Новый эпик";
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
    @DisplayName("Должен сохранять эпик в менеджер при отправке запроса в HttpServer")
    public void test_AddEpic_WhenSendEpicWithId_ManagerShouldStore() throws IOException, InterruptedException {
        //given
        URI url = URI.create("http://localhost:8080/epics");
        Epic epic = new Epic(EPIC_NAME, EPIC_DESCRIPTION);

        String epicJson = gson.toJson(epic);
        HttpRequest request = HttpRequest.newBuilder()
                .uri(url)
                .POST(HttpRequest.BodyPublishers.ofString(epicJson))
                .build();

        //when
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        List<Epic> epicsFromManager = manager.getAllEpics();

        // then
        assertEquals(HttpStatusCode.CREATED.getCode(), response.statusCode());
        assertNotNull(epicsFromManager, "Эпики не возвращаются");
        assertEquals(1, epicsFromManager.size(), "Некорректное количество эпиков");
        assertEquals(EPIC_NAME, epicsFromManager.get(0).getName(), "Некорректное имя эпиков");
    }

    @Test
    @DisplayName("Должен обновлять эпик в менеджере при отправке запроса в HttpServer")
    public void test_UpdateEpic_WhenSendEpicWithId_ManagerShouldStoreUpdate() throws IOException, InterruptedException {
        //given
        URI url = URI.create("http://localhost:8080/epics");
        Epic epic = manager.addEpic(new Epic(EPIC_NAME, EPIC_DESCRIPTION));
        Epic updatedEpic = new Epic(EPIC_NAME_UPDATED, EPIC_DESCRIPTION_UPDATED);
        updatedEpic.setId(epic.getId());
        String epicJson = gson.toJson(updatedEpic);
        HttpRequest request = HttpRequest.newBuilder()
                .uri(url)
                .POST(HttpRequest.BodyPublishers.ofString(epicJson))
                .build();

        //when
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        List<Epic> epicsFromManager = manager.getAllEpics();

        // then
        assertEquals(HttpStatusCode.OK.getCode(), response.statusCode());
        assertNotNull(epicsFromManager, "Эпики не возвращаются");
        assertEquals(1, epicsFromManager.size(), "Некорректное количество эпиков");
        assertEquals(EPIC_NAME_UPDATED, epicsFromManager.get(0).getName(), "Некорректное имя эпика");
    }

    @Test
    @DisplayName("Должен возвращать эпик по id из менеджера при отправке запроса в HttpServer")
    public void test_GetEpicById_WhenSendRequest_ShouldReturnById() throws IOException, InterruptedException {
        //given
        Epic epic = manager.addEpic(new Epic(EPIC_NAME, EPIC_DESCRIPTION));
        URI url = URI.create("http://localhost:8080/epics/" + epic.getId());
        HttpRequest request = HttpRequest.newBuilder()
                .uri(url)
                .GET()
                .build();

        //when
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        List<Epic> epicsFromManager = manager.getAllEpics();

        // then
        assertEquals(HttpStatusCode.OK.getCode(), response.statusCode());
        assertNotNull(epicsFromManager, "Эпики не возвращаются");
        assertEquals(1, epicsFromManager.size(), "Некорректное количество эпиков");
        assertEquals(EPIC_NAME, epicsFromManager.get(0).getName(), "Некорректное имя эпика");
    }

    @Test
    @DisplayName("Должен удалять эпик по id в менеджере при отправке запроса в HttpServer")
    public void test_DeleteEpicById_WhenSendCorrectId_ShouldReturnNull() throws IOException, InterruptedException {
        //given
        Epic epic = manager.addEpic(new Epic(EPIC_NAME, EPIC_DESCRIPTION));
        URI url = URI.create("http://localhost:8080/epics" + "/" + epic.getId());
        HttpRequest request = HttpRequest.newBuilder()
                .uri(url)
                .DELETE()
                .build();

        //when
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        List<Epic> epicsFromManager = manager.getAllEpics();

        // then
        assertEquals(HttpStatusCode.OK.getCode(), response.statusCode());
        assertNotNull(epicsFromManager, "Эпики не возвращаются");
        assertEquals(0, epicsFromManager.size(), "Некорректное количество эпиков");
    }

    @Test
    @DisplayName("Должен возвращать все эпики из менеджера при отправке запроса в HttpServer")
    public void test_GetAllEpics_WhenSendRequest_ShouldReturnAll() throws IOException, InterruptedException {
        //given
        manager.addEpic(new Epic(EPIC_NAME, EPIC_DESCRIPTION));
        manager.addEpic(new Epic(EPIC_NAME_UPDATED, EPIC_DESCRIPTION_UPDATED));
        URI url = URI.create("http://localhost:8080/epics");
        HttpRequest request = HttpRequest.newBuilder()
                .uri(url)
                .GET()
                .build();

        //when
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        List<Epic> epicsFromManager = manager.getAllEpics();

        // then
        assertEquals(HttpStatusCode.OK.getCode(), response.statusCode());
        assertNotNull(epicsFromManager, "Эпики не возвращаются");
        assertEquals(2, epicsFromManager.size(), "Некорректное количество эпиков");
        assertEquals(EPIC_NAME, epicsFromManager.get(0).getName(), "Некорректное имя эпика");
        assertEquals(EPIC_NAME_UPDATED, epicsFromManager.get(1).getName(), "Некорректное имя эпика");
    }

    @Test
    @DisplayName("Не должен сохранять эпик в менеджер при отправке запроса по некорректному адресу")
    public void test_AddEpic_WhenIncorrectURL_ShouldReturn404() throws IOException, InterruptedException {
        //given
        URI incorrectUrl = URI.create("http://localhost:8080/addEpic");
        Epic epic = new Epic(EPIC_NAME, EPIC_DESCRIPTION);
        String epicJson = gson.toJson(epic);
        HttpRequest request = HttpRequest.newBuilder()
                .uri(incorrectUrl)
                .POST(HttpRequest.BodyPublishers.ofString(epicJson))
                .build();

        //when
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        // then
        assertEquals(HttpStatusCode.NOT_FOUND.getCode(), response.statusCode(),
                "Не должен добавлять эпик при некорректном URL");
    }

    @Test
    @DisplayName("Не должен обновлять эпик в менеджере при отправке запроса с несуществующим id")
    public void test_UpdateEpic_WhenIdNotExist_ShouldReturn404() throws IOException, InterruptedException {
        //given
        URI url = URI.create("http://localhost:8080/epics");
        Epic epic = manager.addEpic(new Epic(EPIC_NAME, EPIC_DESCRIPTION));
        Epic updatedEpic = new Epic(EPIC_NAME_UPDATED, EPIC_DESCRIPTION_UPDATED);
        updatedEpic.setId(epic.getId() + 1);
        String epicJson = gson.toJson(updatedEpic);
        HttpRequest request = HttpRequest.newBuilder()
                .uri(url)
                .POST(HttpRequest.BodyPublishers.ofString(epicJson))
                .build();

        //when
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        // then
        assertEquals(HttpStatusCode.NOT_FOUND.getCode(), response.statusCode(),
                "Не должен обновлять эпик по несуществующему id");
    }

    @Test
    @DisplayName("Не должен возвращать эпик из менеджера при отправке запроса с несуществующим id ")
    public void test_GetEpicById_WhenIdNotExist_ShouldReturn404() throws IOException, InterruptedException {
        //given
        Epic epic = manager.addEpic(new Epic(EPIC_NAME, EPIC_DESCRIPTION));
        URI url = URI.create("http://localhost:8080/epics/" + (epic.getId() + 1));
        HttpRequest request = HttpRequest.newBuilder()
                .uri(url)
                .GET()
                .build();

        //when
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        // then
        assertEquals(HttpStatusCode.NOT_FOUND.getCode(), response.statusCode(),
                "Не должен возвращать эпик по несуществующему id");
    }

    @Test
    @DisplayName("Не должен возвращать эпик из менеджера при отправке запроса с некорректным id ")
    public void test_GetEpicById_WhenIdNotAcceptable_ShouldReturn500() throws IOException, InterruptedException {
        //given
        Epic epic = manager.addEpic(new Epic(EPIC_NAME, EPIC_DESCRIPTION));
        URI url = URI.create("http://localhost:8080/epics/" + epic.getId() + "test");
        HttpRequest request = HttpRequest.newBuilder()
                .uri(url)
                .GET()
                .build();

        //when
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        // then
        assertEquals(HttpStatusCode.INTERNAL_SERVER_ERROR.getCode(), response.statusCode(),
                "Не должен возвращать эпик по некорректному id");
    }

    @Test
    @DisplayName("Не должен удалять эпик в менеджере при отправке запроса с несуществующим id ")
    public void test_DeleteEpicById_WhenIdNotExist_ShouldReturn404() throws IOException, InterruptedException {
        //given
        Epic epic = manager.addEpic(new Epic(EPIC_NAME, EPIC_DESCRIPTION));
        URI url = URI.create("http://localhost:8080/epics/" + (epic.getId() + 1));
        HttpRequest request = HttpRequest.newBuilder()
                .uri(url)
                .DELETE()
                .build();

        //when
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        // then
        assertEquals(HttpStatusCode.NOT_FOUND.getCode(), response.statusCode(),
                "Не должен удалять эпик по несуществующему id");
    }

    @Test
    @DisplayName("Не должен удалять эпик в менеджере при отправке запроса с некорректным id ")
    public void test_DeleteEpicById_WhenIdNotAcceptable_ShouldReturn500() throws IOException, InterruptedException {
        //given
        Epic epic = manager.addEpic(new Epic(EPIC_NAME, EPIC_DESCRIPTION));
        URI url = URI.create("http://localhost:8080/epics/" + epic.getId() + "test");
        HttpRequest request = HttpRequest.newBuilder()
                .uri(url)
                .DELETE()
                .build();

        //when
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        // then
        assertEquals(HttpStatusCode.INTERNAL_SERVER_ERROR.getCode(), response.statusCode(),
                "Не должен удалять эпик по некорректному id");
    }
}