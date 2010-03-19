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

import java.io.*;
import java.security.*;
import java.util.*;

/**
 * Class that implements command-line interface for accessing functionality
 * implemented by {@link UUIDGenerator}.
 */
public class Jug
{
    private final static HashMap mTypes = new HashMap();
    static {
        mTypes.put("time-based", "t");
        mTypes.put("random-based", "r");
        mTypes.put("name-based", "n");
        mTypes.put("tag-uri-no-timestamp", "u");
        mTypes.put("tag-uri-with-timestamp", "U");
    }

    private final static HashMap mOptions = new HashMap();
    static {
        mOptions.put("count", "c");
        mOptions.put("ethernet-address", "e");
        mOptions.put("help", "h");
        mOptions.put("namespace", "s");
        mOptions.put("name", "n");
        mOptions.put("performance", "p");
        mOptions.put("verbose", "v");
    }
    
    static void printUsage()
    {
        String clsName = Jug.class.getName();
        System.err.println("Usage: java "+clsName+" [options] type");
        System.err.println("Where options are:");
        System.err.println("  --count / -c <number>: will generate <number> UUIDs (default: 1");
        System.err.println("  --ethernet-address / -e <ether-address>: defines the ethernet address");
        System.err.println("    (in xx:xx:xx:xx:xx:xx notation, usually obtained using 'ifconfig' etc)");
        System.err.println("    to use with time-based UUID generation");
        System.err.println("  --help / -h: lists the usage (ie. what you see now)");
        System.err.println("  --name / -n: specifies");
        System.err.println("     o name for name-based UUID generation");
        System.err.println("     o 'information' part of tag-URI for tag-URI UUID generation");
        System.err.println("  --namespace / -s: specifies");
        System.err.println("    o the namespace (DNS or URL) for name-based UUID generation");
        System.err.println("    o 'authority' part of tag-URI for tag-URI UUID generation;");
        System.err.println("        (fully-qualified domain name, email address)");
        System.err.println("  --performance / -p: measure time it takes to generate UUID(s).");
        System.err.println("    [note that UUIDs are not printed out unless 'verbose' is also specified]");
        System.err.println("  --verbose / -v: lists additional information about UUID generation\n    (by default only UUIDs are printed out (to make it usable in scripts)");
        System.err.println("And type is one of:");
        System.err.println("  time-based / t: generate UUID based on current time and optional\n    location information (defined with -e option)");
        System.err.println("  random-based / r: generate UUID based on the default secure random number generator");
        System.err.println("  name-based / n: generate UUID based on the na the default secure random number generator");
    }

    private static void printMap(Map m, PrintStream out, boolean option)
    {
        Iterator it = m.keySet().iterator();
        int count = 0, len = m.size();

        while (it.hasNext()) {
            String key = (String) it.next();
            String value = (String) m.get(key);

            if (++count > 1) {
                if (count < len) {
                    out.print(", ");
                } else {
                    out.print(" and ");
                }
            }
            if (option) {
                out.print("--");
            }
            out.print(key);
            out.print(" (");
            if (option) {
                out.print("-");
            }
            out.print(value);
            out.print(")");
        }
    }

    public static void main(String[] args)
    {
        if (args.length == 0) {
            printUsage();
            return;
        }

        int count = args.length;
        String type = args[count-1];
        boolean verbose = false;
        int genCount = 1;
        String name = null, nameSpace = null;
        EthernetAddress addr = null;
        boolean performance = false;

        --count;

        // Type we recognize?
        String tmp = (String) mTypes.get(type);
        if (tmp == null) {
            if (!mTypes.containsValue(type)) {
                System.err.println("Unrecognized UUID generation type '"+
                                   type+"'; currently available ones are:");
                printMap(mTypes, System.err, false);
                System.err.println();
                System.exit(1);
            }
        } else {
            // Long names get translated to shorter ones:
            type = tmp;
        }

        for (int i = 0; i < count; ++i) {
            String opt = args[i];

            if (opt.length() == 0 || opt.charAt(0) != '-') {
                System.err.println("Unrecognized option '"+opt+"' (missing leading hyphen?), exiting.");
                System.exit(1);
            }

            char option = (char)0;
            if (opt.startsWith("--")) {
                String o = (String) mOptions.get(opt.substring(2));
                // Let's translate longer names to simple names:
                if (o != null) {
                    option = o.charAt(0);
                }
            } else {
                if (mOptions.containsValue(opt.substring(1))) {
                    option = opt.charAt(1);
                }
            }

            if (option == (char) 0) {
                System.err.println("Unrecognized option '"+opt+"'; exiting.");
                System.err.print("[options currently available are: ");
                printMap(mOptions, System.err, true);
                System.err.println("]");
                System.exit(1);
            }

            // K. Now we have one-letter options to handle:
            try {
                String next;
                switch (option) {
                case 'c':
                    // Need a number now:
                    next = args[++i];
                    try {
                        genCount = Integer.parseInt(next);
                    } catch (NumberFormatException nex) {
                        System.err.println("Invalid number argument for option '"+opt+"', exiting.");
                        System.exit(1);
                    }
                    if (genCount < 1) {
                        System.err.println("Invalid number argument for option '"+opt+"'; negative numbers not allowed, ignoring (defaults to 1).");
                    }
                    break;
                case 'e':
                    // Need the ethernet address:
                    next = args[++i];
                    try {
                        addr = EthernetAddress.valueOf(next);
                    } catch (NumberFormatException nex) {
                        System.err.println("Invalid ethernet address for option '"+opt+"', error: "+nex.toString());
                        System.exit(1);
                    }
                    break;
                case 'h':
                    printUsage();
                    return;
                case 'n':
                    // Need the name
                    name = args[++i];
                    break;
                case 'p': // performance:
                    performance = true;
                    break;
                case 's':
                    // Need the namespace id
                    nameSpace = args[++i];
                    break;
                case 'v':
                    verbose = true;
                    break;
                }
            } catch (IndexOutOfBoundsException ie) {
                // We get here when an arg is missing...
                System.err.println("Missing argument for option '"+opt+"', exiting.");
                System.exit(1);
            }
        } // for (int i = 0....)

        /* Ok, args look ok so far. Now to the generation; some args/options
         * can't be validated without knowing the type:
         */
        boolean timestamp = false;
        char typeC = type.charAt(0);
        UUID nsUUID = null;
        TagURI nsTagURI = null;

        UUIDGenerator uuidGenerator = UUIDGenerator.getInstance();
        boolean usesRnd = false;

        switch (typeC) {
        case 't': // time-based
            usesRnd = true;
            // No address specified? Need a dummy one...
            if (addr == null) {
                if (verbose) {
                    System.out.print("(no address specified, generating dummy address: ");
                }
                addr = uuidGenerator.getDummyAddress();
                if (verbose) {
                    System.out.print(addr.toString());
                    System.out.println(")");
                }
            }
            break;
        case 'r': // random-based
            usesRnd = true;
            if (verbose) {
                Random r = uuidGenerator.getRandomNumberGenerator();
                if (r instanceof SecureRandom) {
                    SecureRandom sr = (SecureRandom) r;
                    System.out.print("(using the default random generator, info = '"+sr.getProvider().getInfo()+"')");
                } else {
                    System.out.print("(using the default random generator, class: "+r.getClass().toString()+".");
                }
            }
            break;
        case 'U': // tagURI-based, use timestamp
            timestamp = true;
            // falldown to next
        case 'n': // name-based
            // falldown to next
        case 'u': // tagURI-based, no timestamp
            if (name == null) {
                System.err.println("--name-space (-s) - argument missing when using method that requires it, exiting.");
                System.exit(1);
            }
            if (name == null) {
                System.err.println("--name (-n) - argument missing when using method that requires it, exiting.");
                System.exit(1);
            }
            if (typeC == 'n') {
                String orig = nameSpace;
                nameSpace = nameSpace.toLowerCase();
                if (nameSpace.equals("url")) {
                    nameSpace = UUID.NAMESPACE_URL;
                } else  if (nameSpace.equals("dns")) {
                    nameSpace = UUID.NAMESPACE_DNS;
                } else {
                    System.err.println("Unrecognized namespace '"+orig
                                       +"'; only DNS and URL allowed for name-based generation.");
                    System.exit(1);
                }
		
                try {
                    nsUUID = new UUID(nameSpace);
                } catch (NumberFormatException nex) {
                    System.err.println("Internal error: "+nex.toString());
                    System.err.println("Exiting.");
                    System.exit(1);
                }
            } else if (!timestamp) {
                nsTagURI = new TagURI(nameSpace, name, null);
                if (verbose) {
                    System.out.println("(Using tagURI '"+nsTagURI.toString()+"')");
                }
            }

            if (verbose) {
                MessageDigest md = uuidGenerator.getHashAlgorithm();
                System.out.println("(Using the default hash algorithm, type = '"
                                   +md.getAlgorithm()+"', provider info - '"
                                   +md.getProvider().getInfo()+"')");
            }
            break;
        }

        // And then let's rock:
        if (verbose) {
            System.out.println();
        }

        /* When measuring performance, make sure that the random number
         * generator is initialized prior to measurements...
         */
        long now = 0L;

        if (performance) {
            // No need to pre-initialize for name-based schemes?
            if (usesRnd) {
                if (verbose) {
                    System.out.println("(initializing random number generator before UUID generation so that performance measurements are not skewed due to one-time init costs)");
                }
                Random r = uuidGenerator.getRandomNumberGenerator();
                byte[] tmpB = new byte[1];
                r.nextBytes(tmpB);
                if (verbose) {
                    System.out.println("(random number generator initialized ok)");
                }
            }
            now = System.currentTimeMillis();
        }

        for (int i = 0; i < genCount; ++i) {
            UUID uuid = null;
            switch (typeC) {
            case 't': // time-based
                uuid = uuidGenerator.generateTimeBasedUUID(addr);
                break;
            case 'r': // random-based
                uuid = uuidGenerator.generateRandomBasedUUID();
                break;
            case 'n': // name-based
                uuid = uuidGenerator.generateNameBasedUUID(nsUUID, name);
                break;
            case 'u': // tagURI-based, no timestamp
            case 'U': // tagURI-based, use timestamp
                if (timestamp) {
                    nsTagURI = new TagURI(nameSpace, name, Calendar.getInstance());
                    if (verbose) {
                        System.out.println("(Using tagURI '"+nsTagURI.toString()+"')");
                    }
                }
                uuid = uuidGenerator.generateTagURIBasedUUID(nsTagURI);
                break;
            }
            if (verbose) {
                System.out.print("UUID: ");
            }
            if (!performance || verbose) {
                System.out.println(uuid.toString());
            }
        } // for (int i = 0; ...)

        if (verbose) {
            System.out.println("Done.");
        }
        if (performance) {
            now = System.currentTimeMillis() - now;
            long avg = (now * 10 + (genCount / 2)) / genCount;
            System.out.println("Performance: took "+now+" milliseconds to generate (and print out) "+genCount+" UUIDs; average being "+(avg / 10)+"."+(avg%10)+" msec.");
        }
    }
}
