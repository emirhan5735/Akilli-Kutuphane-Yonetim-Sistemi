package com.project.LibraryApp.service;

import com.project.LibraryApp.model.Kullanici;
import com.project.LibraryApp.repository.KullaniciRepository;
import com.project.LibraryApp.security.JwtTokenProvider;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.Random;

@Service
public class AuthService {

    private final KullaniciService kullaniciService;
    private final KullaniciRepository kullaniciRepository;
    private final JwtTokenProvider tokenProvider;
    private final BildirimService bildirimService;
    private final PasswordEncoder passwordEncoder;


    public AuthService(KullaniciService kullaniciService,
                       KullaniciRepository kullaniciRepository,
                       JwtTokenProvider tokenProvider,
                       BildirimService bildirimService,
                       PasswordEncoder passwordEncoder) {
        this.kullaniciService = kullaniciService;
        this.kullaniciRepository = kullaniciRepository;
        this.tokenProvider = tokenProvider;
        this.bildirimService = bildirimService;
        this.passwordEncoder = passwordEncoder;
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


    public String initiatePasswordReset(String email) {

        Optional<Kullanici> kullaniciOpt = kullaniciRepository.findByMail(email);

        if (kullaniciOpt.isPresent()) {
            Kullanici kullanici = kullaniciOpt.get();

            String code = String.valueOf(new Random().nextInt(900000) + 100000);

            kullanici.setResetToken(code);
            kullanici.setResetTokenCreationDate(LocalDateTime.now());

            kullaniciRepository.save(kullanici);

            bildirimService.sendPasswordResetCode(email, code);
            return "Kod gönderildi.";
        } else {
            return "Kullanıcı bulunamadı.";
        }
    }


    public String resetPassword(String email, String code, String newPassword, String confirmPassword) {
        Optional<Kullanici> kullaniciOpt = kullaniciRepository.findByMail(email);

        if (!kullaniciOpt.isPresent()) {
            return "Kullanıcı bulunamadı.";
        }

        Kullanici kullanici = kullaniciOpt.get();

        if (kullanici.getResetToken() == null || !kullanici.getResetToken().equals(code)) {
            return "Geçersiz veya hatalı kod.";
        }

        if (kullanici.getResetTokenCreationDate() == null) {
            return "Kod süresi dolmuş.";
        }

        long minutes = Duration.between(kullanici.getResetTokenCreationDate(), LocalDateTime.now()).toMinutes();
        if (minutes > 15) {
            return "Kodun süresi dolmuş. Lütfen tekrar deneyin.";
        }

        if (!newPassword.equals(confirmPassword)) {
            return "Şifreler uyuşmuyor.";
        }

        kullanici.setSifreHash(passwordEncoder.encode(newPassword));


        kullanici.setResetToken(null);
        kullanici.setResetTokenCreationDate(null);

        kullaniciRepository.save(kullanici);

        return "Başarılı";
    }
}