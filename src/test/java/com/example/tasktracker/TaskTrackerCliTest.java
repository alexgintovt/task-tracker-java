package com.example.tasktracker;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.nio.file.Path;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

class TaskTrackerCliTest {

    @TempDir
    Path tempDirectory;

    private Path tasksFile;
    private ByteArrayOutputStream output;
    private ByteArrayOutputStream errors;

    @BeforeEach
    void setUp() {
        tasksFile = tempDirectory.resolve("tasks.txt");
        output = new ByteArrayOutputStream();
        errors = new ByteArrayOutputStream();
    }

    @Test
    void listPrintsNumberStatusAndDescription() {
        TaskTrackerCli.run(new String[]{"add", "Read", "a", "book"}, tasksFile, out(), err());
        TaskTrackerCli.run(new String[]{"add", "Write", "tests"}, tasksFile, out(), err());
        output.reset();

        TaskTrackerCli.run(new String[]{"list"}, tasksFile, out(), err());

        String lineSeparator = System.lineSeparator();
        String expected = "1|not_done|Read a book" + lineSeparator
            + "2|not_done|Write tests" + lineSeparator;
        String actual = output.toString();
        printStringDiagnostics("Expected", expected);
        printStringDiagnostics("Actual", actual);

        assertEquals(expected, actual);
        assertEquals("", errors.toString());
    }

    @Test
    void missingTaskPrintsErrorToStderr() {
        TaskTrackerCli.run(new String[]{"complete", "99"}, tasksFile, out(), err());

        assertEquals("The task with number 99 does not exist\r\n", errors.toString());
    }

    @Test
    void alreadyCompletedTaskPrintsErrorToStderr() {
        TaskTrackerCli.run(new String[]{"add", "Read", "a", "book"}, tasksFile, out(), err());
        TaskTrackerCli.run(new String[]{"complete", "1"}, tasksFile, out(), err());
        output.reset();
        errors.reset();

        TaskTrackerCli.run(new String[]{"complete", "1"}, tasksFile, out(), err());

        assertEquals("This task is already done.\r\n", errors.toString());
    }

    private PrintStream out() {
        return new PrintStream(output);
    }

    private PrintStream err() {
        return new PrintStream(errors);
    }

    private void printStringDiagnostics(String label, String value) {
        String escaped = value.replace("\\", "\\\\")
                .replace("\r", "\\r")
                .replace("\n", "\\n");
        String characterCodes = value.chars()
                .mapToObj(character -> String.format("U+%04X", character))
                .toList()
                .toString();
        System.out.println(label + " string: " + escaped);
        System.out.println(label + " character codes: " + characterCodes);
    }
}