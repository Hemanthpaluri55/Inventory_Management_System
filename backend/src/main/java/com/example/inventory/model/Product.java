package com.example.inventory.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.PositiveOrZero;
import java.math.BigDecimal;

@Entity
@Table(name="products")
public class Product {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @NotBlank private String name;
    private String sku;
    private String category;
    @PositiveOrZero private BigDecimal price = BigDecimal.ZERO;
    @Min(0) private int minimumStock = 5;
    @ManyToOne(fetch=FetchType.EAGER) @JoinColumn(name="supplier_id")
    private Supplier supplier;

    public Product() {}
    public Long getId(){return id;} public void setId(Long id){this.id=id;}
    public String getName(){return name;} public void setName(String name){this.name=name;}
    public String getSku(){return sku;} public void setSku(String sku){this.sku=sku;}
    public String getCategory(){return category;} public void setCategory(String category){this.category=category;}
    public BigDecimal getPrice(){return price;} public void setPrice(BigDecimal price){this.price=price;}
    public int getMinimumStock(){return minimumStock;} public void setMinimumStock(int minimumStock){this.minimumStock=minimumStock;}
    public Supplier getSupplier(){return supplier;} public void setSupplier(Supplier supplier){this.supplier=supplier;}
}
