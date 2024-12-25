package com.auth.payment_api.repositories;

import com.auth.payment_api.models.Customer;
import com.auth.payment_api.models.Transaction;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface TransactionRepo extends JpaRepository<Transaction, Integer> {

        List<Transaction> findByCustomer(Customer customer);
    }


