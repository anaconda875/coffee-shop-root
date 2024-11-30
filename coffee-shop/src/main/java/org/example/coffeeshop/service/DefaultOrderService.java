package org.example.coffeeshop.service;

import java.util.List;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicReference;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.example.coffeeshop.domain.model.OrderPosition;
import org.example.coffeeshop.dto.request.OrderRequest;
import org.example.coffeeshop.dto.response.OrderResponse;
import org.example.coffeeshop.exception.OrderException;
import org.example.coffeeshop.exception.QueueNotAvailableException;
import org.example.coffeeshop.exception.ResourceNotFoundException;
import org.example.coffeeshop.repository.OrderPositionRepository;
import org.redisson.api.RDequeReactive;
import org.redisson.api.RedissonReactiveClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.util.function.Tuple3;

@Service
public class DefaultOrderService implements OrderService {

	private static final String SEPARATOR = ":";

	private final String shopId;
	private final List<String> queues;
	private final OrderPositionRepository repository;
	private final RedissonReactiveClient client;

	public DefaultOrderService(
			@Value("#{appProperties.shopId}") String shopId,
			@Value("#{appProperties.queues}") List<String> queues,
			OrderPositionRepository repository,
			RedissonReactiveClient client) {
		this.shopId = shopId;
		this.queues = queues;
		this.repository = repository;
		this.client = client;
	}

	@Override
	public Mono<OrderResponse> onOrder(OrderRequest orderRequest) {
		AtomicReference<String> queueName = new AtomicReference<>();

		return Flux.fromIterable( queues )
				.flatMap( queue -> {
					RDequeReactive<OrderRequest> dequeReactive = client.getDeque( shopId + SEPARATOR + queue );
					return Mono.zip( dequeReactive.size(), Mono.just( dequeReactive ), Mono.just( queue ) );
				} )
				.reduce( (f, l) -> {
					if ( f.getT1() <= l.getT1() ) {
						queueName.set( f.getT3() );
						return f;
					}

					queueName.set( l.getT3() );
					return l;
				} )
				.map( Tuple3::getT2 )
				.switchIfEmpty( Mono.error( QueueNotAvailableException::new ) )
				.flatMap( minQueue -> {
					try {
						UUID id = UUID.randomUUID();
						orderRequest.setId( id );
						//Redis is single threaded, so the last position returned by addLast
						//is actually the position of the user in the queue
						return minQueue.addLast( new OrderRequest[] { orderRequest } )
								.flatMap( pos -> {
									OrderResponse response = OrderResponse.of( orderRequest, pos );
									//store which order belongs to which queue
									Mono<OrderInfo> put = client.<String, OrderInfo>getMap( shopId ).put(
											id.toString(),
											new OrderInfo(
													queueName.get(),
													orderRequest
											)
									);

									return put
											.then( repository.save( new OrderPosition( id, response.getPos(), response.getOf() ) ) )
											.then( Mono.just( response ) );
								} );
					}
					catch (Exception e) {
						return Mono.error( () -> new OrderException( "Could not make an order for " + orderRequest ) );
					}
				} );
	}

	@Override
	public Mono<Void> onCancel(UUID id) {
		return client.<String, OrderInfo>getMap( shopId ).get( id.toString() )
				.switchIfEmpty( Mono.error( ResourceNotFoundException::new ) )
				.flatMap( orderInfo -> {
					Mono<Boolean> removed1 = client.<OrderRequest>getDeque( shopId + SEPARATOR + orderInfo.getQueueName() )
							.remove(
									orderInfo.getRequest() );
					Mono<Object> removed2 = client.getMap( shopId ).remove( id.toString() );
					Mono<?> removed3 = repository.deleteByOrderId( id );

					return Mono.zip( removed1, removed2, removed3 );
				} )
				.then();
	}

	@Data
	@NoArgsConstructor
	@AllArgsConstructor
	public static class OrderInfo {

		private String queueName;
		private OrderRequest request;

	}

}
