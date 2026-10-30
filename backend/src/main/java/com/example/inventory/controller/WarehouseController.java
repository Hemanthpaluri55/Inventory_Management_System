package com.example.inventory.controller;
import com.example.inventory.model.Warehouse;
import com.example.inventory.repository.WarehouseRepository;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import java.util.List;
@RestController @RequestMapping("/api/warehouses")
public class WarehouseController {
    private final WarehouseRepository repo;
    public WarehouseController(WarehouseRepository repo){this.repo=repo;}
    @GetMapping public List<Warehouse> all(){return repo.findAll();}
    @PostMapping @ResponseStatus(HttpStatus.CREATED) public Warehouse create(@Valid @RequestBody Warehouse w){w.setId(null);return repo.save(w);}
    @PutMapping("/{id}") public Warehouse update(@PathVariable Long id,@Valid @RequestBody Warehouse w){
        Warehouse old=repo.findById(id).orElseThrow(()->new ResponseStatusException(HttpStatus.NOT_FOUND,"Warehouse not found"));
        old.setName(w.getName());old.setLocation(w.getLocation());old.setManagerName(w.getManagerName());return repo.save(old);
    }
    @DeleteMapping("/{id}") @ResponseStatus(HttpStatus.NO_CONTENT) public void delete(@PathVariable Long id){
        if(!repo.existsById(id))throw new ResponseStatusException(HttpStatus.NOT_FOUND,"Warehouse not found");
        repo.deleteById(id);
    }
}
