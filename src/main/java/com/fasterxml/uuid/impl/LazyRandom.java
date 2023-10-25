package com.fasterxml.uuid.impl;

import java.security.SecureRandom;

/**
 * Trivial helper class that uses class loading as synchronization
 * mechanism for lazy instantiation of the shared secure random
 * instance.
 */
public final class LazyRandom
{
    private static final Object lock = new Object();
    private static volatile SecureRandom shared;

    public static SecureRandom sharedSecureRandom() {
        // Double check lazy initialization idiom (Effective Java 3rd edition item 11.6)
        // Use so that native code generation tools do not detect a SecureRandom instance in a static final field.
        SecureRandom result = shared;

        if (result != null) {
            return result;
        }

        synchronized (lock) {
            result = shared;

            if (result == null) {
                result = shared = new SecureRandom();
            }

            return result;
        }
    }
}