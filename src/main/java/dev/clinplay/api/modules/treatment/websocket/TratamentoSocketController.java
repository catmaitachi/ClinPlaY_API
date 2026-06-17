package dev.clinplay.api.modules.treatment.websocket;

import java.security.Principal;
import java.util.UUID;

import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;

import dev.clinplay.api.modules.treatment.websocket.dto.MensagemEntrada;
import lombok.RequiredArgsConstructor;

@Controller
@RequiredArgsConstructor
public class TratamentoSocketController {

    private final TratamentoSocketService service;

    @MessageMapping("/tratamento/{tratamentoId}")
    public void handleMessage(
            @DestinationVariable UUID tratamentoId,
            @Payload MensagemEntrada mensagem,
            Principal principal) {

        Authentication auth = (Authentication) principal;
        UUID userId = (UUID) auth.getPrincipal();

        service.processar(userId, auth, tratamentoId, mensagem);
    }

}
