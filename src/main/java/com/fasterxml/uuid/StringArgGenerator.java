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
    public abstract UUID generate(String arg);

}
