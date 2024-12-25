package com.auth.payment_api.repositories;

import com.auth.payment_api.models.Customer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Repository
public interface CustomerRepo extends JpaRepository<Customer, Integer> {
    Optional<Customer> findByUsername(String username);
    boolean existsByUsername(String username);
}