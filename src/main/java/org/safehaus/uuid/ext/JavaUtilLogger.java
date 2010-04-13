/* JUG Java Uuid Generator
 *
 * Copyright (c) 2002- Tatu Saloranta, tatu.saloranta@iki.fi
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

package org.safehaus.uuid.ext;

import java.io.*;

//import org.safehaus.uuid.Logger;

/**
 * Simple wrapper that allows easy connecting of JUG logging into JDK 1.4+
 * logging implementation (aka "java.util.logging" aka "JUL".
 *<p>
 * Note: using this class requires JDK 1.4 or above.
 */
public class JavaUtilLogger
    extends org.safehaus.uuid.Logger
{
    private java.util.logging.Logger mPeer;

    private JavaUtilLogger(java.util.logging.Logger peer)
    {
        mPeer = peer;
    }

    /**
     * Static method to call to make JUG use to proxy all of its logging
     * through the specified j.u.l <code>Logger</code> instance.
     *<p>
     * Method will create a simple wrapper, and call
     * {@link org.safehaus.uuid.Logger#setLogger} with the wrapper as
     * the argument. This will then re-direct logging from the previously
     * defined Logger (which initially is the simple JUG logger) to the
     * new wrapper, which routes logging messages to the log4j peer Logger
     * instance.
     */
    public static void connectToJavaUtilLogging(java.util.logging.Logger peer)
    {
        JavaUtilLogger logger = new JavaUtilLogger(peer);
        // This is static method of the base class...
        setLogger(logger);
    }

    /**
     * Static method to call to make JUG use a log4j proxy all of its logging
     * through a j.u.l <code>Logger</code> constructed to correspond with
     * <code>org.safehaus.uuid.Logger</code> class (this generally determines
     * j.u.l category output etc settings).
     *<p>
     * Method will create a simple wrapper, and call
     * {@link org.safehaus.uuid.Logger#setLogger} with the wrapper as
     * the argument. This will then re-direct logging from the previously
     * defined Logger (which initially is the simple JUG logger) to the
     * new wrapper, which routes logging messages to the j.u.l peer Logger
     * instance.
     */
    public static void connectToJavaUtilLogging()
    {
        connectToJavaUtilLogging(java.util.logging.Logger.getLogger(org.safehaus.uuid.Logger.class.getName()));
    }

    /*
    /////////////////////////////////////////////////////////////
    // Overridable implementation/instance methods from
    // Logger base class
    /////////////////////////////////////////////////////////////
    */

    // // // Config

    // This is ok; let's just use base class functionality:
    //protected void doSetLogLevel(int ll);
            
    /**
     * Note: this method is meaningless with log4j, since it has more
     * advanced output mapping and filtering mechanisms. As such, it's
     * a no-op
     */
    protected void doSetOutput(PrintStream str)
    {
        // Could also throw an Error.. but for now, let's log instead...
        mPeer.warning("doSetOutput(PrintStream) called on "+getClass()+" instance, ignoring.");
    }

    /**
     * Note: this method is meaningless with log4j, since it has more
     * advanced output mapping and filtering mechanisms. As such, it's
     * a no-op
     */
    protected void doSetOutput(Writer w)
    {
        mPeer.warning("doSetOutput(Writer) called on "+getClass()+" instance, ignoring.");
    }

    // // // Logging methods

    protected void doLogInfo(String msg)
    {
        if (mLogLevel  <= LOG_INFO_AND_ABOVE) {
            mPeer.info(msg);
        }
    }

    protected void doLogWarning(String msg)
    {
        if (mLogLevel  <= LOG_WARNING_AND_ABOVE) {
            mPeer.warning(msg);
        }
    }

    protected void doLogError(String msg)
    {
        /* Hmmh. JUL doesn't have error... and SEVERE is bit drastic. But,
         * well, let's use that for ERRORs for now.
         */
        if (mLogLevel <= LOG_ERROR_AND_ABOVE) {
            mPeer.severe(msg);
        }
    }
}
