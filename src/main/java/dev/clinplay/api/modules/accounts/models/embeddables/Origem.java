package dev.clinplay.api.modules.accounts.models.embeddables;

import jakarta.persistence.Embeddable;
import jakarta.servlet.http.HttpServletRequest;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
@Embeddable
@Builder
@Getter
@Setter
public class Origem {
    
    private String ip;
    private String agente;

    public Origem ( HttpServletRequest request ) {

        // * Extrair IP

        String ip = request.getHeader("X-FORWARDED-FOR");

        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) ip = request.getRemoteAddr();

        ip = ip.split(",")[0].trim();

        // * Construir e retornar a origem

        this.ip = ip;
        this.agente = parseAgente(request.getHeader("User-Agent"));

    }

    private String parseAgente( String ua ) {

        if (ua == null || ua.isBlank()) return "Desconhecido";

        return detectarSO(ua) + " - " + detectarNavegador(ua);

    }

    private String detectarSO(String ua) {

        if (ua.contains("Android")) return "Android";
        if (ua.contains("iPhone") || ua.contains("iPad")) return "iOS";
        if (ua.contains("Windows")) return "Windows";
        if (ua.contains("Mac OS X")) return "macOS";
        if (ua.contains("Linux")) return "Linux";
        return "Desconhecido";

    }

    private String detectarNavegador(String ua) {

        if (ua.contains("PostmanRuntime")) return "Postman";
        if (ua.contains("Edg/") || ua.contains("Edge/")) return "Edge";
        if (ua.contains("OPR/") || ua.contains("Opera")) return "Opera";
        if (ua.contains("Chrome/")) return "Chrome";
        if (ua.contains("Firefox/")) return "Firefox";
        if (ua.contains("Safari/")) return "Safari";
        return "Desconhecido";
        
    }

}
