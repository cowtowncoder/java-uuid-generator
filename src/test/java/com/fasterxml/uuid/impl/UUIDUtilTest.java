package com.fasterxml.uuid.impl;

import java.util.UUID;
import java.util.concurrent.ThreadLocalRandom;

import com.fasterxml.uuid.Generators;
import junit.framework.TestCase;
import org.junit.Test;
import org.junit.runners.JUnit4;

/**
 * Test class focusing on verifying functionality provided by
 * {@link UUIDUtil}.
 *<p>
 * NOTE: some of {@code UUIDUtil} testing is via main
 * {@link com.fasterxml.uuid.UUIDTest}.
 */
public class UUIDUtilTest extends TestCase
{
    public void testNilUUID() {
        UUID nil = UUIDUtil.nilUUID();
        // Should be all zeroes:
        assertEquals(0L, nil.getMostSignificantBits());
        assertEquals(0L, nil.getLeastSignificantBits());
    }

    public void testMaxUUID() {
        UUID max = UUIDUtil.maxUUID();
        // Should be all ones:
        assertEquals(~0, max.getMostSignificantBits());
        assertEquals(~0, max.getLeastSignificantBits());
    }

    public void testExtractTimestampUUIDTimeBased() {
        TimeBasedGenerator generator = Generators.timeBasedGenerator();
        for (int i = 0; i < 9000; i++) {
            long rawTimestamp = ThreadLocalRandom.current().nextLong() >>> 60;
            UUID uuid = generator.construct(rawTimestamp);
            assertEquals(rawTimestamp, UUIDUtil.extractTimestamp(uuid));
        }
    }

    public void testExtractTimestampUUIDTimeBasedReordered() {
        TimeBasedReorderedGenerator generator = Generators.timeBasedReorderedGenerator();
        for (int i = 0; i < 9000; i++) {
            long rawTimestamp = ThreadLocalRandom.current().nextLong() >>> 60;
            UUID uuid = generator.construct(rawTimestamp);
            assertEquals(rawTimestamp, UUIDUtil.extractTimestamp(uuid));
        }
    }

    public void testExtractTimestampUUIDEpochBased() {
        TimeBasedEpochGenerator generator = Generators.timeBasedEpochGenerator();
        for (int i = 0; i < 9000; i++) {
            long rawTimestamp = ThreadLocalRandom.current().nextLong() >>> 60;
            UUID uuid = generator.construct(rawTimestamp);
            assertEquals(rawTimestamp, UUIDUtil.extractTimestamp(uuid));
        }
    }

    public void testExtractTimestampUUIDOnOtherValues() {
        assertEquals(0L, UUIDUtil.extractTimestamp(null));
        assertEquals(0L, UUIDUtil.extractTimestamp(UUID.fromString("00000000-0000-0000-0000-000000000000")));
        assertEquals(0L, UUIDUtil.extractTimestamp(UUIDUtil.nilUUID()));
        assertEquals(0L, UUIDUtil.extractTimestamp(UUIDUtil.maxUUID()));
    }
}
