package com.example.inventory.dto;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
public class StockRequest {
    @NotNull private Long productId;
    @NotNull private Long warehouseId;
    @Min(1) private int quantity;
    private String note;
    private Long destinationWarehouseId;
    public Long getProductId(){return productId;} public void setProductId(Long productId){this.productId=productId;}
    public Long getWarehouseId(){return warehouseId;} public void setWarehouseId(Long warehouseId){this.warehouseId=warehouseId;}
    public int getQuantity(){return quantity;} public void setQuantity(int quantity){this.quantity=quantity;}
    public String getNote(){return note;} public void setNote(String note){this.note=note;}
    public Long getDestinationWarehouseId(){return destinationWarehouseId;} public void setDestinationWarehouseId(Long id){this.destinationWarehouseId=id;}
}
