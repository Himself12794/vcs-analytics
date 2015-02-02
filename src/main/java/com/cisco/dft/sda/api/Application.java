package com.cisco.dft.sda.api;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.context.annotation.ComponentScan;

import com.cisco.dft.sda.api.config.JdbcConfigLoader;

import org.slf4j.*;
/**
 * Responsible to bootstrap and launch Spring Boot Application context
 * 
 * @author sujmuthu
 * @version 1.0
 * @date January 22, 2015
 */
@ComponentScan
@EnableAutoConfiguration
public class Application {
	static  Logger logger= LoggerFactory.getLogger(Application.class);

	public static void main(String[] args) {
		
		
		SpringApplication.run(new Object[] { Application.class,
				JdbcConfigLoader.class }, args);
		logger.warn("hello i am here");
	}
}	