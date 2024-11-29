package com.example.coffeeshop.web.rest;

import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("${app.root-path}/booking")
public class BookingResource {

	@PostMapping("/{shopId}")
	void book(@PathVariable String shopId) {

	}

}
