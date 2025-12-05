package com.fasterxml.uuid;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.lang.reflect.Array;
import java.util.*;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.StringContains.containsString;
import static org.junit.jupiter.api.Assertions.*;

public class JugNoArgsTest {
    private String useCase;

    private PrintStream oldStrOut;
    private PrintStream oldStrErr;

    private final ByteArrayOutputStream outContent = new ByteArrayOutputStream();
    private final ByteArrayOutputStream errContent = new ByteArrayOutputStream();
    private Jug jug_underTest;

    @BeforeEach
    public void setup() {
        jug_underTest = new Jug();
        oldStrOut = System.out;
        oldStrErr = System.err;
        PrintStream stubbedStream = new PrintStream(outContent);
        System.setOut(stubbedStream);
        PrintStream stubbedErrStream = new PrintStream(errContent);
        System.setErr(stubbedErrStream);
    }

    @AfterEach
    public void cleanup() {
        System.setOut(oldStrOut);
        System.setErr(oldStrErr);
    }

    @ParameterizedTest
    @ValueSource(strings = {"t", "o", "r", "e", "m"})
    public void run_givenNoOptions_shouldProduceUUID(String useCase) {
        this.useCase = useCase;
        // given

        // when
        jug_underTest.run(new String[]{useCase});

        // then - if it is a UUID then we should be able to parse it back out
        String actualUuid = outContent.toString();
        assertEquals('\n', actualUuid.charAt(actualUuid.length() - 1));

        assertEquals(UUID.class,
                UUID.fromString(actualUuid.substring(0, actualUuid.length() - 1)).getClass());
    }

    @ParameterizedTest
    @ValueSource(strings = {"t", "o", "r", "e", "m"})
    public void run_givenCount1_shouldProduceUUID(String useCase) {
        this.useCase = useCase;
        // given

        // when
        List<String> arguments = new ArrayList<>(Arrays.asList("-c", "1"));
        arguments.add(useCase);
        jug_underTest.run(arguments.toArray((String[]) Array.newInstance(String.class, 0)));

        // then - if it is a UUID then we should be able to parse it back out
        String actualUuid = outContent.toString();
        assertEquals('\n', actualUuid.charAt(actualUuid.length() - 1));

        assertEquals(UUID.class,
                UUID.fromString(actualUuid.substring(0, actualUuid.length() - 1)).getClass());
    }

    @ParameterizedTest
    @ValueSource(strings = {"t", "o", "r", "e", "m"})
    public void run_givenCount2_shouldProduce2UUIDs(String useCase) {
        this.useCase = useCase;
        // given

        // when
        List<String> arguments = new ArrayList<>(Arrays.asList("-c", "2"));
        arguments.add(useCase);
        jug_underTest.run(arguments.toArray((String[]) Array.newInstance(String.class, 0)));

        // then - if it is a UUID then we should be able to parse it back out
        String[] actualUuids = outContent.toString().split("\n");
        assertEquals(2, actualUuids.length);

        for(String actualUuid: actualUuids) {
            assertEquals(UUID.class,
                    UUID.fromString(actualUuid).getClass());
        }
    }

    @ParameterizedTest
    @ValueSource(strings = {"t", "o", "r", "e", "m"})
    public void run_givenEthernet_shouldProduceUUID(String useCase) {
        this.useCase = useCase;
        // given

        // when
        List<String> arguments = new ArrayList<>(Arrays.asList("-e", ":::::"));
        arguments.add(useCase);
        jug_underTest.run(arguments.toArray((String[]) Array.newInstance(String.class, 0)));

        // then - if it is a UUID then we should be able to parse it back out
        String actualUuid = outContent.toString();
        assertEquals('\n', actualUuid.charAt(actualUuid.length() - 1));

        assertEquals(UUID.class,
                UUID.fromString(actualUuid.substring(0, actualUuid.length() - 1)).getClass());
    }

    @ParameterizedTest
    @ValueSource(strings = {"t", "o", "r", "e", "m"})
    public void run_givenName_shouldProduceUUID(String useCase) {
        this.useCase = useCase;
        // given

        // when
        List<String> arguments = new ArrayList<>(Arrays.asList("-n", "hello"));
        arguments.add(useCase);
        jug_underTest.run(arguments.toArray((String[]) Array.newInstance(String.class, 0)));

        // then - if it is a UUID then we should be able to parse it back out
        String actualUuid = outContent.toString();
        assertEquals('\n', actualUuid.charAt(actualUuid.length() - 1));

        assertEquals(UUID.class,
                UUID.fromString(actualUuid.substring(0, actualUuid.length() - 1)).getClass());
    }

    @ParameterizedTest
    @ValueSource(strings = {"t", "o", "r", "e", "m"})
    public void run_givenDnsNameSpace_shouldProduceUUID(String useCase) {
        this.useCase = useCase;
        // given

        // when
        List<String> arguments = new ArrayList<>(Arrays.asList("-s", "dns"));
        arguments.add(useCase);
        jug_underTest.run(arguments.toArray((String[]) Array.newInstance(String.class, 0)));

        // then - if it is a UUID then we should be able to parse it back out
        String actualUuid = outContent.toString();
        assertEquals('\n', actualUuid.charAt(actualUuid.length() - 1));

        assertEquals(UUID.class,
                UUID.fromString(actualUuid.substring(0, actualUuid.length() - 1)).getClass());
    }

    @ParameterizedTest
    @ValueSource(strings = {"t", "o", "r", "e", "m"})
    public void run_givenUrlNameSpace_shouldProduceUUID(String useCase) {
        this.useCase = useCase;
        // given

        // when
        List<String> arguments = new ArrayList<>(Arrays.asList("-s", "url"));
        arguments.add(useCase);
        jug_underTest.run(arguments.toArray((String[]) Array.newInstance(String.class, 0)));

        // then - if it is a UUID then we should be able to parse it back out
        String actualUuid = outContent.toString();
        assertEquals('\n', actualUuid.charAt(actualUuid.length() - 1));

        assertEquals(UUID.class,
                UUID.fromString(actualUuid.substring(0, actualUuid.length() - 1)).getClass());
    }

    @ParameterizedTest
    @ValueSource(strings = {"t", "o", "r", "e", "m"})
    public void run_givenPerformance_shouldProducePerformanceInfo(String useCase) {
        this.useCase = useCase;
        // given

        // when
        List<String> arguments = Arrays.asList("-p", useCase);
        jug_underTest.run(arguments.toArray((String[]) Array.newInstance(String.class, 0)));

        // then
        String actualOutput = outContent.toString();

        assertThat(actualOutput, containsString("Performance: took"));
    }

    @ParameterizedTest
    @ValueSource(strings = {"t", "o", "r", "e", "m"})
    public void run_givenHelp_shouldProduceHelpInfo(String useCase) {
        this.useCase = useCase;
        // given

        // when
        List<String> arguments = Arrays.asList("-h", useCase);
        jug_underTest.run(arguments.toArray((String[]) Array.newInstance(String.class, 0)));

        // then
        String actualOutput = errContent.toString();

        assertThat(actualOutput, containsString("Usage: java"));
    }

    @ParameterizedTest
    @ValueSource(strings = {"t", "o", "r", "e", "m"})
    public void run_givenVerbose_shouldProduceExtraInfo(String useCase) {
        this.useCase = useCase;
        // given

        // when
        List<String> arguments = Arrays.asList("-v", useCase);
        jug_underTest.run(arguments.toArray((String[]) Array.newInstance(String.class, 0)));

        // then
        String actualOutput = outContent.toString();

        assertThat(actualOutput, containsString("Done."));
    }

}