package ru.yandex.javacourse.http;

import com.google.gson.Gson;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import ru.yandex.javacourse.model.Task;
import ru.yandex.javacourse.model.TaskStatus;
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

public class HttpTaskServerTasksTest {
    private static final String TASK_NAME = "Задача";
    private static final String TASK_DESCRIPTION = "Новая задача";
    private static final String TASK_NAME_UPDATED = "Обновлённая задача";
    private static final String TASK_DESCRIPTION_UPDATED = "Новая задача";
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
    @DisplayName("Должен сохранять задачу в менеджер при отправке запроса в HttpServer")
    public void test_AddTask_WhenSendTaskWithId_ManagerShouldStore() throws IOException, InterruptedException {
        //given
        URI url = URI.create("http://localhost:8080/tasks");
        Task task = new Task(TASK_NAME, TASK_DESCRIPTION, TaskStatus.NEW, Duration.ofMinutes(5), LocalDateTime.now());
        String taskJson = gson.toJson(task);
        HttpRequest request = HttpRequest.newBuilder()
                .uri(url)
                .POST(HttpRequest.BodyPublishers.ofString(taskJson))
                .build();

        //when
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        List<Task> tasksFromManager = manager.getAllTasks();

        // then
        assertEquals(HttpStatusCode.CREATED.getCode(), response.statusCode());
        assertNotNull(tasksFromManager, "Задачи не возвращаются");
        assertEquals(1, tasksFromManager.size(), "Некорректное количество задач");
        assertEquals(TASK_NAME, tasksFromManager.get(0).getName(), "Некорректное имя задачи");
    }

    @Test
    @DisplayName("Должен обновлять задачу в менеджере при отправке запроса в HttpServer")
    public void test_UpdateTask_WhenSendTaskWithId_ManagerShouldStoreUpdate() throws IOException, InterruptedException {
        //given
        URI url = URI.create("http://localhost:8080/tasks");
        Task task = manager.addTask(new Task(TASK_NAME, TASK_DESCRIPTION, TaskStatus.NEW, Duration.ofMinutes(5),
                LocalDateTime.now()));
        Task updatedTask = new Task(TASK_NAME_UPDATED, TASK_DESCRIPTION_UPDATED, TaskStatus.IN_PROGRESS,
                Duration.ofMinutes(5), LocalDateTime.now());
        updatedTask.setId(task.getId());
        String taskJson = gson.toJson(updatedTask);
        HttpRequest request = HttpRequest.newBuilder()
                .uri(url)
                .POST(HttpRequest.BodyPublishers.ofString(taskJson))
                .build();

        //when
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        List<Task> tasksFromManager = manager.getAllTasks();


        // then
        assertEquals(HttpStatusCode.OK.getCode(), response.statusCode());
        assertNotNull(tasksFromManager, "Задачи не возвращаются");
        assertEquals(1, tasksFromManager.size(), "Некорректное количество задач");
        assertEquals(TASK_NAME_UPDATED, tasksFromManager.get(0).getName(), "Некорректное имя задачи");
    }

    @Test
    @DisplayName("Должен возвращать задачу по id из менеджера при отправке запроса в HttpServer")
    public void test_GetTaskById_WhenSendRequest_ShouldReturnById() throws IOException, InterruptedException {
        //given
        Task task = manager.addTask(new Task(TASK_NAME, TASK_DESCRIPTION, TaskStatus.NEW, Duration.ofMinutes(5),
                LocalDateTime.now()));
        URI url = URI.create("http://localhost:8080/tasks/" + task.getId());
        HttpRequest request = HttpRequest.newBuilder()
                .uri(url)
                .GET()
                .build();

        //when
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        List<Task> tasksFromManager = manager.getAllTasks();


        // then
        assertEquals(HttpStatusCode.OK.getCode(), response.statusCode());
        assertNotNull(tasksFromManager, "Задачи не возвращаются");
        assertEquals(1, tasksFromManager.size(), "Некорректное количество задач");
        assertEquals(TASK_NAME, tasksFromManager.get(0).getName(), "Некорректное имя задачи");
    }

    @Test
    @DisplayName("Должен удалять задачу по id в менеджере при отправке запроса в HttpServer")
    public void test_DeleteTaskById_WhenSendCorrectId_ShouldReturnNull() throws IOException, InterruptedException {
        //given
        Task task = manager.addTask(new Task(TASK_NAME, TASK_DESCRIPTION, TaskStatus.NEW, Duration.ofMinutes(5),
                LocalDateTime.now()));
        URI url = URI.create("http://localhost:8080/tasks" + "/" + task.getId());
        HttpRequest request = HttpRequest.newBuilder()
                .uri(url)
                .DELETE()
                .build();

        //when
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        List<Task> tasksFromManager = manager.getAllTasks();


        // then
        assertEquals(HttpStatusCode.OK.getCode(), response.statusCode());
        assertNotNull(tasksFromManager, "Задачи не возвращаются");
        assertEquals(0, tasksFromManager.size(), "Некорректное количество задач");
    }

    @Test
    @DisplayName("Должен возвращать все задачи из менеджера при отправке запроса в HttpServer")
    public void test_GetAllTasks_WhenSendRequest_ShouldReturnAll() throws IOException, InterruptedException {
        //given
        manager.addTask(new Task(TASK_NAME, TASK_DESCRIPTION, TaskStatus.NEW, Duration.ofMinutes(5),
                LocalDateTime.now()));
        manager.addTask(new Task(TASK_NAME_UPDATED, TASK_DESCRIPTION_UPDATED, TaskStatus.NEW, Duration.ofMinutes(5),
                LocalDateTime.now().plusHours(1)));
        URI url = URI.create("http://localhost:8080/tasks");
        HttpRequest request = HttpRequest.newBuilder()
                .uri(url)
                .GET()
                .build();

        //when
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        List<Task> tasksFromManager = manager.getAllTasks();


        // then
        assertEquals(HttpStatusCode.OK.getCode(), response.statusCode());
        assertNotNull(tasksFromManager, "Задачи не возвращаются");
        assertEquals(2, tasksFromManager.size(), "Некорректное количество задач");
        assertEquals(TASK_NAME, tasksFromManager.get(0).getName(), "Некорректное имя задачи");
        assertEquals(TASK_NAME_UPDATED, tasksFromManager.get(1).getName(), "Некорректное имя задачи");
    }

    @Test
    @DisplayName("Не должен сохранять задачу в менеджер при отправке запроса по некорректному адресу")
    public void test_AddTask_WhenIncorrectURL_ShouldReturn404() throws IOException, InterruptedException {
        //given
        URI incorrectUrl = URI.create("http://localhost:8080/addTask");
        Task task = new Task(TASK_NAME, TASK_DESCRIPTION, TaskStatus.NEW, Duration.ofMinutes(5), LocalDateTime.now());
        String taskJson = gson.toJson(task);
        HttpRequest request = HttpRequest.newBuilder()
                .uri(incorrectUrl)
                .POST(HttpRequest.BodyPublishers.ofString(taskJson))
                .build();

        //when
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        // then
        assertEquals(HttpStatusCode.NOT_FOUND.getCode(), response.statusCode(),
                "Не должен добавлять задачу при некорректном URL");
    }

    @Test
    @DisplayName("Не должен обновлять задачу из менеджера при отправке запроса с несуществующим id")
    public void test_UpdateTask_WhenIdNotExist_ShouldReturn404() throws IOException, InterruptedException {
        //given
        URI url = URI.create("http://localhost:8080/tasks");
        Task task = manager.addTask(new Task(TASK_NAME, TASK_DESCRIPTION, TaskStatus.NEW, Duration.ofMinutes(5),
                LocalDateTime.now()));
        Task updatedTask = new Task(TASK_NAME_UPDATED, TASK_DESCRIPTION_UPDATED, TaskStatus.IN_PROGRESS,
                Duration.ofMinutes(5), LocalDateTime.now());
        updatedTask.setId(task.getId() + 1);
        String taskJson = gson.toJson(updatedTask);
        HttpRequest request = HttpRequest.newBuilder()
                .uri(url)
                .POST(HttpRequest.BodyPublishers.ofString(taskJson))
                .build();

        //when
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        // then
        assertEquals(HttpStatusCode.NOT_FOUND.getCode(), response.statusCode(),
                "Не должен обновлять задачу по несуществующему id");
    }

    @Test
    @DisplayName("Не должен возвращать задачу из менеджера при отправке запроса с несуществующим id ")
    public void test_GetTaskById_WhenIdNotExist_ShouldReturn404() throws IOException, InterruptedException {
        //given
        Task task = manager.addTask(new Task(TASK_NAME, TASK_DESCRIPTION, TaskStatus.NEW, Duration.ofMinutes(5),
                LocalDateTime.now()));
        URI url = URI.create("http://localhost:8080/tasks/" + (task.getId() + 1));
        HttpRequest request = HttpRequest.newBuilder()
                .uri(url)
                .GET()
                .build();

        //when
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        // then
        assertEquals(HttpStatusCode.NOT_FOUND.getCode(), response.statusCode(),
                "Не должен возвращать задачу по несуществующему id");
    }

    @Test
    @DisplayName("Не должен возвращать задачу в менеджере при отправке запроса с некорректным id ")
    public void test_GetTaskById_WhenIdNotAcceptable_ShouldReturn500() throws IOException, InterruptedException {
        //given
        Task task = manager.addTask(new Task(TASK_NAME, TASK_DESCRIPTION, TaskStatus.NEW, Duration.ofMinutes(5),
                LocalDateTime.now()));
        URI url = URI.create("http://localhost:8080/tasks/" + task.getId() + "test");
        HttpRequest request = HttpRequest.newBuilder()
                .uri(url)
                .GET()
                .build();

        //when
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        // then
        assertEquals(HttpStatusCode.INTERNAL_SERVER_ERROR.getCode(), response.statusCode(),
                "Не должен возвращать задачу по некорректному id");
    }

    @Test
    @DisplayName("Не должен удалять задачу в менеджере при отправке запроса с несуществующим id ")
    public void test_DeleteTaskById_WhenIdNotExist_ShouldReturn404() throws IOException, InterruptedException {
        //given
        Task task = manager.addTask(new Task(TASK_NAME, TASK_DESCRIPTION, TaskStatus.NEW, Duration.ofMinutes(5),
                LocalDateTime.now()));
        URI url = URI.create("http://localhost:8080/tasks/" + (task.getId() + 1));
        HttpRequest request = HttpRequest.newBuilder()
                .uri(url)
                .DELETE()
                .build();

        //when
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        // then
        assertEquals(HttpStatusCode.NOT_FOUND.getCode(), response.statusCode(),
                "Не должен удалять задачу по несуществующему id");
    }

    @Test
    @DisplayName("Не должен удалять задачу в менеджере при отправке запроса с некорректным id ")
    public void test_DeleteTaskById_WhenIdNotAcceptable_ShouldReturn500() throws IOException, InterruptedException {
        //given
        Task task = manager.addTask(new Task(TASK_NAME, TASK_DESCRIPTION, TaskStatus.NEW, Duration.ofMinutes(5),
                LocalDateTime.now()));
        URI url = URI.create("http://localhost:8080/tasks/" + task.getId() + "test");
        HttpRequest request = HttpRequest.newBuilder()
                .uri(url)
                .DELETE()
                .build();

        //when
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        // then
        assertEquals(HttpStatusCode.INTERNAL_SERVER_ERROR.getCode(), response.statusCode(),
                "Не должен удалять задачу по некорректному id");
    }
}

