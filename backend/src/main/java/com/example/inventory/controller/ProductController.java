package com.example.inventory.controller;

import com.example.inventory.model.Product;
import com.example.inventory.repository.ProductRepository;
import com.example.inventory.repository.WarehouseStockRepository;
import com.example.inventory.service.LowStockEmailService;
import org.springframework.beans.factory.ObjectProvider;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import java.util.*;

@RestController @RequestMapping("/api/products")
public class ProductController {
    private final ProductRepository repo; private final WarehouseStockRepository stocks;
    private final ObjectProvider<LowStockEmailService> notificationService;
    public ProductController(ProductRepository repo, WarehouseStockRepository stocks,
                             ObjectProvider<LowStockEmailService> notificationService){
        this.repo=repo;this.stocks=stocks;this.notificationService=notificationService;
    }
    @GetMapping public List<Map<String,Object>> all(@RequestParam(required=false) String search){
        List<Product> list=(search==null||search.isBlank())?repo.findAll():repo.findByNameContainingIgnoreCaseOrSkuContainingIgnoreCase(search,search);
        return list.stream().map(this::view).toList();
    }
    @GetMapping("/{id}") public Map<String,Object> one(@PathVariable Long id){return view(repo.findById(id).orElseThrow(()->new ResponseStatusException(HttpStatus.NOT_FOUND,"Product not found")));}
    @PostMapping @ResponseStatus(HttpStatus.CREATED) public Map<String,Object> create(@Valid @RequestBody Product p){
        p.setId(null);
        Product saved=repo.save(p);
        notificationService.ifAvailable(service -> service.sendProductCreated(saved));
        return view(saved);
    }
    @PutMapping("/{id}") public Map<String,Object> update(@PathVariable Long id,@Valid @RequestBody Product p){
        Product old=repo.findById(id).orElseThrow(()->new ResponseStatusException(HttpStatus.NOT_FOUND,"Product not found"));
        old.setName(p.getName());old.setSku(p.getSku());old.setCategory(p.getCategory());old.setPrice(p.getPrice());old.setMinimumStock(p.getMinimumStock());old.setSupplier(p.getSupplier());
        Product saved=repo.save(old);
        notificationService.ifAvailable(service -> service.sendProductUpdated(saved));
        return view(saved);
    }
    @DeleteMapping("/{id}") @ResponseStatus(HttpStatus.NO_CONTENT) public void delete(@PathVariable Long id){
        if(!repo.existsById(id)) throw new ResponseStatusException(HttpStatus.NOT_FOUND,"Product not found");
        if(!stocks.findByProductId(id).isEmpty()) throw new ResponseStatusException(HttpStatus.CONFLICT,"Remove warehouse stock before deleting this product");
        repo.deleteById(id);
    }
    private Map<String,Object> view(Product p){
        int total=stocks.findByProductId(p.getId()).stream().mapToInt(s->s.getQuantity()).sum();
        Map<String,Object> m=new LinkedHashMap<>();m.put("id",p.getId());m.put("name",p.getName());m.put("sku",p.getSku());m.put("category",p.getCategory());
        m.put("price",p.getPrice());m.put("minimumStock",p.getMinimumStock());m.put("quantity",total);m.put("supplier",p.getSupplier());m.put("supplierId",p.getSupplier()==null?null:p.getSupplier().getId());
        return m;
    }
}
