package dev.clinplay.api.modules.security.config;

import dev.clinplay.api.modules.auth.oauth2.OAuth2SuccessHandler;
import dev.clinplay.api.modules.security.jwt.JwtAuthFilter;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.HttpStatusEntryPoint;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    /**
     * Origens aceitas em CORS. Precisa ser uma lista porque o mesmo app é
     * servido em mais de um endereço (domínio próprio, com e sem `www`, e a
     * URL da Vercel). Antes aqui só cabia o valor de `url.frontend`, então
     * qualquer acesso pelo domínio próprio tomava 403 em toda chamada.
     *
     * São valores explícitos de propósito. Curinga (`*`) ou reflexão do
     * header `Origin` junto com `allowCredentials(true)` permitiria que
     * qualquer site lesse respostas autenticadas destes usuários.
     */
    @Value("${url.origens}")
    private List<String> origensPermitidas;

    private static final String[] ENDPOINTS_PUBLICOS = {
        "/oauth2/**",
        "/auth/setup",
        "/auth/login",
        "/auth/fcm-token",
        "/auth/refresh",
        "/login/oauth2/**",
        "/swagger-ui/**",
        "/v3/api-docs/**",
        "/ws/**",
        // Ping de disponibilidade (ver HealthController): mantém a instância
        // do Render acordada sem depender do Swagger continuar exposto.
        "/health",
    };

    private final JwtAuthFilter jwtAuthFilter;
    private final OAuth2SuccessHandler oAuth2SuccessHandler;

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        return http
            .csrf(AbstractHttpConfigurer::disable)
            .sessionManagement(s ->
                s
                    .sessionCreationPolicy(SessionCreationPolicy.IF_REQUIRED)
                    .sessionFixation()
                    .none()
            )
            .cors(cors -> cors.configurationSource(corsConfigurationSource()))
            .authorizeHttpRequests(auth ->
                auth
                    .requestMatchers(ENDPOINTS_PUBLICOS)
                    .permitAll()
                    // Cadastro inicial: autenticado pelo cookie de setup, não por Bearer.
                    // Apenas o POST destes recursos é liberado; GET/PUT/DELETE seguem protegidos.
                    .requestMatchers(HttpMethod.POST, "/paciente", "/profissional")
                    .permitAll()
                    .anyRequest()
                    .authenticated()
            )
            // Para XHR sem autenticação, responde 401 em vez de redirecionar para o
            // fluxo OAuth do Google (o que causava o erro de CORS no accounts.google.com).
            .exceptionHandling(e ->
                e.authenticationEntryPoint(
                    new HttpStatusEntryPoint(HttpStatus.UNAUTHORIZED)
                )
            )
            .oauth2Login(oauth2 ->
                oauth2
                    .authorizationEndpoint(a -> a.baseUri("/auth/oauth2"))
                    .redirectionEndpoint(r -> r.baseUri("/login/oauth2/code/*"))
                    .successHandler(oAuth2SuccessHandler)
            )
            .logout(AbstractHttpConfigurer::disable)
            .addFilterBefore(
                jwtAuthFilter,
                UsernamePasswordAuthenticationFilter.class
            )
            .build();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration config = new CorsConfiguration();

        config.setAllowedOrigins(origensPermitidas);
        config.setAllowedMethods(
            List.of("GET", "POST", "PUT", "DELETE", "OPTIONS", "PATCH")
        );
        config.setAllowedHeaders(List.of("*"));
        config.setAllowCredentials(true);
        config.setMaxAge(3600L);

        UrlBasedCorsConfigurationSource source =
            new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", config);

        return source;
    }

    @Bean
    public AuthenticationManager authenticationManager(
        AuthenticationConfiguration config
    ) throws Exception {
        return config.getAuthenticationManager();
    }
}
