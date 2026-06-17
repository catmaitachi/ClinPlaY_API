package dev.clinplay.api.modules.clinics.dtos;

import java.util.List;
import java.util.UUID;

import com.fasterxml.jackson.annotation.JsonInclude;
import dev.clinplay.api.modules.clinics.models.ClinPaciente;
import dev.clinplay.api.modules.clinics.models.ClinProfissional;
import dev.clinplay.api.modules.clinics.models.embeddables.Permissoes;
import dev.clinplay.api.modules.subscriptions.models.Assinatura;
import dev.clinplay.api.modules.treatment.dtos.ObterTratamento;
import lombok.Data;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ObterClinicasUsuario {

    private UUID clinicaId;
    private String nome;
    private String cnpj;
    private String tag;
    private String especialidade;
    private String uf;
    private String cidade;
    private Permissoes permissoes;
    private List<ObterTratamento> tratamentos;
    private String planoNome;
    private String planoStatus;

    public ObterClinicasUsuario(ClinPaciente cp) {
        this.clinicaId = cp.getClinica().getId();
        this.nome = cp.getClinica().getNome();
        this.cnpj = cp.getClinica().getCnpj();
        this.tag = cp.getClinica().getTag();
        this.especialidade = cp.getClinica().getEspecialidade();
        this.uf = cp.getClinica().getUf();
        this.cidade = cp.getClinica().getCidade();
        this.tratamentos = cp.getTratamentos().stream().map(ObterTratamento::new).toList();
        Assinatura assinatura = cp.getClinica().getAssinatura();
        if (assinatura != null) {
            this.planoNome = assinatura.getPlano().getNome();
            this.planoStatus = assinatura.getStatus().name();
        }
    }

    public ObterClinicasUsuario(ClinProfissional cp) {
        this.clinicaId = cp.getClinica().getId();
        this.nome = cp.getClinica().getNome();
        this.cnpj = cp.getClinica().getCnpj();
        this.tag = cp.getClinica().getTag();
        this.especialidade = cp.getClinica().getEspecialidade();
        this.uf = cp.getClinica().getUf();
        this.cidade = cp.getClinica().getCidade();
        this.permissoes = cp.getPermissoes();
        this.tratamentos = cp.getTratamentos().stream().map(ObterTratamento::new).toList();
        Assinatura assinatura = cp.getClinica().getAssinatura();
        if (assinatura != null) {
            this.planoNome = assinatura.getPlano().getNome();
            this.planoStatus = assinatura.getStatus().name();
        }
    }

}
