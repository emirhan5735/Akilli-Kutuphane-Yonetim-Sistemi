package com.project.LibraryApp.service;

import com.project.LibraryApp.model.Kitap;
import com.project.LibraryApp.model.Kullanici;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import java.time.LocalDate;

@Service
public class BildirimService {

    private final JavaMailSender mailSender;

    @Value("${app.mail.from}")
    private String from;

    public BildirimService(JavaMailSender mailSender) {
        this.mailSender = mailSender;
    }


    public void sendLoanMail(Kullanici kullanici, Kitap kitap, LocalDate beklenenIadeTarihi) {
        SimpleMailMessage mail = new SimpleMailMessage();
        mail.setFrom(from);
        mail.setTo(kullanici.getMail());
        mail.setSubject("Kitap Ödünç Aldınız");
        mail.setText(
                "Merhaba " + kullanici.getAd() + " " + kullanici.getSoyad() + ",\n\n" +
                        "Ödünç aldığınız kitap: " + kitap.getAd() + "\n" +
                        "Son iade tarihi: " + beklenenIadeTarihi + "\n\n" +
                        "LibraryApp"
        );
        mailSender.send(mail);
    }


    public void sendLateReturnMail(Kullanici kullanici, Kitap kitap, LocalDate beklenenIadeTarihi, long gecikmeGun) {
        SimpleMailMessage mail = new SimpleMailMessage();
        mail.setFrom(from);
        mail.setTo(kullanici.getMail());
        mail.setSubject("ACİL: Kitap İade Süreniz Geçmiştir!");
        mail.setText(
                "Sayın " + kullanici.getAd() + " " + kullanici.getSoyad() + ",\n\n" +
                        "Ödünç aldığınız '" + kitap.getAd() + "' adlı kitabın iade tarihi " +
                        gecikmeGun + " gün önce (" + beklenenIadeTarihi + ") geçmiştir.\n" +
                        "Lütfen kitabı en kısa sürede iade ediniz.\n\n" +
                        "LibraryApp"
        );
        mailSender.send(mail);
    }


    public void sendResetMail(String mailAdres, String yeniSifre) {
        SimpleMailMessage mail = new SimpleMailMessage();
        mail.setFrom(from);
        mail.setTo(mailAdres);
        mail.setSubject("Şifre Yenileme");
        mail.setText(
                "Yeni şifreniz: " + yeniSifre + "\n" +
                        "Giriş yaptıktan sonra değiştirmenizi öneririz.\n\n" +
                        "LibraryApp"
        );
        mailSender.send(mail);
    }
}