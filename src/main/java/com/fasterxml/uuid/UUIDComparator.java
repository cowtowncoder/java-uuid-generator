package com.fasterxml.uuid;

import java.util.Comparator;
import java.util.UUID;

/**
 * Default {@link java.util.UUID} comparator is not very useful, since
 * it just does blind byte-by-byte comparison which does not work well
 * for time+location - based UUIDs. This comparator does implement
 * proper lexical ordering: starting with type (different types are collated
 * separately), followed by location and time (for time/location based),
 * and simple lexical (byte-by-byte) ordering for name/hash and random
 * variants.
 * 
 * @author tatu
 */
public class UUIDComparator implements Comparator<UUID>
{
    @Override
    public int compare(UUID o1, UUID o2)
    {
        // !!! TBI
        // TODO Auto-generated method stub
        return 0;
    }
}
