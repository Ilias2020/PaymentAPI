package com.auth.payment_api.controllers;

import com.auth.payment_api.models.AuthenticationRequest;
import com.auth.payment_api.models.AuthenticationResponse;
import com.auth.payment_api.models.RegisterRequest;
import com.auth.payment_api.services.AuthenticationService;
import com.auth.payment_api.services.PaymentService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthenticationController {

    private final AuthenticationService service;
    private final PaymentService paymentService;

    @PostMapping("/register")
    public ResponseEntity<AuthenticationResponse> register(@RequestBody RegisterRequest request) {
        return ResponseEntity.ok(service.register(request));
    }

    @PostMapping("/login")
    public ResponseEntity<AuthenticationResponse> authenticate(
            @RequestBody AuthenticationRequest request
    ) throws Exception {
        return ResponseEntity.ok(service.authenticate(request));
    }

    @PostMapping("/payment")
    public ResponseEntity<String> payment(@RequestHeader("Authorization") String authHeader) throws Exception {
        paymentService.processPayment(authHeader);
        return ResponseEntity.ok("Payment of 1.1 USD processed successfully!");
    }

}
