package com.project.LibraryApp.service;

import com.project.LibraryApp.model.Kullanici;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Service
public class AuthService {

    private final KullaniciService kullaniciService;

    public AuthService(KullaniciService kullaniciService) {
        this.kullaniciService = kullaniciService;
    }

    public Map<String, Object> authenticateAndGenerateToken(String kullaniciAdi, String sifre) {

        Optional<Kullanici> kullaniciOpt = kullaniciService.findByKullaniciAdi(kullaniciAdi);

        if (!kullaniciOpt.isPresent()) {
            throw new RuntimeException("Kullanıcı adı veya şifre yanlış.");
        }

        Kullanici kullanici = kullaniciOpt.get();

        boolean isPasswordValid = kullaniciService.verifyPassword(sifre, kullanici.getSifreHash());

        if (!isPasswordValid) {
            throw new RuntimeException("Kullanıcı adı veya şifre yanlış.");
        }

        String jwt = "SAMPLE_JWT_TOKEN_FOR_" + kullaniciAdi;

        Map<String, Object> response = new HashMap<>();
        response.put("token", jwt);
        response.put("kullaniciAdi", kullanici.getKullaniciAdi());
        response.put("rol", kullanici.getRol());

        return response;
    }
}