/* JUG Java UUID Generator
 *
 * Copyright (c) 2010 Tatu Saloranta
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

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.UUID;

import com.fasterxml.uuid.impl.TimeBasedEpochGenerator;

import junit.framework.TestCase;

public class UUIDComparatorTest
    extends TestCase
{
    public void testIntComp()
    {
        assertEquals(0, UUIDComparator.compareUInts(123, 123));
        assertEquals(0, UUIDComparator.compareUInts(-9999, -9999));
        assertEquals(0, UUIDComparator.compareUInts(0, 0));
        assertEquals(0, UUIDComparator.compareUInts(Integer.MIN_VALUE, Integer.MIN_VALUE));
        assertEquals(0, UUIDComparator.compareUInts(Integer.MAX_VALUE, Integer.MAX_VALUE));

        assertTrue(UUIDComparator.compareUInts(0, 5) < 0);
        assertTrue(UUIDComparator.compareUInts(5, 0) > 0);

        assertTrue(UUIDComparator.compareUInts(4, 0xFFFFFFFE) < 0);
        assertTrue(UUIDComparator.compareUInts(0xFFFFFFFE, 129) > 0);

        assertTrue(UUIDComparator.compareUInts(0xFFFFFFFC, 0xFFFFFFFE) < 0);
        assertTrue(UUIDComparator.compareUInts(0xFFFFFFFE, 0xFFFFFFFC) > 0);
        assertTrue(UUIDComparator.compareUInts(0xFFFFFF17, 0xFFFFFF00) > 0);
        assertTrue(UUIDComparator.compareUInts(0xFFFFFF00, 0xFFFFFF17) < 0);
    }

    public void testLongComp()
    {
        assertEquals(0, UUIDComparator.compareULongs(123L, 123L));
        assertEquals(0, UUIDComparator.compareULongs(-9999L, -9999L));
        assertEquals(0, UUIDComparator.compareULongs(0L, 0L));
        assertEquals(0, UUIDComparator.compareULongs(Long.MIN_VALUE, Long.MIN_VALUE));
        assertEquals(0, UUIDComparator.compareULongs(Long.MAX_VALUE, Long.MAX_VALUE));

        assertTrue(UUIDComparator.compareULongs(0L, 5L) < 0);
        assertTrue(UUIDComparator.compareULongs(5L, 0L) > 0);

        // Ok, repeat int values first
        assertTrue(UUIDComparator.compareULongs(4L, 0xFFFFFFFEL) < 0);
        assertTrue(UUIDComparator.compareULongs(0xFFFFFFFEL, 129L) > 0);
        assertTrue(UUIDComparator.compareULongs(0xFFFFFFFCL, 0xFFFFFFFEL) < 0);
        assertTrue(UUIDComparator.compareULongs(0xFFFFFF17L, 0xFFFFFF00L) > 0);

        assertTrue(UUIDComparator.compareULongs(1L, 0xffffffffFFFFFFFEL) < 0);
        assertTrue(UUIDComparator.compareULongs(0xffffffffFFFFFFFEL, 13L) > 0);
        assertTrue(UUIDComparator.compareULongs(0xffffffffFFFFFFFCL, 0xffffffffFFFFFFFEL) < 0);
        assertTrue(UUIDComparator.compareULongs(0xffffffffFFFFFFFEL, 0xffffffffFFFFFFFCL) > 0);
        assertTrue(UUIDComparator.compareULongs(0xffffffffFFFFFF17L, 0xffffffffFFFFFF00L) > 0);
        assertTrue(UUIDComparator.compareULongs(0xffffffffFFFFFF00L, 0xffffffffFFFFFF17L) < 0);
    }

    /*
     * [Issue#13]
     */
    public void testSorting()
    {
        String[] src = new String[] {
            "7ef7c38a-bb6e-11e3-9e8f-000000000000",
            "7f905a0b-bb6e-11e3-9e8f-000000000000",
            "8028f08c-bb6e-11e3-9e8f-000000000000",
            "80c1870d-bb6e-11e3-9e8f-000000000000"
        };

        /* 03-Apr-2014, tatu: NOTE: JDK's UUID.compareTo() is broken, and it can
         *   NOT be used. Which is why we have "UUIDComparator" that does work.
         */
        final UUIDComparator comp = new UUIDComparator();
        for (int i = 0; i < src.length-1; ++i) {
            
            UUID u1 = UUID.fromString(src[i]);
            UUID u2 = UUID.fromString(src[i+1]);

            assertEquals(0, comp.compare(u1, u1));
            assertEquals(0, comp.compare(u2, u2));

            int x = comp.compare(u1, u2);
            if (x >= 0) {
                fail("Entry #"+i+" should have value < 0, had "+x);
            }
            int y = comp.compare(u2, u1);
            if (y <= 0) {
                fail("Entry #"+i+" should have value > 0, had "+y);
            }
        }
    }

    public void testSortingMV7() throws Exception {
        final int count = 10000000;
        Random entropy = new Random(0x666); 
        final TimeBasedEpochGenerator generator = Generators.timeBasedEpochGenerator(entropy);
        List<UUID> created = new ArrayList<UUID>(count);
        for (int i = 0; i < count; i++) {
            created.add(generator.generate());
        }
        List<UUID> sortedUUID = new ArrayList<UUID>(created);
        sortedUUID.sort(new UUIDComparator());
        HashSet<UUID> unique = new HashSet<UUID>(count);
        
        for (int i = 0; i < created.size(); i++) {
            assertEquals("Error at: " + i, created.get(i), sortedUUID.get(i));
            if (!unique.add(created.get(i))) {
                System.out.println("Duplicate at: " + i);
            }
        }
    }
}
