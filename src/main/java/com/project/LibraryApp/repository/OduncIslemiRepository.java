package com.project.LibraryApp.repository;

import com.project.LibraryApp.model.OduncIslemi;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface OduncIslemiRepository extends JpaRepository<OduncIslemi, Long> {
    List<OduncIslemi> findAllByKullaniciId(Long kullaniciId);

    List<OduncIslemi> findAllByGercekIadeTarihiIsNullAndBeklenenIadeTarihiBeforeAndGecikmeMailGonderildiMiFalse(java.time.LocalDate date);
}