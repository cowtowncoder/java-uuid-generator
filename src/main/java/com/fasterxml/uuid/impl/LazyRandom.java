package com.fasterxml.uuid.impl;

import java.security.SecureRandom;

/**
 * Trivial helper class that uses class loading as synchronization
 * mechanism for lazy instantiation of the shared secure random
 * instance.
 *<p>
 * Since 5.0 has been lazily created to avoid issues with native-generation
 * tools like Graal.
 */
public final class LazyRandom
{
    private static final Object lock = new Object();
    private static volatile SecureRandom shared;

    public static SecureRandom sharedSecureRandom() {
        synchronized (lock) {
            SecureRandom result = shared;
            if (result == null) {
                shared = result = new SecureRandom();
            }

            return result;
        }
    }
}