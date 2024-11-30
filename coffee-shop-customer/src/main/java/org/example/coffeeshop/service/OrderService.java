package org.example.coffeeshop.service;

import org.example.coffeeshop.dto.request.OrderRequest;
import org.example.coffeeshop.dto.response.OrderResponse;

public interface OrderService {

	OrderResponse order(OrderRequest orderRequest);

}
