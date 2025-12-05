package com.fasterxml.uuid.impl;

import java.security.SecureRandom;
import java.util.Objects;
import java.util.Random;
import java.util.UUID;
import java.util.function.Consumer;

import com.fasterxml.uuid.NoArgGenerator;
import com.fasterxml.uuid.UUIDClock;
import com.fasterxml.uuid.UUIDType;

/**
 * Implementation of UUID generator that uses time/location based generation
 * method field from the Unix Epoch timestamp source - the number of 
 * milliseconds seconds since midnight 1 Jan 1970 UTC, leap seconds excluded.
 * This is usually referred to as "Version 7".
 * <p>
 * As all JUG provided implementations, this generator is fully thread-safe.
 * Additionally it can also be made externally synchronized with other instances
 * (even ones running on other JVMs); to do this, use
 * {@link com.fasterxml.uuid.ext.FileBasedTimestampSynchronizer} (or
 * equivalent).
 *
 * @since 4.1
 */
public class TimeBasedEpochGenerator extends NoArgGenerator
{ 
    private static final int ENTROPY_BYTE_LENGTH = 10;

    /*
    /**********************************************************************
    /* Configuration
    /**********************************************************************
     */

    /**
     * Source for random numbers used to fill a byte array with entropy.
     *
     * @since 5.3 (replaced earlier {@code java.util.Random _random})
     */
    protected final Consumer<byte[]> _randomNextBytes;

    /**
     * Underlying {@link UUIDClock} used for accessing current time, to use for
     * generation.
     *
     * @since 4.3
     */
    protected final UUIDClock _clock;

    private long _lastTimestamp = -1;
    private final byte[] _lastEntropy  = new byte[ENTROPY_BYTE_LENGTH];

    /*
    /**********************************************************************
    /* Construction
    /**********************************************************************
     */

    /**
     * @param rnd Random number generator to use for generating UUIDs; if null,
     *   shared default generator is used. Note that it is strongly recommend to
     *   use a <b>good</b> (pseudo) random number generator; for example, JDK's
     *   {@link SecureRandom}.
     */
    public TimeBasedEpochGenerator(Random rnd) {
        this(rnd, UUIDClock.systemTimeClock());
    }

    /**
     * @param rnd Random number generator to use for generating UUIDs; if null,
     *   shared default generator is used. Note that it is strongly recommend to
     *   use a <b>good</b> (pseudo) random number generator; for example, JDK's
     *   {@link SecureRandom}.
     * @param clock clock Object used for accessing current time to use for generation
     */
    public TimeBasedEpochGenerator(Random rnd, UUIDClock clock)
    {
        this((rnd == null ? LazyRandom.sharedSecureRandom() : rnd)::nextBytes, clock);
    }

    /**
     * 
     * @param randomNextBytes Source for random numbers to use for generating UUIDs.
     *  Note that it is strongly recommend to use a <b>good</b> (pseudo) random number source;
     *  for example, JDK's {@code SecureRandom::nextBytes}.
     * @param clock clock Object used for accessing current time to use for generation
     *
     * @since 5.3
     */
    protected TimeBasedEpochGenerator(Consumer<byte[]> randomNextBytes, UUIDClock clock)
    {
        _randomNextBytes = Objects.requireNonNull(randomNextBytes);
        _clock = clock;
    }

    /*
    /**********************************************************************
    /* Access to config
    /**********************************************************************
     */

    @Override
    public UUIDType getType() { return UUIDType.TIME_BASED_EPOCH; }

    /*
    /**********************************************************************
    /* UUID generation
    /**********************************************************************
     */

    @Override
    public UUID generate()
    {
        return construct(_clock.currentTimeMillis());
    }

    /**
     * Method that will construct actual {@link UUID} instance for given
     * unix epoch timestamp: called by {@link #generate()} but may alternatively be
     * called directly to construct an instance with known timestamp.
     * NOTE: calling this method directly produces somewhat distinct UUIDs as
     * "entropy" value is still generated as necessary to avoid producing same
     * {@link UUID} even if same timestamp is being passed.
     *
     * @param rawTimestamp unix epoch millis
     *
     * @return unix epoch time based UUID
     *
     * @since 4.3
     */
    public UUID construct(long rawTimestamp)
    {
        final long mostSigBits, leastSigBits;
        synchronized (_lastEntropy) {
            if (rawTimestamp == _lastTimestamp) {
                carry:
                {
                    for (int i = ENTROPY_BYTE_LENGTH - 1; i > 0; i--) {
                        _lastEntropy[i] = (byte) (_lastEntropy[i] + 1);
                        if (_lastEntropy[i] != 0x00) {
                            break carry;
                        }
                    }
                    _lastEntropy[0] = (byte) (_lastEntropy[0] + 1);
                    if (_lastEntropy[0] >= 0x04) {
                        throw new IllegalStateException("overflow on same millisecond");
                    }
                }
            } else {
                _lastTimestamp = rawTimestamp;
                _randomNextBytes.accept(_lastEntropy);
                // In the most significant byte, only 2 bits will fit in the UUID, and one of those should be cleared
                // to guard against overflow.
                _lastEntropy[0] &= 0x01;
            }
            mostSigBits = rawTimestamp << 16 |
                    (long) UUIDType.TIME_BASED_EPOCH.raw() << 12 |
                    Byte.toUnsignedLong(_lastEntropy[0]) << 10 |
                    Byte.toUnsignedLong(_lastEntropy[1]) << 2 |
                    Byte.toUnsignedLong(_lastEntropy[2]) >>> 6;
            long right62Mask = (1L << 62) - 1;
            long variant = 0x02;
            leastSigBits = variant << 62 |
                    _toLong(_lastEntropy, 2) & right62Mask;
        }
        return new UUID(mostSigBits, leastSigBits);
    }
}
