package mx.bs.cross.security.provisioning;

import java.util.Date;
import java.util.TimeZone;

import javax.annotation.PostConstruct;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.scheduling.annotation.EnableScheduling;

import mx.bs.cross.security.core.app.SecuritySAPIApplication;

@SpringBootApplication
@EnableEurekaClient
@EnableAutoConfiguration
@EnableScheduling
@ComponentScan(basePackages = "mx.bs.cross.security.db.config")
@ComponentScan(basePackages = "mx.bs.cross.security.provisioning.controllers")
public class SecurityProvisioningApplication extends SecuritySAPIApplication {

	public static void main(String[] args) {
		SpringApplication.run(SecurityProvisioningApplication.class, args);

	}

	@PostConstruct
	void init() {
		Date current = new Date();
		System.out.println(current);
		//TimeZone.setDefault(TimeZone.getTimeZone("UTC"));
		
		current = new Date();
		System.out.println(current);
	}
}
