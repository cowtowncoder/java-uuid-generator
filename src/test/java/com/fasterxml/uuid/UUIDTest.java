/* JUG Java Uuid Generator
 * UUIDTest.java
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

package com.fasterxml.uuid;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;
import junit.textui.TestRunner;

import java.util.Arrays;
import java.util.UUID;

import com.fasterxml.uuid.UUIDType;
import com.fasterxml.uuid.impl.UUIDUtil;

/**
 * This class tests UUID for correct functionality.
 *
 * @author Eric Bie
 */
public class UUIDTest extends TestCase
{
    final static UUID nullUUID = new UUID(0L, 0L);
    
    public UUIDTest(java.lang.String testName)
    {
        super(testName);
    }
    
    public static Test suite()
    {
        TestSuite suite = new TestSuite(UUIDTest.class);
        return suite;
    }
    
    public static void main(String[] args)
    {
        TestRunner.run(suite());
    }
    
    /**************************************************************************
     * Begin constructor tests
     *************************************************************************/
    /**
     * Test of UUID() constructor, of class com.fasterxml.uuid.UUID.
     */
    public void testDefaultUUIDConstructor()
    {
        // this test technically relies on the toString() and toByteArray()
        // methods of the UUID class working properly.
        // If it fails, that is fine... the test only needs to indicate
        // proper working behavior or that it needs to be fixed.
        UUID uuid = nullUUID;
        assertEquals("Default constructor did not create expected null UUID",
                    NULL_UUID_STRING,
                    uuid.toString());
        assertTrue("Expected array did not equal actual array",
            Arrays.equals(NULL_UUID_BYTE_ARRAY, UUIDUtil.asByteArray(uuid)));
    }
    
    /**
     * Test of UUID(byte[]) constructor, of class com.fasterxml.uuid.UUID.
     */
    public void testByteArrayUUIDConstructor()
    {
        // passing array that is too small
        try
        {
            /*UUID uuid =*/ UUIDUtil.uuid(new byte[UUID_BYTE_ARRAY_LENGTH - 1]);
            fail("Expected exception not caught");
        }
        catch (IllegalArgumentException ex) {
            // this is the success case so do nothing
        } catch (Exception ex) {
            fail("Caught unexpected exception: " + ex);
        }

        // test that creating a uuid from an zero'd array
        // gives us a null UUID (definition of a null UUID)
        UUID uuid = UUIDUtil.uuid(new byte[UUID_BYTE_ARRAY_LENGTH]);
        assertEquals("constructor did not create expected null UUID",
                    NULL_UUID_STRING,
                    uuid.toString());
        assertTrue("Expected array did not equal actual array",
            Arrays.equals(NULL_UUID_BYTE_ARRAY, UUIDUtil.asByteArray(uuid)));
        
        // test creating an array from a good byte array
        uuid = UUIDUtil.uuid(VALID_UUID_BYTE_ARRAY);
        assertEquals("constructor did not create expected UUID",
                    MIXED_CASE_VALID_UUID_STRING.toLowerCase(),
                    uuid.toString().toLowerCase());

        // test creating an array from a good byte array with extra data on end
        uuid = UUIDUtil.uuid(VALID_UUID_BYTE_ARRAY_WITH_EXTRA_END);
        assertEquals("constructor did not create expected UUID",
                    MIXED_CASE_VALID_UUID_STRING.toLowerCase(),
                    uuid.toString().toLowerCase());
    }
    
    /**
     * Test of UUID(String) constructor, of class com.fasterxml.uuid.UUID.
     */
    public void testStringUUIDConstructor()
    {
        // test a null string case
        try
        {
            /*UUID uuid =*/ UUIDUtil.uuid((String)null);
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
        badStringUUIDConstructorHelper(IMPROPER_NUM_DASHES_UUID_STRING_1);
        badStringUUIDConstructorHelper(IMPROPER_NUM_DASHES_UUID_STRING_2);
        badStringUUIDConstructorHelper(IMPROPER_NUM_DASHES_UUID_STRING_3);
        badStringUUIDConstructorHelper(IMPROPER_NUM_DASHES_UUID_STRING_4);
        badStringUUIDConstructorHelper(IMPROPER_NUM_DASHES_UUID_STRING_5);
        badStringUUIDConstructorHelper(IMPROPER_NUM_DASHES_UUID_STRING_6);
        badStringUUIDConstructorHelper(NON_HEX_UUID_STRING);
        badStringUUIDConstructorHelper(RANDOM_PROPER_LENGTH_STRING);
        
        // test some good cases
        goodStringUUIDConstructorHelper(NULL_UUID_STRING);
        goodStringUUIDConstructorHelper(UPPER_CASE_VALID_UUID_STRING);
        goodStringUUIDConstructorHelper(LOWER_CASE_VALID_UUID_STRING);
        goodStringUUIDConstructorHelper(MIXED_CASE_VALID_UUID_STRING);
    }
    /**************************************************************************
     * End constructor tests
     *************************************************************************/
    
    /**
     * Test of asByteArray method, of class com.fasterxml.uuid.UUID.
     */
    public void testAsByteArray()
    {
        // we'll test making a couple UUIDs and then check that the asByteArray
        // gives back the same value in byte form that we used to create it
        
        // first we'll test the null uuid
        UUID uuid = nullUUID;
        assertEquals("Expected length of returned array wrong",
                    UUID_BYTE_ARRAY_LENGTH,
                    UUIDUtil.asByteArray(uuid).length);
        assertTrue("Expected array did not equal actual array",
            Arrays.equals(NULL_UUID_BYTE_ARRAY, UUIDUtil.asByteArray(uuid)));
        
        // now test a non-null uuid
        uuid = UUIDUtil.uuid(MIXED_CASE_VALID_UUID_STRING);
        assertEquals("Expected length of returned array wrong",
                    UUID_BYTE_ARRAY_LENGTH,
                    UUIDUtil.asByteArray(uuid).length);
        assertTrue("Expected array did not equal actual array",
            Arrays.equals(VALID_UUID_BYTE_ARRAY, UUIDUtil.asByteArray(uuid)));
        
        // let's make sure that changing the returned array doesn't mess with
        // the wrapped UUID's internals
        uuid = UUIDUtil.uuid(MIXED_CASE_VALID_UUID_STRING);
        assertEquals("Expected length of returned array wrong",
                    UUID_BYTE_ARRAY_LENGTH,
                    UUIDUtil.asByteArray(uuid).length);
        assertTrue("Expected array did not equal actual array",
            Arrays.equals(VALID_UUID_BYTE_ARRAY, UUIDUtil.asByteArray(uuid)));

        byte[] test_byte_array = UUIDUtil.asByteArray(uuid);
        // now stir it up a bit and then check that the original UUID was
        // not changed in the process. The easiest stir is to sort it ;)
        Arrays.sort(test_byte_array);
        assertFalse("Expected array was equal other array",
            Arrays.equals(VALID_UUID_BYTE_ARRAY, test_byte_array));
        assertFalse("Expected array was equal other array",
            Arrays.equals(UUIDUtil.asByteArray(uuid), test_byte_array));
        assertTrue("Expected array did not equal actual array",
            Arrays.equals(VALID_UUID_BYTE_ARRAY, UUIDUtil.asByteArray(uuid)));
    }
    
    /**
     * Test of compareTo method, of class com.fasterxml.uuid.UUID.
     */
    public void testCompareTo()
    {
        // first, let's make sure calling compareTo with null
        // throws the appropriate NullPointerException 
        try {
            NULL_UUID.compareTo(null);
            fail("Expected exception not thrown");
        } catch (NullPointerException ex) {
            // good, we caught the expected exception, so we passed
        } catch (Exception ex) {
            fail("Caught an unexpected exception: " + ex);
        }
        
        // now we'll test some simple base cases
        // 2 null uuids always compare to 0
        assertUUIDEqualOrderHelper(NULL_UUID, nullUUID);
        
        // 2 of the same value UUIDs are always 0
        assertUUIDEqualOrderHelper(TIME3_MAC1_UUID, UUIDUtil.uuid(TIME3_MAC1_UUID.toString()));
        
        // the 'null UUID' always comes first in the ordering
        assertUUIDGreaterOrderHelper(TIME3_MAC1_UUID, NULL_UUID);
        
        // a UUID with a greater time is always comes after a lower time uuid
        // given the same MAC address
        assertUUIDGreaterOrderHelper(TIME3_MAC1_UUID, TIME1_MAC1_UUID);
        
        // a UUID with a greater time and a different MAC will always sort
        // with the greater time coming later
        assertUUIDGreaterOrderHelper(TIME3_MAC1_UUID, TIME1_MAC2_UUID);
        
        // a UUID with the same time stamp and different MAC will always sort
        // with the 'numerically' greater MAC coming later
        assertUUIDGreaterOrderHelper(TIME1_MAC2_UUID, TIME1_MAC1_UUID);
        
        // now we will test a bigger case of the compareTo functionality
        // of the UUID class
        // easiest way to do this is to create an array of UUIDs and sort it
        // then test that this array is in the expected order
        
        // first we'll try a MAC address homogeneous sort
        // before sort, the array contains (in psudo-random order)
        // 10 UUIDs of this distribution:
        // 2 - null uuid
        // 2 - time1_mac1
        // 1 - time2_mac1
        // 2 - time3_mac1
        // 2 - time4_mac1
        // 1 - time5_mac1
        UUID test_uuid_array[] = new UUID[10];
        test_uuid_array[0] = TIME3_MAC1_UUID;
        test_uuid_array[1] = TIME4_MAC1_UUID;
        test_uuid_array[2] = TIME1_MAC1_UUID;
        test_uuid_array[3] = NULL_UUID;
        test_uuid_array[4] = TIME3_MAC1_UUID;
        test_uuid_array[5] = TIME5_MAC1_UUID;
        test_uuid_array[6] = TIME2_MAC1_UUID;
        test_uuid_array[7] = TIME1_MAC1_UUID;
        test_uuid_array[8] = NULL_UUID;
        test_uuid_array[9] = TIME4_MAC1_UUID;
        
        Arrays.sort(test_uuid_array, new UUIDComparator());
        // now we should be able to see that the array is in order
        assertUUIDsMatchHelper(NULL_UUID, test_uuid_array[0]);
        assertUUIDsMatchHelper(NULL_UUID, test_uuid_array[1]);
        assertUUIDsMatchHelper(TIME1_MAC1_UUID, test_uuid_array[2]);
        assertUUIDsMatchHelper(TIME1_MAC1_UUID, test_uuid_array[3]);
        assertUUIDsMatchHelper(TIME2_MAC1_UUID, test_uuid_array[4]);
        assertUUIDsMatchHelper(TIME3_MAC1_UUID, test_uuid_array[5]);
        assertUUIDsMatchHelper(TIME3_MAC1_UUID, test_uuid_array[6]);
        assertUUIDsMatchHelper(TIME4_MAC1_UUID, test_uuid_array[7]);
        assertUUIDsMatchHelper(TIME4_MAC1_UUID, test_uuid_array[8]);
        assertUUIDsMatchHelper(TIME5_MAC1_UUID, test_uuid_array[9]);
        
        // allow array to be GC'd (and make sure we don't somehow use the wrong
        // array below)
        test_uuid_array = null;
        
        // now lets try a MAC address heterogeneous case
        // before sort, the array contains (in psudo-random order)
        // 15 UUIDs of this distribution:
        // 1 - null uuid
        // 2 - time1_mac1
        // 1 - time1_mac2
        // 1 - time2_mac1
        // 2 - time2_mac2
        // 2 - time3_mac1
        // 2 - time3_mac2
        // 1 - time4_mac1
        // 1 - time4_mac2
        // 1 - time5_mac1
        // 1 - time5_mac2
        test_uuid_array = new UUID[15];
        test_uuid_array[0] = TIME3_MAC1_UUID;
        test_uuid_array[1] = TIME4_MAC1_UUID;
        test_uuid_array[2] = TIME1_MAC1_UUID;
        test_uuid_array[3] = TIME3_MAC2_UUID;
        test_uuid_array[4] = TIME2_MAC2_UUID;
        test_uuid_array[5] = TIME3_MAC2_UUID;
        test_uuid_array[6] = TIME1_MAC1_UUID;
        test_uuid_array[7] = NULL_UUID;
        test_uuid_array[8] = TIME5_MAC1_UUID;
        test_uuid_array[9] = TIME2_MAC2_UUID;
        test_uuid_array[10] = TIME3_MAC1_UUID;
        test_uuid_array[11] = TIME4_MAC2_UUID;
        test_uuid_array[12] = TIME1_MAC2_UUID;
        test_uuid_array[13] = TIME5_MAC2_UUID;
        test_uuid_array[14] = TIME2_MAC1_UUID;
        
        Arrays.sort(test_uuid_array, new UUIDComparator());
        // now we should be able to see that the array is in order
        assertUUIDsMatchHelper(NULL_UUID, test_uuid_array[0]);
        assertUUIDsMatchHelper(TIME1_MAC1_UUID, test_uuid_array[1]);
        assertUUIDsMatchHelper(TIME1_MAC1_UUID, test_uuid_array[2]);
        assertUUIDsMatchHelper(TIME1_MAC2_UUID, test_uuid_array[3]);
        assertUUIDsMatchHelper(TIME2_MAC1_UUID, test_uuid_array[4]);
        assertUUIDsMatchHelper(TIME2_MAC2_UUID, test_uuid_array[5]);
        assertUUIDsMatchHelper(TIME2_MAC2_UUID, test_uuid_array[6]);
        assertUUIDsMatchHelper(TIME3_MAC1_UUID, test_uuid_array[7]);
        assertUUIDsMatchHelper(TIME3_MAC1_UUID, test_uuid_array[8]);
        assertUUIDsMatchHelper(TIME3_MAC2_UUID, test_uuid_array[9]);
        assertUUIDsMatchHelper(TIME3_MAC2_UUID, test_uuid_array[10]);
        assertUUIDsMatchHelper(TIME4_MAC1_UUID, test_uuid_array[11]);
        assertUUIDsMatchHelper(TIME4_MAC2_UUID, test_uuid_array[12]);
        assertUUIDsMatchHelper(TIME5_MAC1_UUID, test_uuid_array[13]);
        assertUUIDsMatchHelper(TIME5_MAC2_UUID, test_uuid_array[14]);
    }
    
    /**
     * Test of equals method, of class com.fasterxml.uuid.UUID.
     */
    public void testEquals()
    {
        // test passing null to equals returns false
        // (as specified in the JDK docs for Object)
        UUID x = UUIDUtil.uuid(VALID_UUID_BYTE_ARRAY);
        assertFalse("equals(null) didn't return false", x.equals((Object)null));
        
        // test that passing an object which is not a UUID returns false
        assertFalse("x.equals(non_UUID_object) didn't return false", x.equals(new Object()));
        
        // test a case where two UUIDs are definitly not equal
        UUID w = UUIDUtil.uuid(ANOTHER_VALID_UUID_BYTE_ARRAY);
        assertFalse("x.equals(w) didn't return false", x.equals(w));

        // test refelexivity
        assertTrue("x.equals(x) didn't return true", x.equals(x));
        
        // test symmetry
        UUID y = UUIDUtil.uuid(VALID_UUID_BYTE_ARRAY);
        assertTrue("y.equals(x) didn't return true", y.equals(x));
        assertTrue("x.equals(y) didn't return true", x.equals(y));
        
        // now we'll test transitivity
        UUID z = UUIDUtil.uuid(VALID_UUID_BYTE_ARRAY);
        assertTrue("x.equals(y) didn't return true", x.equals(y));
        assertTrue("y.equals(z) didn't return true", y.equals(z));
        assertTrue("x.equals(z) didn't return true", x.equals(z));
        
        // test consistancy (this test is just calling equals multiple times)
        assertTrue("x.equals(y) didn't return true", x.equals(y));
        assertTrue("x.equals(y) didn't return true", x.equals(y));
        assertTrue("x.equals(y) didn't return true", x.equals(y));
    }
    
    /**
     * Test of getNullUUID method, of class com.fasterxml.uuid.UUID.
     */
    public void testGetNullUUID()
    {
        UUID uuid = nullUUID;
        assertEquals("getNullUUID did not create expected null UUID",
                    NULL_UUID_STRING,
                    uuid.toString());
        assertTrue("Expected array did not equal actual array",
            Arrays.equals(NULL_UUID_BYTE_ARRAY, UUIDUtil.asByteArray(uuid)));
        
        // also, validate that getNullUUID is getting the same null each time
        UUID uuid2 = nullUUID;
        assertEquals("getNullUUID did not create expected null UUID",
                    NULL_UUID_STRING,
                    uuid2.toString());
        assertTrue("Expected array did not equal actual array",
            Arrays.equals(NULL_UUID_BYTE_ARRAY, UUIDUtil.asByteArray(uuid2)));
        assertTrue("two returned null UUIDs were not the sam object instance",
                    uuid == uuid2);
    }
    
    /**
     * Test of getType method, of class com.fasterxml.uuid.UUID.
     */
    public void testGetType()
    {
        // here we will test that UUID's constructed with the right type
        // have the correct type returned from getType
        
        // test creating a null UUID
        UUID uuid = nullUUID;
        assertTrue("Expected array did not equal actual array",
            Arrays.equals(NULL_UUID_BYTE_ARRAY, UUIDUtil.asByteArray(uuid)));
        assertEquals("Expected type was not returned",
                    UUIDUtil.typeOf(nullUUID),
            		UUIDUtil.typeOf(uuid));
        
        // test Random UUID in this case
        uuid = UUIDUtil.uuid(VALID_UUID_BYTE_ARRAY);
        assertTrue("Expected array did not equal actual array",
            Arrays.equals(VALID_UUID_BYTE_ARRAY, UUIDUtil.asByteArray(uuid)));
        assertEquals("Expected type was not returned",
        		UUIDType.RANDOM_BASED,
        		UUIDUtil.typeOf(uuid));
        
        // test time based UUID in this case
        uuid = UUIDUtil.uuid(UUIDUtil.asByteArray(TIME1_MAC1_UUID));
        assertEquals("constructor did not create expected UUID",
                    TIME1_MAC1_UUID.toString().toLowerCase(),
                    uuid.toString().toLowerCase());
        assertEquals("Expected type was not returned",
        		UUIDType.TIME_BASED,
            		UUIDUtil.typeOf(uuid));
        
        // test name based UUID in this case
        uuid = UUIDUtil.uuid(NAME_BASED_UUID_STRING);
        assertTrue("Expected array did not equal actual array",
            Arrays.equals(NAME_BASED_UUID_BYTE_ARRAY, UUIDUtil.asByteArray(uuid)));
        assertEquals("Expected type was not returned",
        		UUIDType.NAME_BASED_MD5,
            		UUIDUtil.typeOf(uuid));
        
        // test DCE based UUID in this case
        uuid = UUIDUtil.uuid(DCE_BASED_UUID_BYTE_ARRAY);
        assertTrue("Expected array did not equal actual array",
            Arrays.equals(DCE_BASED_UUID_BYTE_ARRAY, UUIDUtil.asByteArray(uuid)));
        assertEquals("Expected type was not returned",
        		UUIDType.DCE,
            		UUIDUtil.typeOf(uuid));
    }
    
    /**
     * Test of hashCode method, of class com.fasterxml.uuid.UUID.
     */
    public void testHashCode()
    {
        // as lifted from the JDK Object JavaDocs:
        // Whenever it is invoked on the same object more than once
        // during an execution of a Java application, the hashCode 
        // method must consistently return the same integer, provided
        // no information used in equals comparisons on the object is
        // modified. This integer need not remain consistent from one
        // execution of an application to another execution of the
        // same application
        UUID x = UUIDUtil.uuid(VALID_UUID_BYTE_ARRAY);
        assertTrue("x.equals(x) didn't return true",
                    x.equals(x));
        assertEquals("x.hashCode() didn't equal x.hashCode()",
                    x.hashCode(),
                    x.hashCode());
        assertEquals("x.hashCode() didn't equal x.hashCode()",
                    x.hashCode(),
                    x.hashCode());
        
        // as lifted from the JDK Object JavaDocs:
        // If two objects are equal according to the equals(Object) method,
        // then calling the hashCode method on each of the two objects
        // must produce the same integer result
        UUID y = UUIDUtil.uuid(VALID_UUID_BYTE_ARRAY);
        assertFalse("x == y didn't return false",
                    x == y);
        assertTrue("x.equals(y) didn't return true",
                    x.equals(y));
        assertEquals("x.hashCode() didn't equal y.hashCode()",
                    x.hashCode(),
                    y.hashCode());
        
        // it is not REQUIRED that hashCode return different ints for different
        // objects where x.equals(z) is not true.
        // So, there is no test for that here
    }
    
    /**
     * Test of isNullUUID method, of class com.fasterxml.uuid.UUID.
     */
    public void testIsNullUUID()
    {
        // this test will test isNullUUID using the five main ways you could
        // create a null UUID and test a case where it should NOT be true
        
        // test using default constructor
        UUID uuid = nullUUID;
        assertIsNullUUID(uuid);
        
        // test by string creation using null uuid represented in string form
        uuid = UUIDUtil.uuid(NULL_UUID_STRING);
        assertIsNullUUID(uuid);
        
        // test by byte[] creation using null uuid represented in byte[] form
        uuid = UUIDUtil.uuid(NULL_UUID_BYTE_ARRAY);
        assertIsNullUUID(uuid);
        
        // test by byte[] creation using null uuid represented in byte[] form
        // starting at an offset
        byte[] null_uuid_array = new byte[20];
        Arrays.fill(null_uuid_array, 0, 3, (byte)'x');
        uuid = UUIDUtil.uuid(null_uuid_array, 4);
        assertIsNullUUID(uuid);
        
        // test a not null case
        uuid = UUIDUtil.uuid(VALID_UUID_BYTE_ARRAY);
        assertFalse(0L == uuid.getMostSignificantBits());
        assertFalse(0L == uuid.getLeastSignificantBits());
    }

    private void assertIsNullUUID(UUID uuid) {
    	assertEquals(0L, uuid.getMostSignificantBits());
    	assertEquals(0L, uuid.getLeastSignificantBits());
    }

    /**
     * Test of toByteArray() method, of class com.fasterxml.uuid.UUID.
     */
    public void testToByteArray()
    {
        // we'll test making a couple UUIDs and then check that the toByteArray
        // gives back the same value in byte form that we used to create it
        
        // first we'll test the null uuid
        UUID uuid = nullUUID;
        assertEquals("Expected length of returned array wrong",
                    UUID_BYTE_ARRAY_LENGTH,
                    UUIDUtil.asByteArray(uuid).length);
        assertTrue("Expected array did not equal actual array",
            Arrays.equals(NULL_UUID_BYTE_ARRAY, UUIDUtil.asByteArray(uuid)));
        
        // now test a non-null uuid
        uuid = UUIDUtil.uuid(MIXED_CASE_VALID_UUID_STRING);
        assertEquals("Expected length of returned array wrong",
                    UUID_BYTE_ARRAY_LENGTH,
                    UUIDUtil.asByteArray(uuid).length);
        assertTrue("Expected array did not equal actual array",
            Arrays.equals(VALID_UUID_BYTE_ARRAY, UUIDUtil.asByteArray(uuid)));
        
        // let's make sure that changing the returned array doesn't mess with
        // the wrapped UUID's internals
        uuid = UUIDUtil.uuid(MIXED_CASE_VALID_UUID_STRING);
        assertEquals("Expected length of returned array wrong",
                    UUID_BYTE_ARRAY_LENGTH,
                    UUIDUtil.asByteArray(uuid).length);
        assertTrue("Expected array did not equal actual array",
            Arrays.equals(VALID_UUID_BYTE_ARRAY, UUIDUtil.asByteArray(uuid)));
        byte[] test_byte_array = UUIDUtil.asByteArray(uuid);
        // now stir it up a bit and then check that the original UUID was
        // not changed in the process. The easiest stir is to sort it ;)
        Arrays.sort(test_byte_array);
        assertFalse("Expected array was equal other array",
            Arrays.equals(VALID_UUID_BYTE_ARRAY, test_byte_array));
        assertFalse("Expected array was equal other array",
            Arrays.equals(UUIDUtil.asByteArray(uuid), test_byte_array));
        assertTrue("Expected array did not equal actual array",
            Arrays.equals(VALID_UUID_BYTE_ARRAY, UUIDUtil.asByteArray(uuid)));
    }

    /**
     * Test of toByteArray(byte[]) method, of class com.fasterxml.uuid.UUID.
     */
    public void testToByteArrayDest()
    {
        // constant for use in this test
        final int EXTRA_DATA_LENGTH = 9;
        
        // lets test some error cases
        // first, passing null
        try
        {
            UUID test_uuid = nullUUID;
            UUIDUtil.toByteArray(test_uuid, (byte[])null);
            
            // if we reached here we failed because we didn't get an exception
            fail("Expected exception not caught");
        }
        catch (IllegalArgumentException ex)
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
            UUID test_uuid = nullUUID;
            byte[] uuid_array = new byte[UUID_BYTE_ARRAY_LENGTH - 1];
            UUIDUtil.toByteArray(test_uuid, uuid_array);
            
            // if we reached here we failed because we didn't get an exception
            fail("Expected exception not caught");
        }
        catch (IllegalArgumentException ex)
        {
            // this is the success case so do nothing
        }
        catch (Exception ex)
        {
            fail("Caught unexpected exception: " + ex);
        }

        // we'll test making a couple UUIDs and then check that the toByteArray
        // gives back the same value in byte form that we used to create it
        
        // here we'll test the null uuid
        UUID test_uuid = nullUUID;
        byte[] test_array = new byte[UUID_BYTE_ARRAY_LENGTH];
        UUIDUtil.toByteArray(test_uuid, test_array);
        assertTrue("Expected array did not equal actual array",
            Arrays.equals(NULL_UUID_BYTE_ARRAY, test_array));
        
        // now test a non-null uuid
        test_uuid = UUIDUtil.uuid(MIXED_CASE_VALID_UUID_STRING);
        UUIDUtil.toByteArray(test_uuid, test_array);
        assertTrue("Expected array did not equal actual array",
            Arrays.equals(VALID_UUID_BYTE_ARRAY, test_array));
        
        // now test a null uuid case with extra data in the array
        test_uuid = nullUUID;
        test_array = new byte[UUID_BYTE_ARRAY_LENGTH + EXTRA_DATA_LENGTH];
        Arrays.fill(test_array, (byte)'x');
        UUIDUtil.toByteArray(test_uuid, test_array);
        for (int i = 0; i < UUID_BYTE_ARRAY_LENGTH; ++i)
        {
            assertEquals("Expected array values did not match",
                NULL_UUID_BYTE_ARRAY[i],
                test_array[i]);
        }
        for (int i = 0; i < EXTRA_DATA_LENGTH; i++)
        {
            assertEquals("Expected array fill value changed",
                        (byte)'x',
                        test_array[i + UUID_BYTE_ARRAY_LENGTH]);
        }
        
        // now test a good uuid case with extra data in the array
        test_uuid = UUIDUtil.uuid(MIXED_CASE_VALID_UUID_STRING);
        test_array = new byte[UUID_BYTE_ARRAY_LENGTH + EXTRA_DATA_LENGTH];
        Arrays.fill(test_array, (byte)'x');
        UUIDUtil.toByteArray(test_uuid, test_array);
        for (int i = 0; i < UUID_BYTE_ARRAY_LENGTH; ++i)
        {
            assertEquals("Expected array values did not match",
                VALID_UUID_BYTE_ARRAY[i],
                test_array[i]);
        }
        for (int i = 0; i < EXTRA_DATA_LENGTH; i++)
        {
            assertEquals("Expected array fill value changed",
                        (byte)'x',
                        test_array[i + UUID_BYTE_ARRAY_LENGTH]);
        }
    }
    
    /**
     * Test of toByteArray(byte[], int) method,
     * of class com.fasterxml.uuid.UUID.
     */
    public void testToByteArrayDestOffset()
    {
        // constant value for use in this test
        final int EXTRA_DATA_LENGTH = 9;
        
        // now an array that is too small
        try
        {
            UUID test_uuid = nullUUID;
            byte[] uuid_array = new byte[UUID_BYTE_ARRAY_LENGTH - 1];
            UUIDUtil.toByteArray(test_uuid, uuid_array, 0);            
            
            // if we reached here we failed because we didn't get an exception
            fail("Expected exception not caught");
        } catch (IllegalArgumentException ex) {
            // this is the success case so do nothing
        } catch (Exception ex) {
            fail("Caught unexpected exception: " + ex);
        }

        // now an index that is negative
        try {
            UUID test_uuid = nullUUID;
            byte[] uuid_array = new byte[UUID_BYTE_ARRAY_LENGTH];
            UUIDUtil.toByteArray(test_uuid, uuid_array, -1);
            
            // if we reached here we failed because we didn't get an exception
            fail("Expected exception not caught");
        } catch (IllegalArgumentException ex) {
            // this is the success case so do nothing
        } catch (Exception ex) {
            fail("Caught unexpected exception: " + ex);
        }
        
        // now an index that is too big
        try {
            UUID test_uuid = nullUUID;
            byte[] uuid_array = new byte[UUID_BYTE_ARRAY_LENGTH];
            UUIDUtil.toByteArray(test_uuid, uuid_array, UUID_BYTE_ARRAY_LENGTH);
            
            // if we reached here we failed because we didn't get an exception
            fail("Expected exception not caught");
        } catch (IllegalArgumentException ex) {
            // this is the success case so do nothing
        } catch (Exception ex) {
            fail("Caught unexpected exception: " + ex);
        }
        
        // now an index that is in the array,
        // but without enough bytes to read UUID_BYTE_ARRAY_LENGTH
        try {
            UUID test_uuid = nullUUID;
            byte[] uuid_array = new byte[UUID_BYTE_ARRAY_LENGTH];
            UUIDUtil.toByteArray(test_uuid, uuid_array, 1);
            
            // if we reached here we failed because we didn't get an exception
            fail("Expected exception not caught");
        } catch (IllegalArgumentException ex) {
            // this is the success case so do nothing
        } catch (Exception ex) {
            fail("Caught unexpected exception: " + ex);
        }
        
        // we'll test making a couple UUIDs and then check that the toByteArray
        // gives back the same value in byte form that we used to create it
        
        // here we'll test the null uuid at offset 0
        UUID test_uuid = nullUUID;
        byte[] test_array = new byte[UUID_BYTE_ARRAY_LENGTH];
        UUIDUtil.toByteArray(test_uuid, test_array, 0);
        assertTrue("Expected array did not equal actual array",
            Arrays.equals(NULL_UUID_BYTE_ARRAY, test_array));
        
        // now test a non-null uuid
        test_uuid = UUIDUtil.uuid(MIXED_CASE_VALID_UUID_STRING);
        UUIDUtil.toByteArray(test_uuid, test_array);
        assertTrue("Expected array did not equal actual array",
            Arrays.equals(VALID_UUID_BYTE_ARRAY, test_array));
        
        // now test a null uuid case with extra data in the array
        test_uuid = nullUUID;
        test_array = new byte[UUID_BYTE_ARRAY_LENGTH + EXTRA_DATA_LENGTH];
        Arrays.fill(test_array, (byte)'x');
        UUIDUtil.toByteArray(test_uuid, test_array, 0);
        for (int i = 0; i < UUID_BYTE_ARRAY_LENGTH; ++i) {
            assertEquals("Expected array values did not match",
                NULL_UUID_BYTE_ARRAY[i],
                test_array[i]);
        }
        for (int i = 0; i < EXTRA_DATA_LENGTH; i++) {
            assertEquals("Expected array fill value changed",
                        (byte)'x',
                        test_array[i + UUID_BYTE_ARRAY_LENGTH]);
        }
        
        // now test a null uuid case with extra data in the array
        test_uuid = nullUUID;
        test_array = new byte[UUID_BYTE_ARRAY_LENGTH + EXTRA_DATA_LENGTH];
        Arrays.fill(test_array, (byte)'x');
        UUIDUtil.toByteArray(test_uuid, test_array, EXTRA_DATA_LENGTH/2);
        // first check the data (in the middle of the array)
        for (int i = 0; i < UUID_BYTE_ARRAY_LENGTH; ++i) {
            assertEquals("Expected array values did not match (offset "+i+")",
                NULL_UUID_BYTE_ARRAY[i],
                test_array[i + EXTRA_DATA_LENGTH/2]);
        }
        // and now check that the surrounding bytes were not changed
        for (int i = 0; i < EXTRA_DATA_LENGTH/2; ++i) {
            assertEquals("Expected array fill value changed",
                (byte)'x',
                test_array[i]);
            assertEquals("Expected array fill value changed",
                (byte)'x',
                test_array[i + UUID_BYTE_ARRAY_LENGTH + EXTRA_DATA_LENGTH/2]);
        }
        
        // now test a good uuid case with extra data in the array
        test_uuid = UUIDUtil.uuid(MIXED_CASE_VALID_UUID_STRING);
        test_array = new byte[UUID_BYTE_ARRAY_LENGTH + EXTRA_DATA_LENGTH];
        Arrays.fill(test_array, (byte)'x');
        UUIDUtil.toByteArray(test_uuid, test_array, 0);
        for (int i = 0; i < UUID_BYTE_ARRAY_LENGTH; ++i) {
            assertEquals("Expected array values did not match",
                VALID_UUID_BYTE_ARRAY[i],
                test_array[i]);
        }
        for (int i = 0; i < EXTRA_DATA_LENGTH; i++) {
            assertEquals("Expected array fill value changed",
                (byte)'x',
                test_array[i + UUID_BYTE_ARRAY_LENGTH]);
        }

        // now test a good uuid case with extra data in the array
        // to make sure we aren't blowing the bounds of the buffer
        test_uuid = UUIDUtil.uuid(MIXED_CASE_VALID_UUID_STRING);
        test_array = new byte[UUID_BYTE_ARRAY_LENGTH + EXTRA_DATA_LENGTH];
        Arrays.fill(test_array, (byte)'x');
        UUIDUtil.toByteArray(test_uuid, test_array, EXTRA_DATA_LENGTH/2);
        // first check the data (in the middle of the array)
        for (int i = 0; i < UUID_BYTE_ARRAY_LENGTH; ++i) {
            assertEquals("Expected array values did not match",
                VALID_UUID_BYTE_ARRAY[i],
                test_array[i + EXTRA_DATA_LENGTH/2]);
        }
        // and now check that the surrounding bytes were not changed
        for (int i = 0; i < EXTRA_DATA_LENGTH/2; ++i) {
            assertEquals("Expected array fill value changed",
                (byte)'x',
                test_array[i]);
            assertEquals("Expected array fill value changed",
                (byte)'x',
                test_array[i + UUID_BYTE_ARRAY_LENGTH + EXTRA_DATA_LENGTH/2]);
        }
    }
    
    /**
     * Test of toString method, of class com.fasterxml.uuid.UUID.
     */
    public void testToString()
    {
        // test making a couple UUIDs and then check that the toString
        // gives back the same value in string form that was used to create it
        
        // test the null uuid
        UUID uuid = nullUUID;
        assertEquals("null uuid string and toString did not match",
                    NULL_UUID_STRING.toLowerCase(),
                    uuid.toString().toLowerCase());
        
        // test a non-null uuid
        uuid = UUIDUtil.uuid(VALID_UUID_BYTE_ARRAY);
        assertEquals("uuid string and toString results did not match",
                    MIXED_CASE_VALID_UUID_STRING.toLowerCase(),
                    uuid.toString().toLowerCase());
        
        // The current UUID implementation returns strings all lowercase.
        // Although relying on this behavior in code is not recommended,
        // here is a unit test which will break if this assumption
        // becomes bad. This will act as an early warning to anyone
        // who relies on this particular behavior.
        uuid = UUIDUtil.uuid(VALID_UUID_BYTE_ARRAY);
        assertFalse("mixed case uuid string and toString " +
                "matched (expected toString to be all lower case)",
            MIXED_CASE_VALID_UUID_STRING.equals(uuid.toString()));
        assertEquals("mixed case string toLowerCase and " +
                "toString results did not match (expected toString to " +
                "be all lower case)",
            MIXED_CASE_VALID_UUID_STRING.toLowerCase(),
            uuid.toString());
    }

    /**
     * Test of valueOf(String) method, of class com.fasterxml.uuid.UUID.
     */
    public void testValueOfString()
    {
        // test some failure cases for the string constructor
        badStringValueOfHelper(IMPROPER_NUM_DASHES_UUID_STRING_1);
        badStringValueOfHelper(IMPROPER_NUM_DASHES_UUID_STRING_2);
        badStringValueOfHelper(IMPROPER_NUM_DASHES_UUID_STRING_3);
        badStringValueOfHelper(IMPROPER_NUM_DASHES_UUID_STRING_4);
        badStringValueOfHelper(IMPROPER_NUM_DASHES_UUID_STRING_5);
        badStringValueOfHelper(IMPROPER_NUM_DASHES_UUID_STRING_6);
        badStringValueOfHelper(NON_HEX_UUID_STRING);
        badStringValueOfHelper(RANDOM_PROPER_LENGTH_STRING);
        
        // test the good cases
        goodStringValueOfHelper(NULL_UUID_STRING);
        goodStringValueOfHelper(UPPER_CASE_VALID_UUID_STRING);
        goodStringValueOfHelper(LOWER_CASE_VALID_UUID_STRING);
        goodStringValueOfHelper(MIXED_CASE_VALID_UUID_STRING);
    }

    /**************************************************************************
     * Begin private helper functions for use in tests
     *************************************************************************/
    private void badStringUUIDConstructorHelper(String uuidString)
    {
        try
        {
            /*UUID uuid =*/ UUIDUtil.uuid(uuidString);
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
    
    private void goodStringUUIDConstructorHelper(String uuidString)
    {
        UUID temp_uuid = null;
        try
        {
            temp_uuid = UUIDUtil.uuid(uuidString);
        }
        catch (Exception ex)
        {
            fail("Caught unexpected exception: " + ex);
        }
        
        assertEquals("uuid strings were not equal",
                    uuidString.toLowerCase(),
                    temp_uuid.toString().toLowerCase());        
    }
    
    private void badStringValueOfHelper(String uuidString)
    {
        try
        {
            /*UUID uuid =*/ UUIDUtil.uuid(uuidString);
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
    
    private void goodStringValueOfHelper(String uuidString)
    {
        UUID temp_uuid = null;
        try
        {
            temp_uuid = UUIDUtil.uuid(uuidString);
        }
        catch (Exception ex)
        {
            fail("Caught unexpected exception: " + ex);
        }
        
        assertEquals("UUID strings were not equal",
                    uuidString.toLowerCase(),
                    temp_uuid.toString().toLowerCase());
    }

    private void assertUUIDsMatchHelper(UUID expected, UUID actual)
    {
        // technically, toString will always return lowercase uuid strings,
        // but just to be paranoid, we will always do toLowerCase in this test
        assertEquals("UUID strings did not match",
                    expected.toString().toLowerCase(),
                    actual.toString().toLowerCase());
        
        assertEquals("UUID equals did not match",
                    expected,
                    actual);
    }
    
    private void assertUUIDEqualOrderHelper(UUID uuid1, UUID uuid2)
    {
        assertTrue(uuid1 + " did not test as equal to " + uuid2,
                    0 == UUIDComparator.staticCompare(uuid1, uuid2));
        assertTrue(uuid2 + " did not test as equal to " + uuid1,
                    0 == UUIDComparator.staticCompare(uuid2, uuid1));
    }
    
    private void assertUUIDGreaterOrderHelper(UUID uuid1, UUID uuid2)
    {
        int diff = UUIDComparator.staticCompare(uuid1, uuid2);
        assertTrue(uuid1 + " did not test as larger than " + uuid2+", diff: "+diff, diff > 0);
        diff = UUIDComparator.staticCompare(uuid2, uuid1);
        assertTrue(uuid2 + " did not test as smaller than " + uuid1+", diff: "+diff, diff < 0);
    }
    /**************************************************************************
     * End private helper functions for use in tests
     *************************************************************************/
    
    /**************************************************************************
     * Begin private constants for use in tests above
     *************************************************************************/
    private static final int UUID_BYTE_ARRAY_LENGTH = 16;
    
    // some strings for failure case tests
    private static final String IMPROPER_NUM_DASHES_UUID_STRING_1 =
        "01234567089AB-CDEF-0123-456789ABCDEF";
    private static final String IMPROPER_NUM_DASHES_UUID_STRING_2 =
        "01234567-89AB0CDEF-0123-456789ABCDEF";
    private static final String IMPROPER_NUM_DASHES_UUID_STRING_3 =
        "01234567-89AB-CDEF00123-456789ABCDEF";
    private static final String IMPROPER_NUM_DASHES_UUID_STRING_4 =
        "01234567-89AB-CDEF-01230456789ABCDEF";
    private static final String IMPROPER_NUM_DASHES_UUID_STRING_5 =
        "01234567089AB0CDEF001230456789ABCDEF";
    private static final String IMPROPER_NUM_DASHES_UUID_STRING_6 =
        "0123-4567-89AB-CDEF-0123-456789ABCDE";
    private static final String NON_HEX_UUID_STRING =
        "01THISIS-ANON-HEX0-UUID-FORSURE01234";
    private static final String RANDOM_PROPER_LENGTH_STRING =
        "String Of The Same Length as a UUID!";
    
    // some strings and matching byte arrays for the success case tests
    private static final String NULL_UUID_STRING =
        "00000000-0000-0000-0000-000000000000";
    private static final byte[] NULL_UUID_BYTE_ARRAY =
        new byte[UUID_BYTE_ARRAY_LENGTH];
    
    private static final String UPPER_CASE_VALID_UUID_STRING =
        "4D687664-3A1E-4F30-ACC1-87F59306D30C";
    private static final String MIXED_CASE_VALID_UUID_STRING =
        "4d687664-3A1e-4F30-aCc1-87F59306d30C";
    private static final String LOWER_CASE_VALID_UUID_STRING =
        "4d687664-3a1e-4f30-acc1-87f59306d30c";
    private static final byte[] VALID_UUID_BYTE_ARRAY =
    {
        (byte)0x4d, (byte)0x68, (byte)0x76, (byte)0x64,
        (byte)0x3a, (byte)0x1e, (byte)0x4f, (byte)0x30,
        (byte)0xac, (byte)0xc1, (byte)0x87, (byte)0xf5,
        (byte)0x93, (byte)0x06, (byte)0xd3, (byte)0x0c
    };
    /*
    private static final byte[] VALID_UUID_BYTE_ARRAY_WITH_EXTRA_START =
    {
        'e', 'x', 't', 'r', 'a', ' ', 'j', 'u', 'n', 'k',
        (byte)0x4d, (byte)0x68, (byte)0x76, (byte)0x64,
        (byte)0x3a, (byte)0x1e, (byte)0x4f, (byte)0x30,
        (byte)0xac, (byte)0xc1, (byte)0x87, (byte)0xf5,
        (byte)0x93, (byte)0x06, (byte)0xd3, (byte)0x0c
    };
    */
    private static final byte[] VALID_UUID_BYTE_ARRAY_WITH_EXTRA_END =
    {
        (byte)0x4d, (byte)0x68, (byte)0x76, (byte)0x64,
        (byte)0x3a, (byte)0x1e, (byte)0x4f, (byte)0x30,
        (byte)0xac, (byte)0xc1, (byte)0x87, (byte)0xf5,
        (byte)0x93, (byte)0x06, (byte)0xd3, (byte)0x0c,
        'o', 'n', ' ', 't', 'h', 'e', ' ', 'e', 'n', 'd',
        ' ', 'a', 's', ' ', 'w', 'e', 'l', 'l'
    };
    /*
    private static final byte[] VALID_UUID_BYTE_ARRAY_WITH_EXTRA_BOTH =
    {
        'e', 'x', 't', 'r', 'a', ' ', 'j', 'u', 'n', 'k',
        (byte)0x4d, (byte)0x68, (byte)0x76, (byte)0x64,
        (byte)0x3a, (byte)0x1e, (byte)0x4f, (byte)0x30,
        (byte)0xac, (byte)0xc1, (byte)0x87, (byte)0xf5,
        (byte)0x93, (byte)0x06, (byte)0xd3, (byte)0x0c,
        'o', 'n', ' ', 't', 'h', 'e', ' ', 'e', 'n', 'd',
        ' ', 'a', 's', ' ', 'w', 'e', 'l', 'l'
    };
    */
    //private static final String ANOTHER_VALID_UUID_STRING = "4aba2d17-08c9-4376-92fe-4cdefbba5a1c";
    private static final byte[] ANOTHER_VALID_UUID_BYTE_ARRAY =
    {
        (byte)0x4a, (byte)0xba, (byte)0x2d, (byte)0x17,
        (byte)0x08, (byte)0xc9, (byte)0x43, (byte)0x76,
        (byte)0x92, (byte)0xfe, (byte)0x4c, (byte)0xde,
        (byte)0xfb, (byte)0xba, (byte)0x5a, (byte)0x1c
    };

    // valid namespace based UUID string
    private static final String NAME_BASED_UUID_STRING =
        "71ee9b64-39d3-386c-bce3-c70549ca8829";
    private static final byte[] NAME_BASED_UUID_BYTE_ARRAY =
    {
        (byte)0x71, (byte)0xee, (byte)0x9b, (byte)0x64,
        (byte)0x39, (byte)0xd3, (byte)0x38, (byte)0x6c,
        (byte)0xbc, (byte)0xe3, (byte)0xc7, (byte)0x05,
        (byte)0x49, (byte)0xca, (byte)0x88, (byte)0x29
    };
    
    // dummy DCE based UUID string since I have no real examples to use
    //private static final String DCE_BASED_UUID_STRING = "01234567-0123-2000-8000-0123456789ab";
    private static final byte[] DCE_BASED_UUID_BYTE_ARRAY =
    {
        (byte)0x01, (byte)0x23, (byte)0x45, (byte)0x67,
        (byte)0x01, (byte)0x23, (byte)0x20, (byte)0x00,
        (byte)0x80, (byte)0x00, (byte)0x01, (byte)0x23,
        (byte)0x45, (byte)0x67, (byte)0x89, (byte)0xab
    };
    
    // some strings for the "ordered" uuid test cases
    // notice that these uuid cases vary in the time portion and for each
    // "exact time" there is a case for two different MAC addresses
    // to insure the ordering test between different MAC addresses
    private static final UUID NULL_UUID = nullUUID;
    private static final UUID TIME1_MAC1_UUID =
        UUIDUtil.uuid("ebb8e8fe-b1b1-11d7-8adb-00b0d078fa18");
    private static final UUID TIME1_MAC2_UUID =
        UUIDUtil.uuid("ebb8e8fe-b1b1-11d7-8adb-baa07db6d227");
    private static final UUID TIME2_MAC1_UUID =
        UUIDUtil.uuid("ec3ffdda-b1b1-11d7-8adb-00b0d078fa18");
    private static final UUID TIME2_MAC2_UUID =
        UUIDUtil.uuid("ec3ffdda-b1b1-11d7-8adb-baa07db6d227");
    private static final UUID TIME3_MAC1_UUID =
        UUIDUtil.uuid("eca4c616-b1b1-11d7-8adb-00b0d078fa18");
    private static final UUID TIME3_MAC2_UUID =
        UUIDUtil.uuid("eca4c616-b1b1-11d7-8adb-baa07db6d227");
    private static final UUID TIME4_MAC1_UUID =
        UUIDUtil.uuid("ed17de08-b1b1-11d7-8adb-00b0d078fa18");
    private static final UUID TIME4_MAC2_UUID =
        UUIDUtil.uuid("ed17de08-b1b1-11d7-8adb-baa07db6d227");
    private static final UUID TIME5_MAC1_UUID =
        UUIDUtil.uuid("ed94244a-b1b1-11d7-8adb-00b0d078fa18");
    private static final UUID TIME5_MAC2_UUID =
        UUIDUtil.uuid("ed94244a-b1b1-11d7-8adb-baa07db6d227");
    /**************************************************************************
     * End private constants for use in tests above
     *************************************************************************/
}
