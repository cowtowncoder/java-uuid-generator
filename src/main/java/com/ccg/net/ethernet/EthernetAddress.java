/*----------------------------------------------------------------
 * $Id: EthernetAddress.java,v 1.2 2001/10/11 21:13:28 pkb Exp $
 * 
 * (c)2001 - Paul Blankenbaker
 *
 * Revision Log
 *
 * $Log: EthernetAddress.java,v $
 * Revision 1.2  2001/10/11 21:13:28  pkb
 * Changed organization of native code - moved binaries to
 * $COMHOME/native/OS/ARCH directories
 *
 * Revision 1.1  2001/10/04 19:42:33  pkb
 *
 * Added package related to Ethernet addresses (turns out to be a
 * non-trivial exercise to get a ethernet address in a cross platform
 * manner). Currently uses native code (as I don't know of another way)
 * and supports Windows, Linux, Solaris. Tested on (Windows 98, RedHat
 * 7.1 and Solaris 8).
 *
 *
 */
//----------------------------------------------------------------

package com.ccg.net.ethernet;

import java.util.*;


//----------------------------------------------------------------
/** Manage ethernet address objects and provide a means to determine
 * the ethernet address of the machine the JVM is running on.
 *
 * <p>This class is used to examine (work with) ethernet addresses. It
 * was primarily created to provide a means to determine the ethernet
 * address(es) of the local machine (which turned out to be a
 * non-trivial project).
 *
 * <p><b>IMPORTANT INSTALLATION INSTRUCTIONS</b></p>
 *
 * <p>This class relies on native code when determining the ethernet
 * address. Because of this, a shared library module needs to be
 * installed BEFORE you will be able to use the methods in this class
 * related to the local ethernet address of the machine.
 *
 * <p>To do the installation, you need to:
 *
 * <ul>
 * <li>Determine which shared library module you need.
 * <li>Copy the shared library module to its final location.
 * </ul>
 *
 * <p>It is important to note that the shared libraries need to be
 * copied to a location that is within the library search path for
 * your environment. I've found that the $(JREHOME)/bin directory
 * tends to always be in the search path (at least for
 * Linux/Windows). For Sun's JRE installation, look for
 * $(JREHOME)/lib/ARCH (like "/opt/jdk/jre/lib/sparc"). If you are
 * unable to copy the library to this location, you may need to update
 * your library search path before executing code.
 *
 * <p>The source code for each of the libraries is available, however,
 * it is often easier not to have to locate a compiler and simply use
 * one of the pre-compiled binary files. The following binary files
 * are available:
 *
 * <dl>
 *
 * <dt><b>$COMHOME/ccg/native/linux/x86/libEthernetAddress.so</b></dt><dd>This
 * library is intended for use on Intel x86 based Linux
 * platforms. This file needs to be installed within your shared
 * library search path with a final name of "libEthernetAddress.so". A
 * developer can typically install this library with the following
 * command (as root):
 *
 * <pre>
 * cp $COMHOME/ccg/native/linux/x86/libEthernetAddress.so \
 *   $JREHOME/bin/libEthernetAddress.so</pre></dd>
 *
 * <dt><b>$COMHOME/ccg/native/solaris/sparc/libEthernetAddress.so</b></dt><dd>This
 * library is intended for use on Sparc based Solaris platforms. This
 * file needs to be installed within your shared library search path
 * with a final name of "libEthernetAddress.so". A developer can
 * typically install this library with the following command (as
 * root):
 *
 * <pre>
 * cp $COMHOME/ccg/native/solaris/sparc/libEthernetAddress.so \
 *   $JREHOME/lib/sparc/libEthernetAddress.so</pre></dd>
 *
 * <dt><b>$COMHOME/native/win/x86/EthernetAddress.dll</b></dt><dd>This
 * library is intended for use on Intel x86 based Windows
 * platforms. This file needs to be installed within your shared
 * library search path with a final name of "EthernetAddress.dll". If
 * you put this file in the same directory as your "java.exe" file, it
 * seems to be found. A developer can typically install this library
 * with the following command:
 *
 * <pre>
 * copy %COMHOME%/ccg/native/win/x86/EthernetAddress.dll \
 *   %JREHOME%/bin/EthernetAddress.dll</pre></dd>
 *
 * </dl>
 *
 * <p><b>Developer Notes:</b></p>
 *
 * <p>If you need to add support for additional platforms (such as a
 * Mac/Beos/etc), you should take one of the source 'C' files (like
 * EthernetAddress_linux.c) as your starting point and create a new
 * 'C' source file for the native platform you'd like to support.
 * 
 * @version $Revision: 1.2 $
 * 
 * @since 1.0
 * 
 * @author $Author: pkb $
 * 
 * @see #getPrimaryAdapter
 * @see <a href=doc-files/PrintMAC.java>PrintMAC.java</a> */
//----------------------------------------------------------------

public final class EthernetAddress {

  //----------------------------------------------------------------
  /** Native method to look up the ethernet address for a adapter.
   * 
   * @param	i
   * 
   * 	ID of the next ethernet address you want to check.
   * 
   * @param	ea
   * 
   * 	Byte array which is at least 6 bytes long to store the
   * 	ethernet address in.
   * 
   * @return
   * 
   * 	true if able to determine address for adapter, false if not.
   * 
   * @since	1.0 */
  //----------------------------------------------------------------

  private static native boolean getLocalEthernet(int i, byte[] ea);



  //----------------------------------------------------------------
  /** Tries to create a EthernetAddress object for adapter N.
   * 
   * @param	n
   * 
   * 	ID of adapter you want to get address of (start at 0).
   * 
   * @return
   * 
   * 	EthernetAddress object if able to determine, or null if not.
   * 
   * @since	1.0 */
  //----------------------------------------------------------------

  private static EthernetAddress getLocalEthernetAddress(int i) {
				// load native code

				// load ALL adapters we can find on the system
    byte[] ea = new byte[6];

    if (!getLocalEthernet(i,ea)) return null;

    return fromBytes(ea);
    
  }


  //----------------------------------------------------------------
  /** Check to see if all bytes of the ethernet address are zero.
   * 
   * <p>This method checks all of the bytes of a ethernet address to
   * see if they are zero. If they are, then the ethernet address is
   * "0:0:0:0:0:0", which we consider the "null" ethernet address.
   * 
   * @return
   * 
   * 	true if all bytes of the ethernet address are 0.
   * 
   * @since	1.0
   * 
   * @see #NULL */
  //----------------------------------------------------------------

  public boolean isNull() {
    for (int i = 0; i < _Bytes.length; i++) if (_Bytes[i] != 0) return false;
    return true;
  }


  //----------------------------------------------------------------
  /** Constant ethernet address object which has the "null address".
   * 
   * <p>This constant can be used when you want a non-null
   * EthernetAddress object reference, but want a invalid (or null)
   * ethernet address contained.
   *
   * <p>The {@link #isNull isNull()} method will ALWAYS return true
   * for this constant.
   * 
   * @serial	
   * 
   * @since	1.0
   * 
   * @see #isNull */
  //----------------------------------------------------------------

  public static final EthernetAddress NULL=new EthernetAddress();
  

  //----------------------------------------------------------------
  /** Try to determine the primary ethernet address of the machine.
   * 
   * <p>This method will try to return the primary ethernet address of
   * the machine. In order for this to succeed:
   *
   * <ul> 
   * 
   * <li>The necessary native library must be installed (as
   * described in the {@link EthernetAddress class overview}.
   *
   * <li>The native code must find at least one ethernet address for
   * the system.
   *
   * </ul>
   * 
   * @throws	UnsatisfiedLinkError
   * 
   * 	This exception is thrown if we are unable to load the native
   * 	library (like: libEthernetAddress.so or EthernetAddress.dll)
   * 	which is required to query the system for the ethernet
   * 	address.
   * 
   * @return
   * 
   * 	Ethernet address of the machine if able to determine/guess -
   * 	otherwise null.
   * 
   * @since	1.0
   * 
   * @see #getAllAdapters */
  //----------------------------------------------------------------

  public static EthernetAddress getPrimaryAdapter() 
    throws UnsatisfiedLinkError {

    return getLocalEthernetAddress(0);
  }


  //----------------------------------------------------------------
  /** Get all of the ethernet addresses associated with the local machine.
   * 
   * <p>This method will try and find ALL of the ethernet adapters
   * which are currently available on the system. This is heavily OS
   * dependent and may not be supported on all platforms. When not
   * supported, you should still get back a collection with the {@link
   * #getPrimaryAdapter primary adapter} in it.
   *
   * @throws	UnsatisfiedLinkError
   * 
   * 	This exception is thrown if we are unable to load the native
   * 	library (like: libEthernetAddress.so or EthernetAddress.dll)
   * 	which is required to query the system for the ethernet
   * 	address.
   * 
   * @return
   * 
   * 	Array of all ethernet adapters (never returns null, but may
   * 	return a 0 length array if no adapters could be found).
   * 
   * @see #getPrimaryAdapter */
  //----------------------------------------------------------------

  public static Collection getAllAdapters()
    throws UnsatisfiedLinkError {

				// allocate vector to hold info
    Vector av = new Vector();
    EthernetAddress ea=null;
    for (int i = 0; (ea = getLocalEthernetAddress(i)) != null; i++) {
      av.addElement(ea);
    }

    return av;
  }


  //----------------------------------------------------------------
  /** Constructs object with "null values" (address of "0:0:0:0:0:0").
   * 
   * @since	1.0 */
  //----------------------------------------------------------------

  EthernetAddress() {
    _Bytes = new byte[6];
  }


  //----------------------------------------------------------------
  /** Holds the binary ID of your ethernet adapter.
   * 
   * @serial	
   * 
   * @since	1.0  */
  //----------------------------------------------------------------

  private byte[] _Bytes;


  //----------------------------------------------------------------
  /** Set the binary ID of your ethernet adapter.
   * 
   * @param	val
   * 
   * 	New byte[] value to assign.
   * 
   * @see #getBytes  */
  //----------------------------------------------------------------

  public static EthernetAddress fromBytes(byte[] val) 
    throws BadAddressException {
    if (val == null || val.length != 6) {
      throw new BadAddressException("ethernet address not 6 bytes long");
    }

    EthernetAddress ea = new EthernetAddress();
    for (int i = 0; i < val.length; i++) ea._Bytes[i] = val[i];
    return ea;
  }


  //----------------------------------------------------------------
  /** Get the binary ID of your ethernet adapter.
   * 
   * @return
   * 
   * 	Copy of the current byte[] value assigned.
   * 
   * @see #fromBytes  */
  //----------------------------------------------------------------

  public byte[] getBytes() {
    return (byte[]) _Bytes.clone();
  }


  //----------------------------------------------------------------
  /** Parse a ethernet address object from a string.
   *
   * <p>Ethernet addresses are typically shown as 6 hexadecimal values
   * (range: [0,ff]) separated by colons. They have the form:
   *
   * <pre>
   * x:x:x:x:x:x
   * </pre>
   *
   * <p>This method is fairly lenient in its parsing. It allows any
   * character (and omission) of the separator (shown above). And each
   * hex value may be one or two digits long and upper or lower case.
   *
   * <p>The following shows several different ways to list the same
   * ethernet address:
   *
   * <pre>
   * 00:E0:98:06:92:0E
   * 0:e0:98:6:92:e
   * 0-e0-98 6-92-e
   * 00e0980692e0
   * </pre>
   *
   * @param	sval
   * 	String value to try and parse a ethernet address from (must
   * 	not be null).
   * 	
   * @throws	BadAddressException
   * 	If we could not parse a ethernet address from the string you
   * 	passed.
   * 
   * @see #toString  */
  //----------------------------------------------------------------

  public static EthernetAddress fromString(String sval)
    throws BadAddressException {

    byte[] eab = new byte[6];
    int ei = 0;
    boolean needHiNyb = true;

    boolean lastWasSep = true;

    int val = -1;

    int slen = sval.length();
    for (int i = 0; i < slen; i++) {
      char c = sval.charAt(i);
      int cval = Character.digit(c,16);

      if (cval == -1) {		// if not hex digit
	if (lastWasSep) {	// if last char was separator
	  ei = 0;		// reset to zero bytes
	}
	else if (val != -1) {	// if we have value to store
	  if (ei >= eab.length) {
	    throw new BadAddressException("too many bytes in \""+sval+"\"");
	  }
	  eab[ei++] = (byte) val;
	  val = -1;
	  needHiNyb = true;
	}
      }
      else {			// got hex digit
	lastWasSep = false;
	if (needHiNyb) {	// if need hi-nybble, save value
	  val = cval;
	  needHiNyb = false;
	}
	else {			// if lo-nybble, then update array
	  val <<= 4;
	  val += cval;
	  needHiNyb = true;
	  if (ei >= eab.length) {
	    throw new BadAddressException("too many bytes in \""+sval+"\"");
	  }
	  eab[ei++] = (byte) val;
	  val = -1;
	}
      }
    }

				// if last byte value is single digit,
				// catch it here outside of loop
    if ((val != -1) && !needHiNyb) {
      if (ei >= eab.length) {
	throw new BadAddressException("too many bytes in \""+sval+"\"");
      }
      eab[ei++] = (byte) val;
    }

    if (ei != eab.length) {
      throw new BadAddressException("not enough bytes in \""+sval+"\"");
    }

    EthernetAddress ea = new EthernetAddress();
    ea._Bytes = eab;
    return ea;

  }


  //----------------------------------------------------------------
  /** Get a hash code for the object.
   * 
   * <p>This method obeys the hash code contract and returns a hash
   * value that will try to be random, but will be identical for
   * objects which are {@link #equals equal}.
   * 
   * @return
   * 
   * 	A reasonable hash code for the object.
   * 
   * @since	1.0 */
  //----------------------------------------------------------------

  public int hashCode() {

    int blen = _Bytes.length;

    if (blen == 0) return 0;

    int hc = _Bytes[0];
    for (int i = 1; i < blen; i++) {
      hc *= 37;
      hc += _Bytes[i];
    }

    return hc;
    
  }


  //----------------------------------------------------------------
  /** Determine if two ethernet address objects are "equal".
   * 
   * @param	o
   * 
   * 	Other object to compare to (you can pass null).
   * 
   * @return
   * 
   * 	true if two objects have same Ethernet address, false if not.
   * 
   * @since	1.0 */
  //----------------------------------------------------------------

  public boolean equals(Object o) {

    if (!(o instanceof EthernetAddress)) return false;

    byte[] bao = ((EthernetAddress) o)._Bytes;
    if (bao.length != _Bytes.length) return false;

    for (int i = 0; i < bao.length; i++) if (bao[i] != _Bytes[i]) return false;
    return true;
  }


  //----------------------------------------------------------------
  /** Get the string representation of the ethernet address.
   * 
   * @return
   * 
   * 	String representation of ehternet address in form:
   * 	"xx:xx:xx:xx:xx:xx".
   * 
   * @see #fromString  */
  //----------------------------------------------------------------

  public String toString() {
    int blen = _Bytes.length;
    StringBuffer sb = new StringBuffer(blen*3);
    for (int i = 0; i < blen; i++) {
      int lo = _Bytes[i];
      int hi = ((lo >> 4) & 0xF);
      lo &= 0xF;
      if (i != 0) sb.append(':');
      sb.append(Character.forDigit(hi,16));
      sb.append(Character.forDigit(lo,16));
    }
    return sb.toString();
  }

  //----------------------------------------------------------------
  // Static class method to load native library first time class is
  // loaded
  //----------------------------------------------------------------

    /* 08-Sep-2002, TSa: Commented out to allow for alternative
     *   dynamic library loading (loading  is handled from outside this
     *   class now, to allow dynamically choosing the correct lib as well
     *   as catching possible exceptions)
     */
    /*
  static {
    try {
      System.loadLibrary("EthernetAddress");
    } catch (Throwable t) {
      com.ccg.util.Log.error("problem loading \"EthernetAddress"+
			     "\" native library",t);
    }
  }
    */
}
