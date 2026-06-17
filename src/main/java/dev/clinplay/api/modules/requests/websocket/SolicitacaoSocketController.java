package dev.clinplay.api.modules.requests.websocket;

import java.security.Principal;
import java.util.UUID;

import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;

import lombok.RequiredArgsConstructor;

@Controller
@RequiredArgsConstructor
public class SolicitacaoSocketController {

    private final SolicitacaoSocketService service;

    @MessageMapping("/solicitacoes/{clinicaId}")
    public void handleSolicitacoes(@DestinationVariable UUID clinicaId, Principal principal) {
        UUID userId = (UUID) ((Authentication) principal).getPrincipal();
        service.enviarEstadoAtual(userId, clinicaId);
    }

}
