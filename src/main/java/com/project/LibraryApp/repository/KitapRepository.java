package com.project.LibraryApp.repository;

import com.project.LibraryApp.model.Kitap;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;


@Repository
public interface KitapRepository extends JpaRepository<Kitap, Long> {

}
