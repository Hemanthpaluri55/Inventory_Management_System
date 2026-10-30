package com.example.inventory.service;

import com.example.inventory.dto.StockRequest;
import com.example.inventory.model.*;
import com.example.inventory.repository.*;
import org.springframework.http.HttpStatus;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;
import java.util.*;

@Service
public class InventoryService {
    private final ProductRepository products;
    private final WarehouseRepository warehouses;
    private final WarehouseStockRepository stocks;
    private final StockTransactionRepository transactions;
    private final ObjectProvider<LowStockEmailService> lowStockEmailService;

    public InventoryService(ProductRepository products, WarehouseRepository warehouses,
                            WarehouseStockRepository stocks, StockTransactionRepository transactions,
                            ObjectProvider<LowStockEmailService> lowStockEmailService) {
        this.products=products; this.warehouses=warehouses; this.stocks=stocks; this.transactions=transactions;
        this.lowStockEmailService=lowStockEmailService;
    }

    private Product product(Long id){return products.findById(id).orElseThrow(()->new ResponseStatusException(HttpStatus.NOT_FOUND,"Product not found"));}
    private Warehouse warehouse(Long id){return warehouses.findById(id).orElseThrow(()->new ResponseStatusException(HttpStatus.NOT_FOUND,"Warehouse not found"));}

    @Transactional
    public StockTransaction stockIn(StockRequest r){
        Product p=product(r.getProductId()); Warehouse w=warehouse(r.getWarehouseId());
        WarehouseStock s=stocks.findByProductIdAndWarehouseId(p.getId(),w.getId()).orElseGet(()->new WarehouseStock(p,w,0));
        s.setQuantity(s.getQuantity()+r.getQuantity()); stocks.save(s);
        return transactions.save(new StockTransaction(p,w,null,"STOCK_IN",r.getQuantity(),r.getNote()));
    }
    @Transactional
    public StockTransaction stockOut(StockRequest r){
        Product p=product(r.getProductId()); Warehouse w=warehouse(r.getWarehouseId());
        WarehouseStock s=stocks.findByProductIdAndWarehouseId(p.getId(),w.getId()).orElseThrow(()->new ResponseStatusException(HttpStatus.BAD_REQUEST,"No stock record exists for this product in the selected warehouse"));
        if(s.getQuantity()<r.getQuantity()) throw new ResponseStatusException(HttpStatus.BAD_REQUEST,"Insufficient stock");
        int totalBefore=stocks.findByProductId(p.getId()).stream().mapToInt(WarehouseStock::getQuantity).sum();
        s.setQuantity(s.getQuantity()-r.getQuantity()); stocks.save(s);
        StockTransaction transaction=transactions.save(new StockTransaction(p,w,null,"STOCK_OUT",r.getQuantity(),r.getNote()));
        int totalAfter=totalBefore-r.getQuantity();
        if((totalBefore>=p.getMinimumStock() && totalAfter<p.getMinimumStock()) || (totalBefore>0 && totalAfter==0)) {
            lowStockEmailService.ifAvailable(service -> service.sendLowStockAlert(p, totalAfter));
        }
        return transaction;
    }
    @Transactional
    public StockTransaction transfer(StockRequest r){
        if(r.getDestinationWarehouseId()==null) throw new ResponseStatusException(HttpStatus.BAD_REQUEST,"Destination warehouse is required");
        if(r.getWarehouseId().equals(r.getDestinationWarehouseId())) throw new ResponseStatusException(HttpStatus.BAD_REQUEST,"Source and destination warehouses must differ");
        Product p=product(r.getProductId()); Warehouse from=warehouse(r.getWarehouseId()); Warehouse to=warehouse(r.getDestinationWarehouseId());
        WarehouseStock source=stocks.findByProductIdAndWarehouseId(p.getId(),from.getId()).orElseThrow(()->new ResponseStatusException(HttpStatus.BAD_REQUEST,"No stock record exists at source warehouse"));
        if(source.getQuantity()<r.getQuantity()) throw new ResponseStatusException(HttpStatus.BAD_REQUEST,"Insufficient stock for transfer");
        WarehouseStock dest=stocks.findByProductIdAndWarehouseId(p.getId(),to.getId()).orElseGet(()->new WarehouseStock(p,to,0));
        source.setQuantity(source.getQuantity()-r.getQuantity()); dest.setQuantity(dest.getQuantity()+r.getQuantity());
        stocks.save(source); stocks.save(dest);
        return transactions.save(new StockTransaction(p,from,to,"TRANSFER",r.getQuantity(),r.getNote()));
    }
    public List<Map<String,Object>> stockList(){
        List<Map<String,Object>> out=new ArrayList<>();
        for(WarehouseStock s:stocks.findAll()){
            Map<String,Object> m=new LinkedHashMap<>();
            m.put("id",s.getId());m.put("productId",s.getProduct().getId());m.put("productName",s.getProduct().getName());
            m.put("sku",s.getProduct().getSku());m.put("warehouseId",s.getWarehouse().getId());m.put("warehouseName",s.getWarehouse().getName());
            m.put("quantity",s.getQuantity());m.put("minimumStock",s.getProduct().getMinimumStock());
            out.add(m);
        }
        return out;
    }
    public Map<String,Object> dashboard(){
        List<Product> ps=products.findAll(); List<WarehouseStock> ss=stocks.findAll();
        int units=ss.stream().mapToInt(WarehouseStock::getQuantity).sum();
        long low=ps.stream().filter(p->ss.stream().filter(s->s.getProduct().getId().equals(p.getId())).mapToInt(WarehouseStock::getQuantity).sum()<p.getMinimumStock()).count();
        Map<String,Object> m=new LinkedHashMap<>();
        m.put("productCount",ps.size());m.put("supplierCount",0);m.put("warehouseCount",warehouses.count());
        m.put("unitsInStock",units);m.put("lowStockCount",low);m.put("recentTransactions",transactions.findTop100ByOrderByTransactionDateDesc().stream().limit(5).toList());
        return m;
    }
}
