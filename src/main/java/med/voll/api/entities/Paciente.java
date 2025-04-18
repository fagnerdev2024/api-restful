package med.voll.api.entities;

import jakarta.persistence.*;
import lombok.*;
import med.voll.api.dtos.DadosAtualizacaoPaciente;
import med.voll.api.dtos.DadosCadastroPaciente;

import java.util.function.Consumer;

@Getter
@Setter
@EqualsAndHashCode(of = "id")
@NoArgsConstructor
@AllArgsConstructor
@Entity(name = "Paciente")
@Table(name = "pacientes")
public class Paciente {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String nome;
    @Getter
    private String email;
    private String cpf;
    private String telefone;

    @Embedded
    private Endereco endereco;

    private Boolean ativo;

    public Paciente(DadosCadastroPaciente dadosCadastroPaciente) {
        this.nome = dadosCadastroPaciente.nome();
        this.email = dadosCadastroPaciente.email();
        this.telefone = dadosCadastroPaciente.telefone();
        this.cpf = dadosCadastroPaciente.cpf();
        this.endereco = new Endereco(dadosCadastroPaciente.endereco());
        this.ativo = true;
    }

    public void atualizarInformacoes(DadosAtualizacaoPaciente dados) {
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
