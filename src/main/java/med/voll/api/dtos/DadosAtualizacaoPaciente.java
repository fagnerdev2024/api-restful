package med.voll.api.dtos;

import jakarta.validation.Valid;

public record DadosAtualizacaoPaciente(

        Long id,
        String nome,
        String telefone,

        @Valid
        DadosEndereco endereco) {
}
