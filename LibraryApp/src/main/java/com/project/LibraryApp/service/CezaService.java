package com.project.LibraryApp.service;

import com.project.LibraryApp.model.Ceza;
import com.project.LibraryApp.repository.CezaRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class CezaService {

    private final CezaRepository cezaRepository;

    public CezaService(CezaRepository cezaRepository) {
        this.cezaRepository = cezaRepository;
    }

    public List<Ceza> findAllCezalar() {
        return cezaRepository.findAll();
    }

    public Optional<Ceza> findCezaById(Long id) {
        return cezaRepository.findById(id);
    }

    public Ceza saveCeza(Ceza ceza) {
        return cezaRepository.save(ceza);
    }

    public void odemeYap(Long cezaId) {
        Ceza ceza = findCezaById(cezaId)
                .orElseThrow(() -> new RuntimeException("Ödenecek ceza kaydı bulunamadı."));

        if (ceza.getOdendiMi()) {
            throw new RuntimeException("Bu ceza zaten ödenmiştir.");
        }

        ceza.setOdendiMi(true);
        cezaRepository.save(ceza);
    }
}