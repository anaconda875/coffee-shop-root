package org.example.coffeeshop.service;

import java.util.AbstractMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicReference;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.example.coffeeshop.dto.request.OrderRequest;
import org.example.coffeeshop.dto.response.OrderResponse;
import org.example.coffeeshop.exception.BookingException;
import org.example.coffeeshop.exception.QueueNotAvailableException;
import org.example.coffeeshop.exception.ResourceNotFoundException;
import org.redisson.api.RDeque;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class DefaultOrderService implements OrderService {

	private static final String SEPARATOR = ":";

	private final String shopId;
	private final List<String> queues;
	private final RedissonClient client;

	public DefaultOrderService(@Value( "#{appProperties.shopId}" ) String shopId,
														 @Value( "#{appProperties.queues}" ) List<String> queues,
														 RedissonClient client) {
		this.shopId = shopId;
		this.queues = queues;
		this.client = client;
	}

	@Override
	public OrderResponse onOrder(OrderRequest orderRequest) {
		AtomicReference<String> queueName = new AtomicReference<>();
		RDeque<OrderRequest> minQueue = queues.stream()
				.map( queue -> new AbstractMap.SimpleImmutableEntry<>( client.<OrderRequest>getDeque( shopId + SEPARATOR + queue ), queue ) )
				.reduce( (f, l) -> {
					if ( f.getKey().size() <= l.getKey().size() ) {
						queueName.set( f.getValue() );
						return f;
					}

					queueName.set( l.getValue() );
					return l;
				} )
				.map( Map.Entry::getKey)
				.orElseThrow( QueueNotAvailableException::new );

		try {
			//Redis is single threaded, so the last position returned by addLast
			//is actually the position of the user in the queue
			UUID id = UUID.randomUUID();
			orderRequest.setId( id );
			int pos = minQueue.addLast( new OrderRequest[] { orderRequest } );
			OrderResponse response = OrderResponse.of( orderRequest, pos );
			//store which order belongs to which queue
			client.getMap( shopId ).put( id.toString(), new OrderInfo(queueName.get(), orderRequest) );

			return response;
		} catch (Exception e) {
			throw new BookingException( "Could not make an order for " + orderRequest );
		}
	}

	@Override
	public void onCancel(UUID id) {
		OrderInfo orderInfo = client.<String, OrderInfo>getMap( shopId ).get( id.toString() );
		if(orderInfo == null) {
			throw new ResourceNotFoundException();
		}

		client.<OrderRequest>getDeque( shopId + SEPARATOR + orderInfo.getQueueName() ).remove(orderInfo.getRequest());
		client.getMap( shopId ).remove( id.toString() );
	}

	@Data
	@NoArgsConstructor
	@AllArgsConstructor
	public static class OrderInfo {

		private String queueName;
		private OrderRequest request;

	}

}
