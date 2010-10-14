package com.fasterxml.uuid;

import java.util.UUID;

/**
 * Intermediate base class for UUID generators that take one String argument for individual
 * calls. This includes name-based generators, but not random and time-based generators.
 * 
 * @since 3.0
 */
public abstract class StringArgGenerator extends UUIDGenerator
{
    /**
     * Method for generating name-based UUIDs using specified name (serialized to
     * bytes using UTF-8 encoding)
     */
    public abstract UUID generate(String name);

    /**
     * Method for generating name-based UUIDs using specified byte-serialization
     * of name.
     * 
     * @since 3.1
     */
    public abstract UUID generate(byte[] nameBytes);
}
