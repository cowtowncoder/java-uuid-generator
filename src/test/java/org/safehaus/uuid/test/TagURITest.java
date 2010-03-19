/* JUG Java Uuid Generator
 * TagURITest.java
 * Created on October 8, 2003, 12:22 AM
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

import java.util.Calendar;

import org.safehaus.uuid.TagURI;

/**
 * JUnit Test class for the org.safehaus.uuid.TagURI class.
 *
 * @author Eric Bie
 */
public class TagURITest extends TestCase
{
    private static final String[] AUTHORITIES =
    {
        "www.w3c.org",
        "www.google.com",
        "www.fi",
        "tatu.saloranta@iki.fi"
    };

    private static final String[] IDS =
    {
        "1234",
        "/home/billg/public_html/index.html",
        "6ba7b810-9dad-11d1-80b4-00c04fd430c8",
        "foobar"
    };
    
    public TagURITest(java.lang.String testName)
    {
        super(testName);
    }
    
    public static Test suite()
    {
        TestSuite suite = new TestSuite(TagURITest.class);
        return suite;
    }
    
    /**
     * Test of toString method, of class org.safehaus.uuid.TagURI.
     */
    public void testToString()
    {
        final Calendar CALENDAR = Calendar.getInstance();
        
        // we'll test that a few expected constructed TagURI's create the
        // expected strings
        
        // first, some tests with a null calendar
        for (int i = 0; i < 4; ++i)
        {
            for (int j = 0; j < 4; ++j)
            {
                TagURI tag_uri = new TagURI(AUTHORITIES[i], IDS[j], null);
                String expected = "tag:" + AUTHORITIES[i] + ":" + IDS[j];
                assertEquals(
                    "Expected string did not match generated toString()",
                    expected,
                    tag_uri.toString());
            }
        }

        // now some cases with date
        for (int i = 0; i < 4; ++i)
        {
            CALENDAR.set(Calendar.MONTH, Calendar.JULY);
            CALENDAR.set(Calendar.DAY_OF_MONTH, 4);

            for (int j = 0; j < 4; ++j)
            {
                TagURI tag_uri = new TagURI(AUTHORITIES[i], IDS[j], CALENDAR);
                String expected = "tag:" + AUTHORITIES[i] + "," +
                    CALENDAR.get(Calendar.YEAR) + "-" +
                    (CALENDAR.get(Calendar.MONTH) + 1) + "-" +
                    CALENDAR.get(Calendar.DAY_OF_MONTH) + ":" + IDS[j];
                assertEquals(
                    "Expected string did not match generated toString()",
                    expected,
                    tag_uri.toString());
            }
        }

        // now some cases with date such that day is left out
        // (first of the month)
        for (int i = 0; i < 4; ++i)
        {
            CALENDAR.set(Calendar.MONTH, Calendar.APRIL);
            CALENDAR.set(Calendar.DAY_OF_MONTH, 1);

            for (int j = 0; j < 4; ++j)
            {
                TagURI tag_uri = new TagURI(AUTHORITIES[i], IDS[j], CALENDAR);
                String expected = "tag:" + AUTHORITIES[i] + "," +
                    CALENDAR.get(Calendar.YEAR) + "-" +
                    (CALENDAR.get(Calendar.MONTH) + 1) + ":" + IDS[j];
                assertEquals(
                    "Expected string did not match generated toString()",
                    expected,
                    tag_uri.toString());
            }
        }

        // now some cases with date such that day and month are left out
        // (jan-1)
        for (int i = 0; i < 4; ++i)
        {
            CALENDAR.set(Calendar.MONTH, Calendar.JANUARY);
            CALENDAR.set(Calendar.DAY_OF_MONTH, 1);

            for (int j = 0; j < 4; ++j)
            {
                TagURI tag_uri = new TagURI(AUTHORITIES[i], IDS[j], CALENDAR);
                String expected = "tag:" + AUTHORITIES[i] + "," +
                    CALENDAR.get(Calendar.YEAR) + ":" + IDS[j];
                assertEquals(
                    "Expected string did not match generated toString()",
                    expected,
                    tag_uri.toString());
            }
        }
    }
    
    /**
     * Test of equals method, of class org.safehaus.uuid.TagURI.
     */
    public void testEquals()
    {
        // test passing null to equals returns false
        // (as specified in the JDK docs for Object)
        TagURI x = new TagURI(AUTHORITIES[1], IDS[2], null);
        assertFalse("equals(null) didn't return false",
                x.equals((Object)null));
        
        // test that passing an object which is not a TagURI returns false
        assertFalse("x.equals(non_TagURI_object) didn't return false",
                    x.equals(new Object()));
        
        // test a case where two TagURIs are definitly not equal
        TagURI w = new TagURI(AUTHORITIES[2], IDS[0], Calendar.getInstance());
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
        TagURI y = new TagURI(AUTHORITIES[1], IDS[2], null);
        assertFalse("x == y didn't return false",
                    x == y);
        assertTrue("y.equals(x) didn't return true",
                    y.equals(x));
        assertTrue("x.equals(y) didn't return true",
                    x.equals(y));
        
        // now we'll test transitivity
        TagURI z = new TagURI(AUTHORITIES[1], IDS[2], null);
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
}
