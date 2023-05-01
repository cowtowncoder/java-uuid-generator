package com.fasterxml.uuid.impl;

import java.security.SecureRandom;
import java.util.Random;
import java.util.UUID;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import com.fasterxml.uuid.NoArgGenerator;
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
     * Random number generator that this generator uses.
     */
    protected final Random _random;
    private long _lastTimestamp = -1;
    private final byte[] _lastEntropy  = new byte[ENTROPY_BYTE_LENGTH];
    private final Lock lock = new ReentrantLock();

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
    
    public TimeBasedEpochGenerator(Random rnd)
    {
        if (rnd == null) {
            rnd = LazyRandom.sharedSecureRandom(); 
        }
        _random = rnd;
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
        lock.lock();
        try { 
            long rawTimestamp = System.currentTimeMillis();
            if (rawTimestamp == _lastTimestamp) {
                boolean c = true;
                for (int i = ENTROPY_BYTE_LENGTH - 1; i >= 0; i--) {
                    if (c) {
                        byte temp = _lastEntropy[i];
                        temp = (byte) (temp + 0x01);
                        c = _lastEntropy[i] == (byte) 0xff && c;
                        _lastEntropy[i] = temp;
                    }
                }
                if (c) {
                    throw new IllegalStateException("overflow on same millisecond");
                }
            } else {
                _lastTimestamp = rawTimestamp;
                _random.nextBytes(_lastEntropy);
            }
            return UUIDUtil.constructUUID(UUIDType.TIME_BASED_EPOCH, (rawTimestamp << 16) | _toShort(_lastEntropy, 0), _toLong(_lastEntropy, 2));
        } finally {
            lock.unlock();
        }
    }

    /*
    /**********************************************************************
    /* Internal methods
    /**********************************************************************
     */

    protected final static long _toLong(byte[] buffer, int offset)
    {
        long l1 = _toInt(buffer, offset);
        long l2 = _toInt(buffer, offset+4);
        long l = (l1 << 32) + ((l2 << 32) >>> 32);
        return l;
    }

    private final static long _toInt(byte[] buffer, int offset)
    {
        return (buffer[offset] << 24)
            + ((buffer[++offset] & 0xFF) << 16)
            + ((buffer[++offset] & 0xFF) << 8)
            + (buffer[++offset] & 0xFF);
    }

    private final static long _toShort(byte[] buffer, int offset)
    {
        return ((buffer[offset] & 0xFF) << 8)
            + (buffer[++offset] & 0xFF);
    }
}
