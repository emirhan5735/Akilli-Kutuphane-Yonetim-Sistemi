package com.project.LibraryApp.service;

import com.project.LibraryApp.model.Kullanici;
import com.project.LibraryApp.security.JwtTokenProvider;
import org.springframework.stereotype.Service;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Service
public class AuthService {

    private final KullaniciService kullaniciService;
    private final JwtTokenProvider tokenProvider;

    public AuthService(KullaniciService kullaniciService, JwtTokenProvider tokenProvider) {
        this.kullaniciService = kullaniciService;
        this.tokenProvider = tokenProvider;
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

        String jwt = tokenProvider.generateToken(kullanici);

        Map<String, Object> response = new HashMap<>();
        response.put("token", jwt);
        response.put("kullaniciAdi", kullanici.getKullaniciAdi());
        response.put("rol", kullanici.getRol().name());
        response.put("id", kullanici.getId());

        return response;
    }
}