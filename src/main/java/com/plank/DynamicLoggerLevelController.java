package com.plank;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import com.netflix.appinfo.InstanceInfo;
import com.netflix.discovery.EurekaClient;
import com.netflix.discovery.shared.Application;

@ComponentScan
@RestController
public class DynamicLoggerLevelController {
	@Autowired
	private RestTemplate restTemplate;

	@Autowired
	private EurekaClient eurekaClient;

	@Autowired
	private LoggerMap loggerMap;

	@GetMapping(value = "/getInstanceInfo")
	public Response getInstanceInfo() {
		List<InstanceInfo> lii = new ArrayList<InstanceInfo>();
		List<Application> applications = eurekaClient.getApplications().getRegisteredApplications();
		for (Application application : applications) {
			List<InstanceInfo> instances = application.getInstances();
			for (InstanceInfo instanceInfo : instances) {
				String homePageUrl = instanceInfo.getHomePageUrl();
				ResponseEntity<Map> responseEntity = null;
				try {
					responseEntity = restTemplate.getForEntity(homePageUrl + "checkLogger", Map.class);
				} catch (Exception e) {
					continue;
				}
				if (responseEntity.getStatusCode() == HttpStatus.OK) {
					Map body = responseEntity.getBody();
					if (body.get("code").toString().equals("0")) {
						lii.add(instanceInfo);
					}
				}
			}
		}
		return new Response().success(lii);
	}

	@GetMapping(value = "/checkLogger")
	public Response checkLogger() {
		if (ConstantUtil.logFrameworkType == LogFrameworkType.UNKNOWN)
			return new Response().failure("-1", "");
		return new Response().success();
	}

	@PostMapping(value = "/setLoggerLevel")
	public Response setLoggerLevel(@RequestBody List<Map> lm) {
		try {
			funcSetLoggerLevel(lm);
			return new Response().success();
		} catch (Exception e) {
			return new Response().failure("-1", e.getMessage());
		}
	}

	@GetMapping(value = "/getLoggerList")
	public Response getLoggerList() {
		try {
			return new Response().success(funcGetLoggerList());
		} catch (Exception e) {
			return new Response().failure("-1", e.getMessage());
		}
	}

	private Map funcGetLoggerList() {
		Map<String, Object> result = new HashMap<String, Object>();
		result.put("logFrameworkType", ConstantUtil.logFrameworkType);
		List<Map> loggerList = new ArrayList<Map>();
		for (ConcurrentHashMap.Entry<String, Object> entry : loggerMap.getLoggerMap().entrySet()) {
			Map<String, String> mss = new HashMap<String, String>();
			mss.put("name", entry.getKey());
			if (ConstantUtil.logFrameworkType == LogFrameworkType.LOGBACK) {
				ch.qos.logback.classic.Logger targetLogger = (ch.qos.logback.classic.Logger) entry.getValue();
				mss.put("level", targetLogger.getLevel().toString());
			} else if (ConstantUtil.logFrameworkType == LogFrameworkType.LOG4J2) {
				org.apache.logging.log4j.core.config.LoggerConfig targetLogger = (org.apache.logging.log4j.core.config.LoggerConfig) entry
						.getValue();
				mss.put("level", targetLogger.getLevel().toString());
			} else {
				throw new RuntimeException("logger绫诲瀷涓嶈瘑鍒�");
			}
			loggerList.add(mss);
		}
		result.put("loggerList", loggerList);
		return result;
	}

	/*
	 * name level
	 */
	private boolean funcSetLoggerLevel(List<Map> lm) {
		if (lm == null || lm.isEmpty())
			throw new IllegalArgumentException();
		for (Map m : lm) {
			Object logger = loggerMap.getLoggerMap().get(m.get("name").toString());
			if (logger == null) {
				throw new RuntimeException("闇�瑕佷慨鏀规棩蹇楃骇鍒殑logger涓嶅瓨鍦�");
			}
			if (ConstantUtil.logFrameworkType == LogFrameworkType.LOGBACK) {
				ch.qos.logback.classic.Logger targetLogger = (ch.qos.logback.classic.Logger) logger;
				ch.qos.logback.classic.Level targetLevel = ch.qos.logback.classic.Level
						.toLevel(m.get("level").toString());
				targetLogger.setLevel(targetLevel);
			} else if (ConstantUtil.logFrameworkType == LogFrameworkType.LOG4J2) {
				org.apache.logging.log4j.core.config.LoggerConfig loggerConfig = (org.apache.logging.log4j.core.config.LoggerConfig) logger;
				org.apache.logging.log4j.Level targetLevel = org.apache.logging.log4j.Level
						.toLevel(m.get("level").toString());
				loggerConfig.setLevel(targetLevel);
				org.apache.logging.log4j.core.LoggerContext ctx = (org.apache.logging.log4j.core.LoggerContext) org.apache.logging.log4j.LogManager
						.getContext(false);
				ctx.updateLoggers();
			} else {
				throw new RuntimeException("logger绫诲瀷涓嶈瘑鍒�");
			}
		}
		return true;
	}

}