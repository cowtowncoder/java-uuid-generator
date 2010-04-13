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
 * Simple wrapper that allows easy connecting of JUG logging into log4j
 * logging subsystem.
 *<p>
 * Note: using this class implies all the dependencies that the log4j
 * subsystem in use requires (JDK 1.2 or above, in general)
 */
public class Log4jLogger
    extends org.safehaus.uuid.Logger
{
    private org.apache.log4j.Logger mPeer;

    private Log4jLogger(org.apache.log4j.Logger peer)
    {
        mPeer = peer;
    }

    /**
     * Static method to call to make JUG use to proxy all of its logging
     * through the specified log4j <code>Logger</code> instance.
     *<p>
     * Method will create a simple wrapper, and call
     * {@link org.safehaus.uuid.Logger#setLogger} with the wrapper as
     * the argument. This will then re-direct logging from the previously
     * defined Logger (which initially is the simple JUG logger) to the
     * new wrapper, which routes logging messages to the log4j peer Logger
     * instance.
     */
    public static void connectToLog4j(org.apache.log4j.Logger peer)
    {
        Log4jLogger logger = new Log4jLogger(peer);
        // This is static method of the base class...
        setLogger(logger);
    }

    /**
     * Static method to call to make JUG use a log4j proxy all of its logging
     * through a log4j <code>Logger</code> constructed to correspond with
     * <code>org.safehaus.uuid.Logger</code> class (this generally determines
     * log4j category output etc settings).
     *<p>
     * Method will create a simple wrapper, and call
     * {@link org.safehaus.uuid.Logger#setLogger} with the wrapper as
     * the argument. This will then re-direct logging from the previously
     * defined Logger (which initially is the simple JUG logger) to the
     * new wrapper, which routes logging messages to the log4j peer Logger
     * instance.
     */
    public static void connectToLog4j()
    {
        connectToLog4j(org.apache.log4j.Logger.getLogger(org.safehaus.uuid.Logger.class));
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
        mPeer.warn("doSetOutput(PrintStream) called on "+getClass()+" instance, ignoring.");
    }

    /**
     * Note: this method is meaningless with log4j, since it has more
     * advanced output mapping and filtering mechanisms. As such, it's
     * a no-op
     */
    protected void doSetOutput(Writer w)
    {
        mPeer.warn("doSetOutput(Writer) called on "+getClass()+" instance, ignoring.");
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
            mPeer.warn(msg);
        }
    }

    protected void doLogError(String msg)
    {
        if (mLogLevel  <= LOG_ERROR_AND_ABOVE) {
            mPeer.error(msg);
        }
    }
}
