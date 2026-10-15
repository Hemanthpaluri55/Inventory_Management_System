package com.example.inventory.controller;
import com.example.inventory.dto.StockRequest;
import com.example.inventory.model.StockTransaction;
import com.example.inventory.repository.StockTransactionRepository;
import com.example.inventory.repository.SupplierRepository;
import com.example.inventory.service.InventoryService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;
import java.util.*;
@RestController @RequestMapping("/api")
public class InventoryController {
    private final InventoryService service; private final StockTransactionRepository transactions; private final SupplierRepository suppliers;
    public InventoryController(InventoryService service,StockTransactionRepository transactions,SupplierRepository suppliers){this.service=service;this.transactions=transactions;this.suppliers=suppliers;}
    @GetMapping("/dashboard") public Map<String,Object> dashboard(){Map<String,Object> m=service.dashboard();m.put("supplierCount",suppliers.count());return m;}
    @GetMapping("/stock") public List<Map<String,Object>> stock(){return service.stockList();}
    @GetMapping("/transactions") public List<StockTransaction> transactions(){return transactions.findTop100ByOrderByTransactionDateDesc();}
    @PostMapping("/transactions/stock-in") public StockTransaction stockIn(@Valid @RequestBody StockRequest r){return service.stockIn(r);}
    @PostMapping("/transactions/stock-out") public StockTransaction stockOut(@Valid @RequestBody StockRequest r){return service.stockOut(r);}
    @PostMapping("/transactions/transfer") public StockTransaction transfer(@Valid @RequestBody StockRequest r){return service.transfer(r);}
}
