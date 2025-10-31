package com.project.LibraryApp.repository;

import com.project.LibraryApp.model.Yazar;
import org.springframework.data.domain.Example;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface YazarRepository extends JpaRepository<Yazar, Long> {

    Example<Yazar> id(long id);
}
