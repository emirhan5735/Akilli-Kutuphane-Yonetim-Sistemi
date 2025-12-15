package com.project.LibraryApp.model;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import com.fasterxml.jackson.annotation.JsonIgnore;
import java.time.LocalDate;
import java.util.List;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Table (name = "kitap")

public class Kitap {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String ad;

    @Column(nullable = false, unique = true)
    private String isbn;

    @Column(nullable = false)
    private Integer yayinYili;

    @Column(nullable = false)
    private Integer kopyaSayisi;



    @ManyToOne
    @JoinColumn(name = "yazar_id", nullable = false)
    private Yazar yazar;

    @ManyToOne
    @JoinColumn(name = "kategori_id", nullable = false)
    private Kategori kategori;

    @OneToMany(mappedBy = "kitap", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JsonIgnore
    private List<OduncIslemi> oduncIslemleri;
}
