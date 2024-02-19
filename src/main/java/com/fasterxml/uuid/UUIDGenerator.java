/* JUG Java UUID Generator
 *
 * Copyright (c) 2002- Tatu Saloranta, tatu.saloranta@iki.fi
 *
 * Licensed under the License specified in the file LICENSE which is
 * included with the source code.
 * You may not use this file except in compliance with the License.
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.fasterxml.uuid;

/**
 * Minimal "tag" base class from which all generator implementations
 * derive. Actual generation methods are not included since different
 * generators take different number of arguments.
 * 
 * @since 3.0
 */
public abstract class UUIDGenerator
{
    /*
    /**********************************************************
    /* Life-cycle
    /**********************************************************
     */

    /**
     * Constructor is private to enforce singleton access.
     */
    protected UUIDGenerator() { }

    /*
    /**********************************************************
    /* Shared (minimal) API
    /**********************************************************
     */

    /**
     * Accessor for determining type of UUIDs (version) that this
     * generator instance will produce.
     */
    public abstract UUIDType getType();

    /*
    /**********************************************************
    /* Helper methods for implementations
    /**********************************************************
     */

    protected final static long _toLong(byte[] buffer, int offset)
    {
        long l1 = _toInt(buffer, offset);
        long l2 = _toInt(buffer, offset+4);
        long l = (l1 << 32) + ((l2 << 32) >>> 32);
        return l;
    }

    protected final static long _toInt(byte[] buffer, int offset)
    {
        return (buffer[offset] << 24)
            + ((buffer[++offset] & 0xFF) << 16)
            + ((buffer[++offset] & 0xFF) << 8)
            + (buffer[++offset] & 0xFF);
    }

    protected final static long _toShort(byte[] buffer, int offset)
    {
        return ((buffer[offset] & 0xFF) << 8)
            + (buffer[++offset] & 0xFF);
    }
}
