package com.fasterxml.uuid.impl;

import java.util.Random;
import java.util.UUID;

import com.fasterxml.uuid.Generators;
import junit.framework.TestCase;

/**
 * Test class focusing on verifying functionality provided by
 * {@link UUIDUtil}.
 *<p>
 * NOTE: some of {@code UUIDUtil} testing is via main
 * {@link com.fasterxml.uuid.UUIDTest}.
 */
public class UUIDUtilTest extends TestCase
{
    final static int TEST_REPS = 1_000_000;

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
        final Random rnd = new Random(1);
        for (int i = 0; i < TEST_REPS; i++) {
            long rawTimestamp = rnd.nextLong() >>> 4;
            UUID uuid = generator.construct(rawTimestamp);
            assertEquals(rawTimestamp, UUIDUtil.extractTimestamp(uuid));
        }
    }

    public void testExtractTimestampUUIDTimeBasedReordered() {
        TimeBasedReorderedGenerator generator = Generators.timeBasedReorderedGenerator();
        final Random rnd = new Random(2);
        for (int i = 0; i < TEST_REPS; i++) {
            long rawTimestamp = rnd.nextLong() >>> 4;
            UUID uuid = generator.construct(rawTimestamp);
            assertEquals(rawTimestamp, UUIDUtil.extractTimestamp(uuid));
        }
    }

    public void testExtractTimestampUUIDEpochBased() {
        TimeBasedEpochGenerator generator = Generators.timeBasedEpochGenerator();
        final Random rnd = new Random(3);
        for (int i = 0; i < TEST_REPS; i++) {
            long rawTimestamp = rnd.nextLong() >>> 16;
            UUID uuid = generator.construct(rawTimestamp);
            assertEquals(rawTimestamp, UUIDUtil.extractTimestamp(uuid));
        }
    }

    public void testExtractTimestampUUIDEpochRandomBased() {
        TimeBasedEpochRandomGenerator generator = Generators.timeBasedEpochRandomGenerator();
        final Random rnd = new Random(3);
        for (int i = 0; i < TEST_REPS; i++) {
            long rawTimestamp = rnd.nextLong() >>> 16;
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
