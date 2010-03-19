/*----------------------------------------------------------------
 * $Id: BadAddressException.java,v 1.1 2001/10/04 19:42:33 pkb Exp $
 * 
 * (c)2001 - 
 *
 * Revision Log
 *
 * $Log: BadAddressException.java,v $
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

//----------------------------------------------------------------
/** Exception thrown when passed a bad value to decode a ethernet
 * address from.
 *
 * <p>The {@link EthernetAddress} class provides several methods to
 * construct ethernet address objects from. If one passes a bad
 * parameter to these methods, this type of exception might occur.
 * 
 * @version $Revision: 1.1 $
 * 
 * @since 1.0
 * 
 * @author $Author: pkb $
 * 
 * @see EthernetAddress */
//----------------------------------------------------------------

public final class BadAddressException extends IllegalArgumentException {

  //----------------------------------------------------------------
  /** Construct exception with a particular message.
   * 
   * @param	text
   * 
   * 	Text message to associate with exception
   * 
   * @since	1.0 */
  //----------------------------------------------------------------

  BadAddressException(String message) {
    super(message);
  }

}
