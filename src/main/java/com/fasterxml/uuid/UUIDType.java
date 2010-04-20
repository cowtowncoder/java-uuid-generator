package com.fasterxml.uuid;

/**
 * Enumeration of different flavors of UUIDs: 4 specified by specs; and
 * virtual fifth one ("null") to represent invalid one that consists of
 * all zero bites
 */
public enum UUIDType {
    TIME_BASED(1),
    DCE(2),
    NAME_BASED(3),
    RANDOM_BASED(4),
    NULL(0)
    ;

    private final int _raw;
	
    private UUIDType(int raw) {
       _raw = raw;
    }

    /**
     * Returns "raw" type constants, embedded within UUID bytes.
     */
    public int raw() { return _raw; }
}
