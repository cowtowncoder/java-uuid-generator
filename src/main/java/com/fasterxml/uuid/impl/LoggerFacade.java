package com.fasterxml.uuid.impl;

import com.fasterxml.uuid.Jug;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Wrapper we (only) need to support CLI usage (see {@link Jug}
 * wherein we do not actually  have logger package included; in which case we
 * will print warning(s) out to {@code System.err}.
 * For normal embedded usage no benefits, except if someone forgot their SLF4j API
 * package. :)
 *
 * @since 4.1
 */
public class LoggerFacade {
    private final Class<?> _forClass;

    private WrappedLogger _logger;
    
    private LoggerFacade(Class<?> forClass) {
        _forClass = forClass;
    }

    public static LoggerFacade getLogger(Class<?> forClass) {
        return new LoggerFacade(forClass);
    }

    public void warn(String msg) {
        _warn(msg);
    }

    public void warn(String msg, Object arg) {
        _warn(String.format(msg, arg));
    }

    public void warn(String msg, Object arg, Object arg2) {
        _warn(String.format(msg, arg, arg2));
    }

    private synchronized void _warn(String message) {
        if (_logger == null) {
            _logger = WrappedLogger.logger(_forClass);
        }
        _logger.warn(message);
    }

    private static class WrappedLogger {
        private final Logger _logger;

        private WrappedLogger(Logger l) {
            _logger = l;
        }

        public static WrappedLogger logger(Class<?> forClass) {
            // Why all these contortions? To support case where Slf4j API missing
            // (or, if it ever fails for not having impl) to just print to STDERR
            try {
                return new WrappedLogger(LoggerFactory.getLogger(forClass));
            } catch (Throwable t) {
                return new WrappedLogger(null);
            }
        }

        public void warn(String message) {
            if (_logger != null) {
                _logger.warn(message);
            } else {
                System.err.println("WARN: "+message);
            }
        }
    }
}
