package com.example.inventory.config;

import com.example.inventory.model.*;
import com.example.inventory.repository.*;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.*;
import java.math.BigDecimal;

@Configuration
public class DataSeeder {
    @Bean CommandLineRunner seed(ProductRepository products, SupplierRepository suppliers,
                                 WarehouseRepository warehouses, WarehouseStockRepository stocks) {
        return args -> {
            if (suppliers.count()==0) {
                suppliers.save(new Supplier("Northstar Supplies","sales@northstar.example","+91 98765 43210","Vijayawada"));
                suppliers.save(new Supplier("BluePeak Traders","contact@bluepeak.example","+91 91234 56780","Hyderabad"));
            }
            if (warehouses.count()==0) {
                warehouses.save(new Warehouse("Central Warehouse","Vijayawada","Ravi Kumar"));
                warehouses.save(new Warehouse("East Warehouse","Guntur","Priya Reddy"));
            }
            if (products.count()==0) {
                var ss=suppliers.findAll(); 
                Product p1=new Product();p1.setName("Wireless Mouse");p1.setSku("ACC-1001");p1.setCategory("Accessories");p1.setPrice(new BigDecimal("599.00"));p1.setMinimumStock(10);p1.setSupplier(ss.get(0));products.save(p1);
                Product p2=new Product();p2.setName("Mechanical Keyboard");p2.setSku("ACC-1002");p2.setCategory("Accessories");p2.setPrice(new BigDecimal("2499.00"));p2.setMinimumStock(8);p2.setSupplier(ss.get(0));products.save(p2);
                Product p3=new Product();p3.setName("USB-C Hub");p3.setSku("ELE-2001");p3.setCategory("Electronics");p3.setPrice(new BigDecimal("1799.00"));p3.setMinimumStock(6);p3.setSupplier(ss.get(Math.min(1,ss.size()-1)));products.save(p3);
                var ws=warehouses.findAll();
                stocks.save(new WarehouseStock(p1,ws.get(0),24));stocks.save(new WarehouseStock(p2,ws.get(0),6));stocks.save(new WarehouseStock(p3,ws.get(1),4));
            }
        };
    }
}
