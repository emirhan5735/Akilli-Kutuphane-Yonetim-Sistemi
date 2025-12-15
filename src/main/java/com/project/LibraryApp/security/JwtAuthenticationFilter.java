package com.project.LibraryApp.security;

import com.project.LibraryApp.service.KullaniciService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import java.io.IOException;
import java.util.Collections;

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtTokenProvider tokenProvider;
    private final KullaniciService kullaniciService;

    public JwtAuthenticationFilter(JwtTokenProvider tokenProvider, KullaniciService kullaniciService) {
        this.tokenProvider = tokenProvider;
        this.kullaniciService = kullaniciService;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain)
            throws ServletException, IOException {

        String token = getJwtFromRequest(request);

        System.out.println("JWT FILTER ÇALIŞTI");
        System.out.println("TOKEN: " + token);

        try {
            if (token != null && tokenProvider.validateToken(token)) {
                System.out.println("TOKEN GEÇERLİ");

                String username = tokenProvider.getUsername(token);
                String role = tokenProvider.getRole(token);

                System.out.println("USERNAME: " + username);
                System.out.println("ROLE: " + role);

                GrantedAuthority authority =
                        new SimpleGrantedAuthority("ROLE_" + role);

                UsernamePasswordAuthenticationToken authentication =
                        new UsernamePasswordAuthenticationToken(
                                username,
                                null,
                                Collections.singletonList(authority)
                        );

                SecurityContextHolder.getContext().setAuthentication(authentication);
            } else {
                System.out.println("TOKEN GEÇERSİZ");
            }
        } catch (Exception e) {
            System.out.println("JWT FILTER HATA: " + e.getClass().getName());
            System.out.println("MESAJ: " + e.getMessage());
        }

        filterChain.doFilter(request, response);
    }


    private String getJwtFromRequest(HttpServletRequest request) {
        String bearerToken = request.getHeader("Authorization");

        if (bearerToken != null && bearerToken.startsWith("Bearer ")) {
            return bearerToken.substring(7);
        }
        return null;
    }
}