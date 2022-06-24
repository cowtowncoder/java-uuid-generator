package com.fasterxml.uuid.impl;


import java.nio.ByteBuffer;
import java.security.SecureRandom;
import java.util.Random;
import java.util.UUID;

import com.fasterxml.uuid.NoArgGenerator; 
import com.fasterxml.uuid.UUIDType;
import com.fasterxml.uuid.impl.RandomBasedGenerator.LazyRandom;

/**
 * Implementation of UUID generator that uses time/location based generation
 * method field from the Unix Epoch timestamp source - the number of 
 * milliseconds seconds since midnight 1 Jan 1970 UTC, leap seconds excluded
 * <p>
 * As all JUG provided implementations, this generator is fully thread-safe.
 * Additionally it can also be made externally synchronized with other instances
 * (even ones running on other JVMs); to do this, use
 * {@link com.fasterxml.uuid.ext.FileBasedTimestampSynchronizer} (or
 * equivalent).
 *
 * @since 3.1
 */
public class TimeBasedEpochGenerator extends NoArgGenerator
{
    
    /*
    /**********************************************************************
    /* Configuration
    /**********************************************************************
     */


    /**
     * Random number generator that this generator uses.
     */
    protected final Random _random;
    
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
        ByteBuffer buff = ByteBuffer.allocate(2 * 8);
        final long rawTimestamp = System.currentTimeMillis();
        final byte[] buffer = new byte[10];
        _random.nextBytes(buffer);
        buff.position(6);
        buff.put(buffer);
        buff.position(0);
        buff.putLong(rawTimestamp << 16);
        buff.flip();
        return UUIDUtil.constructUUID(UUIDType.TIME_BASED_EPOCH, buff.array());
    }
}
