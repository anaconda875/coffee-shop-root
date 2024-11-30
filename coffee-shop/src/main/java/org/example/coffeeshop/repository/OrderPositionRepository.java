package org.example.coffeeshop.repository;

import java.util.UUID;

import com.htech.data.jpa.reactive.repository.ReactiveJpaRepository;
import org.example.coffeeshop.domain.model.OrderPosition;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Mono;

public interface OrderPositionRepository extends ReactiveJpaRepository<OrderPosition, Long> {

	@Transactional
	Mono<?> deleteByOrderId(UUID orderId);

}
