package com.project.LibraryApp.repository;

import com.project.LibraryApp.model.Kategori;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;


@Repository
public interface KategoriRepository extends JpaRepository<Kategori, Long> {

}