package com.suraj.inventoryservice.repository;

import com.suraj.inventoryservice.model.Inventory;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface InventoryRepository extends JpaRepository<Inventory, Long> {
	Optional<Inventory> findBySkuCode(String skuCode);
}
