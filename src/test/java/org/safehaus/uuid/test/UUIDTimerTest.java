/* JUG Java Uuid Generator
 * UUIDTimerTest.java
 * Created on July 28, 2003, 10:09 PM
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

import java.security.SecureRandom;

import java.util.Arrays;
import java.util.Comparator;
import java.util.HashSet;
import java.util.Set;

import org.safehaus.uuid.UUIDTimer;

/**
 * JUnit Test class for the org.safehaus.uuid.UUIDTimer class.
 *
 * @author Eric Bie
 */
public class UUIDTimerTest extends TestCase
{
    // constants for use in the tests
    private static final int UUID_TIMER_ARRAY_LENGTH = 10;
    private static final int SIZE_OF_TEST_ARRAY = 10000;
    
    public UUIDTimerTest(java.lang.String testName)
    {
        super(testName);
    }
    
    public static Test suite()
    {
        TestSuite suite = new TestSuite(UUIDTimerTest.class);
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
     * Test of UUIDTimer(SecureRandom) constructor,
     * of class org.safehaus.uuid.UUIDTimer.
     */
    public void testSecureRandomUUIDTimerConstructor()
    {
        // try passing a null SecureRandom argument
        try
        {
            /*UUIDTimer uuid_timer =*/ new UUIDTimer((SecureRandom)null);
            // if we reach here we didn't catch what we should have
            fail("Expected exception not caught");
        }
        catch (NullPointerException ex)
        {
            // caught the expected exception, this is good, just go on
        }
        catch (Exception ex)
        {
            fail("Unexpected exception caught");
        }
        
        // now construct a valid case
        SecureRandom secure_random = new SecureRandom();
        UUIDTimer uuid_timer = new UUIDTimer(secure_random);
        
        // we'll do a simple run to see that it at least produces output
        byte[] test_array = new byte[UUID_TIMER_ARRAY_LENGTH];
        uuid_timer.getAndSetTimestamp(test_array);
        // check that it's not all null
        assertArrayNotEqual(test_array,
                            new byte[UUID_TIMER_ARRAY_LENGTH],
                            UUID_TIMER_ARRAY_LENGTH);
    }    
    /**************************************************************************
     * End constructor tests
     *************************************************************************/

    /**
     * Test of getAndSetTimestamp method, of class org.safehaus.uuid.UUIDTimer.
     */
    public void testGetTimestamp()
    {
        // constant for use in this test
        final int EXTRA_DATA_LENGTH = 9;
        
        // construct a UUIDTimer
        SecureRandom secure_random = new SecureRandom();
        UUIDTimer uuid_timer = new UUIDTimer(secure_random);
        
        // test an array thats too small
        try
        {
            byte[] test_array = new byte[UUID_TIMER_ARRAY_LENGTH - 1];
            uuid_timer.getAndSetTimestamp(test_array);
            // if we get here, we didn't catch the expected exception
            fail("Expected exception not caught");
        }
        catch (ArrayIndexOutOfBoundsException ex)
        {
            // caught the expected exception, this is good, just go on
        }
        catch (Exception ex)
        {
            fail("Unexpected exception caught");
        }
        
        // construct a valid array exactly big enough and see that it works
        byte[] test_array = new byte[UUID_TIMER_ARRAY_LENGTH];
        uuid_timer.getAndSetTimestamp(test_array);
        // check that it's not all null
        assertArrayNotEqual(test_array,
                            new byte[UUID_TIMER_ARRAY_LENGTH],
                            UUID_TIMER_ARRAY_LENGTH);
        
        // construct a valid array bigger then we need
        // and make sure getAndSetTimeStamp only touches the begining part
        test_array = new byte[UUID_TIMER_ARRAY_LENGTH + EXTRA_DATA_LENGTH];
        Arrays.fill(test_array, (byte)'x');
        uuid_timer.getAndSetTimestamp(test_array);
        for (int i = 0; i < EXTRA_DATA_LENGTH; ++i)
        {
            assertEquals("test_array element was corrupted",
                        (byte)'x',
                        test_array[i + UUID_TIMER_ARRAY_LENGTH]);
        }
        // check that the timer portion is not all null
        assertArrayNotEqual(test_array,
                            new byte[UUID_TIMER_ARRAY_LENGTH],
                            UUID_TIMER_ARRAY_LENGTH);
        
        // now make a bunch of timer elements and validate that they are
        // are well behaved timer elements
        byte[][] array_of_uuid_timer_byte_arrays =
            new byte[SIZE_OF_TEST_ARRAY][UUID_TIMER_ARRAY_LENGTH];
        
        // before generating all the uuid timer arrays, get the start time
        long start_time = System.currentTimeMillis();
        
        // now create the array of uuid timer output arrays
        for (int i = 0; i < array_of_uuid_timer_byte_arrays.length; i++)
        {
            uuid_timer.getAndSetTimestamp(array_of_uuid_timer_byte_arrays[i]);
        }
        
        // now capture the end time
        long end_time = System.currentTimeMillis();
        
        // convert the array into array of longs holding the numerical values
        Long[] uuid_timer_array_of_longs =
            convertArrayOfByteArraysToArrayOfLongs(
                array_of_uuid_timer_byte_arrays);
        
        // check that none of the UUID Timer arrays are all null
        checkUUIDTimerLongArrayForNonNullTimes(uuid_timer_array_of_longs);

        // check that all UUID Timers were generated with correct order
        checkUUIDTimerLongArrayForCorrectOrdering(uuid_timer_array_of_longs);
        
        // check that all UUID Timers were unique
        checkUUIDTimerLongArrayForUniqueness(uuid_timer_array_of_longs);
        
        // check that all timestamps are between the start and end time
        checkUUIDTimerLongArrayForCorrectCreationTime(
            uuid_timer_array_of_longs, start_time, end_time);
    }
    
    /**************************************************************************
     * Begin private helper functions for use in tests
     *************************************************************************/
    private Long[] convertArrayOfByteArraysToArrayOfLongs(
        byte[][] uuidTimerArrayOfByteArrays)
    {
        Long[] array_of_longs = new Long[uuidTimerArrayOfByteArrays.length];
        for (int i = 0; i < uuidTimerArrayOfByteArrays.length; i++)
        {
            // collect the UUID time stamp which is
            // the number of 100-nanosecond intervals since
            // 00:00:00.00 15 October 1582
            long uuid_timer = 0L;
            uuid_timer |= ((uuidTimerArrayOfByteArrays[i][3] & 0xFFL) <<  0);
            uuid_timer |= ((uuidTimerArrayOfByteArrays[i][2] & 0xFFL) <<  8);
            uuid_timer |= ((uuidTimerArrayOfByteArrays[i][1] & 0xFFL) << 16);
            uuid_timer |= ((uuidTimerArrayOfByteArrays[i][0] & 0xFFL) << 24);
            uuid_timer |= ((uuidTimerArrayOfByteArrays[i][5] & 0xFFL) << 32);
            uuid_timer |= ((uuidTimerArrayOfByteArrays[i][4] & 0xFFL) << 40);
            uuid_timer |= ((uuidTimerArrayOfByteArrays[i][7] & 0xFFL) << 48);
            uuid_timer |= ((uuidTimerArrayOfByteArrays[i][6] & 0xFFL) << 56);
            
            array_of_longs[i] = new Long(uuid_timer);
        }
        
        return array_of_longs;
    }

    private class ReverseOrderUUIDTimerLongComparator implements Comparator
    {
        // this Comparator class has a compare which orders reverse of the
        // compare method in UUIDTimerArrayComparator (so we can be sure our
        // arrays below are 'not ordered in sorted order'
        // before we sort them).
        public int compare(Object o1, Object o2)
        {           
            Long uuid_timer_long1 = (Long)o1;
            Long uuid_timer_long2 = (Long)o2;
            
            return -uuid_timer_long1.compareTo(uuid_timer_long2);
        }
        
        // we are only implementing equals because it's needed, super should do
        public boolean equals(Object o)
        {
            return super.equals(o);
        }
    }
    
    private void checkUUIDTimerLongArrayForCorrectOrdering(
        Long[] uuidTimerLongArray)
    {
        // now we'll clone the array and reverse it
        Long[] uuid_timer_sorted_arrays = (Long[])uuidTimerLongArray.clone();
        assertEquals("Cloned array length did not match",
                    uuidTimerLongArray.length,
                    uuid_timer_sorted_arrays.length);
        
        ReverseOrderUUIDTimerLongComparator rev_order_uuid_timer_comp =
            new ReverseOrderUUIDTimerLongComparator();
        Arrays.sort(uuid_timer_sorted_arrays, rev_order_uuid_timer_comp);
        
        // let's check that the array is actually reversed
        int sorted_arrays_length = uuid_timer_sorted_arrays.length;
        for (int i = 0; i < sorted_arrays_length; i++)
        {
            assertTrue(
                "Reverse order check on uuid timer arrays failed" +
                    " on element " + i + ": " +
                    uuidTimerLongArray[i].longValue() + " does not equal " +
                    uuid_timer_sorted_arrays[
                        sorted_arrays_length - (1 + i)].longValue(),
                uuidTimerLongArray[i].equals(
                    uuid_timer_sorted_arrays[sorted_arrays_length - (1 + i)]));
        }
        
        // now let's sort the reversed array and check that it sorted to
        // the same order as the original
        Arrays.sort(uuid_timer_sorted_arrays);
        for (int i = 0; i < sorted_arrays_length; i++)
        {
            assertTrue(
                "Same order check on uuid timer arrays failed on element " +
                    i + ": " + uuidTimerLongArray[i].longValue() +
                    " does not equal " +
                    uuid_timer_sorted_arrays[i].longValue(),
                uuidTimerLongArray[i].equals(uuid_timer_sorted_arrays[i]));
        }
    }

    private void checkUUIDTimerLongArrayForUniqueness(
        Long[] uuidTimerLongArray)
    {
        // here we'll assert that all elements in the list are not equal to
        // each other (aka, there should be no duplicates) we'll do this by
        // inserting all elements into a Set and making sure none of them
        // were already present (add will return false if it was already there)
        Set set = new HashSet();
        for (int i = 0; i < uuidTimerLongArray.length; i++)
        {
            assertTrue("Uniqueness test failed on insert into HashSet",
                set.add(uuidTimerLongArray[i]));
            assertFalse(
                "Paranoia Uniqueness test failed (second insert into HashSet)",
                set.add(uuidTimerLongArray[i]));
        }
    }
    
    private void checkUUIDTimerLongArrayForCorrectCreationTime(
        Long[] uuidTimerLongArray,
        long startTime,
        long endTime)
    {
        // we need to convert from 100-naonsecond units (as used in UUIDs)
        // to millisecond units as used in UTC based time
        final long MILLI_CONVERSION_FACTOR = 10000L;
        // Since System.currentTimeMillis() returns time epoc time
        // (from 1-Jan-1970), and UUIDs use time from the beginning of
        // Gregorian calendar (15-Oct-1582) we have a offset for correction
        final long GREGORIAN_CALENDAR_START_TO_UTC_START_OFFSET =
            122192928000000000L;
        
        assertTrue("Start time was not before the end time",
                startTime < endTime);
        
        // let's check that all the uuid timer longs in the array have a
        // timestamp which lands between the start and end time
        for (int i = 0; i < uuidTimerLongArray.length; i++)
        {
            long uuid_time = uuidTimerLongArray[i].longValue();
            
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
                    " was not before the end time: " + endTime,
                uuid_time <= endTime);
        }
    }

    private void checkUUIDTimerLongArrayForNonNullTimes(
        Long[] uuidTimerLongArray)
    {
        for (int i = 0; i < uuidTimerLongArray.length; i++)
        {
            assertFalse("Timer Long was null",
                0 == uuidTimerLongArray[i].longValue());
        }
    }

    private void assertArrayNotEqual(byte[] array1, byte[] array2, int length)
    {
        assertTrue("array1 was not equal or longer then length",
                    array1.length >= length);
        assertTrue("array2 was not equal or longer then length",
                    array2.length >= length);
        
        for (int i = 0; i < length; ++i)
        {
            // we know the arrays aren't equal the first time we
            // fine an array element that isn't equal.
            // in that case just return
            if (array1[i] != array2[i])
            {
                return;
            }
        }
        // if we get out of the loop, both arrays were identical, so fail
        fail("All elements of Array1 were equal to all elements of Array2");
    }
    /**************************************************************************
     * End private helper functions for use in tests
     *************************************************************************/
}
