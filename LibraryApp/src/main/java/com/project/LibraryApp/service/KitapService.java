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

    public void updateStok(Long kitapId, int miktarDegisimi) {
        Kitap kitap = findKitapById(kitapId)
                .orElseThrow(() -> new RuntimeException("Güncellenecek kitap bulunamadı."));

        int yeniStok = kitap.getKopyaSayisi() + miktarDegisimi;

        if (yeniStok < 0) {
            throw new RuntimeException("Stok sıfırın altına düşemez.");
        }

        kitap.setKopyaSayisi(yeniStok);
        kitapRepository.save(kitap);
    }
}