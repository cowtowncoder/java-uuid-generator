package com.fasterxml.uuid;

/**
 * Enumeration of different flavors of UUIDs: 5 specified by specs
 * (<a href="http://tools.ietf.org/html/rfc4122">RFC-4122</a>)
 * and one
 * virtual entry ("UNKNOWN") to represent invalid one that consists of
 * all zero bites
 */
public enum UUIDType {
    TIME_BASED(1),
    DCE(2),
    NAME_BASED_MD5(3),
    RANDOM_BASED(4),
    NAME_BASED_SHA1(5),
    UNKNOWN(0)
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
