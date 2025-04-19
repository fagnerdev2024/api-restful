package med.voll.api.entities;

import jakarta.persistence.*;
import lombok.*;
import med.voll.api.enums.Especialidade;
import med.voll.api.dtos.DadosAtualizacaoMedico;
import med.voll.api.dtos.DadosCadastroMedico;

import java.util.function.Consumer;


@Table(name = "medicos")
@Entity(name = "Medico")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(of = "id")
public class Medico {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String nome;
    private String email;
    private String telefone;
    private String crm;

    @Enumerated(EnumType.STRING)
    private Especialidade especialidade;

    @Embedded
    private Endereco endereco;

    private Boolean ativo;


    public Medico(DadosCadastroMedico dadosCadastroMedico) {
        this.nome = dadosCadastroMedico.nome();
        this.telefone = dadosCadastroMedico.telefone();
        this.email = dadosCadastroMedico.email();
        this.crm = dadosCadastroMedico.crm();
        this.especialidade = dadosCadastroMedico.especialidade();
        this.endereco = new Endereco(dadosCadastroMedico.endereco());
        this.ativo = true;
    }

    public void atualizarInformacoes(DadosAtualizacaoMedico dados) {
        atualizarCampo(dados.nome(), this::setNome);
        atualizarCampo(dados.telefone(), this::setTelefone);
        if(dados.endereco() != null){
            this.endereco.atualizarInformacoes(dados.endereco());
        }
    }

    private <T> void atualizarCampo(T valor, Consumer<T> setter){
        if(valor != null){
            setter.accept(valor);
        }
    }

    public void excluir() {
        this.ativo = false;
    }

    public boolean isAtivo() {
        return ativo;
    }
}
