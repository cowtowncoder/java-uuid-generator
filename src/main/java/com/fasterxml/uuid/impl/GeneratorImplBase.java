package com.fasterxml.uuid.impl;

import java.util.UUID;

import com.fasterxml.uuid.UUIDType;
import com.fasterxml.uuid.UUIDUtil;

/**
 * Shared base class for various UUID generator implementations.
 */
public class GeneratorImplBase
{
    /**
     * Helper method for constructing UUID instances with appropriate type
     */
    protected static UUID constructUUID(UUIDType type, byte[] uuidBytes)
    {
        // first, ensure type is ok
        int b = uuidBytes[UUIDUtil.BYTE_OFFSET_TYPE] & 0xF; // clear out high nibble
        b |= type.raw() << 4;
        uuidBytes[UUIDUtil.BYTE_OFFSET_TYPE] = (byte) b;
        // second, ensure variant is properly set too
        b = uuidBytes[UUIDUtil.BYTE_OFFSET_VARIATION] & 0x3F; // remove 2 MSB
        b |= 0x80; // set as '10'
        uuidBytes[UUIDUtil.BYTE_OFFSET_VARIATION] = (byte) b;
        return UUIDUtil.uuid(uuidBytes);
    }

    protected static UUID constructUUID(UUIDType type, long l1, long l2)
    {
        // first, ensure type is ok
        l1 &= ~0xF000L; // remove high nibble of 6th byte
        l1 |= (long) (type.raw() << 12);
        // second, ensure variant is properly set too (8th byte; most-sig byte of second long)
        l2 = ((l2 << 2) >>> 2); // remove 2 MSB
        l2 |= (2L << 62); // set 2 MSB to '10'
        return new UUID(l1, l2);
    }
}
