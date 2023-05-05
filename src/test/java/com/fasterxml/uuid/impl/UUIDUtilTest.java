package com.fasterxml.uuid.impl;

import java.util.UUID;

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
}
