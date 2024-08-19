package com.bezkoder.spring.security.postgresql;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
//import springfox.documentation.swagger2.annotations.EnableSwagger2;

@SpringBootApplication
//@EnableSwagger2

public class SpringBootSecurityPostgresqlApplication {

	public static void main(String[] args) {
		SpringApplication.run(SpringBootSecurityPostgresqlApplication.class, args);
	}

}
