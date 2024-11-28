package com.example.coffeeshop.tenant.domain.model;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToOne;
import lombok.Data;

@Entity
@Data
public class MenuItem {

	@Id
	private Long id;

	@OneToOne
	private CommonItem commonItem;

	@ManyToOne
	private Menu menu;

	private Double price;

}
