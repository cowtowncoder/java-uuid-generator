/* JUG Java Uuid Generator
 *
 * Copyright (c) 2002 Tatu Saloranta, tatu.saloranta@iki.fi
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

import java.util.*;

/**
 * A class that allows creation of tagURI instances.
 *
 * TagURIs are specified in IETF draft <draft-kindberg-tag-uri-01.txt>;
 * available for example at:
 * 
 * http://sunsite.cnlab-switch.ch/ftp/mirror/internet-drafts/draft-kindberg-tag-uri-01.txt
 */
public class TagURI
{
    private final String _desc;

    /**
     * Constructor for creating tagURI instances.
     *
     * Typical string representations of tagURIs may look like:
     * <ul>
     * <li>tag:hp1.hp.com,2001:tst.1234567890
     * <li>tag:fred@flintstone.biz,2001-07-02:rock.123
     * </ul>
     * (see tagURI draft for more examples and full explanation of the
     * basic concepts)
     *
     * @param authority Authority that created tag URI; usually either a
     *   fully-qualified domain name ("www.w3c.org") or an email address
     *   ("tatu.saloranta@iki.fi").
     * @param identifier A locally unique identifier; often file path or
     *   URL path component (like, "tst.1234567890", "/home/tatu/index.html")
     * @param date Date to add as part of the tag URI, if any; null is used
     *   used to indicate that no datestamp should be added.
     * 
     */
    public TagURI(String authority, String identifier, Calendar date)
    {
	StringBuilder b = new StringBuilder();
	b.append("tag:");
	b.append(authority);
	if (date != null) {
	    b.append(',');
	    b.append(date.get(Calendar.YEAR));
	    // Month is optional if it's "january" and day is "1st":
	    int month = date.get(Calendar.MONTH) - Calendar.JANUARY + 1;
	    int day = date.get(Calendar.DAY_OF_MONTH);
	    if (month != 1 || day != 1) {
		b.append('-');
		b.append(month);
	    }
	    if (day != 1) {
		b.append('-');
		b.append(day);
	    }
	}
	b.append(':');
	b.append(identifier);

	_desc = b.toString();
    }

    public String toString() { return _desc; }

    public boolean equals(Object o)
    {
	if (o instanceof TagURI) {
	    return _desc.equals(((TagURI) o).toString());
	}
	return false;
    }
}
