package org.example.coffeeshop.web.rest;

import java.util.UUID;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.example.coffeeshop.dto.request.OrderRequest;
import org.example.coffeeshop.dto.response.OrderResponse;
import org.example.coffeeshop.service.OrderService;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("#{appProperties.rootPath}/shop/orders")
@RequiredArgsConstructor
public class OrderResource {

	private final OrderService orderService;

	@PostMapping
	Mono<OrderResponse> order(@Valid @RequestBody OrderRequest orderRequest) {
		return orderService.onOrder( orderRequest );
	}

	@PostMapping("/{id}/cancellation")
	Mono<Void> cancel(@PathVariable UUID id) {
		return orderService.onCancel( id );
	}

}
