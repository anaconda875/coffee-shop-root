package org.example.coffeeshop.web.rest;

import java.util.UUID;

import lombok.RequiredArgsConstructor;
import org.example.coffeeshop.dto.request.OrderRequest;
import org.example.coffeeshop.dto.response.OrderResponse;
import org.example.coffeeshop.service.OrderService;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("#{appProperties.rootPath}/#{appProperties.shopId}/orders")
@RequiredArgsConstructor
public class OrderResource {

	private final OrderService orderService;

	@PostMapping
	OrderResponse order(@RequestBody OrderRequest orderRequest) {
		return orderService.onOrder( orderRequest );
	}

	@PostMapping("/{id}/cancellation")
	void cancel(@PathVariable UUID id) {
		orderService.onCancel( id );
	}

}
