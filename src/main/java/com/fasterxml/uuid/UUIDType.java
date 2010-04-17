package com.fasterxml.uuid;

/**
 * Enumeration of different flavors of UUIDs: 4 specified by specs; and
 * virtual fifth one ("null") to represent invalid one that consists of
 * all zero bites
 */
public enum UUIDType {
	TIME_BASED,
	DCE,
	NAME_BASED,
	RANDOM_BASED,
	NULL
	;
}
