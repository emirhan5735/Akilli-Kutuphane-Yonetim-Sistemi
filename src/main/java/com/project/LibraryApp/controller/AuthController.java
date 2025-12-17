package com.project.LibraryApp.controller;

import com.project.LibraryApp.model.Kullanici;
import com.project.LibraryApp.service.AuthService;
import com.project.LibraryApp.service.KullaniciService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.Map;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final KullaniciService kullaniciService;
    private final AuthService authService;

    public AuthController(KullaniciService kullaniciService, AuthService authService) {
        this.kullaniciService = kullaniciService;
        this.authService = authService;
    }

    @PostMapping("/register")
    public ResponseEntity<Kullanici> registerKullanici(@RequestBody Kullanici kullanici) {
        Kullanici registeredKullanici = kullaniciService.registerUser(kullanici);
        return new ResponseEntity<>(registeredKullanici, HttpStatus.CREATED);
    }

    @PostMapping("/login")
    public ResponseEntity<Map<String, Object>> authenticateKullanici(@RequestBody Map<String, String> loginRequest) {

        String kullaniciAdi = loginRequest.get("kullaniciAdi");
        String sifre = loginRequest.get("sifre");

        if (kullaniciAdi == null || sifre == null) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }

        try {
            Map<String, Object> response = authService.authenticateAndGenerateToken(kullaniciAdi, sifre);
            return new ResponseEntity<>(response, HttpStatus.OK);
        } catch (RuntimeException e) {
            return new ResponseEntity<>(Map.of("hata", "Kullanıcı adı veya şifre yanlış."), HttpStatus.UNAUTHORIZED);
        }
    }
}