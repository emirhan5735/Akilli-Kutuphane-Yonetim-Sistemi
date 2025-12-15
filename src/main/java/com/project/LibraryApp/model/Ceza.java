package com.project.LibraryApp.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import java.math.BigDecimal;
import java.time.LocalDate;


@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "cezalar")

public class Ceza {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;


    @OneToOne
    @JoinColumn(name = "odunc_islem_id", unique = true, nullable = false)
    private OduncIslemi oduncIslemi;


    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal cezaMiktari;


    @Column(nullable = false)
    private LocalDate cezaTarihi;


    @Column(nullable = false)
    private Boolean odendiMi = false;
}