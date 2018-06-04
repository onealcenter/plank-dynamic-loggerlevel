package com.plank;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.LoggerFactory;
import org.slf4j.impl.StaticLoggerBinder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

@ComponentScan
@Configuration
public class DynamicLoggerLevelConfiguration {

	@Autowired
	RestTemplateBuilder restTemplateBuilder;

	@Bean
	public RestTemplate restTemplate() {
		return restTemplateBuilder.build();
	}

	@Bean
	public LoggerMap loggerMap() {
		ConcurrentHashMap<String, Object> loggerMap = new ConcurrentHashMap<String, Object>();
		String type = StaticLoggerBinder.getSingleton().getLoggerFactoryClassStr();
		if (ConstantUtil.LOGBACK_LOGGER_FACTORY.equals(type)) {
			ConstantUtil.logFrameworkType = LogFrameworkType.LOGBACK;
			ch.qos.logback.classic.LoggerContext loggerContext = (ch.qos.logback.classic.LoggerContext) LoggerFactory
					.getILoggerFactory();
			for (ch.qos.logback.classic.Logger logger : loggerContext.getLoggerList()) {
				if (logger.getLevel() != null) {
					loggerMap.put(logger.getName(), logger);
				}
			}
			ch.qos.logback.classic.Logger rootLogger = (ch.qos.logback.classic.Logger) LoggerFactory
					.getLogger(ch.qos.logback.classic.Logger.ROOT_LOGGER_NAME);
			loggerMap.put(rootLogger.getName(), rootLogger);
		} else if (ConstantUtil.LOG4J2_LOGGER_FACTORY.equals(type)) {
			ConstantUtil.logFrameworkType = LogFrameworkType.LOG4J2;
			org.apache.logging.log4j.core.LoggerContext loggerContext = (org.apache.logging.log4j.core.LoggerContext) org.apache.logging.log4j.LogManager
					.getContext(false);
			Map<String, org.apache.logging.log4j.core.config.LoggerConfig> map = loggerContext.getConfiguration()
					.getLoggers();
			for (org.apache.logging.log4j.core.config.LoggerConfig loggerConfig : map.values()) {
				String key = loggerConfig.getName();
				if (StringUtils.isBlank(key)) {
					key = "root";
				}
				loggerMap.put(key, loggerConfig);
			}
		} else {
			ConstantUtil.logFrameworkType = LogFrameworkType.UNKNOWN;
		}
		return new LoggerMap(loggerMap);
	}
}