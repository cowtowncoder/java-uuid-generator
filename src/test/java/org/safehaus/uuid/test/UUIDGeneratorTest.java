/* JUG Java Uuid Generator
 * UUIDGeneratorTest.java
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

import java.security.MessageDigest;
import java.security.SecureRandom;

import java.util.Arrays;
import java.util.Comparator;
import java.util.Calendar;
import java.util.HashSet;
import java.util.Random;

import org.safehaus.uuid.EthernetAddress;
import org.safehaus.uuid.TagURI;
import org.safehaus.uuid.UUID;
import org.safehaus.uuid.UUIDGenerator;

/**
 * JUnit Test class for the org.safehaus.uuid.UUIDGenerator class.
 *
 * @author Eric Bie
 */
public class UUIDGeneratorTest extends TestCase
{
    // size of the arrays to create for tests using arrays of values
    private static final int SIZE_OF_TEST_ARRAY = 10000;
    
    public UUIDGeneratorTest(java.lang.String testName)
    {
        super(testName);
    }
    
    public static Test suite()
    {
        TestSuite suite = new TestSuite(UUIDGeneratorTest.class);
        return suite;
    }
    
    public static void main(String[] args)
    {
        TestRunner.run(suite());
    }
    
    /**
     * Test of getInstance method, of class org.safehaus.uuid.UUIDGenerator.
     */
    public void testGetInstance()
    {
        // really, there isn't a lot to test here
        // we'll make sure that getInstance returns the same
        // reference when called twice since it is supposed to
        // be a singleton class factory
        UUIDGenerator uuid_gen1 = UUIDGenerator.getInstance();
        UUIDGenerator uuid_gen2 = UUIDGenerator.getInstance();
        UUIDGenerator uuid_gen3 = UUIDGenerator.getInstance();
        
        assertTrue("uuid_gen1 == uuid_gen2 was not true",
                uuid_gen1 == uuid_gen2);
        assertTrue("uuid_gen2 == uuid_gen3 was not true",
                uuid_gen2 == uuid_gen3);
        assertTrue("uuid_gen1 == uuid_gen3 was not true",
                uuid_gen1 == uuid_gen3);
    }
    
    /**
     * Test of getDummyAddress method,
     * of class org.safehaus.uuid.UUIDGenerator.
     */
    public void testGetDummyAddress()
    {
        // this test will attempt to check for reasonable behavior of the
        // getDummyAddress method
        
        // we need a instance to use
        UUIDGenerator uuid_gen = UUIDGenerator.getInstance();
        
        // for the random UUID generator, we will generate a bunch of
        // dummy ethernet addresses
        // NOTE: although creating a bunch of dummy ethernet addresses
        // is not the normal mode of operation, we'return testing for
        // generally good behavior, so we'll create a bunch to make sure the
        // general patterns are observed
        EthernetAddress ethernet_address_array[] =
            new EthernetAddress[SIZE_OF_TEST_ARRAY];
        
        // now create the array of uuids
        for (int i = 0; i < ethernet_address_array.length; i++)
        {
            ethernet_address_array[i] = uuid_gen.getDummyAddress();
        }
        
        EthernetAddress null_ethernet_address = new EthernetAddress(0L);
        for (int i = 0; i < ethernet_address_array.length; i++)
        {
            byte[] ethernet_address = ethernet_address_array[i].asByteArray();
            // check that none of the EthernetAddresses are null
            assertFalse("dummy EthernetAddress was null",
                    Arrays.equals(null_ethernet_address.asByteArray(),
                                ethernet_address));
            
            // check that the "broadcast" bit is set in the created address
            /* 08-Feb-2004, TSa: Fixed as per fix to actual code; apparently
             *   broadcast bit is LSB, not MSB.
             */
            assertEquals("dummy EthernetAddress was not broadcast",
                    0x01,
                    (ethernet_address[0] & 0x01));
        }
    }
    
    /**
     * Test of getRandomNumberGenerator method,
     * of class org.safehaus.uuid.UUIDGenerator.
     */
    public void testGetRandomNumberGenerator()
    {
        // really, there isn't a lot to test here
        // we'll make sure that getRandomNumberGenerator returns the same
        // reference when called more then once from more then one instance
        // since it is supposed to be a shared generator
        UUIDGenerator uuid_gen1 = UUIDGenerator.getInstance();
        UUIDGenerator uuid_gen2 = UUIDGenerator.getInstance();
        UUIDGenerator uuid_gen3 = UUIDGenerator.getInstance();
        
        assertTrue("uuid_gen1 == uuid_gen2 was not true",
                uuid_gen1 == uuid_gen2);
        assertTrue("uuid_gen2 == uuid_gen3 was not true",
                uuid_gen2 == uuid_gen3);
        assertTrue("uuid_gen1 == uuid_gen3 was not true",
                uuid_gen1 == uuid_gen3);
        
        Random secure_rand1 = uuid_gen1.getRandomNumberGenerator();
        Random secure_rand2 = uuid_gen1.getRandomNumberGenerator();
        Random secure_rand3 = uuid_gen2.getRandomNumberGenerator();
        Random secure_rand4 = uuid_gen2.getRandomNumberGenerator();
        Random secure_rand5 = uuid_gen3.getRandomNumberGenerator();
        Random secure_rand6 = uuid_gen3.getRandomNumberGenerator();

        assertTrue("secure_rand1 == secure_rand2 was not true",
                secure_rand1 == secure_rand2);
        assertTrue("secure_rand2 == secure_rand3 was not true",
                secure_rand2 == secure_rand3);
        assertTrue("secure_rand3 == secure_rand4 was not true",
                secure_rand3 == secure_rand4);
        assertTrue("secure_rand4 == secure_rand5 was not true",
                secure_rand4 == secure_rand5);
        assertTrue("secure_rand5 == secure_rand6 was not true",
                secure_rand5 == secure_rand6);
        assertTrue("secure_rand6 == secure_rand1 was not true",
                secure_rand6 == secure_rand1);
    }
    
    /**
     * Test of getHashAlgorithm method,
     * of class org.safehaus.uuid.UUIDGenerator.
     */
    public void testGetHashAlgorithm()
    {
        // really, there isn't a lot to test here
        // we'll make sure that getHashAlgorithm returns the same
        // reference when called more then once from more then one instance
        // since it is supposed to be a shared MessageDigest
        UUIDGenerator uuid_gen1 = UUIDGenerator.getInstance();
        UUIDGenerator uuid_gen2 = UUIDGenerator.getInstance();
        UUIDGenerator uuid_gen3 = UUIDGenerator.getInstance();
        
        assertTrue("uuid_gen1 == uuid_gen2 was not true",
                uuid_gen1 == uuid_gen2);
        assertTrue("uuid_gen2 == uuid_gen3 was not true",
                uuid_gen2 == uuid_gen3);
        assertTrue("uuid_gen1 == uuid_gen3 was not true",
                uuid_gen1 == uuid_gen3);
        
        MessageDigest message_digest1 = uuid_gen1.getHashAlgorithm();
        MessageDigest message_digest2 = uuid_gen1.getHashAlgorithm();
        MessageDigest message_digest3 = uuid_gen2.getHashAlgorithm();
        MessageDigest message_digest4 = uuid_gen2.getHashAlgorithm();
        MessageDigest message_digest5 = uuid_gen3.getHashAlgorithm();
        MessageDigest message_digest6 = uuid_gen3.getHashAlgorithm();

        assertTrue("message_digest1 == message_digest2 was not true",
                message_digest1 == message_digest2);
        assertTrue("message_digest2 == message_digest3 was not true",
                message_digest2 == message_digest3);
        assertTrue("message_digest3 == message_digest4 was not true",
                message_digest3 == message_digest4);
        assertTrue("message_digest4 == message_digest5 was not true",
                message_digest4 == message_digest5);
        assertTrue("message_digest5 == message_digest6 was not true",
                message_digest5 == message_digest6);
        assertTrue("message_digest6 == message_digest1 was not true",
                message_digest6 == message_digest1);
    }
    
    /**
     * Test of generateRandomBasedUUID method,
     * of class org.safehaus.uuid.UUIDGenerator.
     */
    public void testGenerateRandomBasedUUID()
    {
        // this test will attempt to check for reasonable behavior of the
        // generateRandomBasedUUID method
        
        // we need a instance to use
        UUIDGenerator uuid_gen = UUIDGenerator.getInstance();
        
        // for the random UUID generator, we will generate a bunch of
        // random UUIDs
        UUID uuid_array[] = new UUID[SIZE_OF_TEST_ARRAY];
        
        // now create the array of uuids
        for (int i = 0; i < uuid_array.length; i++)
        {
            uuid_array[i] = uuid_gen.generateRandomBasedUUID();
        }
        
        // check that none of the UUIDs are null
        checkUUIDArrayForNonNullUUIDs(uuid_array);
        
        // check that all the uuids were correct variant and version (type-4)
        checkUUIDArrayForCorrectVariantAndVersion(
            uuid_array, UUID.TYPE_RANDOM_BASED);

        // check that all uuids were unique
        // NOTE: technically, this test 'could' fail, but statistically
        // speaking it should be extremely unlikely unless the implementation
        // of (Secure)Random is bad
        checkUUIDArrayForUniqueness(uuid_array);
    }
    
    /**
     * Test of generateRandomBasedUUID(Random) method,
     * of class org.safehaus.uuid.UUIDGenerator.
     */
    public void testGenerateRandomBasedUUIDWithRandom()
    {
        // this test will attempt to check for reasonable behavior of the
        // generateRandomBasedUUID method
        
        // we need a instance to use
        UUIDGenerator uuid_gen = UUIDGenerator.getInstance();
        
        // first, check that a null passed in causes the appropriate exception
        try
        {
            /*UUID uuid =*/ uuid_gen.generateRandomBasedUUID((Random)null);
            fail("Expected exception not thrown");
        }
        catch (NullPointerException ex)
        {
            // expected exception caught, do nothing
        }
        catch (Exception ex)
        {
            fail("unexpected exception caught: " + ex);
        }
        
        // for the random UUID generator, we will generate a bunch of
        // random UUIDs using a (Secure)Random instance we generated
        SecureRandom secure_random = new SecureRandom();
        UUID uuid_array[] = new UUID[SIZE_OF_TEST_ARRAY];
        
        // now create the array of uuids
        for (int i = 0; i < uuid_array.length; i++)
        {
            uuid_array[i] = uuid_gen.generateRandomBasedUUID(secure_random);
        }
        
        // check that none of the UUIDs are null
        checkUUIDArrayForNonNullUUIDs(uuid_array);
        
        // check that all the uuids were correct variant and version (type-4)
        checkUUIDArrayForCorrectVariantAndVersion(
            uuid_array, UUID.TYPE_RANDOM_BASED);

        // check that all uuids were unique
        // NOTE: technically, this test 'could' fail, but statistically
        // speaking it should be extremely unlikely unless the
        // implementation of SecureRandom is bad
        checkUUIDArrayForUniqueness(uuid_array);
    }
    
    /**
     * Test of generateTimeBasedUUID() method,
     * of class org.safehaus.uuid.UUIDGenerator.
     */
    public void testGenerateTimeBasedUUID()
    {
        // this test will attempt to check for reasonable behavior of the
        // generateTimeBasedUUID method
        
        // we need a instance to use
        UUIDGenerator uuid_gen = UUIDGenerator.getInstance();
        
        // first check that given a number of calls to generateTimeBasedUUID,
        // all returned UUIDs order after the last returned UUID
        // we'll check this by generating the UUIDs into one array and sorting
        // then in another and checking the order of the two match
        // change the number in the array statement if you want more or less
        // UUIDs to be generated and tested
        UUID uuid_array[] = new UUID[SIZE_OF_TEST_ARRAY];
        
        // before we generate all the uuids, lets get the start time
        long start_time = System.currentTimeMillis();
        
        // now create the array of uuids
        for (int i = 0; i < uuid_array.length; i++)
        {
            uuid_array[i] = uuid_gen.generateTimeBasedUUID();
        }
        
        // now capture the end time
        long end_time = System.currentTimeMillis();
        
        // check that none of the UUIDs are null
        checkUUIDArrayForNonNullUUIDs(uuid_array);

        // check that all the uuids were correct variant and version (type-1)
        checkUUIDArrayForCorrectVariantAndVersion(
            uuid_array, UUID.TYPE_TIME_BASED);

        // check that all the uuids were generated with correct order
        checkUUIDArrayForCorrectOrdering(uuid_array);
        
        // check that all uuids were unique
        checkUUIDArrayForUniqueness(uuid_array);
        
        // check that all uuids have timestamps between the start and end time
        checkUUIDArrayForCorrectCreationTime(uuid_array, start_time, end_time);
    }
    
    /**
     * Test of generateTimeBasedUUID(EthernetAddress) method,
     * of class org.safehaus.uuid.UUIDGenerator.
     */
    public void testGenerateTimeBasedUUIDWithEthernetAddress()
    {
        // this test will attempt to check for reasonable behavior of the
        // generateTimeBasedUUID(EthernetAddress) method
        EthernetAddress ethernet_address =
            new EthernetAddress("87:F5:93:06:D3:0C");
        
        // we need a instance to use
        UUIDGenerator uuid_gen = UUIDGenerator.getInstance();
        
        // first, check that a null passed in causes the appropriate exception
        try
        {
            /*UUID uuid =*/ uuid_gen.generateTimeBasedUUID((EthernetAddress)null);
            fail("Expected exception not thrown");
        }
        catch (NullPointerException ex)
        {
            // expected exception caught, do nothing
        }
        catch (Exception ex)
        {
            fail("unexpected exception caught: " + ex);
        }
        
        // check that given a number of calls to generateTimeBasedUUID,
        // all returned UUIDs order after the last returned UUID
        // we'll check this by generating the UUIDs into one array and sorting
        // then in another and checking the order of the two match
        // change the number in the array statement if you want more or less
        // UUIDs to be generated and tested
        UUID uuid_array[] = new UUID[SIZE_OF_TEST_ARRAY];
        
        // before we generate all the uuids, lets get the start time
        long start_time = System.currentTimeMillis();
        
        // now create the array of uuids
        for (int i = 0; i < uuid_array.length; i++)
        {
            uuid_array[i] = uuid_gen.generateTimeBasedUUID(ethernet_address);
        }
        
        // now capture the end time
        long end_time = System.currentTimeMillis();
        
        // check that none of the UUIDs are null
        checkUUIDArrayForNonNullUUIDs(uuid_array);
        
        // check that all the uuids were correct variant and version (type-1)
        checkUUIDArrayForCorrectVariantAndVersion(
            uuid_array, UUID.TYPE_TIME_BASED);

        // check that all the uuids were generated with correct order
        checkUUIDArrayForCorrectOrdering(uuid_array);
        
        // check that all uuids were unique
        checkUUIDArrayForUniqueness(uuid_array);
        
        // check that all uuids have timestamps between the start and end time
        checkUUIDArrayForCorrectCreationTime(uuid_array, start_time, end_time);
        
        // check that all UUIDs have the correct ethernet address in the UUID
        checkUUIDArrayForCorrectEthernetAddress(uuid_array, ethernet_address);
    }
    
    /**
     * Test of generateNameBasedUUID(UUID, String)
     * method, of class org.safehaus.uuid.UUIDGenerator.
     */
    public void testGenerateNameBasedUUIDNameSpaceAndName()
    {
        final UUID NAMESPACE_UUID = new UUID(UUID.NAMESPACE_URL);
        
        // this test will attempt to check for reasonable behavior of the
        // generateNameBasedUUID method
        
        // we need a instance to use
        UUIDGenerator uuid_gen = UUIDGenerator.getInstance();
        
        // first, check that a null passed in causes the appropriate exception
        try
        {
            /*UUID uuid =*/
                uuid_gen.generateNameBasedUUID(NAMESPACE_UUID, (String)null);
            fail("Expected exception not thrown");
        }
        catch (NullPointerException ex)
        {
            // expected exception caught, do nothing
        }
        catch (Exception ex)
        {
            fail("unexpected exception caught: " + ex);
        }
        
        UUID uuid_array[] = new UUID[SIZE_OF_TEST_ARRAY];
        
        // now create the array of uuids
        for (int i = 0; i < uuid_array.length; i++)
        {
            uuid_array[i] =
                uuid_gen.generateNameBasedUUID(
                    NAMESPACE_UUID, "test name" + i);
        }
        
        // check that none of the UUIDs are null
        checkUUIDArrayForNonNullUUIDs(uuid_array);
        
        // check that all the uuids were correct variant and version
        checkUUIDArrayForCorrectVariantAndVersion(
            uuid_array, UUID.TYPE_NAME_BASED);
        
        // check that all uuids were unique
        checkUUIDArrayForUniqueness(uuid_array);
        
        // now create the array of uuids
        for (int i = 0; i < uuid_array.length; i++)
        {
            uuid_array[i] =
                uuid_gen.generateNameBasedUUID(null, "test name" + i);
        }
        
        // check that none of the UUIDs are null
        checkUUIDArrayForNonNullUUIDs(uuid_array);
        
        // check that all the uuids were correct variant and version
        checkUUIDArrayForCorrectVariantAndVersion(
            uuid_array, UUID.TYPE_NAME_BASED);
        
        // check that all uuids were unique
        checkUUIDArrayForUniqueness(uuid_array);
        
        // now, lets make sure generating two sets of name based uuid with the
        // same args always gives the same result
        uuid_array = new UUID[SIZE_OF_TEST_ARRAY];
        
        // now create the array of uuids
        for (int i = 0; i < uuid_array.length; i++)
        {
            uuid_array[i] =
                uuid_gen.generateNameBasedUUID(
                    NAMESPACE_UUID, "test name" + i);
        }
        
        UUID uuid_array2[] = new UUID[SIZE_OF_TEST_ARRAY];
        
        // now create the array of uuids
        for (int i = 0; i < uuid_array2.length; i++)
        {
            uuid_array2[i] =
                uuid_gen.generateNameBasedUUID(
                    NAMESPACE_UUID, "test name" + i);
        }
        
        // check that none of the UUIDs are null
        checkUUIDArrayForNonNullUUIDs(uuid_array);
        checkUUIDArrayForNonNullUUIDs(uuid_array2);
        
        // check that all the uuids were correct variant and version
        checkUUIDArrayForCorrectVariantAndVersion(
            uuid_array, UUID.TYPE_NAME_BASED);
        checkUUIDArrayForCorrectVariantAndVersion(
            uuid_array2, UUID.TYPE_NAME_BASED);
        
        // check that all uuids were unique
        checkUUIDArrayForUniqueness(uuid_array);
        checkUUIDArrayForUniqueness(uuid_array2);
        
        // check that both arrays are equal to one another
        assertTrue("expected both arrays to be equal, they were not!",
            Arrays.equals(uuid_array, uuid_array2));
    }
    
    /**
     * Test of generateNameBasedUUID(UUID, String, MessageDigest)
     * method, of class org.safehaus.uuid.UUIDGenerator.
     */
    public void testGenerateNameBasedUUIDNameSpaceNameAndMessageDigest()
    {
        final UUID NAMESPACE_UUID = new UUID(UUID.NAMESPACE_URL);
        MessageDigest MESSAGE_DIGEST = null;
        try
        {
            MESSAGE_DIGEST = MessageDigest.getInstance("MD5");
        }
        catch (Exception ex)
        {
            fail("exception caught getting test digest : " + ex);
        }
        
        // this test will attempt to check for reasonable behavior of the
        // generateNameBasedUUID method
        
        // we need a instance to use
        UUIDGenerator uuid_gen = UUIDGenerator.getInstance();
        
        // first, check that a null passed in causes the appropriate exception
        try
        {
            /*UUID uuid =*/
                uuid_gen.generateNameBasedUUID(
                    NAMESPACE_UUID, (String)null, MESSAGE_DIGEST);
            fail("Expected exception not thrown");
        }
        catch (NullPointerException ex)
        {
            // expected exception caught, do nothing
        }
        catch (Exception ex)
        {
            fail("unexpected exception caught: " + ex);
        }
        
        try
        {
            /*UUID uuid =*/
                uuid_gen.generateNameBasedUUID(
                    NAMESPACE_UUID, "test name", (MessageDigest)null);
            fail("Expected exception not thrown");
        }
        catch (NullPointerException ex)
        {
            // expected exception caught, do nothing
        }
        catch (Exception ex)
        {
            fail("unexpected exception caught: " + ex);
        }

        try
        {
            /*UUID uuid =*/
                uuid_gen.generateNameBasedUUID(
                    NAMESPACE_UUID, (String)null, (MessageDigest)null);
            fail("Expected exception not thrown");
        }
        catch (NullPointerException ex)
        {
            // expected exception caught, do nothing
        }
        catch (Exception ex)
        {
            fail("unexpected exception caught: " + ex);
        }

        UUID uuid_array[] = new UUID[SIZE_OF_TEST_ARRAY];
        
        // now create the array of uuids
        for (int i = 0; i < uuid_array.length; i++)
        {
            uuid_array[i] =
                uuid_gen.generateNameBasedUUID(
                    NAMESPACE_UUID, "test name" + i, MESSAGE_DIGEST);
        }
        
        // check that none of the UUIDs are null
        checkUUIDArrayForNonNullUUIDs(uuid_array);
        
        // check that all the uuids were correct variant and version
        checkUUIDArrayForCorrectVariantAndVersion(
            uuid_array, UUID.TYPE_NAME_BASED);
        
        // check that all uuids were unique
        checkUUIDArrayForUniqueness(uuid_array);
        
        // now create the array of uuids
        for (int i = 0; i < uuid_array.length; i++)
        {
            uuid_array[i] =
                uuid_gen.generateNameBasedUUID(
                    null, "test name" + i, MESSAGE_DIGEST);
        }
        
        // check that none of the UUIDs are null
        checkUUIDArrayForNonNullUUIDs(uuid_array);
        
        // check that all the uuids were correct variant and version
        checkUUIDArrayForCorrectVariantAndVersion(
            uuid_array, UUID.TYPE_NAME_BASED);
        
        // check that all uuids were unique
        checkUUIDArrayForUniqueness(uuid_array);
        
        // now, lets make sure generating two sets of name based uuid with the
        // same args always gives the same result
        uuid_array = new UUID[SIZE_OF_TEST_ARRAY];
        
        // now create the array of uuids
        for (int i = 0; i < uuid_array.length; i++)
        {
            uuid_array[i] =
                uuid_gen.generateNameBasedUUID(
                    NAMESPACE_UUID, "test name" + i, MESSAGE_DIGEST);
        }
        
        UUID uuid_array2[] = new UUID[SIZE_OF_TEST_ARRAY];
        
        // now create the array of uuids
        for (int i = 0; i < uuid_array2.length; i++)
        {
            uuid_array2[i] =
                uuid_gen.generateNameBasedUUID(
                    NAMESPACE_UUID, "test name" + i, MESSAGE_DIGEST);
        }
        
        // check that none of the UUIDs are null
        checkUUIDArrayForNonNullUUIDs(uuid_array);
        checkUUIDArrayForNonNullUUIDs(uuid_array2);
        
        // check that all the uuids were correct variant and version
        checkUUIDArrayForCorrectVariantAndVersion(
            uuid_array, UUID.TYPE_NAME_BASED);
        checkUUIDArrayForCorrectVariantAndVersion(
            uuid_array2, UUID.TYPE_NAME_BASED);
        
        // check that all uuids were unique
        checkUUIDArrayForUniqueness(uuid_array);
        checkUUIDArrayForUniqueness(uuid_array2);
        
        // check that both arrays are equal to one another
        assertTrue("expected both arrays to be equal, they were not!",
            Arrays.equals(uuid_array, uuid_array2));
    }
    
    /**
     * Test of generateTagURIBasedUUID(TagURI) method,
     * of class org.safehaus.uuid.UUIDGenerator.
     */
    public void testGenerateTagURIBasedUUID()
    {
        final String TEST_AUTHORITY = "www.safehaus.org";

        // this test will attempt to check for reasonable behavior of the
        // generateTagURIBasedUUID method
        
        // we need a instance to use
        UUIDGenerator uuid_gen = UUIDGenerator.getInstance();
        
        // first, check that a null passed in causes the appropriate exception
        try
        {
            /*UUID uuid =*/ uuid_gen.generateTagURIBasedUUID(null);
            fail("Expected exception not thrown");
        }
        catch (NullPointerException ex)
        {
            // expected exception caught, do nothing
        }
        catch (Exception ex)
        {
            fail("unexpected exception caught: " + ex);
        }
        
        UUID uuid_array[] = new UUID[SIZE_OF_TEST_ARRAY];
        
        // now create the array of uuids
        for (int i = 0; i < uuid_array.length; i++)
        {
            TagURI test_tag =
                new TagURI(TEST_AUTHORITY, "test id" + i,
                    Calendar.getInstance());
            uuid_array[i] =
                uuid_gen.generateTagURIBasedUUID(test_tag);
        }
        
        // check that none of the UUIDs are null
        checkUUIDArrayForNonNullUUIDs(uuid_array);
        
        // check that all the uuids were correct variant and version
        checkUUIDArrayForCorrectVariantAndVersion(
            uuid_array, UUID.TYPE_NAME_BASED);
        
        // check that all uuids were unique
        checkUUIDArrayForUniqueness(uuid_array);
        
        // now, lets make sure generating two sets of tag based uuid with the
        // same args always gives the same result
        uuid_array = new UUID[SIZE_OF_TEST_ARRAY];
        UUID uuid_array2[] = new UUID[SIZE_OF_TEST_ARRAY];
        
        // now create the array of uuids
        for (int i = 0; i < uuid_array.length; i++)
        {
            TagURI test_tag =
                new TagURI(TEST_AUTHORITY, "test id" + i,
                    Calendar.getInstance());
            uuid_array[i] =
                uuid_gen.generateTagURIBasedUUID(test_tag);
            uuid_array2[i] =
                uuid_gen.generateTagURIBasedUUID(test_tag);
        }
        
        // check that none of the UUIDs are null
        checkUUIDArrayForNonNullUUIDs(uuid_array);
        checkUUIDArrayForNonNullUUIDs(uuid_array2);
        
        // check that all the uuids were correct variant and version
        checkUUIDArrayForCorrectVariantAndVersion(
            uuid_array, UUID.TYPE_NAME_BASED);
        checkUUIDArrayForCorrectVariantAndVersion(
            uuid_array2, UUID.TYPE_NAME_BASED);
        
        // check that all uuids were unique
        checkUUIDArrayForUniqueness(uuid_array);
        checkUUIDArrayForUniqueness(uuid_array2);
        
        // check that both arrays are equal to one another
        assertTrue("expected both arrays to be equal, they were not!",
            Arrays.equals(uuid_array, uuid_array2));
    }
    
    /**
     * Test of generateTagURIBasedUUID(TagURI, MessageDigest) method,
     * of class org.safehaus.uuid.UUIDGenerator.
     */
    public void testGenerateTagURIBasedUUIDWithMessageDigest()
    {
        final String TEST_AUTHORITY = "www.safehaus.org";
        MessageDigest MESSAGE_DIGEST = null;
        try
        {
            MESSAGE_DIGEST = MessageDigest.getInstance("MD5");
        }
        catch (Exception ex)
        {
            fail("exception caught getting test digest : " + ex);
        }
        
        // this test will attempt to check for reasonable behavior of the
        // generateTagURIBasedUUID method
        
        // we need a instance to use
        UUIDGenerator uuid_gen = UUIDGenerator.getInstance();
        
        // first, check that a null passed in causes the appropriate exception
        try
        {
            /*UUID uuid =*/ uuid_gen.generateTagURIBasedUUID(null, MESSAGE_DIGEST);
            fail("Expected exception not thrown");
        }
        catch (NullPointerException ex)
        {
            // expected exception caught, do nothing
        }
        catch (Exception ex)
        {
            fail("unexpected exception caught: " + ex);
        }
        
        try
        {
            TagURI test_tag =
                new TagURI(TEST_AUTHORITY, "test id", Calendar.getInstance());
            /*UUID uuid =*/ uuid_gen.generateTagURIBasedUUID(test_tag, null);
            fail("Expected exception not thrown");
        }
        catch (NullPointerException ex)
        {
            // expected exception caught, do nothing
        }
        catch (Exception ex)
        {
            fail("unexpected exception caught: " + ex);
        }

        try
        {
            /*UUID uuid =*/ uuid_gen.generateTagURIBasedUUID(null, null);
            fail("Expected exception not thrown");
        }
        catch (NullPointerException ex)
        {
            // expected exception caught, do nothing
        }
        catch (Exception ex)
        {
            fail("unexpected exception caught: " + ex);
        }

        UUID uuid_array[] = new UUID[SIZE_OF_TEST_ARRAY];
        
        // now create the array of uuids
        for (int i = 0; i < uuid_array.length; i++)
        {
            TagURI test_tag =
                new TagURI(TEST_AUTHORITY, "test id" + i,
                    Calendar.getInstance());
            uuid_array[i] =
                uuid_gen.generateTagURIBasedUUID(test_tag, MESSAGE_DIGEST);
        }
        
        // check that none of the UUIDs are null
        checkUUIDArrayForNonNullUUIDs(uuid_array);
        
        // check that all the uuids were correct variant and version
        checkUUIDArrayForCorrectVariantAndVersion(
            uuid_array, UUID.TYPE_NAME_BASED);
        
        // check that all uuids were unique
        checkUUIDArrayForUniqueness(uuid_array);
        
        // now, lets make sure generating two sets of tag based uuid with the
        // same args always gives the same result
        uuid_array = new UUID[SIZE_OF_TEST_ARRAY];
        UUID uuid_array2[] = new UUID[SIZE_OF_TEST_ARRAY];
        
        // now create the array of uuids
        for (int i = 0; i < uuid_array.length; i++)
        {
            TagURI test_tag =
                new TagURI(TEST_AUTHORITY, "test id" + i,
                    Calendar.getInstance());
            uuid_array[i] =
                uuid_gen.generateTagURIBasedUUID(test_tag, MESSAGE_DIGEST);
            uuid_array2[i] =
                uuid_gen.generateTagURIBasedUUID(test_tag, MESSAGE_DIGEST);
        }
        
        // check that none of the UUIDs are null
        checkUUIDArrayForNonNullUUIDs(uuid_array);
        checkUUIDArrayForNonNullUUIDs(uuid_array2);
        
        // check that all the uuids were correct variant and version
        checkUUIDArrayForCorrectVariantAndVersion(
            uuid_array, UUID.TYPE_NAME_BASED);
        checkUUIDArrayForCorrectVariantAndVersion(
            uuid_array2, UUID.TYPE_NAME_BASED);
        
        // check that all uuids were unique
        checkUUIDArrayForUniqueness(uuid_array);
        checkUUIDArrayForUniqueness(uuid_array2);
        
        // check that both arrays are equal to one another
        assertTrue("expected both arrays to be equal, they were not!",
            Arrays.equals(uuid_array, uuid_array2));
    }
    
    /**************************************************************************
     * Begin Private Helper Methods for use in tests 
     *************************************************************************/
    private class ReverseOrderUUIDComparator implements Comparator
    {
        // this Comparator class has a compare which orders reverse of the
        // compareTo methond in UUID (so we can be sure our arrays below are
        // 'not ordered in sorted order' before we sort them.
        public int compare(Object o1, Object o2)
        {
            UUID uuid1 = (UUID)o1;
            UUID uuid2 = (UUID)o2;
            
            return -uuid1.compareTo(uuid2);
        }
        
        // we are only implementing equals because it's needed, super should do
        public boolean equals(Object o)
        {
            return super.equals(o);
        }
    }
    
    private void checkUUIDArrayForCorrectOrdering(UUID[] uuidArray)
    {
        // now we'll clone the array and reverse it
        UUID uuid_sorted_array[] = (UUID[])uuidArray.clone();
        assertEquals("Cloned array length did not match",
                    uuidArray.length,
                    uuid_sorted_array.length);
        
        ReverseOrderUUIDComparator rev_order_uuid_comp =
            new ReverseOrderUUIDComparator();
        Arrays.sort(uuid_sorted_array, rev_order_uuid_comp);
        
        // let's check that the array is actually reversed
        for (int i = 0; i < uuid_sorted_array.length; i++)
        {
            assertTrue(
                "Reverse order check on uuid arrays failed on element " + i,
                uuidArray[i].equals(
                    uuid_sorted_array[uuid_sorted_array.length - (1 + i)]));
        }
        
        // now let's sort the reversed array and check that it
        // sorted to the same order as the original
        Arrays.sort(uuid_sorted_array);
        for (int i = 0; i < uuid_sorted_array.length; i++)
        {
            assertTrue(
                "Same order check on uuid arrays failed on element " + i,
                uuidArray[i].equals(uuid_sorted_array[i]));
        }        
    }
    
    private void checkUUIDArrayForUniqueness(UUID[] uuidArray)
    {
        // here we'll assert that all elements in the list are not equal to
        // each other (aka, there should be no duplicates) we'll do this by
        // inserting all elements into a HashSet and making sure none of them
        //were already present (add will return false if it was already there)
        HashSet hash_set = new HashSet();
        for (int i = 0; i < uuidArray.length; i++)
        {
            assertTrue("Uniqueness test failed on insert into HashSet",
                    hash_set.add(uuidArray[i]));
            assertFalse("Paranoia Uniqueness test failed (second insert)",
                    hash_set.add(uuidArray[i]));
        }
    }
    
    private void checkUUIDArrayForCorrectVariantAndVersion(UUID[] uuidArray,
                                                           int expectedType)
    {
        // let's check that all the UUIDs are valid type-1 UUIDs with the
        // correct variant according to the specification.
        for (int i = 0; i < uuidArray.length; i++)
        {
            assertEquals("Expected version (type) did not match",
                        expectedType,
                        uuidArray[i].getType());

            // now. let's double check the variant and type from the array
            byte[] temp_uuid = uuidArray[i].toByteArray();
            
            // extract type from the UUID and check for correct type
            int type = (temp_uuid[UUID.INDEX_TYPE] & 0xFF) >> 4;
            assertEquals("Expected type did not match",
                        expectedType,
                        type);            
            // extract variant from the UUID and check for correct variant
            int variant = (temp_uuid[UUID.INDEX_VARIATION] & 0xFF) >> 6;
            assertEquals("Expected variant did not match",
                        2,
                        variant);            
        }
    }

    private void checkUUIDArrayForCorrectCreationTime(
        UUID[] uuidArray, long startTime, long endTime)
    {
        // we need to convert from 100-naonsecond units (as used in UUIDs)
        // to millisecond units as used in UTC based time
        final long MILLI_CONVERSION_FACTOR = 10000L;
        // Since System.currentTimeMillis() returns time epoc time
        // (from 1-Jan-1970), and UUIDs use time from the beginning of
        // Gregorian calendar (15-Oct-1582) we have a offset for correction
        final long GREGORIAN_CALENDAR_START_TO_UTC_START_OFFSET =
            122192928000000000L;

        assertTrue("start time was not before the end time",
                startTime < endTime);
        
        // let's check that all uuids in the array have a timestamp which lands
        // between the start and end time
        for (int i = 0; i < uuidArray.length; i++)
        {
            byte[] temp_uuid = uuidArray[i].toByteArray();
            
            // first we'll collect the UUID time stamp which is
            // the number of 100-nanosecond intervals since
            // 00:00:00.00 15 October 1582
            long uuid_time = 0L;
            uuid_time |= ((temp_uuid[3] & 0xF0L) <<  0);
            uuid_time |= ((temp_uuid[2] & 0xFFL) <<  8);
            uuid_time |= ((temp_uuid[1] & 0xFFL) << 16);
            uuid_time |= ((temp_uuid[0] & 0xFFL) << 24);
            uuid_time |= ((temp_uuid[5] & 0xFFL) << 32);
            uuid_time |= ((temp_uuid[4] & 0xFFL) << 40);
            uuid_time |= ((temp_uuid[7] & 0xFFL) << 48);
            uuid_time |= ((temp_uuid[6] & 0x0FL) << 56);
            
            // first we'll remove the gregorian offset
            uuid_time -= GREGORIAN_CALENDAR_START_TO_UTC_START_OFFSET;

            // and convert to milliseconds as the system clock is in millis
            uuid_time /= MILLI_CONVERSION_FACTOR;

            // now check that the times are correct
            assertTrue(
                "Start time: " + startTime +
                    " was not before UUID timestamp: " + uuid_time,
                startTime  <= uuid_time);
            assertTrue(
                "UUID timestamp: " + uuid_time +
                    " was not before the start time: " + endTime,
                uuid_time <= endTime);            
        }
    }

    private void checkUUIDArrayForCorrectEthernetAddress(UUID[] uuidArray,
        EthernetAddress ethernetAddress)
    {
        for (int i = 0; i < uuidArray.length; i++)
        {
            byte[] uuid_ethernet_address = new byte[6];
            System.arraycopy(
                uuidArray[i].toByteArray(), 10, uuid_ethernet_address, 0, 6);
            byte[] ethernet_address = ethernetAddress.asByteArray();
            
            assertTrue(
                "UUID ethernet address did not equal passed ethernetAddress",
                Arrays.equals(ethernet_address, uuid_ethernet_address));
        }
    }
    
    private void checkUUIDArrayForNonNullUUIDs(UUID[] uuidArray)
    {
        for (int i = 0; i < uuidArray.length; i++)
        {
            assertFalse("UUID was null",
                       uuidArray[i].isNullUUID());
        }
    }
    /**************************************************************************
     * End Private Helper Methods for use in tests 
     *************************************************************************/
}
