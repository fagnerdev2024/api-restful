package med.voll.api.consulta.validacoes.agendamento;

import med.voll.api.infra.exceptions.ValidacaoException;
import med.voll.api.dtos.DadosAgendamentoConsulta;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.time.DayOfWeek;

@Component
public class ValidadorHorarioFuncionamentoClinica implements ValidadorAgendamentoDeConsulta {

    private static final Logger log = LoggerFactory.getLogger(ValidadorHorarioFuncionamentoClinica.class);

    private static final int HORA_ABERTURA_CLINICA = 7;
    private static final int HORA_ENCERRAMENTO_CLINICA = 18;

    @Override
    public void validar(DadosAgendamentoConsulta dadosAgendamentoConsulta) {
        var dataConsulta = dadosAgendamentoConsulta.data();
        log.info("Validando horário de funcionamento para a consulta agendada em: {}", dataConsulta);

        var ehDomingo = dataConsulta.getDayOfWeek().equals(DayOfWeek.SUNDAY);
        var antesDaAberturaDaClinica = dataConsulta.getHour() < HORA_ABERTURA_CLINICA;
        var depoisDoEncerramentoDaClinica = dataConsulta.getHour() > HORA_ENCERRAMENTO_CLINICA;

        if (ehDomingo || antesDaAberturaDaClinica || depoisDoEncerramentoDaClinica) {
            log.info("Horário inválido detectado: Domingo={}, Antes da abertura={}, Após o encerramento={}",
                    ehDomingo, antesDaAberturaDaClinica, depoisDoEncerramentoDaClinica);

            throw new ValidacaoException(String.format(
                    "Consulta agendada fora do horário de funcionamento da clínica. " +
                            "Horários permitidos: Segunda a Sábado, entre %d:00 e %d:00.",
                    HORA_ABERTURA_CLINICA, HORA_ENCERRAMENTO_CLINICA));
        }

        log.info("Horário validado com sucesso para a consulta.");
    }
}