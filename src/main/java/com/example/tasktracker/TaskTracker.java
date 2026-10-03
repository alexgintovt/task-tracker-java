package com.example.tasktracker;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

public class TaskTracker {
    private final Path tasksFile;

    public TaskTracker(Path tasksFile) {
        this.tasksFile = tasksFile;
        ensureFileExists();
    }

    public void addTask(String description) {
        if (description == null || description.isBlank()) {
            throw new IllegalArgumentException("Task description cannot be empty.");
        }

        List<Task> tasks = loadTasks();
        tasks.add(new Task("not_done", description.trim()));
        saveTasks(tasks);
    }

    public List<Task> listTasks() {
        return loadTasks();
    }

    public void markComplete(int taskNumber) {
        List<Task> tasks = loadTasks();

        if (taskNumber < 1 || taskNumber > tasks.size()) {
            throw new IllegalArgumentException(
                    "The task with number " + taskNumber + " does not exist");
        }

        Task task = tasks.get(taskNumber - 1);
        if ("done".equals(task.getStatus())) {
            throw new IllegalStateException("This task is already done.");
        }

        task.setStatus("done");
        saveTasks(tasks);
    }

    private void ensureFileExists() {
        try {
            Path parentDirectory = tasksFile.getParent();
            if (parentDirectory != null) {
                Files.createDirectories(parentDirectory);
            }
            if (Files.notExists(tasksFile)) {
                Files.createFile(tasksFile);
            }
        } catch (IOException e) {
            throw new IllegalStateException("Could not initialize tasks file at " + tasksFile, e);
        }
    }

    private List<Task> loadTasks() {
        ensureFileExists();
        List<Task> tasks = new ArrayList<>();

        try {
            List<String> lines = Files.readAllLines(tasksFile);
            for (String line : lines) {
                if (line == null || line.isBlank()) {
                    continue;
                }
                String[] parts = line.split("\\|", 2);
                if (parts.length != 2) {
                    continue;
                }
                String status = parts[0].trim();
                String description = parts[1].trim();
                if ("done".equals(status) || "not_done".equals(status)) {
                    tasks.add(new Task(status, description));
                }
            }
        } catch (IOException e) {
            throw new IllegalStateException("Could not read tasks from " + tasksFile, e);
        }

        return tasks;
    }

    private void saveTasks(List<Task> tasks) {
        try {
            List<String> lines = new ArrayList<>();
            for (Task task : tasks) {
                lines.add(task.toFileLine());
            }
            Files.write(tasksFile, lines);
        } catch (IOException e) {
            throw new IllegalStateException("Could not save tasks to " + tasksFile, e);
        }
    }
}
