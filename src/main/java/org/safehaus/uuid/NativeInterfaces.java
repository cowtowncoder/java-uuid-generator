/* JUG Java Uuid Generator
 *
 * Copyright (c) 2002-2004 Tatu Saloranta, tatu.saloranta@iki.fi
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
import java.util.*;

/**
 * Proxy class that uses JNI-based functionality to obtain information
 * about local interfaces.
 *<p>
 * Note that there are two different places where platform-dependant
 * native code libraries can be located under:
 * <ul>
 * <li>System-dependant standard library location (in unix systems
 *    often something like /lib or /usr/lib). This is not the default;
 *    if you want to enable this loading, you need to call
 *    {@link #setUseStdLibDir} before accessing any access method in
 *    this class.
 * <li>Application dependant directory; often located in same directory
 *   as app, or one of sub-directories. This is default setting; default
 *   sub-directory (under current directory when starting app that uses
 *   JUG) is specified as {@link #sDefaultLibSubdir}
 * </ul>
 */

public class NativeInterfaces
{
    protected final static String sDefaultLibSubdir = "jug-native";

    /**
     * Path to dir that contains native lib code. If not specified,
     * {@link #sDefaultLibSubdir} is used.
     */
    private static File sLibDir = null;

    /**
     * Whether native code is to be loaded from system-dependant standard
     * library location or not. Default is false, meaning that standard
     * location is NOT used.
     */
    private static boolean sUseStdLibDir = false;

    /// Whether native library has already been loaded
    private static boolean mNativeLoaded = false;

    /**
     * Method that allows overriding of default library directory, to
     * allow loading of native interface access code from specific
     * application dependant location.
     */
    public synchronized static void setLibDir(File f)
    {
        sLibDir = f;
    }

    public synchronized static void setUseStdLibDir(boolean b)
    {
        sUseStdLibDir = b;
    }

    protected synchronized static void checkLoad()
    {
        if (!mNativeLoaded) {
            String os = System.getProperty("os.name").trim().toLowerCase();
            String arch = System.getProperty("os.arch").trim().toLowerCase();

            String realOS = null, realArch = null;

            /* Let's try to figure canonical OS name, just in case some
             * JVMs use funny values (unlikely)
             */
            if (os.indexOf("windows") >= 0) {
                realOS = "win";
            } else if (os.indexOf("linux") >= 0) {
                realOS = "linux";
            } else if (os.indexOf("solaris") >= 0
                       || os.indexOf("sunos") >= 0) {
                realOS = "solaris";
            } else if (os.indexOf("mac os x") >= 0
                       || os.indexOf("macosx") >= 0) {
                realOS = "macosx";
            } else if (os.indexOf("bsd") >= 0) {
                if (os.indexOf("freebsd") >= 0) {
                    realOS = "freebsd";
                } else if (os.indexOf("netbsd") >= 0) {
                    realOS = "netbsd";
                } else if (os.indexOf("openbsd") >= 0) {
                    realOS = "openbsd";
                } else { // default
                    realOS = "bsd";
                }
            } else if (os.indexOf("aix") >= 0) {
                realOS = "aix";
            } else if (os.indexOf("hp ux") >= 0) {
                realOS = "hpux";
            } else {
                throw new Error("No native ethernet access library for OS '"+os+"'.");
            }

            /* And ditto for arch value... here it's more likely weird
             * values exist?
             */
            if (arch.indexOf("x86") >= 0 || arch.indexOf("sparc") >= 0
                || arch.indexOf("ppc") >= 0) {
                realArch = arch;

                // Apparently 'i386' means x86 architecture in JVM lingo?
            } else if (arch.indexOf("86") >= 0 || arch.indexOf("amd") >= 0) {
                realArch = "x86";
            } else {
                throw new Error("No native ethernet access library for hardware platform with value '"+arch+"'.");
            }

            /* Still not really guaranteed to work; not all combinations
             * of os + arch are either valid, or have matching library
             * (notably, linux+sparc and solaris+x86 are missing?)
             */

            String libName = realOS + "_" + realArch + "_" + "EtherAddr";

            if (sUseStdLibDir) {
                loadStdLib(libName);
            } else {
                loadAppLib(libName);
            }

            mNativeLoaded = true;
        }
    }

    private static void loadStdLib(String libName) 
    {
        try {
            System.loadLibrary(libName);
        } catch (SecurityException sex) {
            throw new Error("Trying to load library '"+libName+"': error; "+sex.toString());
        } catch (UnsatisfiedLinkError uex) {
            throw new Error("Trying to load library '"+libName+"': error; "+uex.toString());
        }
    }
	
    private static void loadAppLib(String libName) 
    {
        String realLibName = System.mapLibraryName(libName);
        String prefix = "Tried to load library '"+libName
            +"' (filename assumed to be '"+realLibName+"')";
	
        try {
            File f;
	    
            if (sLibDir == null) {
                f = new File(sDefaultLibSubdir);
                f = new File(f, realLibName);
            } else {
                f = new File(sLibDir, realLibName);
            }
            // Let's first check if such a file exists...
            try {
                f = f.getCanonicalFile();
            } catch (IOException ie) {
                throw new Error(prefix+": checking existence of '"+f.getAbsolutePath()+"': "+ie.toString());
            }
            System.load(f.getAbsolutePath());
            // Uncomment for debugging:
            //System.err.println("DEBUG: "+prefix+": Ok.");
        } catch (SecurityException sex) {
            throw new Error(prefix+": error; "+sex.toString());
        } catch (UnsatisfiedLinkError unsatisfiedex) {
            throw new Error(prefix+": error; "+unsatisfiedex.toString());
        }
    }
    
    public static org.safehaus.uuid.EthernetAddress getPrimaryInterface()
    {
        checkLoad();

        try {
            com.ccg.net.ethernet.EthernetAddress ea =
                com.ccg.net.ethernet.EthernetAddress.getPrimaryAdapter();
            if (ea != null) {
                return new org.safehaus.uuid.EthernetAddress(ea.getBytes());
            }
        } catch (UnsatisfiedLinkError ue) {
            /* Should never happen as checkLoad() should have taken
             * care of the problems
             */
            throw new Error(ue.toString());
        }

        return null;
    }

    public static org.safehaus.uuid.EthernetAddress[] getAllInterfaces()
    {
        org.safehaus.uuid.EthernetAddress[] eas = null;

        checkLoad();

        try {
            Collection c = com.ccg.net.ethernet.EthernetAddress.getAllAdapters();
            eas = new org.safehaus.uuid.EthernetAddress[c.size()];
            Iterator it = c.iterator();

            for (int i = 0; it.hasNext(); ++i) {
                com.ccg.net.ethernet.EthernetAddress ea =
                    (com.ccg.net.ethernet.EthernetAddress) it.next();
                eas[i] = new org.safehaus.uuid.EthernetAddress(ea.getBytes());
            }
        } catch (UnsatisfiedLinkError ue) {
            /* Should never happen as checkLoad() should have taken
             * care of the problems
             */
            throw new Error(ue.toString());
        }

        return eas;
    }

    /**
     * Test driver to test if native ethernet adapter/interface access
     * works ok. Tries to get the primary interface and output it; prints
     * out error message if access fails.
     */
    public static void main(String[] args)
    {
        if (args.length > 0 && args[0].equalsIgnoreCase("lib")) {
            System.out.println("Trying to access primary ethernet interface using system-dependant library loading (use 'app' argument for other test)");
            setUseStdLibDir(true);
        } else {
            System.out.println("Trying to access primary ethernet interface using system independent code loading (use 'lib' argument for other test)");
            setUseStdLibDir(false);
        }

        System.out.println("Trying to access primary ethernet interface:");
        try {
            org.safehaus.uuid.EthernetAddress pea = getPrimaryInterface();

            System.out.println("Ok, the interface MAC-address is: "
                               +pea.toString());
        } catch (Throwable t) {
            System.out.println("Failed, error given: "+t.toString());
        }
    }
}
