package com.example.payroll;

import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.IanaLinkRelations;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@RestController
public class EmployeeController {
    private final EmployeeRepository repo;
    private final EmployeeModelAssembler assembler;

    public EmployeeController(EmployeeRepository repo, EmployeeModelAssembler assembler) {
        this.repo = repo;
        this.assembler = assembler;
    }

    /*REST: Getting an aggregate root resource using an assembler for shorter code*/
    @GetMapping("/employees")
    CollectionModel<EntityModel<Employee>> all() {
        List<EntityModel<Employee>> employees = repo.findAll().stream()
                .map(assembler::toModel).collect(Collectors.toList());
        return CollectionModel.of(employees, linkTo(methodOn(EmployeeController.class)).withSelfRel());
    }

    /*POST controller method to handle old and new clients*/
    @PostMapping("/employees")
    public ResponseEntity<?> newEmployee(@RequestBody Employee employee) {
        EntityModel<Employee> newEntity = assembler.toModel(repo.save(employee));
        return ResponseEntity
                .created(newEntity.getRequiredLink(IanaLinkRelations.SELF).toUri())
                .body(newEntity);
    }

    /*REST one by assembler*/
    @GetMapping("/employees/{id}")
    EntityModel<Employee> one(@PathVariable Long id) {
        Employee employee = repo.findById(id).orElseThrow(() -> new EmployeeNotFoundException(id));
        return assembler.toModel(employee);
    }

    @PutMapping("/employees/{id}")
    public ResponseEntity<?> replaceEmployee(@RequestBody Employee employee, @PathVariable Long id) {
        Employee updated = repo.findById(id).map(replacedEmployee -> {
            replacedEmployee.setName(employee.getName());
            replacedEmployee.setRole(employee.getRole());
            return repo.save(employee);
        }).orElseGet(() -> {
            employee.setId(id);
           return repo.save(employee);
        });

        EntityModel<Employee> newEntity = assembler.toModel(updated);
        return ResponseEntity
                .created(newEntity.getRequiredLink(IanaLinkRelations.SELF).toUri())
                .body(newEntity);
    }

    @DeleteMapping("/employees/{id}")
    public ResponseEntity<?> deleteEmployee(@PathVariable Long id) {
        repo.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}
