package org.example.coffeeshop.service;

import java.util.UUID;

import org.example.coffeeshop.dto.request.OrderRequest;
import org.example.coffeeshop.dto.response.OrderResponse;
import reactor.core.publisher.Mono;

public interface OrderService {

	Mono<OrderResponse> onOrder(OrderRequest orderRequest);

	Mono<Void> onCancel(UUID id);

}
