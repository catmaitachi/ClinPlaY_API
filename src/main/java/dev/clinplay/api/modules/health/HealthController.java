package dev.clinplay.api.modules.health;

import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Verificação de disponibilidade da API.
 *
 * Existe para monitoramento externo (UptimeRobot e afins) manter a instância
 * do Render acordada — o plano gratuito derruba o serviço após 15 minutos
 * sem tráfego, e o cold start leva perto de três minutos.
 *
 * Responde também a HEAD: o Spring MVC atende HEAD em handlers mapeados com
 * GET automaticamente, executando o método e descartando o corpo. Isso
 * importa porque o plano gratuito do UptimeRobot não deixa escolher o verbo
 * e sempre envia HEAD. Antes desta rota, o único endpoint público que
 * aceitava HEAD era o `/swagger-ui/index.html`, o que amarrava o
 * monitoramento à documentação continuar exposta em produção.
 *
 * Deliberadamente não toca no banco: um ping a cada cinco minutos, para
 * sempre, não deve acordar o Postgres nem custar conexão do pool. Quem
 * precisar de verificação de dependências que crie uma rota própria.
 */
@RestController
public class HealthController {

    @GetMapping(value = "/health", produces = MediaType.TEXT_PLAIN_VALUE)
    public ResponseEntity<String> health() {
        return ResponseEntity.ok("OK");
    }

}
