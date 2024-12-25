package com.auth.payment_api.services;

import com.auth.payment_api.models.Transaction;
import com.auth.payment_api.repositories.CustomerRepo;
import com.auth.payment_api.repositories.JWTTokenRepo;
import com.auth.payment_api.repositories.TransactionRepo;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.math.BigDecimal;
import java.util.Date;

@Service
@RequiredArgsConstructor
public class PaymentService {

    private final CustomerRepo customerRepo;
    private final JWTService jwtService;
    private final TransactionRepo transactionRepo;
    private final JWTTokenRepo jwtTokenRepo;

    @Transactional
    public void processPayment(String authHeader) throws Exception {
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            throw new Exception("Unauthorized: No valid token provided");
        }

        String token = authHeader.substring(7);

        var tokenFromDatabase = jwtTokenRepo.findByToken(token)
                .orElseThrow(() -> new Exception("Unauthorized: Invalid token"));

        if (tokenFromDatabase.isExpired() || tokenFromDatabase.isRevoked()) {
            throw new Exception("Unauthorized: Token is no longer valid");
        }

        String username = jwtService.extractUsername(token);

        var customer = customerRepo.findByUsername(username)
                .orElseThrow(() -> new Exception("User not found"));

        if (customer.getBalance().compareTo(BigDecimal.valueOf(1.10)) < 0) {
            throw new Exception("Insufficient balance");
        }

        customer.setBalance(customer.getBalance().subtract(BigDecimal.valueOf(1.10)));
        customerRepo.save(customer);

        var transaction = new Transaction(
                BigDecimal.valueOf(1.10),
                new Date(),
                customer
        );
        transactionRepo.save(transaction);
    }
}

