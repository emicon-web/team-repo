package com.bezkoder.springjwt;

import org.springframework.boot.SpringApplication;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.boot.autoconfigure.SpringBootApplication;

//import springfox.documentation.swagger2.annotations.EnableSwagger2;

@SpringBootApplication
//@EnableSwagger2
public class SpringBootSecurityJwtApplication {
	private static final Logger logger = LogManager.getLogger(SpringBootSecurityJwtApplication.class);

	public static void main(String[] args) {
		SpringApplication.run(SpringBootSecurityJwtApplication.class, args);
		
		
        logger.info("Application Started");
       
        }

}
