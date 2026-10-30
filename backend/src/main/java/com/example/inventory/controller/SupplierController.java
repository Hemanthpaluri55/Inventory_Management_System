package com.example.inventory.controller;
import com.example.inventory.model.Supplier;
import com.example.inventory.repository.SupplierRepository;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import java.util.List;
@RestController @RequestMapping("/api/suppliers")
public class SupplierController {
    private final SupplierRepository repo;
    public SupplierController(SupplierRepository repo){this.repo=repo;}
    @GetMapping public List<Supplier> all(){return repo.findAll();}
    @PostMapping @ResponseStatus(HttpStatus.CREATED) public Supplier create(@Valid @RequestBody Supplier s){s.setId(null);return repo.save(s);}
    @PutMapping("/{id}") public Supplier update(@PathVariable Long id,@Valid @RequestBody Supplier s){
        Supplier old=repo.findById(id).orElseThrow(()->new ResponseStatusException(HttpStatus.NOT_FOUND,"Supplier not found"));
        old.setName(s.getName());old.setEmail(s.getEmail());old.setPhone(s.getPhone());old.setAddress(s.getAddress());return repo.save(old);
    }
    @DeleteMapping("/{id}") @ResponseStatus(HttpStatus.NO_CONTENT) public void delete(@PathVariable Long id){
        if(!repo.existsById(id))throw new ResponseStatusException(HttpStatus.NOT_FOUND,"Supplier not found");
        repo.deleteById(id);
    }
}
