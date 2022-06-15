package com.fasterxml.uuid.impl;

import static com.fasterxml.uuid.impl.RandomBasedGenerator._toLong;
import java.security.SecureRandom;
import java.util.Random;
import java.util.UUID;

import com.fasterxml.uuid.NoArgGenerator;
import com.fasterxml.uuid.UUIDTimer;
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
    
    public static int BYTE_OFFSET_TIME_HIGH = 0;
    public static int BYTE_OFFSET_TIME_MID = 4;
    public static int BYTE_OFFSET_TIME_LOW = 7;
    
    /*
    /**********************************************************************
    /* Configuration
    /**********************************************************************
     */

    /**
     * Object used for synchronizing access to timestamps, to guarantee
     * that timestamps produced by this generator are unique and monotonically increasings.
     * Some implementations offer even stronger guarantees, for example that
     * same guarantee holds between instances running on different JVMs (or
     * with native code).
     */
    protected final UUIDTimer _timer;


    /**
     * Random number generator that this generator uses.
     */
    protected final Random _random;

    /**
     * Looks like {@link SecureRandom} implementation is more efficient
     * using single call access (compared to basic {@link java.util.Random}),
     * so let's use that knowledge to our benefit.
     */
    protected final boolean _secureRandom;
    
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
    
    public TimeBasedEpochGenerator(UUIDTimer timer, Random rnd)
    {
        if (rnd == null) {
            rnd = LazyRandom.sharedSecureRandom();
            _secureRandom = true;
        } else {
            _secureRandom = (rnd instanceof SecureRandom);
        }
        _random = rnd;
        _timer = timer;
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
    
    /* As timer is not synchronized (nor _uuidBytes), need to sync; but most
     * importantly, synchronize on timer which may also be shared between
     * multiple instances
     */
    @Override
    public UUID generate()
    {
        final long rawTimestamp = _timer.getTimestampV7();
        // Time field components are kind of shuffled, need to slice:
        int clockHi = (int) (rawTimestamp >>> 32);
        int clockLo = (int) rawTimestamp;
        // and dice
        int midhi = (clockHi << 16) | (clockHi >>> 16); 
        final byte[] b = new byte[2];
        _random.nextBytes(b);
        midhi = midhi | (((b[0] & 0xFF) << 8) + (b[1] & 0xFF)); 
        // need to squeeze in type (4 MSBs in byte 6, clock hi)
        midhi &= ~0xF000; // remove high nibble of 6th byte
        midhi |= 0x7000; // type 7
        long midhiL = (long) midhi;
        midhiL = ((midhiL << 32) >>> 32); // to get rid of sign extension
        // and reconstruct
        long l1 = (((long) clockLo) << 32) | midhiL;
        // last detail: must force 2 MSB to be '10'
        long _uuidL2;
        if (_secureRandom) {
            final byte[] buffer = new byte[16];
            _random.nextBytes(buffer);
            _uuidL2 = _toLong(buffer, 0);
        } else {
            _uuidL2 = _random.nextLong(); 
        }
        return new UUID(l1, _uuidL2);
    }
}
