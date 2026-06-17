package dev.clinplay.api.modules.auth.oauth2;

import java.io.IOException;
import java.util.Map;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;
import org.springframework.web.util.UriComponentsBuilder;

import dev.clinplay.api.modules.accounts.models.embeddables.Origem;
import dev.clinplay.api.modules.auth.models.Login;
import dev.clinplay.api.modules.auth.models.Sessao;
import dev.clinplay.api.modules.auth.services.LoginService;
import dev.clinplay.api.modules.auth.services.SessaoService;
import dev.clinplay.api.modules.security.jwt.JwtService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class OAuth2SuccessHandler implements AuthenticationSuccessHandler {
    
    @Value("${url.frontend}") private String frontUrl;

    private final SessaoService sessaoService;
    private final LoginService loginService;
    private final JwtService jwtService;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException {

        String registrationId = ((OAuth2AuthenticationToken) authentication).getAuthorizedClientRegistrationId();
        OAuth2User oAuth2User = (OAuth2User) authentication.getPrincipal();

        String provedor       = registrationId.toUpperCase();
        String sub            = oAuth2User.getAttribute("sub");
        String nome           = oAuth2User.getAttribute("name");
        String email          = oAuth2User.getAttribute("email");
        String avatar         = oAuth2User.getAttribute("picture");

        Login l = loginService.oAuth(sub, provedor);

        String url;

        if (l.getUsuario() == null) {

            Map<String, Object> claims = Map.of( "name", nome, "email", email, "picture", avatar );

            String setupToken = jwtService.gerarSetupToken(l, claims);
            ResponseCookie cookie = jwtService.criarCookie(setupToken);

            response.addHeader(HttpHeaders.SET_COOKIE, cookie.toString());
            url = UriComponentsBuilder.fromUriString(frontUrl + "/oauth/setup").toUriString();

        } else {

            String refreshToken = jwtService.gerarRefreshToken(l.getUsuario());
            Sessao s = sessaoService.iniciar(l.getUsuario(), refreshToken, new Origem(request));
            String accessToken = jwtService.gerarAcessToken(s);
            ResponseCookie cookie = jwtService.criarCookie(refreshToken);

            response.addHeader(HttpHeaders.SET_COOKIE, cookie.toString());
            url = UriComponentsBuilder.fromUriString(frontUrl + "/oauth/callback").queryParam("access_token", accessToken).toUriString();

        }

        response.sendRedirect(url);

    }

}
