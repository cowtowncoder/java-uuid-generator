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

package org.safehaus.uuid;

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
    private final String mDesc;

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
	StringBuffer b = new StringBuffer();
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

	mDesc = b.toString();
    }

    public String toString() { return mDesc; }

    public boolean equals(Object o)
    {
	if (o instanceof TagURI) {
	    return mDesc.equals(((TagURI) o).toString());
	}
	return false;
    }

    /**
     * A simple test harness is added to make (automated) testing of the
     * class easier. 
     */
    public static void main(String[] args)
    {
	System.out.println("TagURI.main()");
	System.out.println("--------------------");
	System.out.println();

	String[] auths = { "www.w3c.org", "www.google.com", "www.fi",
	 "tatu.saloranta@iki.fi"
	};
	String[] ids = { "1234", "/home/billg/public_html/index.html",
			"6ba7b810-9dad-11d1-80b4-00c04fd430c8",
			"foobar"
	};

	Calendar c = null;
	for (int i = 0; i < 4; ++i) {
	    // Let's just change the date & URL a bit:
	    switch (i) {
	    case 2:
		c.add(Calendar.MONTH, 1);
		break;
	    case 3:
		c.add(Calendar.DAY_OF_MONTH, -7);
		break;
	    }
	    for (int j = 0; j < 4; ++j) {
		TagURI t = new TagURI(auths[i], ids[j], c);
		System.out.println("tagURI: "+t);
	    }
	    if (c == null) {
		c = Calendar.getInstance();
	    }
	}
    }
}
