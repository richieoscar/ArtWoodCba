package com.richieoscar.artwoodcba.config;

import com.richieoscar.artwoodcba.service.CustomerDetailsService;
import com.richieoscar.artwoodcba.service.JwtService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
@RequiredArgsConstructor
public class JwtSecurityFilter extends OncePerRequestFilter {

   private final JwtService jwtService;
   private final CustomerDetailsService customerDetailsService;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        String bearerAuth = request.getHeader(HttpHeaders.AUTHORIZATION);
        String username = null;
        String token = null;
        if (bearerAuth != null && bearerAuth.startsWith("Bearer")) {
            token = bearerAuth.substring(7);
            username = jwtService.getUsernameFromToken(token);
        }
        if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
            UserDetails userDetails = customerDetailsService.loadUserByUsername(username);
            Boolean isTokenValid = jwtService.validateToken(token, userDetails);
            if (Boolean.TRUE.equals(isTokenValid)) {
                UsernamePasswordAuthenticationToken userToken =
                        new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
                userToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                SecurityContextHolder.getContext().setAuthentication(userToken);
            }
        }
        filterChain.doFilter(request, response);

    }
}
