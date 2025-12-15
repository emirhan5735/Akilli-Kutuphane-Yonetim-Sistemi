package com.project.LibraryApp.controller;

import com.project.LibraryApp.model.Kitap;
import com.project.LibraryApp.model.Yazar;
import com.project.LibraryApp.model.Kategori;
import com.project.LibraryApp.service.KitapService;
import com.project.LibraryApp.service.YazarService;
import com.project.LibraryApp.service.KategoriService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;


@RestController
@RequestMapping("/api/admin")
public class AdminController {

    private final KitapService kitapService;
    private final YazarService yazarService;
    private final KategoriService kategoriService;

    public AdminController(KitapService kitapService, YazarService yazarService, KategoriService kategoriService) {
        this.kitapService = kitapService;
        this.yazarService = yazarService;
        this.kategoriService = kategoriService;
    }

    @PostMapping("/yazar")
    public ResponseEntity<Yazar> createYazar(@RequestBody Yazar yazar) {
        Yazar savedYazar = yazarService.saveYazar(yazar);
        return new ResponseEntity<>(savedYazar, HttpStatus.CREATED);
    }

    @GetMapping("/yazarlar")
    public List<Yazar> getAllYazarlar() {
        return yazarService.findAllYazarlar();
    }

    @PutMapping("/yazar/{id}")
    public ResponseEntity<Yazar> updateYazar(@PathVariable Long id, @RequestBody Yazar yazarDetails) {
        return yazarService.findYazarById(id)
                .map(yazar -> {
                    yazar.setAd(yazarDetails.getAd());
                    yazar.setSoyad(yazarDetails.getSoyad());
                    Yazar updatedYazar = yazarService.saveYazar(yazar);
                    return new ResponseEntity<>(updatedYazar, HttpStatus.OK);
                })
                .orElseGet(() -> new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }


    @DeleteMapping("/yazar/{id}")
    public ResponseEntity<Void> deleteYazar(@PathVariable Long id) {
        yazarService.deleteYazar(id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }


    @PostMapping("/kategori")
    public ResponseEntity<Kategori> createKategori(@RequestBody Kategori kategori) {
        Kategori savedKategori = kategoriService.saveKategori(kategori);
        return new ResponseEntity<>(savedKategori, HttpStatus.CREATED);
    }

    @GetMapping("/kategoriler")
    public List<Kategori> getAllKategoriler() {
        return kategoriService.findAllKategoriler();
    }

    @PutMapping("/kategori/{id}")
    public ResponseEntity<Kategori> updateKategori(@PathVariable Long id, @RequestBody Kategori kategoriDetails) {
        return kategoriService.findKategoriById(id)
                .map(kategori -> {
                    kategori.setAd(kategoriDetails.getAd());
                    Kategori updatedKategori = kategoriService.saveKategori(kategori);
                    return new ResponseEntity<>(updatedKategori, HttpStatus.OK);
                })
                .orElseGet(() -> new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    @DeleteMapping("/kategori/{id}")
    public ResponseEntity<Void> deleteKategori(@PathVariable Long id) {
        kategoriService.deleteKategori(id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @PostMapping("/kitap")
    public ResponseEntity<Object> createKitap(@RequestBody Kitap kitap) {
        try {
            Kitap savedKitap = kitapService.saveKitap(kitap);
            return new ResponseEntity<>(savedKitap, HttpStatus.CREATED);
        } catch (RuntimeException e) {
            Map<String, String> hataDetaylari = new HashMap<>();
            hataDetaylari.put("hata", "400 Bad Request");
            hataDetaylari.put("mesaj", e.getMessage());

            return new ResponseEntity<>(hataDetaylari, HttpStatus.BAD_REQUEST);
        }
    }


    @DeleteMapping("/kitap/{id}")
    public ResponseEntity<Void> deleteKitap(@PathVariable Long id) {
        kitapService.deleteKitap(id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}