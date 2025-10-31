package com.project.LibraryApp.service;

import com.project.LibraryApp.model.Kullanici;
import com.project.LibraryApp.model.Rol;
import com.project.LibraryApp.repository.KullaniciRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class KullaniciService {

    private final KullaniciRepository kullaniciRepository;
    private final PasswordEncoder passwordEncoder;

    public KullaniciService(KullaniciRepository kullaniciRepository, PasswordEncoder passwordEncoder) {
        this.kullaniciRepository = kullaniciRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public Kullanici registerUser(Kullanici kullanici) {

        String hashedPassword = passwordEncoder.encode(kullanici.getSifreHash());
        kullanici.setSifreHash(hashedPassword);

        if (kullanici.getRol() == null) {
            kullanici.setRol(Rol.OGRENCI);
        }

        return kullaniciRepository.save(kullanici);
    }

    public List<Kullanici> findAllKullanicilar() {
        return kullaniciRepository.findAll();
    }

    public Optional<Kullanici> findKullaniciById(Long id) {
        return kullaniciRepository.findById(id);
    }

    public Optional<Kullanici> findByKullaniciAdi(String kullaniciAdi) {
        return kullaniciRepository.findByKullaniciAdi(kullaniciAdi);
    }

    public boolean verifyPassword(String rawPassword, String hashedPassword) {
        return passwordEncoder.matches(rawPassword, hashedPassword);
    }

    public void deleteKullanici(Long id) {
        kullaniciRepository.deleteById(id);
    }
}