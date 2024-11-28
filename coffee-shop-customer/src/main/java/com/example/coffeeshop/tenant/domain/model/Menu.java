package com.example.coffeeshop.tenant.domain.model;

import java.util.List;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import lombok.Data;

@Entity
@Data
public class Menu {

	@Id
	private Long id;

	@OneToMany(mappedBy = "menu", cascade = { CascadeType.PERSIST })
	private List<MenuItem> menuItems;

}
