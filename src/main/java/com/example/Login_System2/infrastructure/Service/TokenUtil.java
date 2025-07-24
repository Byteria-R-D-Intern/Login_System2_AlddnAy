package com.example.Login_System2.infrastructure.Service;

import org.springframework.stereotype.Component;

//Endpointlerde token kontrolü için kullanılack.PROFİL endpointlarında ve user endpointlarında kullanılacak.
//Raze bilgi olduğu için önceden yazdım  bıraktım.
@Component
public class TokenUtil {
    public enum TokenValidationStatus{
        VALID,
        MISSING_HEADER,
        INVALID_FORMAT,
        EMPTY_TOKEN,
        INVALID_OR_EXPIRED,
        EXPIRED
    }

    public TokenValidationStatus validationToken(String authorizationHeader , Jwtutil jwtUtil){
        if (authorizationHeader == null) {
            return TokenValidationStatus.MISSING_HEADER;
        }

        if (!authorizationHeader.startsWith("Bearer ")) {
            return TokenValidationStatus.INVALID_FORMAT;
        }

        String token = authorizationHeader.substring(7);

        if (token.trim().isEmpty()) {
            return TokenValidationStatus.EMPTY_TOKEN;
        }

        if (!jwtUtil.validateToken(token)) {
            return TokenValidationStatus.INVALID_OR_EXPIRED;
        }

        if (jwtUtil.isTokenExpired(token)) {
            return TokenValidationStatus.EXPIRED;
        }

        return TokenValidationStatus.VALID;
    }

}
