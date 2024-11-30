package org.example.coffeeshop.service;

import lombok.RequiredArgsConstructor;
import org.example.coffeeshop.dto.request.OrderRequest;
import org.example.coffeeshop.dto.response.OrderResponse;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.client.RestTemplate;

@Service
@RequiredArgsConstructor
public class DefaultOrderService implements OrderService {

	private final RestTemplate restTemplate;

	@Override
	public OrderResponse order(@RequestBody OrderRequest orderRequest) {
		return restTemplate.postForObject("", orderRequest, OrderResponse.class);
	}

}
