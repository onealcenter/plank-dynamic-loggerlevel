package com.plank;

public class ConstantUtil {
	public static final String LOG4J2_LOGGER_FACTORY = "org.apache.logging.slf4j.Log4jLoggerFactory";
	public static final String LOGBACK_LOGGER_FACTORY = "ch.qos.logback.classic.util.ContextSelectorStaticBinder";
	public static LogFrameworkType logFrameworkType = LogFrameworkType.UNKNOWN;
}