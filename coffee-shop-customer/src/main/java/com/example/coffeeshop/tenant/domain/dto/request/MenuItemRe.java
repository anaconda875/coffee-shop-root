package com.example.coffeeshop.tenant.domain.dto.request;

import lombok.Data;

@Data
public class MenuItemRe {

	private CommonItemRe commonItem;

	private MenuRe menu;

	private Double price;

}
