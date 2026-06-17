package dev.clinplay.api.modules.requests.dtos;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

import dev.clinplay.api.modules.requests.models.SolicitacaoProfissional;
import dev.clinplay.api.modules.requests.models.enums.SituacaoSolicitacao;
import dev.clinplay.api.modules.requests.models.enums.TipoSolicitacao;
import lombok.Data;

@Data
public class ObterSolicitacaoProfissional {

    // Solicitação
    private UUID id;
    private TipoSolicitacao tipo;
    private SituacaoSolicitacao situacao;
    private LocalDateTime solicitadoEm;
    private String mensagem;
    private String resposta;
    private UUID clinicaId;
    private String clinicaNome;

    // Profissional
    private UUID profissionalId;
    private String profissionalNome;
    private String profissionalEmail;
    private String profissionalTelefone;
    private LocalDate profissionalNascimento;
    private String profissionalAvatar;
    private String profissionalCrefito;
    private String profissionalEspecialidade;
    private String conselhoNome;
    private String conselhoNumero;
    private String conselhoUf;

    public ObterSolicitacaoProfissional(SolicitacaoProfissional s) {

        this.id = s.getId();
        this.tipo = s.getTipo();
        this.situacao = s.getSituacao();
        this.solicitadoEm = s.getSolicitadoEm();
        this.mensagem = s.getMensagem();
        this.resposta = s.getResposta();
        this.clinicaId = s.getClinica().getId();
        this.clinicaNome = s.getClinica().getNome();

        var p = s.getProfissional();
        this.profissionalId = p.getId();
        this.profissionalNome = p.getNome();
        this.profissionalEmail = p.getEmail();
        this.profissionalTelefone = p.getTelefone();
        this.profissionalNascimento = p.getNascimento();
        this.profissionalAvatar = p.getAvatar();
        this.profissionalCrefito = p.getCrefito();
        this.profissionalEspecialidade = p.getEspecialidade();

        var c = p.getConselho();
        if (c != null) {
            this.conselhoNome = c.getNome();
            this.conselhoNumero = c.getNumero();
            this.conselhoUf = c.getUf();
        }

    }

}
