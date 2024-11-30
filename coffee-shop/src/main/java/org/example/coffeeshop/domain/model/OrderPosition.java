package org.example.coffeeshop.domain.model;

import java.util.UUID;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@NoArgsConstructor
public class OrderPosition {

	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE)
	private Long id;

	private UUID orderId;

	private Integer position;

	private UUID userId;

	public OrderPosition(UUID orderId, Integer position, UUID userId) {
		this.orderId = orderId;
		this.position = position;
		this.userId = userId;
	}
}
