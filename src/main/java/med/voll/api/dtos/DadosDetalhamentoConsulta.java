package med.voll.api.dtos;

import med.voll.api.entities.Consulta;

import java.time.LocalDateTime;

public record DadosDetalhamentoConsulta(

        Long id,

        Long idMedico,

        Long idPaciente,

        LocalDateTime data) {


    public DadosDetalhamentoConsulta(Consulta consulta) {
        this(consulta.getId(), consulta.getMedico().getId(), consulta.getPaciente().getId(), consulta.getData());
    }
}

