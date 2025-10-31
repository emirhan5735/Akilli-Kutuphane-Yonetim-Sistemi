package com.project.LibraryApp.service;

import com.project.LibraryApp.model.Ceza;
import com.project.LibraryApp.model.Kitap;
import com.project.LibraryApp.model.Kullanici;
import com.project.LibraryApp.model.OduncIslemi;
import com.project.LibraryApp.repository.KitapRepository;
import com.project.LibraryApp.repository.OduncIslemiRepository;
import com.project.LibraryApp.repository.KullaniciRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.math.BigDecimal;
import java.util.Optional;

@Service
public class OduncService {

    private final OduncIslemiRepository oduncIslemiRepository;
    private final KitapRepository kitapRepository;
    private final KullaniciRepository kullaniciRepository;
    private final CezaService cezaService; // Yeni enjekte edilen CezaService
    private final BildirimService bildirimService; // Yeni enjekte edilen BildirimService

    public OduncService(OduncIslemiRepository oduncIslemiRepository, KitapRepository kitapRepository,
                        KullaniciRepository kullaniciRepository, CezaService cezaService,
                        BildirimService bildirimService) {
        this.oduncIslemiRepository = oduncIslemiRepository;
        this.kitapRepository = kitapRepository;
        this.kullaniciRepository = kullaniciRepository;
        this.cezaService = cezaService;
        this.bildirimService = bildirimService;
    }

    @Transactional
    public OduncIslemi kitapOduncAl(Long kullaniciId, Long kitapId, LocalDate beklenenIadeTarihi) {

        Kullanici kullanici = kullaniciRepository.findById(kullaniciId)
                .orElseThrow(() -> new RuntimeException("Kullanıcı bulunamadı."));

        Kitap kitap = kitapRepository.findById(kitapId)
                .orElseThrow(() -> new RuntimeException("Kitap bulunamadı."));

        if (kitap.getKopyaSayisi() <= 0) {
            throw new RuntimeException("Kitabın mevcut kopyası kalmamıştır.");
        }

        OduncIslemi yeniIslem = new OduncIslemi();
        yeniIslem.setKullanici(kullanici);
        yeniIslem.setKitap(kitap);
        yeniIslem.setOduncTarihi(LocalDate.now());
        yeniIslem.setBeklenenIadeTarihi(beklenenIadeTarihi);

        kitap.setKopyaSayisi(kitap.getKopyaSayisi() - 1);
        kitapRepository.save(kitap);

        return oduncIslemiRepository.save(yeniIslem);
    }

    @Transactional
    public OduncIslemi kitapIadeEt(Long oduncIslemId) {
        OduncIslemi islem = oduncIslemiRepository.findById(oduncIslemId)
                .orElseThrow(() -> new RuntimeException("Ödünç işlemi bulunamadı."));

        if (islem.getGercekIadeTarihi() != null) {
            throw new RuntimeException("Bu kitap zaten iade edilmiş.");
        }

        islem.setGercekIadeTarihi(LocalDate.now());

        Kitap kitap = islem.getKitap();
        kitap.setKopyaSayisi(kitap.getKopyaSayisi() + 1);
        kitapRepository.save(kitap);

        hesaplaVeOlusturCeza(islem);

        return oduncIslemiRepository.save(islem);
    }

    private void hesaplaVeOlusturCeza(OduncIslemi islem) {

        LocalDate beklenen = islem.getBeklenenIadeTarihi();
        LocalDate gercek = islem.getGercekIadeTarihi();

        if (gercek.isAfter(beklenen)) {

            long gecikmeGun = ChronoUnit.DAYS.between(beklenen, gercek);
            BigDecimal gunlukCeza = new BigDecimal("0.50");
            BigDecimal toplamCezaMiktari = gunlukCeza.multiply(new BigDecimal(gecikmeGun));

            Ceza ceza = new Ceza();
            ceza.setOduncIslemi(islem);
            ceza.setCezaMiktari(toplamCezaMiktari);
            ceza.setCezaTarihi(LocalDate.now());
            ceza.setOdendiMi(false);

            cezaService.saveCeza(ceza); // CezaService üzerinden kaydetme

            // E-posta Bildirimi (Proje Gereksinimi)
            bildirimService.sendLateReturnNotification(islem.getKullanici(), islem.getKitap(), beklenen, gecikmeGun);
        }
    }
}