package com.project.LibraryApp.service;

import com.project.LibraryApp.model.Yazar;
import com.project.LibraryApp.repository.YazarRepository;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Optional;


@Service
public class YazarService {

    private final YazarRepository yazarRepository;

    public YazarService(YazarRepository yazarRepository) {
        this.yazarRepository = yazarRepository;
    }

    public Yazar saveYazar(Yazar yazar) {
        return yazarRepository.save(yazar);
    }

    public List<Yazar> findAllYazarlar() {
        return yazarRepository.findAll();
    }

    public Optional<Yazar> findYazarById(Long id) {
        return yazarRepository.findById(id);
    }

    public void deleteYazar(Long id) {
        yazarRepository.deleteById(id);
    }
}