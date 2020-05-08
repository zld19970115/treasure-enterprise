
/**
 * Copyright (c) 2019 聚宝科技 All rights reserved.
 *
 * https://www.treasure.io
 *
 * 版权所有，侵权必究！
 */

package io.treasure;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * treasure-api
 *
 * @author Super 63600679@qq.com
 */
@SpringBootApplication
//@ServletComponentScan
public class ApiApplication{// extends SpringBootServletInitializer {
//	@Value("${server.http.port}")
//	private Integer port;
//	@Bean
//	public ServletWebServerFactory servletContainer() {
//		TomcatServletWebServerFactory tomcat = new TomcatServletWebServerFactory();
//		tomcat.addAdditionalTomcatConnectors(createStandardConnector()); // 添加http
//		return tomcat;
//	}
//
//	private Connector createStandardConnector() {
//		Connector connector = new Connector("org.apache.coyote.http11.Http11NioProtocol");
//		connector.setPort(port);
//		return connector;
//	}

	public static void main(String[] args) {
		SpringApplication.run(ApiApplication.class, args);
	}

//	@Override
//	protected SpringApplicationBuilder configure(SpringApplicationBuilder application) {
//		return application.sources(ApiApplication.class);
//	}
}
