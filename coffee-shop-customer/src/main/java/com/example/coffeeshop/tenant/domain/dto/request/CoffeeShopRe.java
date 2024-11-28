package com.example.coffeeshop.tenant.domain.dto.request;

import java.time.LocalDateTime;

import lombok.Data;

@Data
public class CoffeeShopRe {

	private String name;

	private String location;

	private String contact;

	private MenuRe menu;

	private Short numberOfQueue;

	private LocalDateTime openingTime;

	private LocalDateTime closingTime;

}
