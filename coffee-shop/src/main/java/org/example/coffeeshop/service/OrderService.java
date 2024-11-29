package org.example.coffeeshop.service;

import java.util.UUID;

import org.example.coffeeshop.dto.request.OrderRequest;
import org.example.coffeeshop.dto.response.OrderResponse;

public interface OrderService {

	OrderResponse onOrder(OrderRequest orderRequest);

	void onCancel(UUID id);

}
