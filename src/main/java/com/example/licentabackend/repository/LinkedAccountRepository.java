package com.example.licentabackend.repository;

import com.example.licentabackend.entities.LinkedAccount;
import org.springframework.data.jpa.repository.JpaRepository;

public interface LinkedAccountRepository extends JpaRepository<LinkedAccount, Integer> {
}
