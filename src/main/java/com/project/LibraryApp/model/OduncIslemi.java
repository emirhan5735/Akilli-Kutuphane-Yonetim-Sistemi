package com.project.LibraryApp.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import java.time.LocalDate;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})

@Table (name = "odunc_islemleri")

public class OduncIslemi {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;


    @ManyToOne
    @JoinColumn(name = "kullanici_id", nullable = false)
    private Kullanici kullanici;


    @ManyToOne
    @JoinColumn(name = "kitap_id", nullable = false)
    private Kitap kitap;


    @JsonFormat(pattern = "yyyy-MM-dd")
    @Column(nullable = false)
    private LocalDate oduncTarihi;


    @JsonFormat(pattern = "yyyy-MM-dd")
    @Column(nullable = false)
    private LocalDate beklenenIadeTarihi;


    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate gercekIadeTarihi;


    @Column(nullable = false)
    private Boolean gecikmeMailGonderildiMi = false;


    @OneToOne(mappedBy = "oduncIslemi", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JsonIgnore
    private Ceza ceza;
}
