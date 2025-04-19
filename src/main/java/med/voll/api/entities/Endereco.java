package med.voll.api.entities;

import jakarta.persistence.Embeddable;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import med.voll.api.dtos.DadosEndereco;
import org.springframework.web.bind.annotation.RestController;

import java.util.function.Consumer;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Embeddable
public class Endereco {

    private String logradouro;
    private String bairro;
    private String cep;
    private String numero;
    private String complemento;
    private String cidade;
    private String uf;


    public Endereco(DadosEndereco dadosEndereco) {
        this.logradouro = dadosEndereco.logradouro();
        this.bairro = dadosEndereco.bairro();
        this.cep = dadosEndereco.cep();
        this.uf = dadosEndereco.uf();
        this.cidade = dadosEndereco.cidade();
        this.numero = dadosEndereco.numero();
        this.complemento = dadosEndereco.complemento();
    }

    public void atualizarInformacoes(DadosEndereco dados) {
        atualizarCampo(dados.logradouro(), this::setLogradouro);
        atualizarCampo(dados.bairro(), this::setBairro);
        atualizarCampo(dados.cep(), this::setCep);
        atualizarCampo(dados.uf(), this::setUf);
        atualizarCampo(dados.cidade(), this::setCidade);
        atualizarCampo(dados.numero(), this::setNumero);
        atualizarCampo(dados.complemento(), this::setComplemento);

    }

    private <T> void atualizarCampo(T valor, Consumer<T> setter){
        if(valor != null){
            setter.accept(valor);
        }
    }
}
