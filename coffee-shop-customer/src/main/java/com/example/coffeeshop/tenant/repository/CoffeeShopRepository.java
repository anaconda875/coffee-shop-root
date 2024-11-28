package com.example.coffeeshop.tenant.repository;

import com.example.coffeeshop.tenant.domain.model.CoffeeShop;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CoffeeShopRepository extends JpaRepository<CoffeeShop, Long> {
}
