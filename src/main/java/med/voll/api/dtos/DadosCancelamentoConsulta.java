package med.voll.api.dtos;

import jakarta.validation.constraints.NotNull;
import med.voll.api.consulta.validacoes.MotivoCancelamento;

public record DadosCancelamentoConsulta(
        @NotNull
        Long idConsulta,

        @NotNull
        MotivoCancelamento motivo) {
}
