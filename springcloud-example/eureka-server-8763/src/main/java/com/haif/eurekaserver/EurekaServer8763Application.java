package com.haif.eurekaserver;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.server.EnableEurekaServer;

@EnableEurekaServer
@SpringBootApplication
public class EurekaServer8763Application {

	public static void main(String[] args) {
		SpringApplication.run(EurekaServer8763Application.class, args);
	}

}
