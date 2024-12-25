package com.auth.payment_api.services;

import com.auth.payment_api.repositories.CustomerRepo;
import com.auth.payment_api.repositories.JWTTokenRepo;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import com.auth.payment_api.models.*;
import java.math.BigDecimal;

@Service
@RequiredArgsConstructor
public class AuthenticationService {

    private final PasswordEncoder passwordEncoder;
    private final CustomerRepo customerRepo;
    private final JWTService jwtService;
    private final JWTTokenRepo jwtTokenRepo;

    @Transactional
    public AuthenticationResponse register(RegisterRequest request) {
        if (customerRepo.existsByUsername(request.getUsername())) {
            throw new RuntimeException("Username is already taken. Please choose a different username.");
        }
        var customer = Customer.builder()
                .username(request.getUsername())
                .pass(passwordEncoder.encode(request.getPassword()))
                .balance(BigDecimal.valueOf(8.00))
                .build();
        customerRepo.save(customer);
        var token = jwtService.generateToken(customer);

        var jwtToken = JWTToken.builder()
                .token(token)
                .expired(false)
                .revoked(false)
                .customer(customer)
                .build();
        jwtTokenRepo.save(jwtToken);
        return AuthenticationResponse.builder()
                .token(token)
                .build();
    }

    @Transactional
    public AuthenticationResponse authenticate(AuthenticationRequest request) throws Exception {

        var customer = customerRepo.findByUsername(request.getUsername())
                .orElseThrow(() -> new Exception("User not found"));

        if (!customer.isAccountNonLocked()) {
            throw new Exception("Account is locked. Try again later.");
        }

        if(!passwordEncoder.matches(request.getPassword(), customer.getPassword())) {
            customer.setFailedAttempts(customer.getFailedAttempts() + 1);
            customer.setLastFailedAttempt(System.currentTimeMillis());
            customerRepo.save(customer);
            throw new Exception("Invalid password or username");
        }
        customer.setFailedAttempts(0);
        customerRepo.save(customer);

        var generatedToken = jwtService.generateToken(customer);

        var jwtToken = JWTToken.builder()
                .token(generatedToken)
                .expired(false)
                .revoked(false)
                .customer(customer)
                .build();
        jwtTokenRepo.save(jwtToken);
        return AuthenticationResponse.builder()
                .token(generatedToken)
                .build();
    }

}
