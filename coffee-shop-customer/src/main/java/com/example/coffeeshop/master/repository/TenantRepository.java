package com.example.coffeeshop.master.repository;

import java.util.UUID;

import com.example.coffeeshop.master.domain.model.TenantPersistenceInfo;
import org.springframework.data.repository.ListCrudRepository;

public interface TenantRepository extends ListCrudRepository<TenantPersistenceInfo, UUID> {
}
