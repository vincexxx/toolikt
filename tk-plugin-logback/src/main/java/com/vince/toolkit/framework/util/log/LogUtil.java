package com.vince.toolkit.framework.util.log;

import com.alibaba.fastjson.JSON;
import org.slf4j.Logger;

public class LogUtil {

    public static void trace(Logger logger, String msg) {
        logger.trace(msg);
    }

    public static void debug(Logger logger, String msg) {
        logger.debug(msg);
    }

    public static void debug(Logger logger, String msg, Object... value) {
        if (!logger.isDebugEnabled()) {
            return;
        }
        logger.debug(msg, value);
    }

    public static void info(Logger logger, String msg) {
        logger.info(msg);
    }

    public static void info(Logger logger, String msg, Object... value) {
        logger.info(msg, value);
    }

    public static void warn(Logger logger, String msg) {
        logger.warn(msg);
    }

    public static void warn(Logger logger, String msg, Object... value) {
        logger.warn(msg, value);
    }

    public static void error(Logger logger, String msg) {
        logger.error(msg);
    }

    public static void error(Logger logger, String msg, Object... value) {
        logger.error(msg, value);
    }

    public static void error(Logger logger, String msg, Throwable t) {
        logger.error(msg, t);
    }

    public static void trace(Logger logger, Object[] msg) {
        if (!logger.isTraceEnabled()) {
            return;
        }
        if (msg == null || msg.length < 1) {
            return;
        }
        trace(logger, JSON.toJSONString(msg));
    }

    public static void debug(Logger logger, Object[] msg) {
        if (!logger.isDebugEnabled()) {
            return;
        }
        if (msg == null || msg.length < 1) {
            return;
        }
        debug(logger, JSON.toJSONString(msg));
    }

    public static void info(Logger logger, Object[] msg) {
        if (!logger.isInfoEnabled()) {
            return;
        }
        if (msg == null || msg.length < 1) {
            return;
        }
        info(logger, JSON.toJSONString(msg));
    }

    public static void warn(Logger logger, Object[] msg) {
        if (!logger.isWarnEnabled()) {
            return;
        }
        if (msg == null || msg.length < 1) {
            return;
        }
        warn(logger, JSON.toJSONString(msg));
    }

    public static void error(Logger logger, Object[] msg) {
        if (!logger.isErrorEnabled()) {
            return;
        }
        if (msg == null || msg.length < 1) {
            return;
        }
        error(logger, JSON.toJSONString(msg));
    }

    public static void error(Logger logger, Object[] msg, Throwable t) {
        if (!logger.isErrorEnabled()) {
            return;
        }
        if (msg == null || msg.length < 1) {
            return;
        }
        error(logger, JSON.toJSONString(msg), t);
    }
}
