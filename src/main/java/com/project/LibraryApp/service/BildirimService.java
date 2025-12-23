package com.project.LibraryApp.service;

import com.project.LibraryApp.model.Kitap;
import com.project.LibraryApp.model.Kullanici;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.function.ServerResponse;
import org.springframework.beans.factory.annotation.Autowired;




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


    public void sendHosgeldinMesaji(String kime, String kullaniciAdi) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom("kutuphanebilgilendirme@gmail.com");
        message.setTo(kime);
        message.setSubject("Kütüphane Sistemine Hoş Geldiniz!");
        message.setText("Merhaba " + kullaniciAdi + ",\n\n" +
                "Akıllı Kütüphane Yönetim Sistemi'ne kaydınız başarıyla oluşturuldu.\n" +
                "Giriş yaparak kütüphanemizdeki eşsiz kitapları keşfetmeye başlayabilirsiniz.\n\n" +
                "İyi Okumalar dileriz.");

        mailSender.send(message);
    }


    public void sendPasswordResetCode(String toEmail, String code) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom("seninmailin@gmail.com");
        message.setTo(toEmail);
        message.setSubject("Şifre Yenileme Kodu");
        message.setText("Merhaba,\n\nŞifrenizi yenilemek için kullanmanız gereken doğrulama kodu: " + code + "\n\nBu kod 15 dakika geçerlidir.");

        mailSender.send(message);
    }
}