package com.example.payroll;

import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
public class EmployeeController {
    private final EmployeeRepository repo;

    public EmployeeController(EmployeeRepository repo) {
        this.repo = repo;
    }

    /*RPC (Remote Procedure Call): Getting an aggregate root*/
    @GetMapping("/employees")
    public List<Employee> allRPC() {
        return repo.findAll();
    }

    @PostMapping("/employees")
    public Employee newEmployee(@RequestBody Employee employee) {
        return repo.save(employee);
    }

    /*RPC one*/
    @GetMapping("/employees/{id}")
    public Employee one(@PathVariable Long id) {
        return repo.findById(id).orElseThrow(() -> new EmployeeNotFoundException(id));
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
