package com.fasterxml.uuid.impl;

import java.util.UUID;

import com.fasterxml.uuid.*;

/**
 * Implementation of UUID generator that uses time/location based generation
 * method (variant 1).
 *<p>
 * As all JUG provided implementations, this generator is fully thread-safe.
 * Additionally it can also be made externally synchronized with other
 * instances (even ones running on other JVMs); to do this,
 * use {@link com.fasterxml.uuid.ext.FileBasedTimestampSynchronizer}
 * (or equivalent).
 *
 * @since 3.0
 */
public class TimeBasedGenerator extends NoArgGenerator
{
    /*
    /**********************************************************************
    /* Configuration
    /**********************************************************************
     */

    protected final EthernetAddress _ethernetAddress;

    /**
     * Object used for synchronizing access to timestamps, to guarantee
     * that timestamps produced by this generator are unique and monotonically increasings.
     * Some implementations offer even stronger guarantees, for example that
     * same guarantee holds between instances running on different JVMs (or
     * with native code).
     */
    protected final UUIDTimer _timer;

    /**
     * Temporary buffer used for constructing bytes for UUID
     */
    protected final byte[] _uuidBytes = new byte[16];
    
    /*
    /**********************************************************************
    /* Construction
    /**********************************************************************
     */

    /**
     * @param addr Hardware address (802.1) to use for generating
     *   spatially unique part of UUID. If system has more than one NIC,
     */
    
    public TimeBasedGenerator(EthernetAddress ethAddr, UUIDTimer timer)
    {
        if (ethAddr == null) {
            ethAddr = EthernetAddress.constructMulticastAddress();
        }
        _ethernetAddress = ethAddr;
        _timer = timer;
    }

    
    /*
    /**********************************************************************
    /* Access to config
    /**********************************************************************
     */

    @Override
    public UUIDType getType() { return UUIDType.TIME_BASED; }

    public EthernetAddress getEthernetAddress() { return _ethernetAddress; }
    
    /*
    /**********************************************************************
    /* UUID generation
    /**********************************************************************
     */
    
    @Override
    public UUID generate()
    {
        long timestamp;
        /* As timer is not synchronized (nor _uuidBytes), need to sync; but most
         * importantly, synchronize on timer which may also be shared between
         * multiple instances
         */
        synchronized (_timer) {
            _ethernetAddress.toByteArray(_uuidBytes, 10);
            timestamp = _timer.getTimestamp(_uuidBytes);
        }
        // Time fields aren't nicely split across the UUID, so can't just
        // linearly dump the stamp:
        int clockHi = (int) (timestamp >>> 32);
        int clockLo = (int) timestamp;

        _uuidBytes[UUIDUtil.BYTE_OFFSET_CLOCK_HI] = (byte) (clockHi >>> 24);
        _uuidBytes[UUIDUtil.BYTE_OFFSET_CLOCK_HI+1] = (byte) (clockHi >>> 16);
        _uuidBytes[UUIDUtil.BYTE_OFFSET_CLOCK_MID] = (byte) (clockHi >>> 8);
        _uuidBytes[UUIDUtil.BYTE_OFFSET_CLOCK_MID+1] = (byte) clockHi;

        _uuidBytes[UUIDUtil.BYTE_OFFSET_CLOCK_LO] = (byte) (clockLo >>> 24);
        _uuidBytes[UUIDUtil.BYTE_OFFSET_CLOCK_LO+1] = (byte) (clockLo >>> 16);
        _uuidBytes[UUIDUtil.BYTE_OFFSET_CLOCK_LO+2] = (byte) (clockLo >>> 8);
        _uuidBytes[UUIDUtil.BYTE_OFFSET_CLOCK_LO+3] = (byte) clockLo;

        return UUIDUtil.constructUUID(UUIDType.TIME_BASED, _uuidBytes);
    }
}
