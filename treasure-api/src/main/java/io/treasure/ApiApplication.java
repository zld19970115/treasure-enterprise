
/**
 * Copyright (c) 2019 聚宝科技 All rights reserved.
 *
 * https://www.treasure.io
 *
 * 版权所有，侵权必究！
 */

package io.treasure;
import com.alipay.api.java_websocket.WebSocketImpl;
import io.treasure.task.Task;
import org.apache.catalina.connector.Connector;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.embedded.tomcat.TomcatServletWebServerFactory;
import org.springframework.boot.web.servlet.ServletComponentScan;
import org.springframework.boot.web.servlet.server.ServletWebServerFactory;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import io.treasure.utils.MyWebScoket;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * treasure-api
 *
 * @author Super 63600679@qq.com
 */

@EnableScheduling
@EnableTransactionManagement
@SpringBootApplication
@ServletComponentScan
@EnableAspectJAutoProxy(proxyTargetClass = true)
public class ApiApplication extends SpringBootServletInitializer {
	@Value("${server.http.port}")
	private Integer port;

	@Autowired
	private Task task;
	@Bean
	public ServletWebServerFactory servletContainer() {
		TomcatServletWebServerFactory tomcat = new TomcatServletWebServerFactory();
		tomcat.addAdditionalTomcatConnectors(createStandardConnector()); // 添加http
		return tomcat;
	}

	private Connector createStandardConnector() {
		Connector connector = new Connector("org.apache.coyote.http11.Http11NioProtocol");
		connector.setPort(port);
		connector.setURIEncoding("UTF-8");
		//connector.setUseBodyEncodingForURI(true);
		return connector;
	}
	public static void main(String[] args) {
		SpringApplication.run(ApiApplication.class, args);
	}

	@Override
	protected SpringApplicationBuilder configure(SpringApplicationBuilder application) {
		return application.sources(ApiApplication.class);
	}
	/**
	 * 启动websocket
	 */
	@Bean
	public void startWebsocketInstantMsg() {
		//WebSocketImpl..DEBUG = false;
		MyWebScoket s;
		s = new MyWebScoket(8066);
		s.start();
		System.out.println("websocket启动成功");
	}

}
