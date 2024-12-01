package org.example.coffeeshop.dto.request;

import java.util.UUID;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class OrderRequest {

	private UUID id;

	/**
	 * Which user is ordering
	 */
	@NotNull
	private UUID from;

	/**
	 * Which item is being bought
	 */
	@NotNull
	@Min( 1 )
	private Long itemId;

	@NotNull
	@Min( 1 )
	private Integer quantity;

}
