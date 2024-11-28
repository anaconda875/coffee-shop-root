package com.example.coffeeshop.tenant.domain.model;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.Data;

@Entity
@Data
public class CommonItem {

	@Id
	private Long id;

	private String name;
}
