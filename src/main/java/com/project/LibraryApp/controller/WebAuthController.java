package com.project.LibraryApp.controller;

import com.project.LibraryApp.service.AuthService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
public class WebAuthController {

    private final AuthService authService;

    public WebAuthController(AuthService authService) {
        this.authService = authService;
    }

    @GetMapping("/forgot-password")
    public String showForgotPasswordForm() {
        return "forgot-password";
    }

    @PostMapping("/forgot-password")
    public String processForgotPassword(@RequestParam String email, RedirectAttributes attributes) {
        String result = authService.initiatePasswordReset(email);

        if ("Kod gönderildi.".equals(result)) {
            attributes.addFlashAttribute("message", "Doğrulama kodu e-postanıza gönderildi.");
            attributes.addFlashAttribute("email", email);
            return "redirect:/reset-password";
        }
        else {
            attributes.addFlashAttribute("error", "Bu e-posta ile kayıtlı kullanıcı bulunamadı.");
            return "redirect:/forgot-password";
        }
    }

    @GetMapping("/reset-password")
    public String showResetPasswordForm(@ModelAttribute("email") String email, Model model) {
        if (email == null || email.isEmpty()) {
            return "redirect:/forgot-password";
        }
        model.addAttribute("email", email);
        return "reset-password";
    }

    @PostMapping("/reset-password")
    public String processResetPassword(@RequestParam String email,
                                       @RequestParam String code,
                                       @RequestParam String newPassword,
                                       @RequestParam String confirmPassword,
                                       RedirectAttributes attributes) {

        String result = authService.resetPassword(email, code, newPassword, confirmPassword);

        if ("Başarılı".equals(result)) {
            attributes.addFlashAttribute("message", "Şifreniz başarıyla güncellendi. Giriş yapabilirsiniz.");
            return "redirect:/";
        } else {
            attributes.addFlashAttribute("error", result);
            attributes.addFlashAttribute("email", email);
            return "redirect:/reset-password";
        }
    }
}