/* JUG Java UUID Generator
 *
 * Copyright (c) 2002- Tatu Saloranta, tatu.saloranta@iki.fi
 *
 * Licensed under the License specified in the file LICENSE which is
 * included with the source code.
 * You may not use this file except in compliance with the License.
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.fasterxml.uuid;

import java.io.*;
import java.security.NoSuchAlgorithmException;
import java.security.MessageDigest;
import java.util.*;

import com.fasterxml.uuid.impl.NameBasedGenerator;
import com.fasterxml.uuid.impl.RandomBasedGenerator;
import com.fasterxml.uuid.impl.TimeBasedEpochGenerator;
import com.fasterxml.uuid.impl.TimeBasedEpochRandomGenerator;
import com.fasterxml.uuid.impl.TimeBasedReorderedGenerator;
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
    /**
     * If no explicit timer (and synchronizer it implicitly uses) is specified,
     * we will create and use a single lazily-constructed timer, which uses in-JVM
     * synchronization but no external file-based syncing.
     */
    protected static UUIDTimer _sharedTimer;

    /**
     * The hardware address of the egress network interface.
     */
    protected static EthernetAddress _preferredIfAddr = null;
    
    // // Random-based generation
    
    /**
     * Factory method for constructing UUID generator that uses default (shared)
     * random number generator for constructing UUIDs according to standard
     * version 4.
     */
    public static RandomBasedGenerator randomBasedGenerator() {
        return randomBasedGenerator(null);
    }

    /**
     * Factory method for constructing UUID generator that uses specified
     * random number generator for constructing UUIDs according to standard
     * version 4.
     */
    public static RandomBasedGenerator randomBasedGenerator(Random rnd) {
        return new RandomBasedGenerator(rnd);
    }

    // // Name-based generation

    /**
     * Factory method for constructing UUID generator that uses specified
     * random number generator for constructing UUIDs according to standard
     * version 5, but without using a namespace.
     * Digester to use will be SHA-1 as recommended by UUID spec.
     */
    public static NameBasedGenerator nameBasedGenerator() {
        return nameBasedGenerator(null);
    }

    /**
     * Factory method for constructing UUID generator that uses specified
     * random number generator for constructing UUIDs according to standard
     * version 5, with specified namespace (or without one if null
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
     * version 3 or 5, with specified namespace (or without one if null
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
            ThreadLocal<MessageDigest> threadLocalDisgester = new ThreadLocal<MessageDigest>() {
                @Override
                protected MessageDigest initialValue() {
                    try {
                        return MessageDigest.getInstance("SHA-1");
                    } catch (NoSuchAlgorithmException nex) {
                        throw new IllegalArgumentException("Couldn't instantiate SHA-1 MessageDigest instance: "+ nex.toString());
                    }
                }
            };
            digester = threadLocalDisgester.get();
            type = UUIDType.NAME_BASED_SHA1;
        }
        return new NameBasedGenerator(namespace, digester, type);
    }
    
    // // Epoch Time+random generation

    /**
     * Factory method for constructing UUID generator that generates UUID using
     * version 7 (Unix Epoch time+random based).
     *<p>
     * NOTE: calls within same millisecond produce very similar values; this may be
     * unsafe in some environments.
     *<p>
     * No additional external synchronization is used.
     */
    public static TimeBasedEpochGenerator timeBasedEpochGenerator()
    {
        return timeBasedEpochGenerator(null);
    }

    /**
     * Factory method for constructing UUID generator that generates UUID using
     * version 7 (Unix Epoch time+random based), using specified {@link Random}
     * number generator.
     *<p>
     * NOTE: calls within same millisecond produce very similar values; this may be
     * unsafe in some environments.
     *<p>
     * No additional external synchronization is used.
     */
    public static TimeBasedEpochGenerator timeBasedEpochGenerator(Random random)
    {
        return new TimeBasedEpochGenerator(random);
    }

    /**
     * Factory method for constructing UUID generator that generates UUID using
     * version 7 (Unix Epoch time+random based), using specified {@link Random}
     * number generator.
     * Timestamp to use is accessed using specified {@link UUIDClock}.
     *<p>
     * NOTE: calls within same millisecond produce very similar values; this may be
     * unsafe in some environments.
     *<p>
     * No additional external synchronization is used.
     *
     * @since 4.3
     */
    public static TimeBasedEpochGenerator timeBasedEpochGenerator(Random random,
            UUIDClock clock)
    {
        return new TimeBasedEpochGenerator(random, clock);
    }

    // // Epoch Time+random generation

    /**
     * Factory method for constructing UUID generator that generates UUID using
     * version 7 (Unix Epoch time+random based).
     *<p>
     * Calls within same millisecond use additional per-call randomness to try to create
     * more distinct values, compared to {@link #timeBasedEpochGenerator(Random)}
     *<p>
     * No additional external synchronization is used.
     *
     * @since 5.1
     */
    public static TimeBasedEpochRandomGenerator timeBasedEpochRandomGenerator()
    {
        return timeBasedEpochRandomGenerator(null);
    }

    /**
     * Factory method for constructing UUID generator that generates UUID using
     * version 7 (Unix Epoch time+random based), using specified {@link Random}
     * number generator.
     *<p>
     * Calls within same millisecond use additional per-call randomness to try to create
     * more distinct values, compared to {@link #timeBasedEpochGenerator(Random)}
     *<p>
     * No additional external synchronization is used.
     *
     * @since 5.0
     */
    public static TimeBasedEpochRandomGenerator timeBasedEpochRandomGenerator(Random random)
    {
        return new TimeBasedEpochRandomGenerator(random);
    }

    /**
     * Factory method for constructing UUID generator that generates UUID using
     * version 7 (Unix Epoch time+random based), using specified {@link Random}
     * number generator.
     * Timestamp to use is accessed using specified {@link UUIDClock}
     *<p>
     * Calls within same millisecond use additional per-call randomness to try to create
     * more distinct values, compared to {@link #timeBasedEpochGenerator(Random)}
     *<p>
     * No additional external synchronization is used.
     *
     * @since 5.0
     */
    public static TimeBasedEpochRandomGenerator timeBasedEpochRandomGenerator(Random random,
            UUIDClock clock)
    {
        return new TimeBasedEpochRandomGenerator(random, clock);
    }

    // // Time+location-based generation

    /**
     * Factory method for constructing UUID generator that generates UUID using version 1
     * (time+location based). This method will use the ethernet address of the interface
     * that routes to the default gateway, or if that cannot be found, then the address of
     * an indeterminately selected non-loopback interface. For most simple and common
     * networking configurations this will be the most appropriate address to use. The
     * default interface is determined by the calling {@link
     * EthernetAddress#fromPreferredInterface()} method.  Note that this will only
     * identify the preferred interface once: if you have a complex network setup where
     * your outbound routes/interfaces may change dynamically.  If you want your UUIDs to
     * accurately reflect a deterministic selection of network interface, you should
     * instead use a generator implementation that uses an explicitly specified address,
     * such as {@link #timeBasedGenerator(EthernetAddress)}.
     *
     * @since 4.2
     */
    public static TimeBasedGenerator defaultTimeBasedGenerator()
    {
        return timeBasedGenerator(preferredInterfaceAddress());
    }

    /**
     * Factory method for constructing UUID generator that generates UUID using
     * version 1 (time+location based).
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
     * version 1 (time+location based), using specified Ethernet address
     * as the location part of UUID.
     * No additional external synchronization is used.
     */
    public static TimeBasedGenerator timeBasedGenerator(EthernetAddress ethernetAddress)
    {
        return timeBasedGenerator(ethernetAddress, (UUIDTimer) null);
    }
    
    /**
     * Factory method for constructing UUID generator that generates UUID using
     * version 1 (time+location based), using specified Ethernet address
     * as the location part of UUID, and specified synchronizer (which may add
     * additional restrictions to guarantee system-wide uniqueness).
     * 
     * @param ethernetAddress (optional) MAC address to use; if null, a transient
     *   random address is generated.
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
     * version 1 (time+location based), using specified Ethernet address
     * as the location part of UUID, and specified {@link UUIDTimer} instance
     * (which includes embedded synchronizer that defines synchronization behavior).
     */
    public static TimeBasedGenerator timeBasedGenerator(EthernetAddress ethernetAddress,
            UUIDTimer timer)
    {
        if (timer == null) {
            timer = sharedTimer();
        }
        return new TimeBasedGenerator(ethernetAddress, timer);
    }

    // // DB Locality Time+location-based generation

    /**
     * Factory method for constructing UUID generator that generates UUID using
     * version 6 (time+location based, reordered for DB locality). Since no Ethernet
     * address is passed, a bogus broadcast address will be constructed for purpose
     * of UUID generation; usually it is better to instead access one of host's NIC
     * addresses using {@link EthernetAddress#fromInterface} which will use one of
     * available MAC (Ethernet) addresses available.
     */
    public static TimeBasedReorderedGenerator timeBasedReorderedGenerator()
    {
        return timeBasedReorderedGenerator(null);
    }

    /**
     * Factory method for constructing UUID generator that generates UUID using
     * version 6 (time+location based, reordered for DB locality), using specified
     * Ethernet address as the location part of UUID. No additional external
     * synchronization is used.
     */
    public static TimeBasedReorderedGenerator timeBasedReorderedGenerator(EthernetAddress ethernetAddress)
    {
        return timeBasedReorderedGenerator(ethernetAddress, (UUIDTimer) null);
    }

    /**
     * Factory method for constructing UUID generator that generates UUID using
     * version 6 (time+location based, reordered for DB locality), using specified
     * Ethernet address as the location part of UUID, and specified
     * {@link UUIDTimer} instance (which includes embedded synchronizer that defines
     * synchronization behavior).
     */
    public static TimeBasedReorderedGenerator timeBasedReorderedGenerator(EthernetAddress ethernetAddress,
            UUIDTimer timer)
    {
        if (timer == null) {
            timer = sharedTimer();
        }
        return new TimeBasedReorderedGenerator(ethernetAddress, timer);
    }

    /*
    /**********************************************************************
    /* Internal methods
    /**********************************************************************
     */

    private static synchronized UUIDTimer sharedTimer()
    {
        if (_sharedTimer == null) {
            try {
                _sharedTimer = new UUIDTimer(new java.util.Random(System.currentTimeMillis()), null);
            } catch (IOException e) {
                throw new IllegalArgumentException("Failed to create UUIDTimer with specified synchronizer: "+e.getMessage(), e);
            }
        }
        return _sharedTimer;
    }

    private static synchronized EthernetAddress preferredInterfaceAddress()
    {
    	  if (_preferredIfAddr == null) {
    	      _preferredIfAddr = EthernetAddress.fromPreferredInterface();
    	  }
    	  return _preferredIfAddr;
  	}
}
