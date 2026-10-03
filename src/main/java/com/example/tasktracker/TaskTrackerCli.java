package com.example.tasktracker;

import java.nio.file.Path;
import java.io.PrintStream;
import java.util.Arrays;
import java.util.List;

public class TaskTrackerCli {
    public static void main(String[] args) {
        run(args, Path.of("tasks.txt"), System.out, System.err);
    }

    static void run(String[] args, Path tasksFile, PrintStream out, PrintStream err) {
        TaskTracker tracker = new TaskTracker(tasksFile);

        if (args.length == 0) {
            printUsage(out);
            return;
        }

        String command = args[0];

        try {
            switch (command) {
                case "add":
                    if (args.length < 2) {
                        throw new IllegalArgumentException("Usage: add <description>");
                    }
                    String description = String.join(" ", Arrays.copyOfRange(args, 1, args.length));
                    tracker.addTask(description);
                    out.println("Task added: " + description);
                    break;

                case "list":
                    List<Task> tasks = tracker.listTasks();
                    if (tasks.isEmpty()) {
                        out.println("No tasks yet.");
                        break;
                    }
                    for (int i = 0; i < tasks.size(); i++) {
                        Task task = tasks.get(i);
                        out.println((i + 1) + "|" + task.getStatus() + "|" + task.getDescription());
                    }
                    break;

                case "complete":
                    if (args.length < 2) {
                        throw new IllegalArgumentException("Usage: complete <task-number>");
                    }
                    int taskNumber = Integer.parseInt(args[1]);
                    tracker.markComplete(taskNumber);
                    out.println("Task " + taskNumber + " marked as done.");
                    break;

                default:
                    printUsage(out);
                    break;
            }
        } catch (NumberFormatException e) {
            err.println("Task number must be an integer.");
        } catch (IllegalArgumentException e) {
            err.println(e.getMessage());
        } catch (IllegalStateException e) {
            err.println(e.getMessage());
        }
    }

    private static void printUsage(PrintStream out) {
        out.println("Usage:");
        out.println("  add <description>");
        out.println("  list");
        out.println("  complete <task-number>");
    }
}
