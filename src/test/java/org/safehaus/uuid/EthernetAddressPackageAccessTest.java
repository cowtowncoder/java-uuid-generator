/* JUG Java Uuid Generator
 * EthernetAddressPackageAccessTest.java
 * Created on October 7, 2003, 10:46 PM
 *
 * Copyright (c) 2003 Eric Bie
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

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;
import junit.textui.TestRunner;

import java.util.Arrays;

import org.safehaus.uuid.EthernetAddress;

/**
 * JUnit Test class for checking the package access
 * methods of the org.safehaus.uuid.EthernetAddress class.
 *
 * @author Eric Bie
 */
public class EthernetAddressPackageAccessTest extends TestCase
{
    // constant defining the length of a valid ethernet address byte array
    private static final int ETHERNET_ADDRESS_ARRAY_LENGTH = 6;
    
    // here are some sets of good ethernet addresses in various forms
    private static final String NULL_ETHERNET_ADDRESS_STRING =
        "00:00:00:00:00:00";
    private static final long NULL_ETHERNET_ADDRESS_LONG = 0x0000000000000000L;
    private static final byte[] NULL_ETHERNET_ADDRESS_BYTE_ARRAY =
        new byte[ETHERNET_ADDRESS_ARRAY_LENGTH];
    private static final int[] NULL_ETHERNET_ADDRESS_INT_ARRAY =
        new int[ETHERNET_ADDRESS_ARRAY_LENGTH];
    private static final EthernetAddress NULL_ETHERNET_ADDRESS =
        new EthernetAddress(0L);
    
    public EthernetAddressPackageAccessTest(java.lang.String testName)
    {
        super(testName);
    }
    
    public static Test suite()
    {
        TestSuite suite =
            new TestSuite(EthernetAddressPackageAccessTest.class);
        return suite;
    }
    
    public static void main(String[] args)
    {
        TestRunner.run(suite());
    }
    
    /**************************************************************************
     * Begin Constructor tests
     *************************************************************************/
    /**
     * Test of EthernetAddress() constructor,
     * of class org.safehaus.uuid.EthernetAddress.
     */
    public void testDefaultEthernetAddressConstructor()
    {
        // this test technically relies on the toString() and toLong()
        // methods of the EthernetAddress class working properly.
        // If it fails, that is fine... the test only needs to indicate
        // proper working behavior or that it needs to be fixed.
        EthernetAddress ethernet_address = new EthernetAddress();
        assertEquals(
            "Default constructor did not create expected null EthernetAddress",
            NULL_ETHERNET_ADDRESS_STRING,
            ethernet_address.toString());
        assertEquals(
            "Default constructor did not create expected null EthernetAddress",
            NULL_ETHERNET_ADDRESS_LONG,
            ethernet_address.toLong());
    }
    /**************************************************************************
     * End Constructor tests
     *************************************************************************/
}
