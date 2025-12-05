package com.fasterxml.uuid;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.StringContains.containsString;
import static org.junit.Assert.assertEquals;

@RunWith(Parameterized.class)
public class JugNamedTest {
    @Parameterized.Parameter
    public UseCase useCase;

    private PrintStream oldStrOut;
    private PrintStream oldStrErr;

    private final ByteArrayOutputStream outContent = new ByteArrayOutputStream();
    private final ByteArrayOutputStream errContent = new ByteArrayOutputStream();
    private Jug jug_underTest;

    @Before
    public void setup() {
        jug_underTest = new Jug();oldStrOut = System.out;
        oldStrErr = System.err;
        PrintStream stubbedStream = new PrintStream(outContent);
        System.setOut(stubbedStream);
        PrintStream stubbedErrStream = new PrintStream(errContent);
        System.setErr(stubbedErrStream);
    }

    @After
    public void cleanup() {
        System.setOut(oldStrOut);
        System.setErr(oldStrErr);
    }

    @Test
    public void run_shouldProduceUUID() {
        // given

        // when
        List<String> arguments = useCase.getArgs();
        jug_underTest.run(arguments.toArray((String[]) Array.newInstance(String.class, 0)));

        // then - if it is a UUID then we should be able to parse it back out
        String actualUuid = outContent.toString();
        assertEquals('\n', actualUuid.charAt(actualUuid.length() - 1));

        assertEquals(UUID.class,
                UUID.fromString(actualUuid.substring(0, actualUuid.length() - 1)).getClass());
    }

    @Test
    public void run_givenCount3_shouldProduceUUID() {
        // given

        // when
        List<String> arguments = useCase.getArgs();
        arguments.add(0, "-c");
        arguments.add(1, "3");
        jug_underTest.run(arguments.toArray((String[]) Array.newInstance(String.class, 0)));

        // then - if it is a UUID then we should be able to parse it back out
        String[] actualUuids = outContent.toString().split("\n");
        for(String actualUuid: actualUuids) {
            assertEquals(UUID.class,
                    UUID.fromString(actualUuid).getClass());
        }
    }

    @Test
    public void run_givenPerformance_shouldProducePerformanceInfo() {
        // given

        // when
        List<String> arguments = useCase.getArgs();
        arguments.add(0, "-p");
        jug_underTest.run(arguments.toArray((String[]) Array.newInstance(String.class, 0)));

        // then
        String actualOutput = outContent.toString();

        assertThat(actualOutput, containsString("Performance: took"));
    }
    @Test
    public void run_givenHelp_shouldProduceHelpInfo() {
        // given

        // when
        List<String> arguments = useCase.getArgs();
        arguments.add(0, "-h");
        jug_underTest.run(arguments.toArray((String[]) Array.newInstance(String.class, 0)));

        // then
        String actualOutput = errContent.toString();

        assertThat(actualOutput, containsString("Usage: java"));
    }

    @Test
    public void run_givenVerbose_shouldProduceExtraInfo() {
        // given

        // when
        List<String> arguments = useCase.getArgs();
        arguments.add(0, "-v");
        jug_underTest.run(arguments.toArray((String[]) Array.newInstance(String.class, 0)));

        // then
        String actualOutput = outContent.toString();

        assertThat(actualOutput, containsString("Done."));
    }

    @Test
    public void run_givenVerboseAndPerformance_shouldProduceExtraInfo() {
        // given

        // when
        List<String> arguments = useCase.getArgs();
        arguments.add(0, "-v");
        arguments.add(1, "-p");
        jug_underTest.run(arguments.toArray((String[]) Array.newInstance(String.class, 0)));

        // then
        String actualOutput = outContent.toString();

        assertThat(actualOutput, containsString("Done."));
        assertThat(actualOutput, containsString("Performance: took"));
    }

    @Parameterized.Parameters(name = "{index} -> {0}")
    public static List<UseCase> useCases() {
        return Arrays.asList(
                new UseCase("n", "-n", "world", "-s", "url"),
                new UseCase("n", "-n", "world", "-s", "dns")
            );
    }

    private static class UseCase {
        private final String type;
        private String[] options = new String[]{};

        public UseCase(String type, String...options) {
            this.type = type;
            if (options != null) {
                this.options = options;
            }
        }

        public List<String> getArgs() {
            List<String> arguments = new ArrayList<>(Arrays.asList(options));
            arguments.add(type);
            return arguments;
        }

        @Override
        public String toString() {
            if (options.length == 0) {
                return String.format("type: %s, options: no options", type);
            } else {
                return String.format("type: %s, options: %s", type, String.join(", ", options));
            }
        }
    }
}