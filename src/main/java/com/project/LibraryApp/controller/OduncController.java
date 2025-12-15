package com.project.LibraryApp.controller;

import com.project.LibraryApp.model.OduncIslemi;
import com.project.LibraryApp.model.Ceza;
import com.project.LibraryApp.service.OduncService;
import com.project.LibraryApp.service.CezaService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/odunc")
public class OduncController {

    private final OduncService oduncService;
    private final CezaService cezaService;

    public OduncController(OduncService oduncService, CezaService cezaService) {
        this.oduncService = oduncService;
        this.cezaService = cezaService;
    }


    @PostMapping("/al")
    public ResponseEntity<?> oduncAl(@RequestBody Map<String, Object> request) {
        try {
            Object kIdObj = request.get("kullaniciId");
            Object kitIdObj = request.get("kitapId");
            String iadeTarihiStr = (String) request.get("iadeTarihi");

            if (kIdObj == null || kitIdObj == null || iadeTarihiStr == null) {
                return new ResponseEntity<>(Map.of("hata", "Eksik bilgi."), HttpStatus.BAD_REQUEST);
            }

            Long kullaniciId = ((Number) kIdObj).longValue();
            Long kitapId = ((Number) kitIdObj).longValue();
            LocalDate beklenenIadeTarihi = LocalDate.parse(iadeTarihiStr);

            oduncService.kitapOduncAl(kullaniciId, kitapId, beklenenIadeTarihi);


            return new ResponseEntity<>(Map.of("mesaj", "İşlem Başarılı"), HttpStatus.CREATED);

        } catch (Exception e) {
            return new ResponseEntity<>(Map.of("hata", e.getMessage()), HttpStatus.BAD_REQUEST);
        }
    }


    @PutMapping("/iade/{islemId}")
    public ResponseEntity<?> iadeEt(@PathVariable Long islemId) {
        try {
            oduncService.kitapIadeEt(islemId);
            return new ResponseEntity<>(Map.of("mesaj", "İade Alındı"), HttpStatus.OK);
        } catch (RuntimeException e) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }


    @GetMapping("/user/{userId}")
    public ResponseEntity<List<Map<String, Object>>> getKullaniciIslemleri(@PathVariable Long userId) {

        List<OduncIslemi> islemler = oduncService.findIslemlerByKullanici(userId);
        List<Map<String, Object>> responseList = new ArrayList<>();

        for (OduncIslemi islem : islemler) {
            Map<String, Object> map = new HashMap<>();
            map.put("id", islem.getId());
            map.put("oduncTarihi", islem.getOduncTarihi().toString());
            map.put("beklenenIadeTarihi", islem.getBeklenenIadeTarihi().toString());

            if (islem.getGercekIadeTarihi() != null) {
                map.put("gercekIadeTarihi", islem.getGercekIadeTarihi().toString());
            } else {
                map.put("gercekIadeTarihi", null);
            }


            Map<String, Object> kitapMap = new HashMap<>();
            kitapMap.put("id", islem.getKitap().getId());
            kitapMap.put("ad", islem.getKitap().getAd());
            if (islem.getKitap().getYazar() != null) {
                Map<String, Object> yazarMap = new HashMap<>();
                yazarMap.put("ad", islem.getKitap().getYazar().getAd());
                yazarMap.put("soyad", islem.getKitap().getYazar().getSoyad());
                kitapMap.put("yazar", yazarMap);
            }

            map.put("kitap", kitapMap);
            responseList.add(map);
        }

        return new ResponseEntity<>(responseList, HttpStatus.OK);
    }


    @GetMapping("/cezalar")
    public List<Ceza> getAllCezalar() {
        return cezaService.findAllCezalar();
    }

    @PutMapping("/ceza/odeme/{cezaId}")
    public ResponseEntity<Void> odemeYap(@PathVariable Long cezaId) {
        cezaService.odemeYap(cezaId);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @GetMapping("/cezalar/kullanici/{userId}")
    public List<Ceza> getKullaniciCezalari(@PathVariable Long userId) {
        return cezaService.findCezalarByKullanici(userId);
    }
}