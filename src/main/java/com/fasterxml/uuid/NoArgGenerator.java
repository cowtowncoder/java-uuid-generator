package com.fasterxml.uuid;

import java.util.UUID;

/**
 * Intermediate base class for UUID generators that do not take arguments for individual
 * calls. This includes random and time-based variants, but not name-based ones.
 * 
 * @since 3.0
 */
public abstract class NoArgGenerator extends UUIDGenerator
{
    public abstract UUID generate();
}
