package com.project.LibraryApp.service;

import com.project.LibraryApp.model.Kitap;
import com.project.LibraryApp.repository.KitapRepository;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Optional;

@Service
public class KitapService {

    private final KitapRepository kitapRepository;
    private final YazarService yazarService;
    private final KategoriService kategoriService;

    public KitapService(KitapRepository kitapRepository, YazarService yazarService, KategoriService kategoriService) {
        this.kitapRepository = kitapRepository;
        this.yazarService = yazarService;
        this.kategoriService = kategoriService;
    }

    public Kitap saveKitap(Kitap kitap) {

        if (kitap.getYazar() == null || !yazarService.findYazarById(kitap.getYazar().getId()).isPresent()) {
            throw new RuntimeException("Geçersiz Yazar ID'si.");
        }
        if (kitap.getKategori() == null || !kategoriService.findKategoriById(kitap.getKategori().getId()).isPresent()) {
            throw new RuntimeException("Geçersiz Kategori ID'si.");
        }
        if (kitap.getIsbn() == null || kitap.getIsbn().isEmpty()) {
            String randomIsbn = "978-" + System.currentTimeMillis();
            kitap.setIsbn(randomIsbn);
        }
        return kitapRepository.save(kitap);
    }

    public List<Kitap> findAllKitaplar() {
        return kitapRepository.findAll();
    }

    public Optional<Kitap> findKitapById(Long id) {
        return kitapRepository.findById(id);
    }

    public List<Kitap> searchKitaplar(String keyword) {
        return kitapRepository.findAll();
    }

    public void deleteKitap(Long id) {
        kitapRepository.deleteById(id);
    }


    public void updateStok(Long id, int yeniStok) {
        Kitap kitap = findKitapById(id)
                .orElseThrow(() -> new RuntimeException("Kitap bulunamadı."));

        if (yeniStok < 0) {
            throw new RuntimeException("Stok adedi 0'dan küçük olamaz.");
        }

        kitap.setKopyaSayisi(yeniStok);
        kitapRepository.save(kitap);
    }
}