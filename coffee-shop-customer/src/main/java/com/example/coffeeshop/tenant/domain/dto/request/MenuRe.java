package com.example.coffeeshop.tenant.domain.dto.request;

import java.util.List;

import lombok.Data;

@Data
public class MenuRe {

	private List<MenuItemRe> menuItems;
}
