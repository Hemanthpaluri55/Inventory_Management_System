package com.example.inventory.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;

@Entity
public class Warehouse {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @NotBlank private String name;
    private String location;
    private String managerName;

    public Warehouse() {}
    public Warehouse(String name, String location, String managerName) {
        this.name=name; this.location=location; this.managerName=managerName;
    }
    public Long getId(){return id;} public void setId(Long id){this.id=id;}
    public String getName(){return name;} public void setName(String name){this.name=name;}
    public String getLocation(){return location;} public void setLocation(String location){this.location=location;}
    public String getManagerName(){return managerName;} public void setManagerName(String managerName){this.managerName=managerName;}
}
