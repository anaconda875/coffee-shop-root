package org.example.coffeeshop.dto.response;

import java.util.UUID;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.example.coffeeshop.dto.request.OrderRequest;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class OrderResponse {

	private UUID id;
	private String of;
	private Long itemId;
	private Integer quantity;
	private Integer pos;

	public static OrderResponse of(OrderRequest request, int pos) {
		return new OrderResponse(request.getId(), request.getFrom(), request.getItemId(), request.getQuantity(), pos);
	}

}
