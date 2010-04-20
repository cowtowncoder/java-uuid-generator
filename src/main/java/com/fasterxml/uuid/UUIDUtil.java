package com.fasterxml.uuid;

import java.util.UUID;

public class UUIDUtil
{
    private final static UUID NULL_UUID = new UUID(0L, 0L);

    private final static long MASK_LOW_INT = 0xFFFFFFFF;

    protected final static int BYTE_OFFSET_CLOCK_LO = 0;
    protected final static int BYTE_OFFSET_CLOCK_MID = 4;
    protected final static int BYTE_OFFSET_CLOCK_HI = 6;

    // note: clock-hi and type occupy same byte (different bits)
    public final static int BYTE_OFFSET_TYPE = 6;

    // similarly, clock sequence and variant are multiplexed
    public final static int BYTE_OFFSET_CLOCK_SEQUENCE = 8;
    public final static int BYTE_OFFSET_VARIATION = 8;
	
    /*
    /****************************************************************
    /* 'Standard' namespaces defined (suggested) by UUID specs:
    /****************************************************************
     */

    /**
     * Namespace for name-based URLs
     */
    public final static String NAMESPACE_DNS = "6ba7b810-9dad-11d1-80b4-00c04fd430c8";

    /**
	 * Namespace for name-based URLs
	 */
    public final static String NAMESPACE_URL = "6ba7b811-9dad-11d1-80b4-00c04fd430c8";
	/**
	 * Namespace for name-based URLs
	 */
    public final static String NAMESPACE_OID = "6ba7b812-9dad-11d1-80b4-00c04fd430c8";
	/**
	 * Namespace for name-based URLs
	 */
    public final static String NAMESPACE_X500 = "6ba7b814-9dad-11d1-80b4-00c04fd430c8";

	private UUIDUtil() { }

	/*
	/***********************************************************************
	/* Factory methods
	/***********************************************************************
	 */
	
	/**
     * Factory method for creating UUIDs from the canonical string
     * representation.
     *
     * @param id String that contains the canonical representation of
     *   the UUID to build; 36-char string (see UUID specs for details).
     *   Hex-chars may be in upper-case too; UUID class will always output
     *   them in lowercase.
     */
	public static UUID uuid(String id)
	{
        if (id == null) {
            throw new NullPointerException();
        }
        if (id.length() != 36) {
            throw new NumberFormatException("UUID has to be represented by the standard 36-char representation");
        }

        long lo, hi;
        lo = hi = 0;
        
        for (int i = 0, j = 0; i < 36; ++j) {
        	
            // Need to bypass hyphens:
            switch (i) {
            case 8:
            case 13:
            case 18:
            case 23:
                if (id.charAt(i) != '-') {
                    throw new NumberFormatException("UUID has to be represented by the standard 36-char representation");
                }
                ++i;
            }
        	int curr;
            char c = id.charAt(i);

            if (c >= '0' && c <= '9') {
                curr = ((c - '0') << 4);
            } else if (c >= 'a' && c <= 'f') {
                curr = ((c - 'a' + 10) << 4);
            } else if (c >= 'A' && c <= 'F') {
                curr = ((c - 'A' + 10) << 4);
            } else {
                throw new NumberFormatException("Non-hex character '"+c+"'");
            }
            curr = (curr << 4);

            c = id.charAt(++i);

            if (c >= '0' && c <= '9') {
                curr |= (byte) (c - '0');
            } else if (c >= 'a' && c <= 'f') {
                curr |= (byte) (c - 'a' + 10);
            } else if (c >= 'A' && c <= 'F') {
                curr |= (byte) (c - 'A' + 10);
            } else {
                throw new NumberFormatException("Non-hex character '"+c+"'");
            }
            if (j < 8) {
            	hi = (hi << 8) | curr;
            } else {
            	lo = (lo << 8) | curr;
            }
            ++i;
        }		
        return new UUID(hi, lo);
	}

	/**
	 * Factory method for constructing {@link java.util.UUID} instance from given
	 * 16 bytes.
	 * NOTE: since absolutely no validation is done for contents, this method should
	 * usually not be used, unless contents are known to be valid.
	 * 
	 * @param bytes
	 * @return
	 */
	public static UUID uuid(byte[] bytes)
	{
		if (bytes == null || bytes.length != 16) {
            throw new IllegalArgumentException("Invalid byte[] passed: can not be null, must be 16 bytes in length");
        }
		return new UUID(_gatherLong(bytes, 0), _gatherLong(bytes, 8));
	}

	public static UUID uuid(byte[] bytes, int offset)
	{
		_checkUUIDByteArray(bytes, offset);
		return new UUID(_gatherLong(bytes, 0), _gatherLong(bytes, 8));
	}
	
	public static UUID nullUUID() {
		return NULL_UUID;
	}

	/*
	/***********************************************************************
	/* Type introspection
	/***********************************************************************
	 */

	/**
	 * Method for determining which type of UUID given UUID is.
	 * Returns null if type can not be determined.
	 * 
	 * @param uuid UUID to check
	 * 
	 * @return Null if uuid is null or type can not be determined (== invalid UUID);
	 *   otherwise type
	 */
	public static UUIDType typeOf(UUID uuid)
	{
		if (uuid == null) {
			return null;
		}
		// Ok: so 4 MSB of byte at offset 6...
		long l = uuid.getMostSignificantBits();
		int typeNibble = (((int) l) >> 12) & 0xF;
		switch (typeNibble) {
		case 0:
			// possibly null?
			if (l == 0L && uuid.getLeastSignificantBits() == l) {
				return UUIDType.NULL;
			}
			break;
		case 1:
			return UUIDType.TIME_BASED;
		case 2:
			return UUIDType.DCE;
		case 3:
			return UUIDType.NAME_BASED;
		case 4:
			return UUIDType.RANDOM_BASED;
		}
		// not recognized: return null
		return null;
	}
	
	/*
	/***********************************************************************
	/* Conversions to other types
	/***********************************************************************
	 */
	
	public static byte[] asByteArray(UUID uuid)
	{
		long hi = uuid.getMostSignificantBits();
		long lo = uuid.getLeastSignificantBits();
		byte[] result = new byte[16];
		_appendInt((int) (hi >> 32), result, 0);
		_appendInt((int) hi, result, 4);
		_appendInt((int) (lo >> 32), result, 8);
		_appendInt((int) lo, result, 12);
		return result;
	}

	public static void toByteArray(UUID uuid, byte[] buffer) {
		toByteArray(uuid, buffer, 0);
	}

	public static void toByteArray(UUID uuid, byte[] buffer, int offset)
	{
		_checkUUIDByteArray(buffer, offset);
		long hi = uuid.getMostSignificantBits();
		long lo = uuid.getLeastSignificantBits();
		_appendInt((int) (hi >> 32), buffer, 0);
		_appendInt((int) hi, buffer, 4);
		_appendInt((int) (lo >> 32), buffer, 8);
		_appendInt((int) lo, buffer, 12);
	}
	
	/*
	/******************************************************************************** 
	/* Internal helper methods
	/******************************************************************************** 
	 */

	private static void _appendInt(int value, byte[] buffer, int offset)
	{
		buffer[offset++] = (byte) (value >> 24);
		buffer[offset++] = (byte) (value >> 16);
		buffer[offset++] = (byte) (value >> 8);
		buffer[offset] = (byte) value;
	}

	private static long _gatherLong(byte[] buffer, int offset)
	{
		long hi = ((long) _gatherInt(buffer, offset)) << 32;
		long lo = ((long) _gatherInt(buffer, offset+4)) & MASK_LOW_INT;

		return hi | lo;
	}
	
	private static int _gatherInt(byte[] buffer, int offset)
	{
		return (buffer[offset] << 24) | ((buffer[offset+1] & 0xFF) << 16)
 		   | ((buffer[offset+2] & 0xFF) << 8) | (buffer[offset+3] & 0xFF);
	}

	private static void _checkUUIDByteArray(byte[] bytes, int offset)
	{
		if (bytes == null) {
            throw new IllegalArgumentException("Invalid byte[] passed: can not be null");
		}
		if (offset < 0) {
            throw new IllegalArgumentException("Invalid offset ("+offset+") passed: can not be negative");
		}
		if ((offset + 16) > bytes.length) {
            throw new IllegalArgumentException("Invalid offset ("+offset+") passed: not enough room in byte array (need 16 bytes)");
        }
	}
}
