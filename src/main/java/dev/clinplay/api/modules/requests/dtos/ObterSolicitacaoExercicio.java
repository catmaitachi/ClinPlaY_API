package dev.clinplay.api.modules.requests.dtos;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

import dev.clinplay.api.modules.accounts.models.Profissional;
import dev.clinplay.api.modules.accounts.models.embeddables.Conselho;
import dev.clinplay.api.modules.requests.models.SolicitacaoExercicio;
import dev.clinplay.api.modules.requests.models.enums.SituacaoSolicitacao;
import dev.clinplay.api.modules.requests.models.enums.TipoSolicitacao;
import dev.clinplay.api.modules.treatment.models.embeddables.ExercicioConfig;
import dev.clinplay.api.modules.treatment.models.enums.Jogo;
import lombok.Data;

@Data
public class ObterSolicitacaoExercicio {

    private UUID id;
    private TipoSolicitacao tipo;
    private SituacaoSolicitacao situacao;
    private LocalDateTime solicitadoEm;
    private String mensagem;
    private String resposta;

    private UUID clinicaId;
    private String clinicaNome;

    private UUID solicitanteId;
    private String solicitanteNome;
    private String solicitanteEmail;
    private String solicitanteTelefone;
    private LocalDate solicitanteNascimento;
    private String solicitanteAvatar;
    private String solicitanteCrefito;
    private String solicitanteEspecialidade;
    private String conselhoNome;
    private String conselhoNumero;
    private String conselhoUf;

    private UUID exercicioId;
    private String nome;
    private String descricao;
    private Jogo jogo;
    private String videoUrl;
    private ExercicioConfig configPadrao;

    public ObterSolicitacaoExercicio(SolicitacaoExercicio s) {
        this.id = s.getId();
        this.tipo = s.getTipo();
        this.situacao = s.getSituacao();
        this.solicitadoEm = s.getSolicitadoEm();
        this.mensagem = s.getMensagem();
        this.resposta = s.getResposta();
        this.clinicaId = s.getClinica().getId();
        this.clinicaNome = s.getClinica().getNome();

        Profissional p = s.getSolicitante();
        this.solicitanteId = p.getId();
        this.solicitanteNome = p.getNome();
        this.solicitanteEmail = p.getEmail();
        this.solicitanteTelefone = p.getTelefone();
        this.solicitanteNascimento = p.getNascimento();
        this.solicitanteAvatar = p.getAvatar();
        this.solicitanteCrefito = p.getCrefito();
        this.solicitanteEspecialidade = p.getEspecialidade();

        Conselho c = p.getConselho();
        if (c != null) {
            this.conselhoNome = c.getNome();
            this.conselhoNumero = c.getNumero();
            this.conselhoUf = c.getUf();
        }

        this.exercicioId = s.getExercicio() != null ? s.getExercicio().getId() : null;
        this.nome = s.getNome();
        this.descricao = s.getDescricao();
        this.jogo = s.getJogo();
        this.videoUrl = s.getVideoUrl();
        this.configPadrao = s.getConfigPadrao();
    }

}
