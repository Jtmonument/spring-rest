package com.example.payroll;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class LoadDatabase {
    private static final Logger log = LoggerFactory.getLogger(LoadDatabase.class);

    @Bean
    CommandLineRunner initDatabase(EmployeeRepository employeRepo, OrderRepository orderRepo) {
        return args -> {
            employeRepo.save(new Employee("John", "Doe", "Java Developer"));
            employeRepo.findAll().forEach(employee -> log.info("Preloaded " + employee));
            orderRepo.save(new Order("iPhone 13", Status.COMPLETED));
            orderRepo.save(new Order("GeForce GTX 1080 Ti", Status.IN_PROGRESS));
            orderRepo.findAll().forEach(order -> log.info("Preloaded " + order));
        };
    }
}
