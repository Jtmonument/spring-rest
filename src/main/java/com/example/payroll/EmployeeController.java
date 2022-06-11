package com.example.payroll;

import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
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

    @PostMapping("/employees")
    public Employee newEmployee(@RequestBody Employee employee) {
        return repo.save(employee);
    }

    /*REST one by assembler*/
    @GetMapping("/employees/{id}")
    EntityModel<Employee> one(@PathVariable Long id) {
        Employee employee = repo.findById(id).orElseThrow(() -> new EmployeeNotFoundException(id));
        return assembler.toModel(employee);
    }

    @PutMapping("/employees/{id}")
    public Employee replaceEmployee(@RequestBody Employee employee, @PathVariable Long id) {
        return repo.findById(id).map(replacedEmployee -> {
            replacedEmployee.setName(employee.getName());
            replacedEmployee.setRole(employee.getRole());
            return repo.save(employee);
        }).orElseGet(() -> {
            employee.setId(id);
           return repo.save(employee);
        });
    }

    @DeleteMapping("/employees/{id}")
    public void deleteEmployee(@PathVariable Long id) {
        repo.deleteById(id);
    }
}
