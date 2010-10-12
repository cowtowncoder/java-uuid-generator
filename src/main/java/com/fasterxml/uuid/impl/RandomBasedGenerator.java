package com.fasterxml.uuid.impl;

import java.security.SecureRandom;
import java.util.Random;
import java.util.UUID;

import com.fasterxml.uuid.NoArgGenerator;
import com.fasterxml.uuid.UUIDType;

/**
 * Implementation of UUID generator that uses generation method 4.
 *<p>
 * Note on random number generation when using {@link SecureRandom} for random number
 * generation: the first time {@link SecureRandom} object is used, there is noticeable delay between
 * calling the method and getting the reply. This is because SecureRandom
 * has to initialize itself to reasonably random state. Thus, if you
 * want to lessen delay, it may be be a good idea to either get the
 * first random UUID asynchronously from a separate thread, or to
 * use the other generateRandomBasedUUID passing a previously initialized
 * SecureRandom instance.
 *
 * @since 3.0
 */
public class RandomBasedGenerator extends NoArgGenerator
{
    /**
     * Default shared random number generator, used if no random number generator
     * is explicitly specified for instance
     */
    protected static Random _sharedRandom = null;

    /**
     * Random number generator that this generator uses.
     */
    protected final Random _random;
    
    /**
     * @param rnd Random number generator to use for generating UUIDs; if null,
     *   shared default generator is used. Note that it is strongly recommened to
     *   use a <b>good</b> (pseudo) random number generator; for example, JDK's
     *   {@link SecureRandom}.
     */
    public RandomBasedGenerator(Random rnd)
    {
        if (rnd == null) {
            /*
             * Could be synchronized, but since side effects are trivial
             * (ie. possibility of generating more than one SecureRandom,
             * of which all but one are dumped) let's not add synchronization
             * overhead.
             */
            if (_sharedRandom == null) {
                _sharedRandom = rnd = new SecureRandom();
            }
        }
        _random = rnd;
    }

    /*
    /**********************************************************************
    /* Access to config
    /**********************************************************************
     */

    @Override
    public UUIDType getType() { return UUIDType.RANDOM_BASED; }

    /*
    /**********************************************************************
    /* UUID generation
    /**********************************************************************
     */
    
    @Override
    public UUID generate() {
        long r1 = _random.nextLong();
        long r2 = _random.nextLong();
        return UUIDUtil.constructUUID(UUIDType.RANDOM_BASED, r1, r2);
    }
}
