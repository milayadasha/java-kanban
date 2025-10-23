package ru.yandex.javacourse.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import ru.yandex.javacourse.model.Epic;
import ru.yandex.javacourse.model.Subtask;
import ru.yandex.javacourse.model.Task;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class FileBackedTaskManagerTest {
    private static final String TASK_NAME = "Задача";
    private static final String TASK_DESCRIPTION = "Новая задача";

    FileBackedTaskManager fileBackedTaskManager;
    File file;

    @BeforeEach
    @DisplayName("Cоздаёт перед каждым тестом новый менеджер и файл")
    public void create() throws IOException {
        file = File.createTempFile("tmpFile", ".txt");
        fileBackedTaskManager = new FileBackedTaskManager(file);
    }

    @Test
    @DisplayName("Должен успешно создавать пустой файл")
    public void test_Save_WhenManagerSaved_FileShouldBeEmpty() {
        //given
        fileBackedTaskManager.save();
        List<String> fileContent = new ArrayList<>();
        String firstString = "";

        //when
        try (BufferedReader br = new BufferedReader(new FileReader(file))) {
            firstString = br.readLine();
            while (br.ready()) {
                String fileString = br.readLine();
                fileContent.add(fileString);
            }
        } catch (IOException exception) {
            System.out.println("Не удалось прочитать информацию из файла");
        }
        //then
        assertNotNull(file, "Файл не создан");
        assertTrue(firstString.contains("id"), "В файле отсутствует строчка с параметрами");
        assertEquals(0, fileContent.size(), "В файле не корректное количество строк");
    }

    @Test
    @DisplayName("Должен успешно добавлять задачу в файл")
    public void test_AddTask_WhenTaskAddedToFileBacked_FileShouldBeNotEmpty() {
        //given
        Task task = fileBackedTaskManager.addTask(new Task(TASK_NAME, TASK_DESCRIPTION));

        //when
        int taskId = -1;
        try (BufferedReader br = new BufferedReader(new FileReader(file))) {
            br.readLine();
            while (br.ready()) {
                String taskInfo = br.readLine();
                String[] splitTaskInfo = taskInfo.split(",");
                taskId = Integer.parseInt(splitTaskInfo[0]);
            }
        } catch (IOException exception) {
            System.out.println("Не удалось прочитать информацию из файла");
        }

        //then
        assertEquals(task.getId(), taskId, "Задачи не совпадают.");
    }

    @Test
    @DisplayName("Должен успешно добавлять несколько задач в файл")
    public void test_AddTask_WhenTwoSubtaskAddedToFileBacked_FileShouldStoreAll() {
        //given
        Task task = fileBackedTaskManager.addTask(new Task(TASK_NAME, TASK_DESCRIPTION));
        Task secondTask = fileBackedTaskManager.addTask(new Task(TASK_NAME, TASK_DESCRIPTION));

        //when
        List<String> fileContent = new ArrayList<>();
        try (BufferedReader br = new BufferedReader(new FileReader(file))) {
            br.readLine();
            while (br.ready()) {
                String taskInfo = br.readLine();
                fileContent.add(taskInfo);
            }
        } catch (IOException exception) {
            System.out.println("Не удалось прочитать информацию из файла");
        }

        //then
        assertNotNull(fileContent, "Файл должен быть не пустым");
        assertEquals(2, fileContent.size(), "В файл сохранилось не корректное количество задач");
    }

    @Test
    @DisplayName("Не должен ничего возвращать из пустого файла")
    public void test_LoadFromFile_WhenReadFromFile_ShouldBeEmpty() throws IOException {
        //given
        FileBackedTaskManager newFileBackedTaskManager = FileBackedTaskManager.loadFromFile(file);

        //when
        ArrayList<Task> tasks = newFileBackedTaskManager.getAllTasks();
        ArrayList<Subtask> subtasks = newFileBackedTaskManager.getAllSubtasks();
        ArrayList<Epic> epics = newFileBackedTaskManager.getAllEpics();

        //then
        assertEquals(0, tasks.size(), "Из должно быть получено 0 задач");
        assertEquals(0, subtasks.size(), "Из должно быть получено 0 подзадач");
        assertEquals(0, epics.size(), "Из должно быть получено 0 эпиков");
    }

    @Test
    @DisplayName("Должен возвращать все задачи, добавленные в файл")
    public void test_LoadFromFile_WhenReadFromFile_ShouldReturnAllTasks() throws IOException {
        //given
        Task task = fileBackedTaskManager.addTask(new Task(TASK_NAME, TASK_DESCRIPTION));
        Task secondTask = fileBackedTaskManager.addTask(new Task(TASK_NAME, TASK_DESCRIPTION));
        FileBackedTaskManager newFileBackedTaskManager = FileBackedTaskManager.loadFromFile(file);

        //when
        ArrayList<Task> tasks = newFileBackedTaskManager.getAllTasks();

        //then
        assertEquals(2, tasks.size(), "Из должно быть получено 0 задач");
    }
}