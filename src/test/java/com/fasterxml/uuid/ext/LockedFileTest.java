package com.fasterxml.uuid.ext;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.util.UUID;
import java.util.concurrent.ThreadLocalRandom;

import static com.fasterxml.uuid.ext.LockedFile.READ_ERROR;
import static org.junit.Assert.*;

public class LockedFileTest
{
    @Rule
    public TemporaryFolder temporaryFolder = new TemporaryFolder();

    @Test
    public void constructor_givenNull_shouldThrowNullPointerException() throws IOException {
        try {
            new LockedFile(null);
            fail("This should have thrown a null pointer exception");
        } catch (NullPointerException nullPointerException) {
            assertNull(nullPointerException.getMessage());
        }
    }

    @Test
    public void constructor_givenEmptyFile_shouldLeaveFileAsIs() throws IOException {
        // given
        File emptyFile = temporaryFolder.newFile();

        // when
        new LockedFile(emptyFile);

        // then
        assertTrue(emptyFile.exists());
        assertTrue(emptyFile.canRead());
        assertTrue(emptyFile.canWrite());
    }

    @Test
    public void constructor_givenNonExistentFile_shouldCreateANewFile() throws IOException {
        // given
        File blankFile = temporaryFolder.newFile();
        File nonExistentFile = new File(blankFile + ".nonexistent");

        if (Files.exists(nonExistentFile.toPath())) {
            fail("temp file should not exist");
        }

        // when
        new LockedFile(nonExistentFile);

        // then - the nonexistent file now exists?
        assertTrue(Files.exists(nonExistentFile.toPath()));
        assertTrue(nonExistentFile.canRead());
        assertTrue(nonExistentFile.canWrite());
    }

    @Test
    public void constructor_canOnlyTakeAFile_shouldThrowFileNotFoundException() throws IOException {
        // given
        File blankFolder = temporaryFolder.newFolder();

        // when
        try {
            new LockedFile(blankFolder);
            fail("This should not succeed");
        } catch (FileNotFoundException fileNotFoundException) {
            // then
            assertEquals(
                    String.format("%s (Is a directory)", blankFolder.getPath()),
                    fileNotFoundException.getMessage()
            );
        }
    }

    @Test
    public void readStamp_givenEmptyFile_shouldReturnREADERROR() throws IOException {
        // given
        File emptyFile = temporaryFolder.newFile();

        // when
        LockedFile lockedFile = new LockedFile(emptyFile);
        long stamp = lockedFile.readStamp();

        // then
        assertEquals(READ_ERROR, stamp);
    }

    @Test
    public void readStamp_givenGibberishFile_shouldReturnREADERROR() throws IOException {
        // given
        File gibberishFile = temporaryFolder.newFile();
        try(FileWriter fileWriter = new FileWriter(gibberishFile)) {
            fileWriter.write(UUID.randomUUID().toString().substring(0, 22));
            fileWriter.flush();
        }

        assertEquals(22, Files.size(gibberishFile.toPath()));

        // when
        LockedFile lockedFile = new LockedFile(gibberishFile);
        long stamp = lockedFile.readStamp();

        // then
        assertEquals(READ_ERROR, stamp);
    }

    @Test
    public void readStamp_givenTimestampedFile_shouldReturnValueInside() throws IOException {
        // given
        File timeStampedFile = temporaryFolder.newFile();
        try(FileWriter fileWriter = new FileWriter(timeStampedFile)) {
            // we are faking the timestamp format
            fileWriter.write("[0x0000000000000001]");
            fileWriter.flush();
        }

        // when
        LockedFile lockedFile = new LockedFile(timeStampedFile);
        long stamp = lockedFile.readStamp();

        // then
        long expectedTimestamp = 1;
        assertEquals(expectedTimestamp, stamp);
    }

    // test for overflows
    @Test
    public void readStamp_givenOverflowedDigitFile_shouldReturnREADERROR() throws IOException {
        // given
        File timeStampedFile = temporaryFolder.newFile();
        try(FileWriter fileWriter = new FileWriter(timeStampedFile)) {
            // we are faking an overflowed timestamp
            fileWriter.write("[0x10000000000000000]");
            fileWriter.flush();
        }

        // when
        LockedFile lockedFile = new LockedFile(timeStampedFile);
        long stamp = lockedFile.readStamp();

        // then
        assertEquals(READ_ERROR, stamp);
    }

    @Test
    public void readStamp_givenMaxLongFile_shouldReturnLargeTimestamp() throws IOException {
        // given
        File timeStampedFile = temporaryFolder.newFile();
        try(FileWriter fileWriter = new FileWriter(timeStampedFile)) {
            // we are faking an overflowed timestamp
            fileWriter.write("[0x7fffffffffffffff]");
            fileWriter.flush();
        }

        // when
        LockedFile lockedFile = new LockedFile(timeStampedFile);
        long stamp = lockedFile.readStamp();

        // then
        assertEquals(Long.MAX_VALUE, stamp);
    }

    @Test
    public void writeStamp_givenNegativeTimestamps_shouldThrowIOException() throws IOException {
        // given
        File timeStampedFile = temporaryFolder.newFile();

        // when
        LockedFile lockedFile = new LockedFile(timeStampedFile);
        try {
            lockedFile.writeStamp(Long.MIN_VALUE);
            fail("This should throw an exception");
        } catch (IOException ioException) {
            // then
            assertTrue(ioException.getMessage().contains("trying to overwrite existing value"));
            assertTrue(ioException.getMessage().contains("with an earlier timestamp"));
        }
    }

    @Test
    public void writeStamp_givenTimestampedFile_withLowerValue_shouldOverrideValue() throws IOException {
        // given
        String inputValue = "[0x0000000000000000]";
        long numericInputValue = 0L;
        long newTimestamp = ThreadLocalRandom.current().nextLong(Long.MAX_VALUE);

        File timeStampedFile = temporaryFolder.newFile();
        try(FileWriter fileWriter = new FileWriter(timeStampedFile)) {
            fileWriter.write(inputValue);
            fileWriter.flush();
        }

        // when
        LockedFile lockedFile = new LockedFile(timeStampedFile);

        lockedFile.writeStamp(newTimestamp);
        long stamp = lockedFile.readStamp();

        // then
        assertNotEquals(numericInputValue, stamp);
        assertEquals(newTimestamp, stamp);
    }

    @Test
    public void writeStamp_givenNewerTimestampedFile_writeNegativeTimestamp_shouldThrowException() throws IOException {
        // given
        String inputValue = "[0x7fffffffffffffff]";
        long newTimestamp = Long.MIN_VALUE;

        File timeStampedFile = temporaryFolder.newFile();
        try(FileWriter fileWriter = new FileWriter(timeStampedFile)) {
            fileWriter.write(inputValue);
            fileWriter.flush();
        }

        // when
        LockedFile lockedFile = new LockedFile(timeStampedFile);

        try {
            lockedFile.writeStamp(newTimestamp);
            fail("This should throw an exception");
        } catch (IOException ioException) {
            // then
            assertTrue(ioException.getMessage().contains("trying to overwrite existing value"));
            assertTrue(ioException.getMessage().contains("with an earlier timestamp"));
        }
    }

    @Test
    public void writeStamp_givenTimestampedFile_writeSameTimestamp_shouldLeaveFileAlone() throws IOException {
        // given
        String inputValue = "[0x7fffffffffffffff]";
        long numericInputValue = Long.MAX_VALUE;
        long newTimestamp = Long.MAX_VALUE;

        File timeStampedFile = temporaryFolder.newFile();
        try(FileWriter fileWriter = new FileWriter(timeStampedFile)) {
            fileWriter.write(inputValue);
            fileWriter.flush();
        }

        // when
        LockedFile lockedFile = new LockedFile(timeStampedFile);

        lockedFile.writeStamp(newTimestamp);
        long stamp = lockedFile.readStamp();

        // then
        assertEquals(numericInputValue, stamp);
        assertEquals(newTimestamp, stamp);
    }
}