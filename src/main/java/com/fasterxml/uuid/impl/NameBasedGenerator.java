package com.fasterxml.uuid.impl;

import java.nio.charset.Charset;
import java.security.MessageDigest;
import java.util.UUID;

import com.fasterxml.uuid.Logger;
import com.fasterxml.uuid.StringArgGenerator;
import com.fasterxml.uuid.UUIDType;

/**
 * Implementation of UUID generator that uses one of name-based generation methods
 * (variants 3 (MD5) and 5 (SHA1)).
 *<p>
 * As all JUG provided implementations, this generator is fully thread-safe; access
 * to digester is synchronized as necessary.
 * 
 * @since 3.0
 */
public class NameBasedGenerator extends StringArgGenerator
{
    public final static Charset _utf8;
    static {
        _utf8 = Charset.forName("UTF-8");
    }
    
    /**
     * Namespace used when name is a DNS name.
     */
    public final static UUID NAMESPACE_DNS = UUID.fromString("6ba7b810-9dad-11d1-80b4-00c04fd430c8");

    /**
     * Namespace used when name is a URL.
     */
    public final static UUID NAMESPACE_URL = UUID.fromString("6ba7b811-9dad-11d1-80b4-00c04fd430c8");
    /**
     * Namespace used when name is an OID.
     */
    public final static UUID NAMESPACE_OID = UUID.fromString("6ba7b812-9dad-11d1-80b4-00c04fd430c8");
    /**
     * Namespace used when name is an X500 identifier
     */
    public final static UUID NAMESPACE_X500 = UUID.fromString("6ba7b814-9dad-11d1-80b4-00c04fd430c8");

    /*
    /**********************************************************************
    /* Configuration
    /**********************************************************************
     */

    /**
     * Namespace to use as prefix.
     */
    protected final UUID _namespace;
    
    /**
     * Message digesster to use for hash calculation
     */
    protected final MessageDigest _digester;

    protected final UUIDType _type;
    
    /*
    /**********************************************************************
    /* Construction
    /**********************************************************************
     */

    /**
     * @param namespace of the namespace, as defined by the
     *   spec. UUID has 4 pre-defined "standard" name space strings
     *   that can be passed to UUID constructor (see example below).
     *   Note that this argument is optional; if no namespace is needed
     *   (for example when name includes namespace prefix), null may be passed.
     * @param digester Hashing algorithm to use. 

    */
    public NameBasedGenerator(UUID namespace, MessageDigest digester, UUIDType type)
    {
        _namespace = namespace;
        // And default digester SHA-1
        if (digester == null) {
            
        }
        if (type == null) {
            String typeStr = digester.getAlgorithm();
            if (typeStr.startsWith("MD5")) {
                type = UUIDType.NAME_BASED_MD5;
            } else if (typeStr.startsWith("SHA")) {
                type = UUIDType.NAME_BASED_SHA1;
            } else {
                // Hmmh... error out? Let's default to SHA-1, but log a warning
                type = UUIDType.NAME_BASED_SHA1;
                Logger.logWarning("Could not determine type of Digester from '"+typeStr+"'; assuming 'SHA-1' type");
            }
        }
        _digester = digester;
        _type = type;
    }

    /*
    /**********************************************************************
    /* Access to config
    /**********************************************************************
     */

    @Override
    public UUIDType getType() { return _type; }
    
    public UUID getNamespace() { return _namespace; }
    
    /*
    /**********************************************************************
    /* UUID generation
    /**********************************************************************
     */

    @Override
    public UUID generate(String name)
    {
        // !!! TODO: 14-Oct-2010, tatu: can repurpose faster UTF-8 encoding from Jackson
        return generate(name.getBytes(_utf8));
    }
    
    @Override
    public UUID generate(byte[] nameBytes)
    {
        byte[] digest;
        synchronized (_digester) {
            _digester.reset();
            if (_namespace != null) {
                _digester.update(UUIDUtil.asByteArray(_namespace));
            }
            _digester.update(nameBytes);
            digest = _digester.digest();
        }
        return UUIDUtil.constructUUID(_type, digest);
    }
}
