package dev.clinplay.api.modules.websocket;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.MessageDeliveryException;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.messaging.support.MessageHeaderAccessor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Component;

import dev.clinplay.api.modules.accounts.models.enums.Perfil;
import dev.clinplay.api.modules.auth.services.SessaoService;
import dev.clinplay.api.modules.security.jwt.JwtService;
import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class JwtChannelInterceptor implements ChannelInterceptor {

    private final JwtService jwtService;
    private final SessaoService sessaoService;

    @Override
    public Message<?> preSend(Message<?> message, MessageChannel channel) {
        StompHeaderAccessor accessor = MessageHeaderAccessor.getAccessor(message, StompHeaderAccessor.class);

        if (accessor == null || !StompCommand.CONNECT.equals(accessor.getCommand())) return message;

        String token = extrairToken(accessor);

        if (token == null || !jwtService.ehValido(token) || !jwtService.isAccessToken(token))
            throw new MessageDeliveryException("Token inválido ou ausente");

        if (!sessaoService.validar(jwtService.extrairSessao(token)))
            throw new MessageDeliveryException("Sessão inativa");

        UUID userId = jwtService.extrairSub(token);
        Perfil role = jwtService.extrairRole(token);
        boolean admin = jwtService.extrairAdmin(token);

        List<GrantedAuthority> authorities = new ArrayList<>();
        authorities.add(new SimpleGrantedAuthority("ROLE_" + role.name()));
        if (admin) authorities.add(new SimpleGrantedAuthority("ROLE_ADMIN"));

        accessor.setUser(new UsernamePasswordAuthenticationToken(userId, null, authorities));

        return message;
    }

    private String extrairToken(StompHeaderAccessor accessor) {
        Map<String, Object> attrs = accessor.getSessionAttributes();
        if (attrs != null) {
            String token = (String) attrs.get("token");
            if (token != null) return token;
        }
        String authHeader = accessor.getFirstNativeHeader("Authorization");
        if (authHeader != null && authHeader.startsWith("Bearer ")) return authHeader.substring(7);
        return null;
    }

}
