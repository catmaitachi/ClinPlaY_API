package dev.clinplay.api.modules.security.jwt;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import dev.clinplay.api.modules.accounts.models.enums.Perfil;
import dev.clinplay.api.modules.auth.services.SessaoService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class JwtAuthFilter extends OncePerRequestFilter {
    
    private final JwtService jwtService;
    private final SessaoService sessaoService;
    
    @Override
    protected void doFilterInternal (HttpServletRequest request, HttpServletResponse response, FilterChain chain) throws ServletException, IOException {

        // * 1º Verificar se o header Authorization está presente

        String authHeader = request.getHeader("Authorization");

        if (
            
            authHeader == null ||  !authHeader.startsWith("Bearer ") || 
            SecurityContextHolder.getContext().getAuthentication() != null
        
        ) { chain.doFilter(request, response); return; }

        // * 2º Verifica o tipo e a validade do token

        String token = authHeader.substring(7);
        boolean tokenValido = jwtService.ehValido(token);
        boolean ehAcessToken = jwtService.isAccessToken(token);

        if (!tokenValido || !ehAcessToken) { chain.doFilter(request, response); return; }

        // * 3º Verifica se o ID de sessão do token corresponde a uma sessão ativa

        boolean sessaoAtiva = sessaoService.validar(jwtService.extrairSessao(token));
            
        if (!sessaoAtiva) { chain.doFilter(request, response); return; } 

        // * 4º Autentica o usuário no contexto de segurança do Spring
        
        UUID usuario = jwtService.extrairSub(token);
        Perfil role = jwtService.extrairRole(token);
        boolean admin = jwtService.extrairAdmin(token);

        List<GrantedAuthority> authorities = new ArrayList<>();
        authorities.add(new SimpleGrantedAuthority("ROLE_" + role.name()));
        if (admin) authorities.add(new SimpleGrantedAuthority("ROLE_ADMIN"));
        UsernamePasswordAuthenticationToken auth = new UsernamePasswordAuthenticationToken(usuario, null, authorities);

        auth.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
        SecurityContextHolder.getContext().setAuthentication(auth);

        chain.doFilter(request, response);

    }

}