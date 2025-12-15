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
import java.util.List;


@Service
public class OduncService {

    private final OduncIslemiRepository oduncIslemiRepository;
    private final KitapRepository kitapRepository;
    private final KullaniciRepository kullaniciRepository;
    private final CezaService cezaService;
    private final BildirimService bildirimService;

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

        if (beklenenIadeTarihi.isBefore(LocalDate.now())) {
            throw new RuntimeException("İade tarihi bugünden önce olamaz.");
        }

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

        OduncIslemi saved = oduncIslemiRepository.save(yeniIslem);
        bildirimService.sendLoanMail(kullanici, kitap, beklenenIadeTarihi);
        return saved;

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

    public List<OduncIslemi> findIslemlerByKullanici(Long kullaniciId) {
        return oduncIslemiRepository.findAllByKullaniciId(kullaniciId);
    }



    private void hesaplaVeOlusturCeza(OduncIslemi islem) {

        LocalDate beklenen = islem.getBeklenenIadeTarihi();
        LocalDate gercek = islem.getGercekIadeTarihi();

        if (gercek.isAfter(beklenen)) {

            long gecikmeGun = ChronoUnit.DAYS.between(beklenen, gercek);
            BigDecimal gunlukCeza =
                    (gecikmeGun <= 3) ? new BigDecimal("5.00")
                            : (gecikmeGun <= 7) ? new BigDecimal("10.00")
                            : new BigDecimal("20.00");
            BigDecimal toplamCezaMiktari = gunlukCeza.multiply(new BigDecimal(gecikmeGun));

            Ceza ceza = new Ceza();
            ceza.setOduncIslemi(islem);
            ceza.setCezaMiktari(toplamCezaMiktari);
            ceza.setCezaTarihi(LocalDate.now());
            ceza.setOdendiMi(false);

            cezaService.saveCeza(ceza);

            bildirimService.sendLateReturnMail(islem.getKullanici(), islem.getKitap(), beklenen, gecikmeGun);
        }
    }
}