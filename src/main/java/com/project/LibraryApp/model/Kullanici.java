package com.project.LibraryApp.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;


@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Table (name = "kullanici")

public class Kullanici {


    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;


    @Column(nullable = false, unique = true)
    private String kullaniciAdi;


    @Column(nullable = false)
    private String sifreHash;


    @Column(nullable = false, unique = true)
    private String mail;


    @Column(nullable = false)
    private String ad;


    @Column(nullable = false)
    private String soyad;


    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Rol rol;
}