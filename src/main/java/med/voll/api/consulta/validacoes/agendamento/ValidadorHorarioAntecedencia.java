package med.voll.api.consulta.validacoes.agendamento;

import med.voll.api.infra.exceptions.ValidacaoException;
import med.voll.api.dtos.DadosAgendamentoConsulta;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.time.LocalDateTime;

@Component("ValidadorHorarioAntecedenciaAgendamento")
public class ValidadorHorarioAntecedencia implements ValidadorAgendamentoDeConsulta {

    private static final int MIN_ANTECEDENCIA_MINUTOS = 30;

    public void validar(DadosAgendamentoConsulta dados) {
        var dataConsulta = dados.data();
        var agora = LocalDateTime.now();
        var diferencaEmMinutos = Duration.between(agora, dataConsulta).toMinutes();

        if (diferencaEmMinutos < MIN_ANTECEDENCIA_MINUTOS) {
            throw new ValidacaoException("Consulta para " + dataConsulta + "deve ser agendada com antecedencia minima de " + MIN_ANTECEDENCIA_MINUTOS + "minutos");
        }

    }
}
