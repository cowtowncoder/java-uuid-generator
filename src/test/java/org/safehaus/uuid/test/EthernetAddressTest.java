/* JUG Java Uuid Generator
 * EthernetAddressTest.java
 * Created on July 16, 2003, 11:17 PM
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

package org.safehaus.uuid.test;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;
import junit.textui.TestRunner;

import java.util.Arrays;

import org.safehaus.uuid.EthernetAddress;

/**
 * JUnit Test class for the org.safehaus.uuid.EthernetAddress class.
 *
 * @author Eric Bie
 */
public class EthernetAddressTest extends TestCase
{
    // constant defining the length of a valid ethernet address byte array
    private static final int ETHERNET_ADDRESS_ARRAY_LENGTH = 6;

    // some strings for failure case tests
    private static final String IMPROPER_NUM_COLONS_ETHERNET_ADDRESS_STRING_1 =
        "01f23:45:67:89:ab";
    private static final String IMPROPER_NUM_COLONS_ETHERNET_ADDRESS_STRING_2 =
        "01:23f45:67:89:ab";
    private static final String IMPROPER_NUM_COLONS_ETHERNET_ADDRESS_STRING_3 =
        "01:23:45f67:89:ab";
    private static final String IMPROPER_NUM_COLONS_ETHERNET_ADDRESS_STRING_4 =
        "01:23:45:67f89:ab";
    private static final String IMPROPER_NUM_COLONS_ETHERNET_ADDRESS_STRING_5 =
        "01:23:45:67:89fab";
    private static final String IMPROPER_NUM_COLONS_ETHERNET_ADDRESS_STRING_6 =
        "01f23f45f67f89fab";
    private static final String NON_HEX_ETHERNET_ADDRESS_STRING =
        "NON-HEX0-FORSURE0";
    private static final String RANDOM_PROPER_LENGTH_STRING =
        "Same LengthString";
    
    // some valid strings for the various dropped digit cases
    private static final String FIRST_GROUP_ALL_NUM_ETHERNET_ADDRESS_STRING =
        "00:23:45:67:89:ab";
    private static final String FIRST_GROUP_ONE_NUM_ETHERNET_ADDRESS_STRING =
        "0:23:45:67:89:ab";
    private static final String FIRST_GROUP_NO_NUM_ETHERNET_ADDRESS_STRING =
        ":23:45:67:89:ab";
    private static final String SECOND_GROUP_ALL_NUM_ETHERNET_ADDRESS_STRING =
        "01:00:45:67:89:ab";
    private static final String SECOND_GROUP_ONE_NUM_ETHERNET_ADDRESS_STRING =
        "01:0:45:67:89:ab";
    private static final String SECOND_GROUP_NO_NUM_ETHERNET_ADDRESS_STRING =
        "01::45:67:89:ab";
    private static final String THIRD_GROUP_ALL_NUM_ETHERNET_ADDRESS_STRING =
        "01:23:00:67:89:ab";
    private static final String THIRD_GROUP_ONE_NUM_ETHERNET_ADDRESS_STRING =
        "01:23:0:67:89:ab";
    private static final String THIRD_GROUP_NO_NUM_ETHERNET_ADDRESS_STRING =
        "01:23::67:89:ab";
    private static final String FOURTH_GROUP_ALL_NUM_ETHERNET_ADDRESS_STRING =
        "01:23:45:00:89:ab";
    private static final String FOURTH_GROUP_ONE_NUM_ETHERNET_ADDRESS_STRING =
        "01:23:45:0:89:ab";
    private static final String FOURTH_GROUP_NO_NUM_ETHERNET_ADDRESS_STRING =
        "01:23:45::89:ab";
    private static final String FIFTH_GROUP_ALL_NUM_ETHERNET_ADDRESS_STRING =
        "01:23:45:67:00:ab";
    private static final String FIFTH_GROUP_ONE_NUM_ETHERNET_ADDRESS_STRING =
        "01:23:45:67:0:ab";
    private static final String FIFTH_GROUP_NO_NUM_ETHERNET_ADDRESS_STRING =
        "01:23:45:67::ab";
    private static final String SIXTH_GROUP_ALL_NUM_ETHERNET_ADDRESS_STRING =
        "01:23:45:67:89:00";
    private static final String SIXTH_GROUP_ONE_NUM_ETHERNET_ADDRESS_STRING =
        "01:23:45:67:89:0";
    private static final String SIXTH_GROUP_NO_NUM_ETHERNET_ADDRESS_STRING =
        "01:23:45:67:89:";
    private static final String MIXED_GROUP_ALL_NUM_ETHERNET_ADDRESS_STRING =
        "01:03:00:07:00:00";
    private static final String MIXED_GROUP_ONE_NUM_ETHERNET_ADDRESS_STRING =
        "1:3:0:7:0:0";
    private static final String MIXED_GROUP_NO_NUM_ETHERNET_ADDRESS_STRING =
        "1:3::7::";

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
    
    private static final String VALID_ETHERNET_ADDRESS_STRING =
        "87:f5:93:06:d3:0c";
    private static final String MIXED_CASE_VALID_ETHERNET_ADDRESS_STRING =
        "87:f5:93:06:D3:0c";
    private static final String UPPER_CASE_VALID_ETHERNET_ADDRESS_STRING =
        "87:F5:93:06:D3:0C";
    private static final String LOWER_CASE_VALID_ETHERNET_ADDRESS_STRING =
        VALID_ETHERNET_ADDRESS_STRING;
    private static final long VALID_ETHERNET_ADDRESS_LONG =
        0x000087f59306d30cL;
    private static final byte[] VALID_ETHERNET_ADDRESS_BYTE_ARRAY =
    {
        (byte)0x87, (byte)0xf5, (byte)0x93, (byte)0x06, (byte)0xd3, (byte)0x0c
    };
    private static final int[] VALID_ETHERNET_ADDRESS_INT_ARRAY =
    {
        0x87, 0xf5, 0x93, 0x06, 0xd3, 0x0c
    };
    private static final EthernetAddress VALID_ETHERNET_ADDRESS =
        new EthernetAddress(VALID_ETHERNET_ADDRESS_LONG);

    private static final String ANOTHER_VALID_ETHERNET_ADDRESS_STRING =
        "4c:de:fb:ba:5a:1c";
    private static final long ANOTHER_VALID_ETHERNET_ADDRESS_LONG =
        0x00004cdefbba5a1cL;
    private static final byte[] ANOTHER_VALID_ETHERNET_ADDRESS_BYTE_ARRAY =
    {
        (byte)0x4c, (byte)0xde, (byte)0xfb, (byte)0xba, (byte)0x5a, (byte)0x1c
    };
    private static final int[] ANOTHER_VALID_ETHERNET_ADDRESS_INT_ARRAY =
    {
        0x4c, 0xde, 0xfb, 0xba, 0x5a, 0x1c
    };
    private static final EthernetAddress ANOTHER_VALID_ETHERNET_ADDRESS =
        new EthernetAddress(ANOTHER_VALID_ETHERNET_ADDRESS_LONG);
        
    // some ethernet addresses for the ordering tests
    private static final EthernetAddress MAC0_ETHERNET_ADDRESS =
        new EthernetAddress(0x0000015ae2e61893L);
    private static final EthernetAddress MAC1_ETHERNET_ADDRESS =
        new EthernetAddress(0x00001f0f1b0e8e6eL);
    private static final EthernetAddress MAC2_ETHERNET_ADDRESS =
        new EthernetAddress(0x000022d8afb0b888L);
    private static final EthernetAddress MAC3_ETHERNET_ADDRESS =
        new EthernetAddress(0x00004cfdc9a5e86aL);
    private static final EthernetAddress MAC4_ETHERNET_ADDRESS =
        new EthernetAddress(0x000091038ffa38eeL);
    private static final EthernetAddress MAC5_ETHERNET_ADDRESS =
        new EthernetAddress(0x00009857e4f202a3L);
    private static final EthernetAddress MAC6_ETHERNET_ADDRESS =
        new EthernetAddress(0x0000a8c0600ccc69L);
    private static final EthernetAddress MAC7_ETHERNET_ADDRESS =
        new EthernetAddress(0x0000a9a18860d8fcL);
    private static final EthernetAddress MAC8_ETHERNET_ADDRESS =
        new EthernetAddress(0x0000c8b30f0b395aL);
    private static final EthernetAddress MAC9_ETHERNET_ADDRESS =
        new EthernetAddress(0x0000cf74d8ef49b8L);
                     
    
    public EthernetAddressTest(java.lang.String testName)
    {
        super(testName);
    }
    
    public static Test suite()
    {
        TestSuite suite = new TestSuite(EthernetAddressTest.class);
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
     * Test of EthernetAddress(byte[]) constructor,
     * of class org.safehaus.uuid.EthernetAddress.
     */
    public void testByteArrayEthernetAddressConstructor()
    {
        // lets test some error cases
        // first, passing null
        try
        {
            EthernetAddress ethernet_address =
                new EthernetAddress((byte[])null);
            // if we reached here we failed because we didn't get an exception
            fail("Expected exception not caught");
        }
        catch (NullPointerException ex)
        {
            // this is the success case so do nothing
        }
        catch (Exception ex)
        {
            fail("Caught unexpected exception: " + ex);
        }
        
        // now an array that is too small
        try
        {
            EthernetAddress ethernet_address =
                new EthernetAddress(
                    new byte[ETHERNET_ADDRESS_ARRAY_LENGTH - 1]);
            // if we reached here we failed because we didn't get an exception
            fail("Expected exception not caught");
        }
        catch (NumberFormatException ex)
        {
            // this is the success case so do nothing
        }
        catch (Exception ex)
        {
            fail("Caught unexpected exception: " + ex);
        }

        // now an array that is too big
        try
        {
            EthernetAddress ethernet_address =
                new EthernetAddress(
                    new byte[ETHERNET_ADDRESS_ARRAY_LENGTH + 1]);
            // if we reached here we failed because we didn't get an exception
            fail("Expected exception not caught");
        }
        catch (NumberFormatException ex)
        {
            // this is the success case so do nothing
        }
        catch (Exception ex)
        {
            fail("Caught unexpected exception: " + ex);
        }

        // let's test that creating a EthernetAddress from an zero'd array
        // gives us a null EthernetAddress (definition of null EthernetAddress)
        EthernetAddress ethernet_address =
            new EthernetAddress(new byte[ETHERNET_ADDRESS_ARRAY_LENGTH]);
        assertEquals(
            "EthernetAddress(byte[]) did not create expected EthernetAddress",
            NULL_ETHERNET_ADDRESS_LONG,
            ethernet_address.toLong());
        
        // let's test creating an array from a good byte array
        ethernet_address =
            new EthernetAddress(VALID_ETHERNET_ADDRESS_BYTE_ARRAY);
        assertEquals(
            "EthernetAddress(byte[]) did not create expected EthernetAddress",
            VALID_ETHERNET_ADDRESS_LONG,
            ethernet_address.toLong());
    }
    
    /**
     * Test of EthernetAddress(long) constructor,
     * of class org.safehaus.uuid.EthernetAddress.
     */
    public void testLongEthernetAddressConstructor()
    {
        // let's test that creating a EthernetAddress from an zero long
        // gives us a null EthernetAddress (definition of null EthernetAddress)
        EthernetAddress ethernet_address =
            new EthernetAddress(0x0000000000000000L);
        assertEquals(
            "EthernetAddress(long) did not create expected EthernetAddress",
            NULL_ETHERNET_ADDRESS_LONG,
            ethernet_address.toLong());
        
        // let's test creating an array from a good long
        ethernet_address = new EthernetAddress(VALID_ETHERNET_ADDRESS_LONG);
        assertEquals(
            "EthernetAddress(long) did not create expected EthernetAddress",
            VALID_ETHERNET_ADDRESS_LONG,
            ethernet_address.toLong());
    }
    
    /**
     * Test of EthernetAddress(String) constructor,
     * of class org.safehaus.uuid.EthernetAddress.
     */
    public void testStringEthernetAddressConstructor()
    {
        // test a null string case
        try
        {
            EthernetAddress ethernet_address =
                new EthernetAddress((String)null);
            fail("Expected exception not caught");
        }
        catch (NullPointerException ex)
        {
            // this is the success case so do nothing
        }
        catch (Exception ex)
        {
            fail("Caught unexpected exception: " + ex);
        }
        
        // test some failure cases for the string constructor
        badStringEthernetAddressConstructorHelper(
            IMPROPER_NUM_COLONS_ETHERNET_ADDRESS_STRING_1);
        badStringEthernetAddressConstructorHelper(
            IMPROPER_NUM_COLONS_ETHERNET_ADDRESS_STRING_2);
        badStringEthernetAddressConstructorHelper(
            IMPROPER_NUM_COLONS_ETHERNET_ADDRESS_STRING_3);
        badStringEthernetAddressConstructorHelper(
            IMPROPER_NUM_COLONS_ETHERNET_ADDRESS_STRING_4);
        badStringEthernetAddressConstructorHelper(
            IMPROPER_NUM_COLONS_ETHERNET_ADDRESS_STRING_5);
        badStringEthernetAddressConstructorHelper(
            IMPROPER_NUM_COLONS_ETHERNET_ADDRESS_STRING_6);
        badStringEthernetAddressConstructorHelper(
            NON_HEX_ETHERNET_ADDRESS_STRING);
        badStringEthernetAddressConstructorHelper(
            RANDOM_PROPER_LENGTH_STRING);
    
        // some valid strings for the various dropped digit cases
        goodStringEthernetAddressConstructorHelper(
            FIRST_GROUP_ALL_NUM_ETHERNET_ADDRESS_STRING,
            FIRST_GROUP_ALL_NUM_ETHERNET_ADDRESS_STRING);
        goodStringEthernetAddressConstructorHelper(
            FIRST_GROUP_ONE_NUM_ETHERNET_ADDRESS_STRING,
            FIRST_GROUP_ALL_NUM_ETHERNET_ADDRESS_STRING);
        goodStringEthernetAddressConstructorHelper(
            FIRST_GROUP_NO_NUM_ETHERNET_ADDRESS_STRING,
            FIRST_GROUP_ALL_NUM_ETHERNET_ADDRESS_STRING);
        goodStringEthernetAddressConstructorHelper(
            SECOND_GROUP_ALL_NUM_ETHERNET_ADDRESS_STRING,
            SECOND_GROUP_ALL_NUM_ETHERNET_ADDRESS_STRING);
        goodStringEthernetAddressConstructorHelper(
            SECOND_GROUP_ONE_NUM_ETHERNET_ADDRESS_STRING,
            SECOND_GROUP_ALL_NUM_ETHERNET_ADDRESS_STRING);
        goodStringEthernetAddressConstructorHelper(
            SECOND_GROUP_NO_NUM_ETHERNET_ADDRESS_STRING,
            SECOND_GROUP_ALL_NUM_ETHERNET_ADDRESS_STRING);
        goodStringEthernetAddressConstructorHelper(
            THIRD_GROUP_ALL_NUM_ETHERNET_ADDRESS_STRING,
            THIRD_GROUP_ALL_NUM_ETHERNET_ADDRESS_STRING);
        goodStringEthernetAddressConstructorHelper(
            THIRD_GROUP_ONE_NUM_ETHERNET_ADDRESS_STRING,
            THIRD_GROUP_ALL_NUM_ETHERNET_ADDRESS_STRING);
        goodStringEthernetAddressConstructorHelper(
            THIRD_GROUP_NO_NUM_ETHERNET_ADDRESS_STRING,
            THIRD_GROUP_ALL_NUM_ETHERNET_ADDRESS_STRING);
        goodStringEthernetAddressConstructorHelper(
            FOURTH_GROUP_ALL_NUM_ETHERNET_ADDRESS_STRING,
            FOURTH_GROUP_ALL_NUM_ETHERNET_ADDRESS_STRING);
        goodStringEthernetAddressConstructorHelper(
            FOURTH_GROUP_ONE_NUM_ETHERNET_ADDRESS_STRING,
            FOURTH_GROUP_ALL_NUM_ETHERNET_ADDRESS_STRING);
        goodStringEthernetAddressConstructorHelper(
            FOURTH_GROUP_NO_NUM_ETHERNET_ADDRESS_STRING,
            FOURTH_GROUP_ALL_NUM_ETHERNET_ADDRESS_STRING);
        goodStringEthernetAddressConstructorHelper(
            FIFTH_GROUP_ALL_NUM_ETHERNET_ADDRESS_STRING,
            FIFTH_GROUP_ALL_NUM_ETHERNET_ADDRESS_STRING);
        goodStringEthernetAddressConstructorHelper(
            FIFTH_GROUP_ONE_NUM_ETHERNET_ADDRESS_STRING,
            FIFTH_GROUP_ALL_NUM_ETHERNET_ADDRESS_STRING);
        goodStringEthernetAddressConstructorHelper(
            FIFTH_GROUP_NO_NUM_ETHERNET_ADDRESS_STRING,
            FIFTH_GROUP_ALL_NUM_ETHERNET_ADDRESS_STRING);
        goodStringEthernetAddressConstructorHelper(
            SIXTH_GROUP_ALL_NUM_ETHERNET_ADDRESS_STRING,
            SIXTH_GROUP_ALL_NUM_ETHERNET_ADDRESS_STRING);
        goodStringEthernetAddressConstructorHelper(
            SIXTH_GROUP_ONE_NUM_ETHERNET_ADDRESS_STRING,
            SIXTH_GROUP_ALL_NUM_ETHERNET_ADDRESS_STRING);
        goodStringEthernetAddressConstructorHelper(
            SIXTH_GROUP_NO_NUM_ETHERNET_ADDRESS_STRING,
            SIXTH_GROUP_ALL_NUM_ETHERNET_ADDRESS_STRING);
        goodStringEthernetAddressConstructorHelper(
            MIXED_GROUP_ALL_NUM_ETHERNET_ADDRESS_STRING,
            MIXED_GROUP_ALL_NUM_ETHERNET_ADDRESS_STRING);
        goodStringEthernetAddressConstructorHelper(
            MIXED_GROUP_ONE_NUM_ETHERNET_ADDRESS_STRING,
            MIXED_GROUP_ALL_NUM_ETHERNET_ADDRESS_STRING);
        goodStringEthernetAddressConstructorHelper(
            MIXED_GROUP_NO_NUM_ETHERNET_ADDRESS_STRING,
            MIXED_GROUP_ALL_NUM_ETHERNET_ADDRESS_STRING);
        
        // test the other good cases
        goodStringEthernetAddressConstructorHelper(
            NULL_ETHERNET_ADDRESS_STRING,
            NULL_ETHERNET_ADDRESS_STRING);
        goodStringEthernetAddressConstructorHelper(
            UPPER_CASE_VALID_ETHERNET_ADDRESS_STRING,
            UPPER_CASE_VALID_ETHERNET_ADDRESS_STRING);
        goodStringEthernetAddressConstructorHelper(
            LOWER_CASE_VALID_ETHERNET_ADDRESS_STRING,
            LOWER_CASE_VALID_ETHERNET_ADDRESS_STRING);
        goodStringEthernetAddressConstructorHelper(
            MIXED_CASE_VALID_ETHERNET_ADDRESS_STRING,
            MIXED_CASE_VALID_ETHERNET_ADDRESS_STRING);
    }

    /**************************************************************************
     * End Constructor tests
     *************************************************************************/
    /**
     * Test of asByteArray method, of class org.safehaus.uuid.EthernetAddress.
     */
    public void testAsByteArray()
    {
        // we'll test making a couple EthernetAddresses and then check that
        // asByteArray returns the same value in long form as used to create it
        
        // first we'll test the null EthernetAddress
        EthernetAddress ethernet_address = new EthernetAddress(0L);
        assertEquals("Expected length of returned array wrong",
            ETHERNET_ADDRESS_ARRAY_LENGTH,
            ethernet_address.asByteArray().length);
        assertEthernetAddressArraysAreEqual(
            NULL_ETHERNET_ADDRESS_BYTE_ARRAY, 0,
            ethernet_address.asByteArray(), 0);
        
        // now test a non-null EthernetAddress
        ethernet_address = new EthernetAddress(VALID_ETHERNET_ADDRESS_LONG);
        assertEquals("Expected length of returned array wrong",
            ETHERNET_ADDRESS_ARRAY_LENGTH,
            ethernet_address.asByteArray().length);
        assertEthernetAddressArraysAreEqual(
            VALID_ETHERNET_ADDRESS_BYTE_ARRAY, 0,
            ethernet_address.asByteArray(), 0);
        
        // let's make sure that changing the returned array doesn't mess with
        // the wrapped EthernetAddress's internals
        byte[] ethernet_address_byte_array = ethernet_address.asByteArray();
        // we'll just stir it up a bit and then check that the original
        // EthernetAddress was not changed in the process.
        // The easiest stir is to sort it ;)
        Arrays.sort(ethernet_address_byte_array);
        assertEthernetAddressArraysAreNotEqual(
            VALID_ETHERNET_ADDRESS_BYTE_ARRAY, 0,
            ethernet_address_byte_array, 0);
        assertEthernetAddressArraysAreNotEqual(
            ethernet_address.asByteArray(), 0,
            ethernet_address_byte_array, 0);
        assertEthernetAddressArraysAreEqual(
            VALID_ETHERNET_ADDRESS_BYTE_ARRAY, 0,
            ethernet_address.asByteArray(), 0);
    }
    
    /**
     * Test of clone method, of class org.safehaus.uuid.EthernetAddress.
     */
    public void testClone()
    {
        // as lifted from the JDK Object JavaDoc for clone:
        // x.clone() Creates and returns a copy of x.
        // The precise meaning of "copy" may depend on
        // the class of the object. The general intent
        // is that, for any object x, the expression: 
        // x.clone() != x 
        // will be true, and that the expression: 
        // x.clone().getClass() == x.getClass()
        // will be true, but these are not absolute requirements.
        // While it is typically the case that:
        // x.clone().equals(x)
        // will be true, this is not an absolute requirement.
        // For EthernetAddress, this test will check that all the above
        // ARE true in the case of EthernetAddress clone() because it is
        // the desired behavior.
        EthernetAddress x = new EthernetAddress(VALID_ETHERNET_ADDRESS_STRING);
        assertTrue("x.clone() != x did not return true",
                    x.clone() != x);
        assertTrue("x.clone().getClass() == x.getClass() did not return true",
                    x.clone().getClass() == x.getClass());
        assertTrue("x.clone().equals(x) did not return true",
                    x.clone().equals(x));
    }
    
    /**
     * Test of compareTo method, of class org.safehaus.uuid.EthernetAddress.
     */
    public void testCompareTo()
    {
        // first, let's make sure calling compareTo with null
        // throws the appropriate NullPointerException 
        try
        {
            // the 'null EthernetAddress' will be fine
            NULL_ETHERNET_ADDRESS.compareTo(null);
            fail("Expected exception not thrown");
        }
        catch (NullPointerException ex)
        {
            // good, we caught the expected exception, so we passed
        }
        catch (Exception ex)
        {
            fail("Caught an unexpected exception: " + ex);
        }
        
        // now, let's make sure giving compareTo a non-EthernetAddress class
        // results in the appropriate ClassCastException
        try
        {
            // the 'null EthernetAddress' will be fine
            NULL_ETHERNET_ADDRESS.compareTo((new Integer(5)));
            fail("Expected exception not thrown");
        }
        catch (ClassCastException ex)
        {
            // good, we caught the expected exception, so we passed
        }
        catch (Exception ex)
        {
            fail("Caught an unexpected exception: " + ex);
        }
        
        // now we'll test some simple base cases
        // 2 null EthernetAddresses always compare to 0
        assertEthernetAddressEqualOrderHelper(NULL_ETHERNET_ADDRESS,
            new EthernetAddress(0L));
        
        // 2 of the same value EthernetAddresses are always 0
        assertEthernetAddressEqualOrderHelper(MAC0_ETHERNET_ADDRESS,
            new EthernetAddress(MAC0_ETHERNET_ADDRESS.toLong()));
        
        // the 'null EthernetAddress' always comes first in the ordering
        assertEthernetAddressGreaterOrderHelper(MAC0_ETHERNET_ADDRESS,
            NULL_ETHERNET_ADDRESS);
        
        // EthernetAddresses will always sort
        // with the 'numerically' greater MAC addresses coming later
        assertEthernetAddressGreaterOrderHelper(MAC4_ETHERNET_ADDRESS,
                                                MAC0_ETHERNET_ADDRESS);        
        assertEthernetAddressGreaterOrderHelper(MAC9_ETHERNET_ADDRESS,
                                                MAC4_ETHERNET_ADDRESS);
        assertEthernetAddressGreaterOrderHelper(MAC9_ETHERNET_ADDRESS,
                                                MAC0_ETHERNET_ADDRESS);
        
        // now we will test a bigger case of the compareTo functionality
        // of the EthernetAddress class
        // easiest way to do this is to create an array of EthernetAddresses
        // and sort it then test that this array is in the expected order
        
        // before sort, the array contains (in psudo-random order)
        // 15 EthernetAddresses of this distribution:
        // 1 - null EthernetAddress
        // 2 - mac0
        // 1 - mac1
        // 1 - mac2
        // 2 - mac3
        // 2 - mac4
        // 2 - mac5
        // 1 - mac6
        // 1 - mac7
        // 1 - mac8
        // 1 - mac9
        EthernetAddress ethernet_address_array[] = new EthernetAddress[15];
        ethernet_address_array[0] = MAC4_ETHERNET_ADDRESS;
        ethernet_address_array[1] = MAC6_ETHERNET_ADDRESS;
        ethernet_address_array[2] = MAC0_ETHERNET_ADDRESS;
        ethernet_address_array[3] = MAC5_ETHERNET_ADDRESS;
        ethernet_address_array[4] = MAC3_ETHERNET_ADDRESS;
        ethernet_address_array[5] = MAC5_ETHERNET_ADDRESS;
        ethernet_address_array[6] = MAC0_ETHERNET_ADDRESS;
        ethernet_address_array[7] = NULL_ETHERNET_ADDRESS;
        ethernet_address_array[8] = MAC8_ETHERNET_ADDRESS;
        ethernet_address_array[9] = MAC3_ETHERNET_ADDRESS;
        ethernet_address_array[10] = MAC4_ETHERNET_ADDRESS;
        ethernet_address_array[11] = MAC7_ETHERNET_ADDRESS;
        ethernet_address_array[12] = MAC1_ETHERNET_ADDRESS;
        ethernet_address_array[13] = MAC9_ETHERNET_ADDRESS;
        ethernet_address_array[14] = MAC2_ETHERNET_ADDRESS;
        
        Arrays.sort(ethernet_address_array);
        // now we should be able to see that the array is in order
        assertEthernetAddressesMatchHelper(
            NULL_ETHERNET_ADDRESS, ethernet_address_array[0]);
        assertEthernetAddressesMatchHelper(
            MAC0_ETHERNET_ADDRESS, ethernet_address_array[1]);
        assertEthernetAddressesMatchHelper(
            MAC0_ETHERNET_ADDRESS, ethernet_address_array[2]);
        assertEthernetAddressesMatchHelper(
            MAC1_ETHERNET_ADDRESS, ethernet_address_array[3]);
        assertEthernetAddressesMatchHelper(
            MAC2_ETHERNET_ADDRESS, ethernet_address_array[4]);
        assertEthernetAddressesMatchHelper(
            MAC3_ETHERNET_ADDRESS, ethernet_address_array[5]);
        assertEthernetAddressesMatchHelper(
            MAC3_ETHERNET_ADDRESS, ethernet_address_array[6]);
        assertEthernetAddressesMatchHelper(
            MAC4_ETHERNET_ADDRESS, ethernet_address_array[7]);
        assertEthernetAddressesMatchHelper(
            MAC4_ETHERNET_ADDRESS, ethernet_address_array[8]);
        assertEthernetAddressesMatchHelper(
            MAC5_ETHERNET_ADDRESS, ethernet_address_array[9]);
        assertEthernetAddressesMatchHelper(
            MAC5_ETHERNET_ADDRESS, ethernet_address_array[10]);
        assertEthernetAddressesMatchHelper(
            MAC6_ETHERNET_ADDRESS, ethernet_address_array[11]);
        assertEthernetAddressesMatchHelper(
            MAC7_ETHERNET_ADDRESS, ethernet_address_array[12]);
        assertEthernetAddressesMatchHelper(
            MAC8_ETHERNET_ADDRESS, ethernet_address_array[13]);
        assertEthernetAddressesMatchHelper(
            MAC9_ETHERNET_ADDRESS, ethernet_address_array[14]);
    }
    
    /**
     * Test of equals method, of class org.safehaus.uuid.EthernetAddress.
     */
    public void testEquals()
    {
        // test passing null to equals returns false
        // (as specified in the JDK docs for Object)
        EthernetAddress x =
            new EthernetAddress(VALID_ETHERNET_ADDRESS_BYTE_ARRAY);
        assertFalse("equals(null) didn't return false",
                x.equals((Object)null));
        
        // test passing an object which is not a EthernetAddress returns false
        assertFalse("x.equals(non_EthernetAddress_object) didn't return false",
                    x.equals(new Object()));
        
        // test a case where two EthernetAddresss are definitly not equal
        EthernetAddress w =
            new EthernetAddress(ANOTHER_VALID_ETHERNET_ADDRESS_BYTE_ARRAY);
        assertFalse("x == w didn't return false",
                    x == w);
        assertFalse("x.equals(w) didn't return false",
                    x.equals(w));

        // test refelexivity
        assertTrue("x == x didn't return true",
                    x == x);
        assertTrue("x.equals(x) didn't return true",
                    x.equals(x));
        
        // test symmetry
        EthernetAddress y =
            new EthernetAddress(VALID_ETHERNET_ADDRESS_BYTE_ARRAY);
        assertFalse("x == y didn't return false",
                    x == y);
        assertTrue("y.equals(x) didn't return true",
                    y.equals(x));
        assertTrue("x.equals(y) didn't return true",
                    x.equals(y));
        
        // now we'll test transitivity
        EthernetAddress z =
            new EthernetAddress(VALID_ETHERNET_ADDRESS_BYTE_ARRAY);
        assertFalse("x == y didn't return false",
                    x == y);
        assertFalse("x == y didn't return false",
                    y == z);
        assertFalse("x == y didn't return false",
                    x == z);
        assertTrue("x.equals(y) didn't return true",
                    x.equals(y));
        assertTrue("y.equals(z) didn't return true",
                    y.equals(z));
        assertTrue("x.equals(z) didn't return true",
                    x.equals(z));
        
        // test consistancy (this test is just calling equals multiple times)
        assertFalse("x == y didn't return false",
                    x == y);
        assertTrue("x.equals(y) didn't return true",
                    x.equals(y));
        assertTrue("x.equals(y) didn't return true",
                    x.equals(y));
        assertTrue("x.equals(y) didn't return true",
                    x.equals(y));
    }
    
    /**
     * Test of toByteArray method, of class org.safehaus.uuid.EthernetAddress.
     */
    public void testToByteArray()
    {
        // we'll test making a couple EthernetAddresses and then check that the
        // toByteArray returns the same value in byte form as used to create it
        
        // first we'll test the null EthernetAddress
        EthernetAddress ethernet_address = new EthernetAddress(0L);
        assertEquals("Expected length of returned array wrong",
            ETHERNET_ADDRESS_ARRAY_LENGTH,
            ethernet_address.toByteArray().length);
        assertEthernetAddressArraysAreEqual(
            NULL_ETHERNET_ADDRESS_BYTE_ARRAY, 0,
            ethernet_address.toByteArray(), 0);
        
        // now test a non-null EthernetAddress
        ethernet_address = new EthernetAddress(VALID_ETHERNET_ADDRESS_LONG);
        assertEquals("Expected length of returned array wrong",
            ETHERNET_ADDRESS_ARRAY_LENGTH,
            ethernet_address.toByteArray().length);
        assertEthernetAddressArraysAreEqual(
            VALID_ETHERNET_ADDRESS_BYTE_ARRAY, 0,
            ethernet_address.toByteArray(), 0);
        
        // let's make sure that changing the returned array doesn't mess with
        // the wrapped EthernetAddress's internals
        byte[] ethernet_address_byte_array = ethernet_address.toByteArray();
        // we'll just stir it up a bit and then check that the original
        // EthernetAddress was not changed in the process.
        // The easiest stir is to sort it ;)
        Arrays.sort(ethernet_address_byte_array);
        assertEthernetAddressArraysAreNotEqual(
            VALID_ETHERNET_ADDRESS_BYTE_ARRAY, 0,
            ethernet_address_byte_array, 0);
        assertEthernetAddressArraysAreNotEqual(
            ethernet_address.toByteArray(), 0,
            ethernet_address_byte_array, 0);
        assertEthernetAddressArraysAreEqual(
            VALID_ETHERNET_ADDRESS_BYTE_ARRAY, 0,
            ethernet_address.toByteArray(), 0);
    }
    
    /**
     * Test of toByteArray(byte[]) method,
     * of class org.safehaus.uuid.EthernetAddress.
     */
    public void testToByteArrayDest()
    {
        // constant for use in this test
        final int EXTRA_DATA_LENGTH = 9;
        
        // lets test some error cases
        // first, passing null
        try
        {
            EthernetAddress ethernet_address = new EthernetAddress(0L);
            ethernet_address.toByteArray((byte[])null);
            // if we reached here we failed because we didn't get an exception
            fail("Expected exception not caught");
        }
        catch (NullPointerException ex)
        {
            // this is the success case so do nothing
        }
        catch (Exception ex)
        {
            fail("Caught unexpected exception: " + ex);
        }
        
        // now an array that is too small
        try
        {
            EthernetAddress ethernet_address = new EthernetAddress(0L);
            byte[] ethernet_address_byte_array =
                new byte[ETHERNET_ADDRESS_ARRAY_LENGTH - 1];
            ethernet_address.toByteArray(ethernet_address_byte_array);
            // if we reached here we failed because we didn't get an exception
            fail("Expected exception not caught");
        }
        catch (ArrayIndexOutOfBoundsException ex)
        {
            // this is the success case so do nothing
        }
        catch (Exception ex)
        {
            fail("Caught unexpected exception: " + ex);
        }

        // we'll test making a couple EthernetAddresses and then check that
        // toByteArray returns the same value in byte form as used to create it
        
        // here we'll test the null EthernetAddress
        EthernetAddress ethernet_address = new EthernetAddress(0L);
        byte[] test_array = new byte[ETHERNET_ADDRESS_ARRAY_LENGTH];
        Arrays.fill(test_array, (byte)'x');
        ethernet_address.toByteArray(test_array);
        assertEthernetAddressArraysAreEqual(
            NULL_ETHERNET_ADDRESS_BYTE_ARRAY, 0, test_array, 0);
        
        // now test a non-null EthernetAddress
        ethernet_address =
            new EthernetAddress(MIXED_CASE_VALID_ETHERNET_ADDRESS_STRING);
        Arrays.fill(test_array, (byte)'x');
        ethernet_address.toByteArray(test_array);
        assertEthernetAddressArraysAreEqual(
            VALID_ETHERNET_ADDRESS_BYTE_ARRAY, 0, test_array, 0);
        
        // now test a null EthernetAddress case with extra data in the array
        ethernet_address = new EthernetAddress(0L);
        test_array =
            new byte[ETHERNET_ADDRESS_ARRAY_LENGTH + EXTRA_DATA_LENGTH];
        Arrays.fill(test_array, (byte)'x');
        ethernet_address.toByteArray(test_array);
        assertEthernetAddressArraysAreEqual(
            NULL_ETHERNET_ADDRESS_BYTE_ARRAY, 0, test_array, 0);
        for (int i = 0; i < EXTRA_DATA_LENGTH; i++)
        {
            assertEquals("Expected array fill value changed",
                        (byte)'x',
                        test_array[i + ETHERNET_ADDRESS_ARRAY_LENGTH]);
        }
        
        // now test a good EthernetAddress case with extra data in the array
        ethernet_address =
            new EthernetAddress(MIXED_CASE_VALID_ETHERNET_ADDRESS_STRING);
        test_array =
            new byte[ETHERNET_ADDRESS_ARRAY_LENGTH + EXTRA_DATA_LENGTH];
        Arrays.fill(test_array, (byte)'x');
        ethernet_address.toByteArray(test_array);
        assertEthernetAddressArraysAreEqual(
            VALID_ETHERNET_ADDRESS_BYTE_ARRAY, 0, test_array, 0);
        for (int i = 0; i < EXTRA_DATA_LENGTH; i++)
        {
            assertEquals("Expected array fill value changed",
                        (byte)'x',
                        test_array[i + ETHERNET_ADDRESS_ARRAY_LENGTH]);
        }
    }
    
    /**
     * Test of toByteArray(byte[], int) method,
     * of class org.safehaus.uuid.EthernetAddress.
     */
    public void testToByteArrayDestOffset()
    {
        // constant value for use in this test
        final int EXTRA_DATA_LENGTH = 9;
        
        // lets test some error cases
        // first, passing null and 0
        try
        {
            EthernetAddress ethernet_address = new EthernetAddress(0L);
            ethernet_address.toByteArray((byte[])null, 0);
            // if we reached here we failed because we didn't get an exception
            fail("Expected exception not caught");
        }
        catch (NullPointerException ex)
        {
            // this is the success case so do nothing
        }
        catch (Exception ex)
        {
            fail("Caught unexpected exception: " + ex);
        }
        
        // now an array that is too small
        try
        {
            EthernetAddress ethernet_address = new EthernetAddress(0L);
            byte[] ethernet_address_byte_array =
                new byte[ETHERNET_ADDRESS_ARRAY_LENGTH - 1];
            ethernet_address.toByteArray(ethernet_address_byte_array, 0);
            // if we reached here we failed because we didn't get an exception
            fail("Expected exception not caught");
        }
        catch (ArrayIndexOutOfBoundsException ex)
        {
            // this is the success case so do nothing
        }
        catch (Exception ex)
        {
            fail("Caught unexpected exception: " + ex);
        }

        // now an index that is negative
        try
        {
            EthernetAddress ethernet_address = new EthernetAddress(0L);
            byte[] ethernet_address_byte_array =
                new byte[ETHERNET_ADDRESS_ARRAY_LENGTH];
            ethernet_address.toByteArray(ethernet_address_byte_array, -1);
            // if we reached here we failed because we didn't get an exception
            fail("Expected exception not caught");
        }
        catch (ArrayIndexOutOfBoundsException ex)
        {
            // this is the success case so do nothing
        }
        catch (Exception ex)
        {
            fail("Caught unexpected exception: " + ex);
        }
        
        // now an index that is too big
        try
        {
            EthernetAddress ethernet_address = new EthernetAddress(0L);
            byte[] ethernet_address_byte_array =
                new byte[ETHERNET_ADDRESS_ARRAY_LENGTH];
            ethernet_address.toByteArray(
                ethernet_address_byte_array, ETHERNET_ADDRESS_ARRAY_LENGTH);
            // if we reached here we failed because we didn't get an exception
            fail("Expected exception not caught");
        }
        catch (ArrayIndexOutOfBoundsException ex)
        {
            // this is the success case so do nothing
        }
        catch (Exception ex)
        {
            fail("Caught unexpected exception: " + ex);
        }
        
        // now an index that is in the array,
        // but without enough bytes to read ETHERNET_ADDRESS_ARRAY_LENGTH
        try
        {
            EthernetAddress ethernet_address = new EthernetAddress(0L);
            byte[] ethernet_address_byte_array =
                new byte[ETHERNET_ADDRESS_ARRAY_LENGTH];
            ethernet_address.toByteArray(ethernet_address_byte_array, 1);
            // if we reached here we failed because we didn't get an exception
            fail("Expected exception not caught");
        }
        catch (ArrayIndexOutOfBoundsException ex)
        {
            // this is the success case so do nothing
        }
        catch (Exception ex)
        {
            fail("Caught unexpected exception: " + ex);
        }
        
        // we'll test making a couple EthernetAddresss and then check
        // that toByteArray
        // returns the same value in byte form as used to create it
        
        // here we'll test the null EthernetAddress at offset 0
        EthernetAddress ethernet_address = new EthernetAddress(0L);
        byte[] test_array = new byte[ETHERNET_ADDRESS_ARRAY_LENGTH];
        Arrays.fill(test_array, (byte)'x');
        ethernet_address.toByteArray(test_array, 0);
        assertEthernetAddressArraysAreEqual(
            NULL_ETHERNET_ADDRESS_BYTE_ARRAY, 0, test_array, 0);
        
        // now test a non-null EthernetAddress
        ethernet_address =
            new EthernetAddress(MIXED_CASE_VALID_ETHERNET_ADDRESS_STRING);
        Arrays.fill(test_array, (byte)'x');
        ethernet_address.toByteArray(test_array);
        assertEthernetAddressArraysAreEqual(
            VALID_ETHERNET_ADDRESS_BYTE_ARRAY, 0, test_array, 0);
        
        // now test a null EthernetAddress case with extra data in the array
        ethernet_address = new EthernetAddress(0L);
        test_array =
            new byte[ETHERNET_ADDRESS_ARRAY_LENGTH + EXTRA_DATA_LENGTH];
        Arrays.fill(test_array, (byte)'x');
        ethernet_address.toByteArray(test_array, 0);
        assertEthernetAddressArraysAreEqual(
            NULL_ETHERNET_ADDRESS_BYTE_ARRAY, 0, test_array, 0);
        for (int i = 0; i < EXTRA_DATA_LENGTH; i++)
        {
            assertEquals("Expected array fill value changed",
                        (byte)'x',
                        test_array[i + ETHERNET_ADDRESS_ARRAY_LENGTH]);
        }
        
        // now test a null EthernetAddress case with extra data in the array
        ethernet_address = new EthernetAddress(0L);
        test_array =
            new byte[ETHERNET_ADDRESS_ARRAY_LENGTH + EXTRA_DATA_LENGTH];
        Arrays.fill(test_array, (byte)'x');
        ethernet_address.toByteArray(test_array, EXTRA_DATA_LENGTH/2);
        assertEthernetAddressArraysAreEqual(
            NULL_ETHERNET_ADDRESS_BYTE_ARRAY, 0,
            test_array, EXTRA_DATA_LENGTH/2);
        for (int i = 0; i < EXTRA_DATA_LENGTH/2; i++)
        {
            assertEquals("Expected array fill value changed",
                (byte)'x',
                test_array[i]);
            assertEquals("Expected array fill value changed",
                (byte)'x',
                test_array[i + ETHERNET_ADDRESS_ARRAY_LENGTH +
                    EXTRA_DATA_LENGTH/2]);
        }
        
        // now test a good EthernetAddress case with extra data in the array
        ethernet_address =
            new EthernetAddress(MIXED_CASE_VALID_ETHERNET_ADDRESS_STRING);
        test_array =
            new byte[ETHERNET_ADDRESS_ARRAY_LENGTH + EXTRA_DATA_LENGTH];
        Arrays.fill(test_array, (byte)'x');
        ethernet_address.toByteArray(test_array, 0);
        assertEthernetAddressArraysAreEqual(
            VALID_ETHERNET_ADDRESS_BYTE_ARRAY, 0, test_array, 0);
        for (int i = 0; i < EXTRA_DATA_LENGTH; i++)
        {
            assertEquals("Expected array fill value changed",
                        (byte)'x',
                        test_array[i + ETHERNET_ADDRESS_ARRAY_LENGTH]);
        }

        // now test a good EthernetAddress case with extra data in the array
        ethernet_address =
            new EthernetAddress(MIXED_CASE_VALID_ETHERNET_ADDRESS_STRING);
        test_array =
            new byte[ETHERNET_ADDRESS_ARRAY_LENGTH + EXTRA_DATA_LENGTH];
        Arrays.fill(test_array, (byte)'x');
        ethernet_address.toByteArray(test_array, EXTRA_DATA_LENGTH/2);
        assertEthernetAddressArraysAreEqual(
            VALID_ETHERNET_ADDRESS_BYTE_ARRAY, 0,
            test_array, EXTRA_DATA_LENGTH/2);
        for (int i = 0; i < EXTRA_DATA_LENGTH/2; i++)
        {
            assertEquals("Expected array fill value changed",
                (byte)'x',
                test_array[i]);
            assertEquals("Expected array fill value changed",
                (byte)'x',
                test_array[i + ETHERNET_ADDRESS_ARRAY_LENGTH +
                    EXTRA_DATA_LENGTH/2]);
        }
    }
    
    /**
     * Test of toLong method, of class org.safehaus.uuid.EthernetAddress.
     */
    public void testToLong()
    {
        // test making a couple EthernetAddresss and then check that the toLong
        // gives back the same value in long form that was used to create it
        
        // test the null EthernetAddress
        EthernetAddress ethernet_address = new EthernetAddress(0L);
        assertEquals("null EthernetAddress long and toLong did not match",
                    NULL_ETHERNET_ADDRESS_LONG,
                    ethernet_address.toLong());
        
        // test a non-null EthernetAddress
        ethernet_address =
            new EthernetAddress(VALID_ETHERNET_ADDRESS_BYTE_ARRAY);
        assertEquals("EthernetAddress long and toLong results did not match",
                    VALID_ETHERNET_ADDRESS_LONG,
                    ethernet_address.toLong());
    }
    
    /**
     * Test of toString method, of class org.safehaus.uuid.EthernetAddress.
     */
    public void testToString()
    {
        // test making a few EthernetAddresss and check that the toString
        // gives back the same value in string form that was used to create it
        
        // test the null EthernetAddress
        EthernetAddress ethernet_address = new EthernetAddress(0L);
        assertEquals("null EthernetAddress string and toString did not match",
                    NULL_ETHERNET_ADDRESS_STRING.toLowerCase(),
                    ethernet_address.toString().toLowerCase());
        
        // test a non-null EthernetAddress
        ethernet_address =
            new EthernetAddress(VALID_ETHERNET_ADDRESS_BYTE_ARRAY);
        assertEquals(
            "EthernetAddress string and toString results did not match",
            MIXED_CASE_VALID_ETHERNET_ADDRESS_STRING.toLowerCase(),
            ethernet_address.toString().toLowerCase());
        
        // EthernetAddress implementation returns strings all lowercase.
        // Although relying on this behavior in code is not recommended,
        // here is a unit test which will break if this assumption
        // becomes bad. This will act as an early warning to anyone
        // who relies on this particular behavior.
        ethernet_address =
            new EthernetAddress(VALID_ETHERNET_ADDRESS_BYTE_ARRAY);
        assertFalse("mixed case EthernetAddress string and toString " +
                "matched (expected toString to be all lower case)",
            MIXED_CASE_VALID_ETHERNET_ADDRESS_STRING.equals(
                ethernet_address.toString()));
        assertEquals("mixed case string toLowerCase and " +
                "toString results did not match (expected toString to " +
                "be all lower case)",
            MIXED_CASE_VALID_ETHERNET_ADDRESS_STRING.toLowerCase(),
                ethernet_address.toString());
    }
    
    /**
     * Test of valueOf(byte[]) method,
     * of class org.safehaus.uuid.EthernetAddress.
     */
    public void testValueOfByteArray()
    {
        // lets test some error cases
        // first, passing null
        try
        {
            EthernetAddress ethernet_address =
                EthernetAddress.valueOf((byte[])null);
            // if we reached here we failed because we didn't get an exception
            fail("Expected exception not caught");
        }
        catch (NullPointerException ex)
        {
            // this is the success case so do nothing
        }
        catch (Exception ex)
        {
            fail("Caught unexpected exception: " + ex);
        }
        
        // now an array that is too small
        try
        {
            EthernetAddress ethernet_address =
                EthernetAddress.valueOf(
                    new byte[ETHERNET_ADDRESS_ARRAY_LENGTH - 1]);
            // if we reached here we failed because we didn't get an exception
            fail("Expected exception not caught");
        }
        catch (NumberFormatException ex)
        {
            // this is the success case so do nothing
        }
        catch (Exception ex)
        {
            fail("Caught unexpected exception: " + ex);
        }

        // now an array that is too big
        try
        {
            EthernetAddress ethernet_address =
                EthernetAddress.valueOf(
                    new byte[ETHERNET_ADDRESS_ARRAY_LENGTH + 1]);
            // if we reached here we failed because we didn't get an exception
            fail("Expected exception not caught");
        }
        catch (NumberFormatException ex)
        {
            // this is the success case so do nothing
        }
        catch (Exception ex)
        {
            fail("Caught unexpected exception: " + ex);
        }

        // test that creating a EthernetAddress from an zero'd array
        // gives us a null EthernetAddress (definition of null EthernetAddress)
        EthernetAddress ethernet_address =
            EthernetAddress.valueOf(new byte[ETHERNET_ADDRESS_ARRAY_LENGTH]);
        assertEquals(
            "EthernetAddress.valueOf did not create expected EthernetAddress",
            NULL_ETHERNET_ADDRESS_LONG,
            ethernet_address.toLong());
        
        // let's test creating an array from a good byte array
        ethernet_address =
            EthernetAddress.valueOf(VALID_ETHERNET_ADDRESS_BYTE_ARRAY);
        assertEquals(
            "EthernetAddress.valueOf did not create expected EthernetAddress",
            VALID_ETHERNET_ADDRESS_LONG,
            ethernet_address.toLong());
    }
    
    /**
     * Test of valueOf(int[]) method,
     * of class org.safehaus.uuid.EthernetAddress.
     */
    public void testValueOfIntArray()
    {
        // lets test some error cases
        // first, passing null
        try
        {
            EthernetAddress ethernet_address =
                EthernetAddress.valueOf((int[])null);
            // if we reached here we failed because we didn't get an exception
            fail("Expected exception not caught");
        }
        catch (NullPointerException ex)
        {
            // this is the success case so do nothing
        }
        catch (Exception ex)
        {
            fail("Caught unexpected exception: " + ex);
        }
        
        // now an array that is too small
        try
        {
            EthernetAddress ethernet_address =
                EthernetAddress.valueOf(
                    new int[ETHERNET_ADDRESS_ARRAY_LENGTH - 1]);
            // if we reached here we failed because we didn't get an exception
            fail("Expected exception not caught");
        }
        catch (NumberFormatException ex)
        {
            // this is the success case so do nothing
        }
        catch (Exception ex)
        {
            fail("Caught unexpected exception: " + ex);
        }

        // now an array that is too big
        try
        {
            EthernetAddress ethernet_address =
                EthernetAddress.valueOf(
                    new int[ETHERNET_ADDRESS_ARRAY_LENGTH + 1]);
            // if we reached here we failed because we didn't get an exception
            fail("Expected exception not caught");
        }
        catch (NumberFormatException ex)
        {
            // this is the success case so do nothing
        }
        catch (Exception ex)
        {
            fail("Caught unexpected exception: " + ex);
        }

        // let's test that creating a EthernetAddress from an zero'd array
        // gives a null EthernetAddress (definition of a null EthernetAddress)
        EthernetAddress ethernet_address =
            EthernetAddress.valueOf(new int[ETHERNET_ADDRESS_ARRAY_LENGTH]);
        assertEquals(
            "EthernetAddress.valueOf did not create expected EthernetAddress",
            NULL_ETHERNET_ADDRESS_LONG,
            ethernet_address.toLong());
        
        // let's test creating an array from a good int array
        ethernet_address =
            EthernetAddress.valueOf(VALID_ETHERNET_ADDRESS_INT_ARRAY);
        assertEquals(
            "EthernetAddress.valueOf did not create expected EthernetAddress",
            VALID_ETHERNET_ADDRESS_LONG,
            ethernet_address.toLong());
    }
    
    /**
     * Test of valueOf(long) method,
     * of class org.safehaus.uuid.EthernetAddress.
     */
    public void testValueOfLong()
    {
        // let's test that creating a EthernetAddress from an zero long
        // gives a null EthernetAddress (definition of a null EthernetAddress)
        EthernetAddress ethernet_address =
            EthernetAddress.valueOf(0x0000000000000000L);
        assertEquals(
            "EthernetAddress.valueOf did not create expected EthernetAddress",
            NULL_ETHERNET_ADDRESS_LONG,
            ethernet_address.toLong());
        
        // let's test creating an array from a good long
        ethernet_address =
            EthernetAddress.valueOf(VALID_ETHERNET_ADDRESS_LONG);
        assertEquals(
            "EthernetAddress.valueOf did not create expected EthernetAddress",
            VALID_ETHERNET_ADDRESS_LONG,
            ethernet_address.toLong());
    }
    
    /**
     * Test of valueOf(String) method,
     * of class org.safehaus.uuid.EthernetAddress.
     */
    public void testValueOfString()
    {
        // test a null string case
        try
        {
            EthernetAddress ethernet_address =
                EthernetAddress.valueOf((String)null);
            fail("Expected exception not caught");
        }
        catch (NullPointerException ex)
        {
            // this is the success case so do nothing
        }
        catch (Exception ex)
        {
            fail("Caught unexpected exception: " + ex);
        }            
        
        // test some failure cases for the string constructor
        badStringValueOfHelper(IMPROPER_NUM_COLONS_ETHERNET_ADDRESS_STRING_1);
        badStringValueOfHelper(IMPROPER_NUM_COLONS_ETHERNET_ADDRESS_STRING_2);
        badStringValueOfHelper(IMPROPER_NUM_COLONS_ETHERNET_ADDRESS_STRING_3);
        badStringValueOfHelper(IMPROPER_NUM_COLONS_ETHERNET_ADDRESS_STRING_4);
        badStringValueOfHelper(IMPROPER_NUM_COLONS_ETHERNET_ADDRESS_STRING_5);
        badStringValueOfHelper(IMPROPER_NUM_COLONS_ETHERNET_ADDRESS_STRING_6);
        badStringValueOfHelper(NON_HEX_ETHERNET_ADDRESS_STRING);
        badStringValueOfHelper(RANDOM_PROPER_LENGTH_STRING);
    
        // some valid strings for the various dropped digit cases
        goodStringValueOfHelper(FIRST_GROUP_ALL_NUM_ETHERNET_ADDRESS_STRING,
                        FIRST_GROUP_ALL_NUM_ETHERNET_ADDRESS_STRING);
        goodStringValueOfHelper(FIRST_GROUP_ONE_NUM_ETHERNET_ADDRESS_STRING,
                        FIRST_GROUP_ALL_NUM_ETHERNET_ADDRESS_STRING);
        goodStringValueOfHelper(FIRST_GROUP_NO_NUM_ETHERNET_ADDRESS_STRING,
                        FIRST_GROUP_ALL_NUM_ETHERNET_ADDRESS_STRING);
        goodStringValueOfHelper(SECOND_GROUP_ALL_NUM_ETHERNET_ADDRESS_STRING,
                        SECOND_GROUP_ALL_NUM_ETHERNET_ADDRESS_STRING);
        goodStringValueOfHelper(SECOND_GROUP_ONE_NUM_ETHERNET_ADDRESS_STRING,
                        SECOND_GROUP_ALL_NUM_ETHERNET_ADDRESS_STRING);
        goodStringValueOfHelper(SECOND_GROUP_NO_NUM_ETHERNET_ADDRESS_STRING,
                        SECOND_GROUP_ALL_NUM_ETHERNET_ADDRESS_STRING);
        goodStringValueOfHelper(THIRD_GROUP_ALL_NUM_ETHERNET_ADDRESS_STRING,
                        THIRD_GROUP_ALL_NUM_ETHERNET_ADDRESS_STRING);
        goodStringValueOfHelper(THIRD_GROUP_ONE_NUM_ETHERNET_ADDRESS_STRING,
                        THIRD_GROUP_ALL_NUM_ETHERNET_ADDRESS_STRING);
        goodStringValueOfHelper(THIRD_GROUP_NO_NUM_ETHERNET_ADDRESS_STRING,
                        THIRD_GROUP_ALL_NUM_ETHERNET_ADDRESS_STRING);
        goodStringValueOfHelper(FOURTH_GROUP_ALL_NUM_ETHERNET_ADDRESS_STRING,
                        FOURTH_GROUP_ALL_NUM_ETHERNET_ADDRESS_STRING);
        goodStringValueOfHelper(FOURTH_GROUP_ONE_NUM_ETHERNET_ADDRESS_STRING,
                        FOURTH_GROUP_ALL_NUM_ETHERNET_ADDRESS_STRING);
        goodStringValueOfHelper(FOURTH_GROUP_NO_NUM_ETHERNET_ADDRESS_STRING,
                        FOURTH_GROUP_ALL_NUM_ETHERNET_ADDRESS_STRING);
        goodStringValueOfHelper(FIFTH_GROUP_ALL_NUM_ETHERNET_ADDRESS_STRING,
                        FIFTH_GROUP_ALL_NUM_ETHERNET_ADDRESS_STRING);
        goodStringValueOfHelper(FIFTH_GROUP_ONE_NUM_ETHERNET_ADDRESS_STRING,
                        FIFTH_GROUP_ALL_NUM_ETHERNET_ADDRESS_STRING);
        goodStringValueOfHelper(FIFTH_GROUP_NO_NUM_ETHERNET_ADDRESS_STRING,
                        FIFTH_GROUP_ALL_NUM_ETHERNET_ADDRESS_STRING);
        goodStringValueOfHelper(SIXTH_GROUP_ALL_NUM_ETHERNET_ADDRESS_STRING,
                        SIXTH_GROUP_ALL_NUM_ETHERNET_ADDRESS_STRING);
        goodStringValueOfHelper(SIXTH_GROUP_ONE_NUM_ETHERNET_ADDRESS_STRING,
                        SIXTH_GROUP_ALL_NUM_ETHERNET_ADDRESS_STRING);
        goodStringValueOfHelper(SIXTH_GROUP_NO_NUM_ETHERNET_ADDRESS_STRING,
                        SIXTH_GROUP_ALL_NUM_ETHERNET_ADDRESS_STRING);
        goodStringValueOfHelper(MIXED_GROUP_ALL_NUM_ETHERNET_ADDRESS_STRING,
                        MIXED_GROUP_ALL_NUM_ETHERNET_ADDRESS_STRING);
        goodStringValueOfHelper(MIXED_GROUP_ONE_NUM_ETHERNET_ADDRESS_STRING,
                        MIXED_GROUP_ALL_NUM_ETHERNET_ADDRESS_STRING);
        goodStringValueOfHelper(MIXED_GROUP_NO_NUM_ETHERNET_ADDRESS_STRING,
                        MIXED_GROUP_ALL_NUM_ETHERNET_ADDRESS_STRING);
        
        // test the other good cases
        goodStringValueOfHelper(NULL_ETHERNET_ADDRESS_STRING,
                        NULL_ETHERNET_ADDRESS_STRING);
        goodStringValueOfHelper(UPPER_CASE_VALID_ETHERNET_ADDRESS_STRING,
                        UPPER_CASE_VALID_ETHERNET_ADDRESS_STRING);
        goodStringValueOfHelper(LOWER_CASE_VALID_ETHERNET_ADDRESS_STRING,
                        LOWER_CASE_VALID_ETHERNET_ADDRESS_STRING);
        goodStringValueOfHelper(MIXED_CASE_VALID_ETHERNET_ADDRESS_STRING,
                        MIXED_CASE_VALID_ETHERNET_ADDRESS_STRING);
    }
    
    /**************************************************************************
     * Begin private helper functions for use in tests
     *************************************************************************/
    private void badStringEthernetAddressConstructorHelper(
        String ethernetAddressString)
    {
        try
        {
            EthernetAddress ethernet_address =
                new EthernetAddress(ethernetAddressString);
            // if we reached here we failed because we didn't get an exception
            fail("Expected exception not caught");
        }
        catch (NumberFormatException ex)
        {
            // this is the success case so do nothing
        }
        catch (Exception ex)
        {
            fail("Caught unexpected exception: " + ex);
        }
    }
    
    private void goodStringEthernetAddressConstructorHelper(
        String ethernetAddressString,
        String expectedEthernetAddressString)
    {
        EthernetAddress ethernet_address = null;
        try
        {
            ethernet_address = new EthernetAddress(ethernetAddressString);
        }
        catch (Exception ex)
        {
            fail("Caught unexpected exception: " + ex);
        }
        
        assertEquals("EthernetAddresses were not equal",
                    expectedEthernetAddressString.toLowerCase(),
                    ethernet_address.toString().toLowerCase());
    }
    
    private void badStringValueOfHelper(String ethernetAddressString)
    {
        try
        {
            EthernetAddress ethernet_address =
                EthernetAddress.valueOf(ethernetAddressString);
            // if we reached here we failed because we didn't get an exception
            fail("Expected exception not caught");
        }
        catch (NumberFormatException ex)
        {
            // this is the success case so do nothing
        }
        catch (Exception ex)
        {
            fail("Caught unexpected exception: " + ex);
        }
    }
    
    private void goodStringValueOfHelper(String ethernetAddressString,
                                         String expectedEthernetAddressString)
    {
        EthernetAddress ethernet_address = null;
        try
        {
            ethernet_address = EthernetAddress.valueOf(ethernetAddressString);
        }
        catch (Exception ex)
        {
            fail("Caught unexpected exception: " + ex);
        }
        
        assertEquals("EthernetAddresses were not equal",
                    expectedEthernetAddressString.toLowerCase(),
                    ethernet_address.toString().toLowerCase());
    }
    
    private void assertEthernetAddressesMatchHelper(EthernetAddress expected,
                                                    EthernetAddress actual)
    {
        assertEquals("EthernetAddresses in long form did not match",
                    expected.toLong(),
                    actual.toLong());
        assertEquals("EthernetAddress equals did not match",
                    expected,
                    actual);
    }
    
    private void assertEthernetAddressEqualOrderHelper(
        EthernetAddress ethernetAddress1,
        EthernetAddress ethernetAddress2)
    {
        assertTrue(ethernetAddress1 + " did not test as equal to " +
                ethernetAddress2,
            0 == ethernetAddress1.compareTo(ethernetAddress2));
        assertTrue(ethernetAddress2 + " did not test as equal to " +
                ethernetAddress1,
            0 == ethernetAddress2.compareTo(ethernetAddress1));
    }
    
    private void assertEthernetAddressGreaterOrderHelper(
        EthernetAddress ethernetAddress1,
        EthernetAddress ethernetAddress2)
    {
        assertTrue(ethernetAddress1 + " did not test as larger then " +
                ethernetAddress2,
            0 < ethernetAddress1.compareTo(ethernetAddress2));
        assertTrue(ethernetAddress2 + " did not test as smaller then " +
                ethernetAddress1,
            0 > ethernetAddress2.compareTo(ethernetAddress1));
    }
    
    private void assertEthernetAddressArraysAreEqual(byte[] array1,
                                                    int array1_start,
                                                    byte[] array2,
                                                    int array2_start)
    {
        assertTrue("Array1 start offset is invalid",
                   0 <= array1_start);
        assertTrue("Array2 start offset is invalid",
                   0 <= array2_start);
        assertTrue("Array1 is not long enough for the given start offset",
            array1.length >= ETHERNET_ADDRESS_ARRAY_LENGTH + array1_start);
        assertTrue("Array2 is not long enough for the given start offset",
            array2.length >= ETHERNET_ADDRESS_ARRAY_LENGTH + array2_start);
        for (int i = 0; i < ETHERNET_ADDRESS_ARRAY_LENGTH; i++)
        {
            assertEquals("Array1 and Array2 did not match",
                    array1[i + array1_start],
                    array2[i + array2_start]);
        }        
    }
    
    private void assertEthernetAddressArraysAreNotEqual(byte[] array1,
                                                        int array1_start,
                                                        byte[] array2,
                                                        int array2_start)
    {
        assertTrue("Array1 start offset is invalid",
                   0 <= array1_start);
        assertTrue("Array2 start offset is invalid",
                   0 <= array2_start);
        assertTrue("Array1 is not long enough for the given start offset",
            array1.length >= ETHERNET_ADDRESS_ARRAY_LENGTH + array1_start);
        assertTrue("Array2 is not long enough for the given start offset",
            array2.length >= ETHERNET_ADDRESS_ARRAY_LENGTH + array2_start);
        for (int i = 0; i < ETHERNET_ADDRESS_ARRAY_LENGTH; i++)
        {
            // as soon as we find a non-matching byte,
            // we know we're not equal, so return
            if (array1[i + array1_start] != array2[i + array2_start])
            {
                return;
            }
        }
        fail("Array1 and Array2 matched");
    }
    /**************************************************************************
     * End private helper functions for use in tests
     *************************************************************************/
}
