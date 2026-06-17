package dev.clinplay.api.modules.requests.dtos;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

import dev.clinplay.api.modules.requests.models.SolicitacaoPaciente;
import dev.clinplay.api.modules.requests.models.enums.SituacaoSolicitacao;
import dev.clinplay.api.modules.requests.models.enums.TipoSolicitacao;
import lombok.Data;

@Data
public class ObterSolicitacaoPaciente {

    // Solicitação
    private UUID id;
    private TipoSolicitacao tipo;
    private SituacaoSolicitacao situacao;
    private LocalDateTime solicitadoEm;
    private String mensagem;
    private String resposta;
    private UUID clinicaId;
    private String clinicaNome;

    // Paciente
    private UUID pacienteId;
    private String pacienteNome;
    private String pacienteEmail;
    private String pacienteTelefone;
    private LocalDate pacienteNascimento;
    private String pacienteAvatar;
    private String pacienteCpf;

    public ObterSolicitacaoPaciente(SolicitacaoPaciente s) {

        this.id = s.getId();
        this.tipo = s.getTipo();
        this.situacao = s.getSituacao();
        this.solicitadoEm = s.getSolicitadoEm();
        this.mensagem = s.getMensagem();
        this.resposta = s.getResposta();
        this.clinicaId = s.getClinica().getId();
        this.clinicaNome = s.getClinica().getNome();

        var p = s.getPaciente();
        this.pacienteId = p.getId();
        this.pacienteNome = p.getNome();
        this.pacienteEmail = p.getEmail();
        this.pacienteTelefone = p.getTelefone();
        this.pacienteNascimento = p.getNascimento();
        this.pacienteAvatar = p.getAvatar();
        this.pacienteCpf = p.getCpf();

    }

}
