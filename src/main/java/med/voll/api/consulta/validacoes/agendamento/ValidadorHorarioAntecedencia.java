package med.voll.api.consulta.validacoes.agendamento;

import med.voll.api.infra.exceptions.ValidacaoException;
import med.voll.api.dtos.DadosAgendamentoConsulta;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.time.LocalDateTime;

@Component("ValidadorHorarioAntecedenciaAgendamento")
public class ValidadorHorarioAntecedencia implements ValidadorAgendamentoDeConsulta {

    private static final Logger log = LoggerFactory.getLogger(ValidadorHorarioAntecedencia.class);

    private static final int MIN_ANTECEDENCIA_MINUTOS = 30;

    @Override
    public void validar(DadosAgendamentoConsulta dadosAgendamentoConsulta) {
        var dataConsulta = dadosAgendamentoConsulta.data();
        var agora = LocalDateTime.now();

        log.info("Validando antecedência para a consulta agendada em: {}", dataConsulta);

        var diferencaEmMinutos = Duration.between(agora, dataConsulta).toMinutes();

        if (diferencaEmMinutos < MIN_ANTECEDENCIA_MINUTOS) {
            log.info("Antecedência insuficiente: {} minutos. Minimo requerido: {} minutos.",
                    diferencaEmMinutos, MIN_ANTECEDENCIA_MINUTOS);

            throw new ValidacaoException(String.format(
                    "Consulta agendada para %s não cumpre a antecedência mínima de %d minutos. " +
                            "Por favor, agende com mais antecedência.", dataConsulta, MIN_ANTECEDENCIA_MINUTOS));
        }

        log.info("Antecedência validada com sucesso para a consulta.");
    }
}