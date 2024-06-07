package com.example.licentabackend;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootApplication
@EntityScan(basePackages = {"com.example.licentabackend.entities"})
@EnableJpaRepositories(basePackages = {"com.example.licentabackend.repository"})
public class LicentaBackendApplication {

    public static void main(String[] args) {
        SpringApplication.run(LicentaBackendApplication.class, args);
    }

}
