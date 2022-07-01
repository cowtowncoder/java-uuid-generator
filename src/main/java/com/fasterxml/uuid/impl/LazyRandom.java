package com.fasterxml.uuid.impl;

import java.security.SecureRandom;

/**
 * Trivial helper class that uses class loading as synchronization
 * mechanism for lazy instantiation of the shared secure random
 * instance.
 */
public final class LazyRandom
{
    private final static SecureRandom shared = new SecureRandom();

    public static SecureRandom sharedSecureRandom() {
        return shared;
    }
}