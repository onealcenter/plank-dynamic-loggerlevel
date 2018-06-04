package com.plank;

import java.util.concurrent.ConcurrentHashMap;

public class LoggerMap {
	public LoggerMap(ConcurrentHashMap<String, Object> loggerMap) {
		this.loggerMap = loggerMap;
	}

	private ConcurrentHashMap<String, Object> loggerMap;

	public ConcurrentHashMap<String, Object> getLoggerMap() {
		return loggerMap;
	}

	public void setLoggerMap(ConcurrentHashMap<String, Object> loggerMap) {
		this.loggerMap = loggerMap;
	}
}