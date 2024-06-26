package com.example.wedecomerce.config;

import com.example.wedecomerce.security.SecurityUtils;
import com.nimbusds.jose.jwk.source.ImmutableSecret;
import com.nimbusds.jose.util.Base64;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;
import org.springframework.security.oauth2.jwt.NimbusJwtEncoder;

import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

@Configuration
public class SecurityJwtConfiguration {

    @Value("${spring.security.authentication.jwt.base64-secret}")
    private String jwtKey;

    @Bean
    public JwtEncoder jwtEncoder() {
        return new NimbusJwtEncoder(new ImmutableSecret<>(getSecretKey()));
    }

    private SecretKey getSecretKey() {
        byte[] keyBytes = Base64.from(jwtKey).decode();
        return new SecretKeySpec(keyBytes, 0, keyBytes.length, SecurityUtils.JWT_ALGORITHM.getName());
    }

    @Bean
    public JwtDecoder jwtDecoder() {
        NimbusJwtDecoder jwtDecoder = NimbusJwtDecoder.withSecretKey(getSecretKey()).macAlgorithm(SecurityUtils.JWT_ALGORITHM).build();
        return token -> {
            try {
                return jwtDecoder.decode(token);
            } catch (Exception e) {
                if (e.getMessage().contains("Invalid signature")) {
//                    metersService.trackTokenInvalidSignature();
                } else if (e.getMessage().contains("Jwt expired at")) {
//                    metersService.trackTokenExpired();
                } else if (e.getMessage().contains("Invalid JWT serialization")) {
//                    metersService.trackTokenMalformed();
                } else if (e.getMessage().contains("Invalid unsecured/JWS/JWE")) {
//                    metersService.trackTokenMalformed();
                }
                throw e;
            }
        };
    }

}
