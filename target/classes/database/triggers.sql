-- 1. HIZLI CEZA TRIGGER (İade tarihini 1 dakika sonraya çeker - Test Amaçlı)
CREATE OR REPLACE FUNCTION set_hizli_ceza_tarihi()
RETURNS TRIGGER AS $$
BEGIN
    NEW.beklenen_iade_tarihi := CURRENT_TIMESTAMP + INTERVAL '1 minute';
RETURN NEW;
END;
$$ LANGUAGE plpgsql;;

-- DÜZELTME: Tablo adı 'odunc_islemi' yerine 'odunc_islemleri' yapıldı
DROP TRIGGER IF EXISTS trg_hizli_ceza ON odunc_islemleri;;

CREATE TRIGGER trg_hizli_ceza
    BEFORE INSERT ON odunc_islemleri
    FOR EACH ROW
    EXECUTE FUNCTION set_hizli_ceza_tarihi();;

-- 2. STOK LOG TABLOSU VE TRIGGER
CREATE TABLE IF NOT EXISTS kitap_stok_log (
                                              id BIGSERIAL PRIMARY KEY,
                                              kitap_id BIGINT,
                                              kitap_adi VARCHAR(255),
    eski_stok INT,
    yeni_stok INT,
    islem_tarihi TIMESTAMP DEFAULT CURRENT_TIMESTAMP
    );;

CREATE OR REPLACE FUNCTION stok_takip_func()
RETURNS TRIGGER AS $$
BEGIN
    -- Sadece stok sayısı değiştiyse log at
    IF OLD.kopya_sayisi <> NEW.kopya_sayisi THEN
        INSERT INTO kitap_stok_log (kitap_id, kitap_adi, eski_stok, yeni_stok)
        VALUES (OLD.id, OLD.ad, OLD.kopya_sayisi, NEW.kopya_sayisi);
END IF;
RETURN NEW;
END;
$$ LANGUAGE plpgsql;;

DROP TRIGGER IF EXISTS trg_stok_degisim ON kitap;;

CREATE TRIGGER trg_stok_degisim
    AFTER UPDATE ON kitap
    FOR EACH ROW
    EXECUTE FUNCTION stok_takip_func();;

-- 3. GECİKME KONTROL RAPORU PROSEDÜRÜ
CREATE OR REPLACE PROCEDURE gecikme_kontrol_raporu()
LANGUAGE plpgsql
AS $$
DECLARE
kayit RECORD;
    gecikme INT;
    gunluk_ceza DECIMAL := 0.50;
    toplam_borc DECIMAL;
BEGIN
    RAISE NOTICE '--- GECİKMİŞ KİTAP RAPORU ---';

    -- DÜZELTME: Burada da tablo adı 'odunc_islemleri' olarak güncellendi
FOR kayit IN
SELECT k.ad, o.beklenen_iade_tarihi
FROM odunc_islemleri o
         JOIN kitap k ON o.kitap_id = k.id
WHERE o.beklenen_iade_tarihi < CURRENT_TIMESTAMP
  AND o.gercek_iade_tarihi IS NULL
    LOOP
        gecikme := EXTRACT(DAY FROM (CURRENT_TIMESTAMP - o.beklenen_iade_tarihi));
toplam_borc := gecikme * gunluk_ceza;
        RAISE NOTICE 'Kitap: %, Gecikme: % Gün, Tahmini Ceza: % TL', kayit.ad, gecikme, toplam_borc;
END LOOP;

    RAISE NOTICE '--- RAPOR BİTTİ ---';
END;
$$;;