package com.vince.toolkit.framework.util.log;

import ch.qos.logback.classic.LoggerContext;
import ch.qos.logback.classic.joran.JoranConfigurator;
import ch.qos.logback.core.util.StatusPrinter;
import com.vince.toolkit.base.exception.BusinessException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.InputStream;

/**
 * Created by Vince on 18/1/3.
 */
public class LogInitialize {

    private final static Logger logger = LoggerFactory.getLogger(LogInitialize.class);

    public static void init() {
        LogUtil.info(logger, "Start init Logback...");
        LoggerContext lc = (LoggerContext) LoggerFactory.getILoggerFactory();
        JoranConfigurator configurator = new JoranConfigurator();
        configurator.setContext(lc);
        lc.reset();
        try {
            InputStream inputStream = LogInitialize.class.getResourceAsStream("/log/logback.xml");
            configurator.doConfigure(inputStream);
        } catch (Exception e) {
            throw new BusinessException("Logback error initialized...", e);
        }
        StatusPrinter.printInCaseOfErrorsOrWarnings(lc);
        LogUtil.info(logger, "Logback initialized...");
    }

}
