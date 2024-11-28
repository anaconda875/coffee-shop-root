package com.example.coffeeshop.master.domain.model;

import java.util.UUID;

import org.hibernate.annotations.GenericGenerator;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.Data;

@Entity
@Data
public class TenantPersistenceInfo {
  
  @Id
  @GeneratedValue(strategy = GenerationType.UUID)
  private UUID id;

}
