package com.example.tasktracker;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

class TaskTrackerTest {

    @TempDir
    Path tempDirectory;

    private Path tasksFile;

    @BeforeEach
    void setUp() {
        tasksFile = tempDirectory.resolve("tasks.txt");
    }

    @Test
    void addTaskPersistsInFile() throws Exception {
        TaskTracker tracker = new TaskTracker(tasksFile);

        tracker.addTask("Read a book");
        tracker.addTask("Write tests");

        List<String> lines = Files.readAllLines(tasksFile);
        assertEquals(2, lines.size());
        assertEquals("not_done|Read a book", lines.get(0));
        assertEquals("not_done|Write tests", lines.get(1));
    }

    @Test
    void listTasksPreservesOrderStatusAndDescription() throws Exception {
        TaskTracker tracker = new TaskTracker(tasksFile);

        tracker.addTask("Read a book");
        tracker.addTask("Write tests");

        List<Task> tasks = tracker.listTasks();
        assertEquals(2, tasks.size());
        assertEquals("Read a book", tasks.get(0).getDescription());
        assertEquals("not_done", tasks.get(0).getStatus());
        assertEquals("Write tests", tasks.get(1).getDescription());
        assertEquals("not_done", tasks.get(1).getStatus());
    }

    @Test
    void completeTaskMarksOnlySelectedTask() throws Exception {
        TaskTracker tracker = new TaskTracker(tasksFile);

        tracker.addTask("Read a book");
        tracker.addTask("Write tests");

        tracker.markComplete(2);

        List<Task> tasks = tracker.listTasks();
        assertEquals("not_done", tasks.get(0).getStatus());
        assertEquals("done", tasks.get(1).getStatus());
        assertEquals(List.of("not_done|Read a book", "done|Write tests"), Files.readAllLines(tasksFile));
    }

    @Test
    void completeMissingTaskThrowsHelpfulError() throws Exception {
        TaskTracker tracker = new TaskTracker(tasksFile);

        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> tracker.markComplete(99)
        );

        assertTrue(exception.getMessage().contains("The task with number 99 does not exist"));
    }

    @Test
    void completeAlreadyDoneTaskThrowsHelpfulError() throws Exception {
        TaskTracker tracker = new TaskTracker(tasksFile);

        tracker.addTask("Read a book");
        tracker.markComplete(1);

        IllegalStateException exception = assertThrows(
                IllegalStateException.class,
                () -> tracker.markComplete(1)
        );

        assertEquals("This task is already done.", exception.getMessage());
    }

    @Test
    void createsMissingTasksFileAutomatically() {
        assertFalse(Files.exists(tasksFile));

        new TaskTracker(tasksFile);

        assertTrue(Files.exists(tasksFile));
    }

    @Test
    void persistsTasksBetweenTrackerInstances() throws Exception {
        TaskTracker firstTracker = new TaskTracker(tasksFile);
        firstTracker.addTask("Read a book");
        firstTracker.markComplete(1);

        TaskTracker secondTracker = new TaskTracker(tasksFile);

        List<Task> tasks = secondTracker.listTasks();
        assertEquals(1, tasks.size());
        assertEquals("done", tasks.get(0).getStatus());
        assertEquals("Read a book", tasks.get(0).getDescription());
    }
}
