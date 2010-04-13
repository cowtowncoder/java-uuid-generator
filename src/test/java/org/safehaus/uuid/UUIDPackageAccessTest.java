/* JUG Java Uuid Generator
 * UUIDPackageAccessTest.java
 * Created on October 7, 2003, 7:56 PM
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

import junit.framework.*;

import org.safehaus.uuid.UUID;

/**
 * JUnit Test class for checking the package access
 * methods of the org.safehaus.uuid.UUID class.
 *
 * @author Eric Bie
 */
public class UUIDPackageAccessTest extends TestCase
{
    public UUIDPackageAccessTest(java.lang.String testName)
    {
        super(testName);
    }
    
    public static Test suite()
    {
        TestSuite suite = new TestSuite(UUIDPackageAccessTest.class);
        return suite;
    }
    
    public static void main(String[] args)
    {
        junit.textui.TestRunner.run(suite());
    }
    
    /**************************************************************************
     * Begin constructor tests
     *************************************************************************/
    /**
     * Test of UUID(int, byte[]) constructor, of class org.safehaus.uuid.UUID.
     */
    public void testTypeAndByteArrayUUIDConstructor()
    {
        // passing null
        try
        {
            /*UUID uuid =*/ new UUID(UUID.TYPE_RANDOM_BASED, (byte[])null);
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
        
        // passing array that is too small
        try
        {
            /*UUID uuid =*/
                new UUID(UUID.TYPE_RANDOM_BASED,
                    new byte[UUID_BYTE_ARRAY_LENGTH - 1]);
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

        UUID uuid;
        
        // test creating an array from a good byte array with extra data on end
        // 09-Sep-2008, tatu: nope, not valid any more: must be 16 bytes sharp:
        /*
        uuid = new UUID(UUID.TYPE_RANDOM_BASED,
                VALID_UUID_BYTE_ARRAY_WITH_EXTRA_END);
        assertEquals("constructor did not create expected UUID",
                    MIXED_CASE_VALID_UUID_STRING.toLowerCase(),
                    uuid.toString().toLowerCase());
        */
        
        // test creating an array from a good byte array with the right type
        // Random UUID in this case
        uuid = new UUID(UUID.TYPE_RANDOM_BASED, VALID_UUID_BYTE_ARRAY);
        assertEquals("constructor did not create expected UUID",
                    MIXED_CASE_VALID_UUID_STRING.toLowerCase(),
                    uuid.toString().toLowerCase());

        // test creating an array from a good byte array with the right type
        // time based UUID in this case
        uuid = new UUID(UUID.TYPE_TIME_BASED, TIME_BASED_UUID_BYTE_ARRAY);
        assertEquals("constructor did not create expected UUID",
                    TIME_BASED_UUID_STRING.toLowerCase(),
                    uuid.toString().toLowerCase());

        // test name based UUID in this case
        uuid = new UUID(UUID.TYPE_NAME_BASED, NAME_BASED_UUID_BYTE_ARRAY);
        assertEquals("constructor did not create expected UUID",
                    NAME_BASED_UUID_STRING.toLowerCase(),
                    uuid.toString().toLowerCase());
        
        // test DCE based UUID in this case
        uuid = new UUID(UUID.TYPE_DCE, DCE_BASED_UUID_BYTE_ARRAY);
        assertEquals("constructor did not create expected UUID",
                    DCE_BASED_UUID_STRING.toLowerCase(),
                    uuid.toString().toLowerCase());

        /*
         * technically, this test does not work (this constructor always pokes
         * a version into the UUID, even if you pass UUID.TYPE_NULL
         * since this is a default access constructor, this is likely
         * acceptable behavior, but test is here and commented out in case
         * there is a desire for this to work differently
         */
//        // test that creating a uuid from a zero'd array with TYPE_NULL
//        // gives us a null UUID (null UUID is array of all 0s)
//        uuid = new UUID(UUID.TYPE_NULL, new byte[UUID_BYTE_ARRAY_LENGTH]);
//        assertEquals("constructor did not create expected null UUID",
//                    NULL_UUID_STRING,
//                    uuid.toString());
//        assertTrue("NULL UUID byte arrays were not equal", 
//            Arrays.equals(NULL_UUID_BYTE_ARRAY, uuid.toByteArray()));
    }
    /**************************************************************************
     * End constructor tests
     *************************************************************************/
    
    /**************************************************************************
     * Begin private constants for use in tests above
     *************************************************************************/
    private static final int UUID_BYTE_ARRAY_LENGTH = 16;

    //private static final String UPPER_CASE_VALID_UUID_STRING = "4D687664-3A1E-4F30-ACC1-87F59306D30C";
    private static final String MIXED_CASE_VALID_UUID_STRING = "4d687664-3A1e-4F30-aCc1-87F59306d30C";
    //private static final String LOWER_CASE_VALID_UUID_STRING = "4d687664-3a1e-4f30-acc1-87f59306d30c";
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
    private static final byte[] VALID_UUID_BYTE_ARRAY_WITH_EXTRA_END =
    {
        (byte)0x4d, (byte)0x68, (byte)0x76, (byte)0x64,
        (byte)0x3a, (byte)0x1e, (byte)0x4f, (byte)0x30,
        (byte)0xac, (byte)0xc1, (byte)0x87, (byte)0xf5,
        (byte)0x93, (byte)0x06, (byte)0xd3, (byte)0x0c,
        'o', 'n', ' ', 't', 'h', 'e', ' ', 'e', 'n', 'd',
        ' ', 'a', 's', ' ', 'w', 'e', 'l', 'l'
    };
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
    
    // valid null UUID string
    //private static final String NULL_UUID_STRING = "00000000-0000-0000-0000-000000000000";
    //private static final byte[] NULL_UUID_BYTE_ARRAY = new byte[UUID_BYTE_ARRAY_LENGTH];
    
    // valid time based UUID string
    private static final String TIME_BASED_UUID_STRING =
        "ebb8e8fe-b1b1-11d7-8adb-00b0d078fa18";
    private static final byte[] TIME_BASED_UUID_BYTE_ARRAY =
    {
        (byte)0xeb, (byte)0xb8, (byte)0xe8, (byte)0xfe,
        (byte)0xb1, (byte)0xb1, (byte)0x11, (byte)0xd7,
        (byte)0x8a, (byte)0xdb, (byte)0x00, (byte)0xb0,
        (byte)0xd0, (byte)0x78, (byte)0xfa, (byte)0x18
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
    private static final String DCE_BASED_UUID_STRING =
        "01234567-0123-2000-8000-0123456789ab";
    private static final byte[] DCE_BASED_UUID_BYTE_ARRAY =
    {
        (byte)0x01, (byte)0x23, (byte)0x45, (byte)0x67,
        (byte)0x01, (byte)0x23, (byte)0x20, (byte)0x00,
        (byte)0x80, (byte)0x00, (byte)0x01, (byte)0x23,
        (byte)0x45, (byte)0x67, (byte)0x89, (byte)0xab
    };
    /**************************************************************************
     * End private constants for use in tests above
     *************************************************************************/
}
