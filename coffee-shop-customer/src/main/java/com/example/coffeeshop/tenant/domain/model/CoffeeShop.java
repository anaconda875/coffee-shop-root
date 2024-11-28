package com.example.coffeeshop.tenant.domain.model;

import java.time.LocalDateTime;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.OneToOne;
import lombok.Data;

@Entity
@Data
public class CoffeeShop {

	@Id
	private Long id;

	private String name;

	private String location;

	private String contact;

	@OneToOne
	private Menu menu;

	private Short numberOfQueue;

	private LocalDateTime openingTime;

	private LocalDateTime closingTime;

}
