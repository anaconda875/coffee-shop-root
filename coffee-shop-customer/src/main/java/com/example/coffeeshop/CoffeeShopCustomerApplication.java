package com.example.coffeeshop;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class CoffeeShopCustomerApplication {

	public static void main(String[] args) {
		SpringApplication.run( CoffeeShopCustomerApplication.class, args);
	}

//	@Bean
//	CommandLineRunner an(CoffeeShopRepository repository) {
//		return args -> {
//			repository.findAll();
//		};
//	}

}
