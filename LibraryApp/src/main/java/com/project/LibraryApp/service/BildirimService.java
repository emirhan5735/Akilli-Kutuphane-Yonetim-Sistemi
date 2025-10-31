package com.project.LibraryApp.service;

import com.project.LibraryApp.model.Kullanici;
import com.project.LibraryApp.model.Kitap;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import java.time.LocalDate;

@Service
public class BildirimService {

    private final JavaMailSender mailSender;

    public BildirimService(JavaMailSender mailSender) {
        this.mailSender = mailSender;
    }

    public void sendLateReturnNotification(Kullanici kullanici, Kitap kitap, LocalDate beklenenIadeTarihi, long gecikmeGun) {
        SimpleMailMessage message = new SimpleMailMessage();

        message.setFrom("kutuphane@sizinprojeniz.com");
        message.setTo(kullanici.getMail());
        message.setSubject("ACİL: Kitap İade Süreniz Geçmiştir!");

        String emailBody = String.format(
                "Sayın %s %s,\n\n" +
                        "Kütüphanemizden ödünç aldığınız '%s' adlı kitabın iade tarihi %s gün önce, %s tarihinde geçmiştir.\n" +
                        "Gecikme nedeniyle sistem otomatik olarak ceza hesaplamıştır. Lütfen kitabı en kısa sürede iade ediniz.\n\n" +
                        "Teşekkürler,\n" +
                        "Akıllı Kütüphane Yönetim Sistemi",
                kullanici.getAd(),
                kullanici.getSoyad(),
                kitap.getAd(),
                gecikmeGun,
                beklenenIadeTarihi
        );

        message.setText(emailBody);
        mailSender.send(message);
    }
}