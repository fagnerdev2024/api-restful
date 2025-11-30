package med.voll.api.consulta.validacoes.cancelamento;

import med.voll.api.dtos.DadosCancelamentoConsulta;
import med.voll.api.infra.exceptions.ValidacaoException;
import med.voll.api.repositories.ConsultaRepository;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.time.LocalDateTime;

@Component("ValidadorHorarioAntecedenciaCancelamento")
public class ValidadorHorarioAntecedencia implements ValidadorCancelamentoDeConsulta {

    private static final long HORAS_MINIMAS_CANCELAMENTO = 24;

    private final ConsultaRepository consultaRepository;


    public ValidadorHorarioAntecedencia(ConsultaRepository consultaRepository) {
        this.consultaRepository = consultaRepository;
    }


    @Override
    public void validar(DadosCancelamentoConsulta dadosCancelamentoConsulta) {
        var consulta = consultaRepository.findById(dadosCancelamentoConsulta.idConsulta())
                .orElseThrow(() -> new ValidacaoException("Consulta não encontrada!"));
        var diferencaEmHoras = calcularDiferencaEmHoras(consulta.getData());
        if (diferencaEmHoras < HORAS_MINIMAS_CANCELAMENTO) {
            throw new ValidacaoException(String.format("Consulta somente pode ser cancelada com antecedência mínima de %d horas!", HORAS_MINIMAS_CANCELAMENTO)
            );
        }
    }

    private long calcularDiferencaEmHoras(LocalDateTime dataConsulta) {
        var agora = LocalDateTime.now();
        return Duration.between(agora, dataConsulta).toHours();
    }
}