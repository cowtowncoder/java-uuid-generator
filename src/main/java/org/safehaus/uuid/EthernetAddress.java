/* JUG Java Uuid Generator
 *
 * Copyright (c) 2002 Tatu Saloranta, tatu.saloranta@iki.fi
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

package org.safehaus.uuid;

import java.io.Serializable;

/**
 * EthernetAddress encapsulates the 6-byte Mac address defined in
 * IEEE 802.1 standard.
 */

public class EthernetAddress
    implements Serializable, Cloneable, Comparable
{
    private final static String kHexChars = "0123456789abcdefABCDEF";

    private final byte[] mAddress = new byte[6];

    /* *** Creation methods *** */

    /**
     * String constructor; given a 'standard' ethernet MAC address string
     * (like '00:C0:F0:3D:5B:7C'), constructs an EthernetAddress instance.
     *
     * Note that string is case-insensitive, and also that leading zeroes
     * may be omitted. Thus '00:C0:F0:3D:5B:7C' and '0:c0:f0:3d:5b:7c' are
     * equivalent, and a 'null' address could be passed as ':::::' as well
     * as '00:00:00:00:00:00' (or any other intermediate combination).
     *
     * @param addrStr String representation of the ethernet address
     */
    public EthernetAddress(String addrStr)
        throws NumberFormatException
    {
        byte[] addr = mAddress;
        int len = addrStr.length();
        
        /* Ugh. Although the most logical format would be the 17-char one
         * (12 hex digits separated by colons), apparently leading zeroes
         * can be omitted. Thus... Can't just check string length. :-/
         */
        for (int i = 0, j = 0; j < 6; ++j) {
            if (i >= len) {
                // Is valid if this would have been the last byte:
                if (j == 5) {
                    addr[5] = (byte) 0;
                    break;
                }
                throw new NumberFormatException("Incomplete ethernet address (missing one or more digits");
            }
            
            char c = addrStr.charAt(i);
            ++i;
            int value;
            
            // The whole number may be omitted (if it was zero):
            if (c == ':') {
                value = 0;
            } else {
                // No, got at least one digit?
                if (c >= '0' && c <= '9') {
                    value = (c - '0');
                } else if (c >= 'a' && c <= 'f') {
                    value = (c - 'a' + 10);
                } else if (c >= 'A' && c <= 'F') {
                    value = (c - 'A' + 10);
                } else {
                    throw new NumberFormatException("Non-hex character '"+c+"'");
                }
                
                // How about another one?
                if (i < len) {
                    c = addrStr.charAt(i);
                    ++i;
                    if (c != ':') {
                        value = (value << 4);
                        if (c >= '0' && c <= '9') {
                            value |= (c - '0');
                        } else if (c >= 'a' && c <= 'f') {
                            value |= (c - 'a' + 10);
                        } else if (c >= 'A' && c <= 'F') {
                            value |= (c - 'A' + 10);
                        } else {
                            throw new NumberFormatException("Non-hex character '"+c+"'");
                        }
                    }
                }
            }
            
            addr[j] = (byte) value;
            
            if (c != ':') {
                if (i < len) {
                    if (addrStr.charAt(i) != ':') {
                        throw new NumberFormatException("Expected ':', got ('"
                                                        + addrStr.charAt(i)
                                                        +"')");
                    }
                    ++i;
                } else if (j < 5) {
                    throw new NumberFormatException("Incomplete ethernet address (missing one or more digits");
                }
            }
        }
    }
    
    /**
     * Binary constructor that constructs an instance given the 6 byte
     * (48-bit) address. Useful if an address is saved in binary format
     * (for saving space for example).
     */
    public EthernetAddress(byte [] addr)
        throws NumberFormatException
    {
        if (addr.length != 6) {
            throw new NumberFormatException("Ethernet address has to consist of 6 bytes");
        }
        for (int i = 0; i < 6; ++i) {
            mAddress[i] = addr[i];
        }
    }
    
    /**
     * Another binary constructor; constructs an instance from the given
     * long argument; the lowest 6 bytes contain the address.
     *
     * @param addr long that contains the MAC address in 6 least significant
     *    bytes.
     */
    public EthernetAddress(long addr)
    {
        for (int i = 0; i < 6; ++i) {
            mAddress[5 - i] = (byte) addr;
            addr >>>= 8;
        }
    }
    
    /**
     * Package internal constructor for creating an 'empty' ethernet address
     */
    EthernetAddress()
    {
        byte z = (byte) 0;
        for (int i = 0; i < 6; ++i) {
            mAddress[i] = z;
        }
    }
    
    /**
     * Default cloning behaviour (bitwise copy) is just fine...
     */
    public Object clone()
    {
        try {
            return super.clone();
	} catch (CloneNotSupportedException e) {
	    // shouldn't happen
	    return null;
	}
    }
    
    /* *** Comparison methods *** */
    
    public boolean equals(Object o)
    {
        if (!(o instanceof EthernetAddress)) {
            return false;
        }
        byte[] otherAddress = ((EthernetAddress) o).mAddress;
        byte[] thisAddress = mAddress;
        for (int i = 0; i < 6; ++i) {
            if (otherAddress[i] != thisAddress[i]) {
                return false;
            }
        }
        return true;
    }

    /**
     * Method that compares this EthernetAddress to one passed in as
     * argument. Comparison is done simply by comparing individual
     * address bytes in the order.
     *
     * @return -1 if this EthernetAddress should be sorted before the
     *  one passed in, 1 if after and 0 if they are equal.
     */
    public int compareTo(Object o)
    {
        byte[] thatA = ((EthernetAddress) o).mAddress;
        byte[] thisA = mAddress;
        
        for (int i = 0; i < 6; ++i) {
            int cmp = (((int) thisA[i]) & 0xFF)
                - (((int) thatA[i]) & 0xFF);
            if (cmp != 0) {
                return cmp;
            }
        }
        
        return 0;
    }
    
    /* *** Type conversion *** */
    
    /**
     * Returns the canonical string representation of this ethernet address.
     * Canonical means that all characters are lower-case and string length
     * is always 17 characters (ie. leading zeroes are not omitted).
     *
     * @return Canonical string representation of this ethernet address.
     */
    public String toString()
    {
        /* Let's not cache the output here (unlike with UUID), assuming
         * this won't be called as often:
         */
        StringBuffer b = new StringBuffer(17);
        byte[] addr = mAddress;
	    
        for (int i = 0; i < 6; ++i) {
            if (i > 0) {
                b.append(":");
            }
            int hex = addr[i] & 0xFF;
            b.append(kHexChars.charAt(hex >> 4));
            b.append(kHexChars.charAt(hex & 0x0f));
        }
        return b.toString();
    }

    /**
     * Returns 6 byte byte array that contains the binary representation
     * of this ethernet address; byte 0 is the most significant byte
     * (and so forth)
     *
     * @return 6 byte byte array that contains the binary representation
     */
    public byte[] asByteArray()
    {
        byte[] result = new byte[6];

        toByteArray(result);

        return result;
    }

    /**
     * Synonym to 'asByteArray()'
     *
     * @return 6 byte byte array that contains the binary representation
     */
    public byte[] toByteArray() { return asByteArray(); }

    public void toByteArray(byte[] array) { toByteArray(array, 0); }

    public void toByteArray(byte[] array, int pos)
    {
        for (int i = 0; i < 6; ++i) {
            array[pos+i] = mAddress[i];
        }
    }

    public long toLong()
    {
        /* Damn Java's having signed bytes sucks... they are NEVER what
         * anyone needs; and sign extension work-arounds are slow.
         */
        byte[] addr = mAddress;
        int hi = (((int) addr[0]) & 0xFF) << 8
            | (((int) addr[1]) & 0xFF);
        int lo = ((int) addr[2]) & 0xFF;
        for (int i = 3; i < 6; ++i) {
            lo = (lo << 8) | (((int) addr[i]) & 0xFF);
        }

        return ((long) hi) << 32 | (((long) lo) & 0xFFFFFFFFL);
    }

    /**
     * Constructs a new EthernetAddress given the byte array that contains
     * binary representation of the address.
     *
     * Note that calling this method returns the same result as would
     * using the matching constructor.
     *
     * @param addr Binary representation of the ethernet address
     *
     * @throws NumberFormatException if addr is invalid (less or more than
     *    6 bytes in array)
     */
    public static EthernetAddress valueOf(byte[] addr)
        throws NumberFormatException
    {
        return new EthernetAddress(addr);
    }

    /**
     * Constructs a new EthernetAddress given the byte array that contains
     * binary representation of the address.
     *
     * Note that calling this method returns the same result as would
     * using the matching constructor.
     *
     * @param addr Binary representation of the ethernet address
     *
     * @throws NumberFormatException if addr is invalid (less or more than
     *    6 ints in array)
     */
    public static EthernetAddress valueOf(int[] addr)
        throws NumberFormatException
    {
        byte[] bAddr = new byte[addr.length];

        for (int i = 0; i < addr.length; ++i) {
            bAddr[i] = (byte) addr[i];
        }
        return new EthernetAddress(bAddr);
    }

    /**
     * Constructs a new EthernetAddress given a string representation of
     * the ethernet address.
     *
     * Note that calling this method returns the same result as would
     * using the matching constructor.
     *
     * @param addrStr String representation of the ethernet address
     *
     * @throws NumberFormatException if addr representation is invalid
     */
    public static EthernetAddress valueOf(String addrStr)
        throws NumberFormatException
    {
        return new EthernetAddress(addrStr);
    }

    /**
     * Constructs a new EthernetAddress given the long int value (64-bit)
     * representation of the ethernet address (of which 48 LSB contain
     * the definition)
     *
     * Note that calling this method returns the same result as would
     * using the matching constructor.
     *
     * @param addr Long int representation of the ethernet address
     */
    public static EthernetAddress valueOf(long addr)
    {
        return new EthernetAddress(addr);
    }

    public static void main(String[] args)
    {
        System.out.println("EthernetAddress.main, test:");
        System.out.println("---------------------------");

        long rnd = 0;
        if (args == null || args.length == 0) {
            System.out.println("[no address passed, using a random address]");
            rnd = System.currentTimeMillis()
                ^ (long) (Math.random() * (double) 0x100000000L);
            args = new String[] { new EthernetAddress(rnd).toString() };
        }

        for (int i = 0; i < args.length; ++i) {
            String s = args[i];
            System.out.println("Address '"+s+"':");
            try {
                EthernetAddress a = EthernetAddress.valueOf(s);
                System.err.println("  Ok, comes out as '"+a.toString()+"'");

                // EthernetAddress <-> long
                System.err.print("  Converting to long, result = ");
                long l = a.toLong();
                System.err.println(""+Long.toHexString(l));
                System.err.print("  Creating address from long, are equal: ");
                EthernetAddress b = EthernetAddress.valueOf(l);
                if (b.equals(a)) {
                    System.err.println("yes (OK)");
                } else {
                    System.err.println("no (FAIL)");
                    break;
                }

                // EthernetAddress <-> byte[]
                System.err.println("  Converting to byte array.");
                byte[] ba = a.asByteArray();
                System.err.print("  Creating address from byte[], are equal: ");
                b = EthernetAddress.valueOf(ba);
                if (b.equals(a)) {
                    System.err.println("yes (OK)");
                } else {
                    System.err.println("no (FAIL)");
                    break;
                }
            } catch (NumberFormatException e) {
                System.out.println("  Fail: "+e.toString());
            }
        }
        System.out.println("---------------------------");
        System.out.println("Done.");
    }
}
