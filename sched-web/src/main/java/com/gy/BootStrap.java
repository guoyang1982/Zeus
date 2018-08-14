package com.gy;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * 系统启动入口，Spring容器初始化入口
 */
@SpringBootApplication
public class BootStrap {
	private Log logger = LogFactory.getLog(getClass());

	public static void main(String args[]){
		System.out.println("start !!!!!!!!!!!!!!!!!!!!!!!!!!!!!!");
		SpringApplication.run(BootStrap.class, args);
	}
}