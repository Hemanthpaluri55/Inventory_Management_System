package com.example.inventory.repository;
import com.example.inventory.model.*;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.*;
public interface WarehouseStockRepository extends JpaRepository<WarehouseStock,Long> {
    Optional<WarehouseStock> findByProductIdAndWarehouseId(Long productId, Long warehouseId);
    List<WarehouseStock> findByProductId(Long productId);
    List<WarehouseStock> findByWarehouseId(Long warehouseId);
}
