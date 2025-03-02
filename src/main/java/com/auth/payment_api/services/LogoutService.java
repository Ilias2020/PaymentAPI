package com.auth.payment_api.services;

import com.auth.payment_api.repositories.JWTTokenRepo;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.logout.LogoutHandler;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class LogoutService implements LogoutHandler {

    private final JWTTokenRepo jwtTokenRepo;

    @Override
    public void logout(HttpServletRequest request, HttpServletResponse response,
                       Authentication authentication
    ) {
        String tokenHeader = request.getHeader("Authorization");
        if(tokenHeader == null && !tokenHeader.startsWith("Bearer "))
            return;

        String jwtToken = tokenHeader.substring(7);
        var tokenFromDatabase = jwtTokenRepo.findByToken(jwtToken).orElse(null);
        if(tokenFromDatabase != null) {
            tokenFromDatabase.setExpired(true);
            tokenFromDatabase.setRevoked(true);
            jwtTokenRepo.save(tokenFromDatabase);
        }
    }
}