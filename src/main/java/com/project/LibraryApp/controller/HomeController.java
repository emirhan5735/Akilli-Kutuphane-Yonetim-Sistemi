package com.project.LibraryApp.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class HomeController {

    @GetMapping("/")
    public String home() {
        return "index";
    }

    @GetMapping("/panel")
    public String showPanel() {
        return "panel";
    }

    @GetMapping("/register")
    public String showRegister() {
        return "register";
    }
}