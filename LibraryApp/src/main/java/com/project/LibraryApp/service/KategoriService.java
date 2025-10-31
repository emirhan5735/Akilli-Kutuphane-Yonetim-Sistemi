package com.project.LibraryApp.service;

import com.project.LibraryApp.model.Kategori;
import com.project.LibraryApp.repository.KategoriRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class KategoriService {

    private final KategoriRepository kategoriRepository;

    public KategoriService(KategoriRepository kategoriRepository) {
        this.kategoriRepository = kategoriRepository;
    }


    public Kategori saveKategori(Kategori kategori) {
        return kategoriRepository.save(kategori);
    }

    public List<Kategori> findAllKategoriler() {
        return kategoriRepository.findAll();
    }


    public Optional<Kategori> findKategoriById(Long id) {
        return kategoriRepository.findById(id);
    }


    public void deleteKategori(Long id) {
        kategoriRepository.deleteById(id);
    }
}