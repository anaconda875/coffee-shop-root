package com.example.coffeeshop.tenant.web.rest;

import com.example.coffeeshop.tenant.repository.CoffeeShopRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/")
@RequiredArgsConstructor
public class Test {

	private final CoffeeShopRepository repository;

	@GetMapping
	public void a() {
		repository.findAll();
	}

}
