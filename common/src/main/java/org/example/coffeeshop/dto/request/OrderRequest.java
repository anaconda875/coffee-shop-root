package org.example.coffeeshop.dto.request;

import java.util.UUID;

import lombok.Data;

@Data
public class OrderRequest {

	private UUID id;

	/**
	 * Which user is ordering
	 */
	private String from;

	/**
	 * Which item is being bought
	 */
	private Long itemId;

	private Integer quantity;

}
