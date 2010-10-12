package com.fasterxml.uuid;

import java.io.*;
import java.security.NoSuchAlgorithmException;
import java.security.MessageDigest;
import java.util.*;

import com.fasterxml.uuid.impl.NameBasedGenerator;
import com.fasterxml.uuid.impl.RandomBasedGenerator;
import com.fasterxml.uuid.impl.TimeBasedGenerator;

/**
 * Root factory class for constructing UUID generators.
 * 
 * @author tatu
 * 
 * @since 3.0
 */
public class Generators
{
    // // Random-based generation
    
    /**
     * Factory method for constructing UUID generator that uses default (shared)
     * random number generator for constructing UUIDs according to standard
     * method number 4.
     */
    public static RandomBasedGenerator randomBasedGenerator() {
        return randomBasedGenerator(null);
    }

    /**
     * Factory method for constructing UUID generator that uses specified
     * random number generator for constructing UUIDs according to standard
     * method number 4.
     */
    public static RandomBasedGenerator randomBasedGenerator(Random rnd) {
        return new RandomBasedGenerator(rnd);
    }

    // // Name-based generation

    /**
     * Factory method for constructing UUID generator that uses specified
     * random number generator for constructing UUIDs according to standard
     * method number 5, but without using a namespace.
     * Digester to use will be SHA-1 as recommened by UUID spec.
     */
    public static NameBasedGenerator nameBasedGenerator() {
        return nameBasedGenerator(null);
    }

    /**
     * Factory method for constructing UUID generator that uses specified
     * random number generator for constructing UUIDs according to standard
     * method number 5, with specified namespace (or without one if null
     * is specified).
     * Digester to use will be SHA-1 as recommened by UUID spec.
     * 
     * @param namespace UUID that represents namespace to use; see
     *   {@link NameBasedGenerator} for 'standard' namespaces specified by
     *   UUID specs
     */
    public static NameBasedGenerator nameBasedGenerator(UUID namespace) {
        return nameBasedGenerator(namespace, null);
    }

    /**
     * Factory method for constructing UUID generator that uses specified
     * random number generator for constructing UUIDs according to standard
     * method number 3 or 5, with specified namespace (or without one if null
     * is specified), using specified digester.
     * If digester is passed as null, a SHA-1 digester will be constructed.
     * 
     * @param namespace UUID that represents namespace to use; see
     *   {@link NameBasedGenerator} for 'standard' namespaces specified by
     *   UUID specs
     * @param digester Digester to use; should be a MD5 or SHA-1 digester.
     */
    public static NameBasedGenerator nameBasedGenerator(UUID namespace, MessageDigest digester)
    {
        UUIDType type = null;
        if (digester == null) {
            try {
                digester = MessageDigest.getInstance("SHA-1");
                type = UUIDType.NAME_BASED_SHA1;
            } catch (NoSuchAlgorithmException nex) {
                throw new IllegalArgumentException("Couldn't instantiate SHA-1 MessageDigest instance: "+nex.toString());
            }
        }
        return new NameBasedGenerator(namespace, digester, type);
    }
    
    // // Time+location-based generation

    /**
     * Factory method for constructing UUID generator that generates UUID using
     * variant 1 (time+location based).
     * Since no Ethernet address is passed, a bogus broadcast address will be
     * constructed for purpose of UUID generation; usually it is better to
     * instead access one of host's NIC addresses using
     * {@link EthernetAddress#fromInterface} which will use one of available
     * MAC (Ethernet) addresses available.
    */
    public static TimeBasedGenerator timeBasedGenerator()
    {
        return timeBasedGenerator(null);
    }

    /**
     * Factory method for constructing UUID generator that generates UUID using
     * variant 1 (time+location based), using specified Ethernet address
     * as the location part of UUID.
     * No additional external synchronization is used.
     */
    public static TimeBasedGenerator timeBasedGenerator(EthernetAddress ethernetAddress)
    {
        return timeBasedGenerator(ethernetAddress, (UUIDTimer) null);
    }
    
    /**
     * Factory method for constructing UUID generator that generates UUID using
     * variant 1 (time+location based), using specified Ethernet address
     * as the location part of UUID, and specified synchronizer (which may add
     * additional restrictions to guarantee system-wide uniqueness).
     * 
     * @see com.fasterxml.uuid.ext.FileBasedTimestampSynchronizer
     */
    public static TimeBasedGenerator timeBasedGenerator(EthernetAddress ethernetAddress,
            TimestampSynchronizer sync)
    {
        UUIDTimer timer;
        try {
            timer = new UUIDTimer(new Random(System.currentTimeMillis()), sync);
        } catch (IOException e) {
            throw new IllegalArgumentException("Failed to create UUIDTimer with specified synchronizer: "+e.getMessage(), e);
        }
        return timeBasedGenerator(ethernetAddress, timer);
    }
    
    /**
     * Factory method for constructing UUID generator that generates UUID using
     * variant 1 (time+location based), using specified Ethernet address
     * as the location part of UUID, and specified {@link UUIDTimer} instance
     * (which includes embedded synchronizer that defines synchronization behavior).
     */
    public static TimeBasedGenerator timeBasedGenerator(EthernetAddress ethernetAddress,
            UUIDTimer timer)
    {
        if (timer == null) {
            try {
                timer = new UUIDTimer(new java.util.Random(System.currentTimeMillis()), null);
            } catch (IOException e) {
                throw new IllegalArgumentException("Failed to create UUIDTimer with specified synchronizer: "+e.getMessage(), e);
            }
        }
        return new TimeBasedGenerator(ethernetAddress, timer);
    }
}

