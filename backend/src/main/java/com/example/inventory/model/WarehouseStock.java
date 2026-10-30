package com.example.inventory.model;

import jakarta.persistence.*;

@Entity
@Table(name="warehouse_stock", uniqueConstraints=@UniqueConstraint(columnNames={"product_id","warehouse_id"}))
public class WarehouseStock {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @ManyToOne(fetch=FetchType.EAGER, optional=false) @JoinColumn(name="product_id")
    private Product product;
    @ManyToOne(fetch=FetchType.EAGER, optional=false) @JoinColumn(name="warehouse_id")
    private Warehouse warehouse;
    private int quantity;

    public WarehouseStock() {}
    public WarehouseStock(Product product, Warehouse warehouse, int quantity){this.product=product;this.warehouse=warehouse;this.quantity=quantity;}
    public Long getId(){return id;} public void setId(Long id){this.id=id;}
    public Product getProduct(){return product;} public void setProduct(Product product){this.product=product;}
    public Warehouse getWarehouse(){return warehouse;} public void setWarehouse(Warehouse warehouse){this.warehouse=warehouse;}
    public int getQuantity(){return quantity;} public void setQuantity(int quantity){this.quantity=quantity;}
}
