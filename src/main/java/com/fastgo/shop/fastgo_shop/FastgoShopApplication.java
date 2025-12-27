package com.fastgo.shop.fastgo_shop;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class FastgoShopApplication {

	public static void main(String[] args) {
		SpringApplication.run(FastgoShopApplication.class, args);
	}

}
