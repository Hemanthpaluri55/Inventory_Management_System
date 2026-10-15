package com.example.inventory.repository;
import com.example.inventory.model.StockTransaction;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
public interface StockTransactionRepository extends JpaRepository<StockTransaction,Long> {
    List<StockTransaction> findTop100ByOrderByTransactionDateDesc();
}
