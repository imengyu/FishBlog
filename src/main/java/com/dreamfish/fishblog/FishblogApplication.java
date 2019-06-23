package com.dreamfish.fishblog;

import com.dreamfish.fishblog.core.listener.ApplicationReadyEventListener;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.event.ApplicationPreparedEvent;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.ApplicationListener;

/**
 * 应用入口
 */
@SpringBootApplication
public class FishblogApplication {

	public static void main(String[] args) {
		SpringApplication springApplication = new SpringApplication(FishblogApplication.class);
		springApplication.addListeners(new ApplicationReadyEventListener());
		springApplication.run(args);
	}

}
