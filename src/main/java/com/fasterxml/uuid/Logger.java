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

package com.fasterxml.uuid;

import java.io.*;

/**
 * This is the simple logging interface used by JUG package. It is meant
 * to provide a minimal but sufficient functionality for JUG to report
 * problems (warnings, errors), in a way that it can be sufficiently
 * customized (redirected, suppressed; even redefined), without forcing
 * overhead of a real
 * full-featured logging sub-system (like log4j or java.util.logging).
 * By being customizable, it is still possible to connect JUG logging into
 * real logging framework (log4j, java.util.logging) used by application
 * or system that uses JUG.
 *<p>
 * To keep things as light-weight as possible, we won't bother defining
 * separate interface or abstract class -- this class defines both API
 * and the default implementation. It can thus be extended to override
 * functionality to provide things like bridging to "real" logging systems.
 * For simple configuration (suppress all, redirect to another stream)
 * default implementation should be sufficient, however.
 *<p>
 * Note: package <code>com.fasterxml.uuid.ext</code> does contain
 * simple wrappers to connect JUG logging to log4j and java.util.logging:
 *
 * @see com.fasterxml.uuid.ext.Log4jLogger
 * @see com.fasterxml.uuid.ext.JavaUtilLogger
 */
public class Logger
{
    /*
    //////////////////////////////////////////////////
    // Constants
    //////////////////////////////////////////////////
    */
    
    public final static int LOG_ALL = 0;
    public final static int LOG_INFO_AND_ABOVE = 1;
    public final static int LOG_WARNING_AND_ABOVE = 2;
    public final static int LOG_ERROR_AND_ABOVE = 3;
    public final static int LOG_NOTHING = 4;

    /*
    /**********************************************************************
    /* Static objects
    /**********************************************************************
     */

    /**
     * By default we'll use this default implementation; however,
     * it can be easily changed.
     */
    private static Logger instance = new Logger();

    /*
    /**********************************************************************
    /* Default impl. configuration
    /**********************************************************************
     */

    /**
     * Threshold to use for outputting varius log statements.
     *<p>
     * Default is to low only warnings and errors
     */
    protected int _logLevel = LOG_ALL;

    /**
     * Output object to use, if defined; initialized to
     * <code>System.err</code>.
     */
    protected PrintStream _output1 = System.err;

    /**
     * Override output used to explicitly specify where to pass diagnostic
     * output, instead of System.err. Used if <code>_output1</code>
     * is null;
     */
    protected PrintWriter _output2 = null;

    /*
    /**********************************************************************
    /* Life-cycle
    /**********************************************************************
     */
    
    protected Logger() { }

    /**
     * Method that can be used to completely re-define the logging
     * functionality JUG uses. When called, JUG will start using the
     * new instance; if instance passed is null, will basically suppress
     * all logging.
     *
     * @param inst Logger instance to use for all logging JUG does; can be
     *   null, but if so, essentially disables all logging.
     */
    public synchronized static void setLogger(Logger inst)
    {
        instance = inst;
    }

    /*
    /**********************************************************************
    /* Actual simple logging API
    /* (static dispatchers to instance methods)
    /**********************************************************************
     */
    
    // // // Configuration

    /**
     * Method to set the minimum level of messages that will get logged
     * using currently specific logger instace. For example, if
     * {@link #LOG_WARNING_AND_ABOVE} is passed as the argument, warnings
     * and errors will be logged, but informational (INFO) messages will
     * not.
     *<p>
     * Note: exact functionality invoked depends on the logger instance:
     * sub-classes of this class may need to do mapping to some other
     * logging sub-system (log4j and JUL logging, for example, use their
     * own severity levels that while generally reasonably easy to map,
     * are nonetheless not one-to-one which the simple logger).
     */
    public static void setLogLevel(int level)
    {
        Logger l = instance;
        if (l != null) {
            l.doSetLogLevel(level);
        }
    }

    /**
     * Method that will re-direct output of the logger using the specified
     * {@link PrintStream}. Null is allowed, and signifies that all the
     * output should be suppressed.
     *<p>
     * Note: exact functionality invoked depends on the logger instance.
     */
    public static void setOutput(PrintStream str)
    {
        Logger l = instance;
        if (l != null) {
            l.doSetOutput(str);
        }
    }

    /**
     * Method that will re-direct output of the logger using the specified
     * {@link Writer}. Null is allowed, and signifies that all the
     * output should be suppressed.
     */
    public static void setOutput(Writer w)
    {
        Logger l = instance;
        if (l != null) {
            l.doSetOutput(w);
        }
    }

    // // // Logging methods

    public static void logInfo(String msg)
    {
        Logger l = instance;
        if (l != null) {
            l.doLogInfo(msg);
        }
    }

    public static void logWarning(String msg)
    {
        Logger l = instance;
        if (l != null) {
            l.doLogWarning(msg);
        }
    }

    public static void logError(String msg)
    {
        Logger l = instance;
        if (l != null) {
            l.doLogError(msg);
        }
    }

    /*
    /**********************************************************************
    /* Overridable implementation/instance methods
    /**********************************************************************
     */

    // // // Config

    protected void doSetLogLevel(int ll)
    {
        /* No need to sync for atomic value that's not used
         * for synced or critical things
         */
        _logLevel = ll;
    }

    protected void doSetOutput(PrintStream str)
    {
        synchronized (this) {
            _output1 = str;
            _output2 = null;
        }
    }

    protected void doSetOutput(Writer w)
    {
        synchronized (this) {
            _output1 = null;
            _output2 = (w instanceof PrintWriter) ?
                (PrintWriter) w : new PrintWriter(w);
        }
    }

    // // // Logging methods

    protected void doLogInfo(String msg)
    {
        if (_logLevel  <= LOG_INFO_AND_ABOVE && isEnabled()) {
            synchronized (this) {
                doWrite("INFO: "+msg);
            }
        }
    }

    protected void doLogWarning(String msg)
    {
        if (_logLevel  <= LOG_WARNING_AND_ABOVE && isEnabled()) {
            synchronized (this) {
                doWrite("WARNING: "+msg);
            }
        }
    }

    protected void doLogError(String msg)
    {
        if (_logLevel  <= LOG_ERROR_AND_ABOVE && isEnabled()) {
            synchronized (this) {
                doWrite("ERROR: "+msg);
            }
        }
    }

    /*
    /**********************************************************************
    /* Internal methods
    /**********************************************************************
     */

    protected void doWrite(String msg)
    {
        if (_output1 != null) {
            _output1.println(msg);
        } else if (_output2 != null) {
            _output2.println(msg);
        }
    }

    /**
     * Internal method used to quickly check if the Logger's output
     * is suppressed or not.
     *<p>
     * Note: not synchronized since it's read-only method that's return
     * value can not be used for reliable syncing.
     */
    protected boolean isEnabled() {
        return (_output1 != null) || (_output2 != null);
    }
}

