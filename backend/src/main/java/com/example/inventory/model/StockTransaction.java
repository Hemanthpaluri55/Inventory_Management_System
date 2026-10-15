package com.example.inventory.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name="stock_transactions")
public class StockTransaction {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @ManyToOne(fetch=FetchType.EAGER) private Product product;
    @ManyToOne(fetch=FetchType.EAGER) private Warehouse warehouse;
    @ManyToOne(fetch=FetchType.EAGER) private Warehouse destinationWarehouse;
    private String type;
    private int quantity;
    private String note;
    private LocalDateTime transactionDate;

    @PrePersist void onCreate(){if(transactionDate==null) transactionDate=LocalDateTime.now();}
    public StockTransaction(){}
    public StockTransaction(Product product, Warehouse warehouse, Warehouse destinationWarehouse, String type, int quantity, String note){
        this.product=product;this.warehouse=warehouse;this.destinationWarehouse=destinationWarehouse;this.type=type;this.quantity=quantity;this.note=note;
    }
    public Long getId(){return id;} public void setId(Long id){this.id=id;}
    public Product getProduct(){return product;} public void setProduct(Product product){this.product=product;}
    public Warehouse getWarehouse(){return warehouse;} public void setWarehouse(Warehouse warehouse){this.warehouse=warehouse;}
    public Warehouse getDestinationWarehouse(){return destinationWarehouse;} public void setDestinationWarehouse(Warehouse destinationWarehouse){this.destinationWarehouse=destinationWarehouse;}
    public String getType(){return type;} public void setType(String type){this.type=type;}
    public int getQuantity(){return quantity;} public void setQuantity(int quantity){this.quantity=quantity;}
    public String getNote(){return note;} public void setNote(String note){this.note=note;}
    public LocalDateTime getTransactionDate(){return transactionDate;} public void setTransactionDate(LocalDateTime transactionDate){this.transactionDate=transactionDate;}
}
