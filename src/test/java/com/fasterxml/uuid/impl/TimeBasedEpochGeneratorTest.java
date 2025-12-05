package com.fasterxml.uuid.impl;

import java.math.BigInteger;
import java.util.Arrays;
import java.util.UUID;
import java.util.function.Consumer;

import com.fasterxml.uuid.UUIDClock;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * @since 5.2
 */
public class TimeBasedEpochGeneratorTest
{
    @Test
    public void testFormat() {
        BigInteger minEntropy = BigInteger.ZERO;
        long minTimestamp = 0;
        TimeBasedEpochGenerator generatorEmpty = new TimeBasedEpochGenerator(staticEntropy(minEntropy), staticClock(minTimestamp));
        UUID uuidEmpty = generatorEmpty.generate();
        assertEquals(0x07, uuidEmpty.version());
        assertEquals(0x02, uuidEmpty.variant());
        assertEquals(minTimestamp, getTimestamp(uuidEmpty));
        assertEquals(minEntropy, getEntropy(uuidEmpty));

        Consumer<byte[]> entropyFull = bytes -> Arrays.fill(bytes, (byte) 0xFF);
        long maxTimestamp = rightBitmask(48);
        TimeBasedEpochGenerator generatorFull = new TimeBasedEpochGenerator(entropyFull, staticClock(maxTimestamp));
        UUID uuidFull = generatorFull.generate();
        assertEquals(0x07, uuidFull.version());
        assertEquals(0x02, uuidFull.variant());
        assertEquals(maxTimestamp, getTimestamp(uuidFull));
        assertEquals(BigInteger.ONE.shiftLeft(73).subtract(BigInteger.ONE), getEntropy(uuidFull));
    }

    @Test
    public void testIncrement() {
        TimeBasedEpochGenerator generator = new TimeBasedEpochGenerator(staticEntropy(BigInteger.ZERO), staticClock(0));
        assertEquals(BigInteger.valueOf(0), getEntropy(generator.generate()));
        assertEquals(BigInteger.valueOf(1), getEntropy(generator.generate()));
        assertEquals(BigInteger.valueOf(2), getEntropy(generator.generate()));
        assertEquals(BigInteger.valueOf(3), getEntropy(generator.generate()));
    }

    @Test
    public void testCarryOnce() {
        TimeBasedEpochGenerator generator = new TimeBasedEpochGenerator(staticEntropy(BigInteger.valueOf(0xFF)), staticClock(0));
        assertEquals(BigInteger.valueOf(0xFF), getEntropy(generator.generate()));
        assertEquals(BigInteger.valueOf(0x100), getEntropy(generator.generate()));
    }

    @Test
    public void testCarryAll() {
        BigInteger largeEntropy = BigInteger.ONE.shiftLeft(73).subtract(BigInteger.ONE);
        TimeBasedEpochGenerator generator = new TimeBasedEpochGenerator(staticEntropy(largeEntropy), staticClock(0));
        assertEquals(largeEntropy, getEntropy(generator.generate()));
        assertEquals(BigInteger.ONE.shiftLeft(73), getEntropy(generator.generate()));
    }

    private long getTimestamp(UUID uuid) {
        return uuid.getMostSignificantBits() >>> 16;
    }

    private BigInteger getEntropy(UUID uuid) {
        return BigInteger.valueOf(uuid.getMostSignificantBits() & rightBitmask(12)).shiftLeft(62).or(
                BigInteger.valueOf(uuid.getLeastSignificantBits() & rightBitmask(62)));
    }

    private Consumer<byte[]> staticEntropy(BigInteger entropy) {
        byte[] entropyBytes = entropy.toByteArray();
        return bytes -> {
            int offset = bytes.length - entropyBytes.length;
            Arrays.fill(bytes, 0, offset, (byte) 0x00);
            System.arraycopy(entropyBytes, 0, bytes, offset, entropyBytes.length);
        };
    }

    private UUIDClock staticClock(long timestamp) {
        return new UUIDClock() {
            @Override
            public long currentTimeMillis() {
                return timestamp;
            }
        };
    }

    private long rightBitmask(int bits) {
        return (1L << bits) - 1;
    }
}
