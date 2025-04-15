package med.voll.api.domain.consulta.validacoes.agendamento;

import med.voll.api.dtos.DadosAgendamentoConsulta;

public interface ValidadorAgendamentoDeConsulta {

    void validar(DadosAgendamentoConsulta dados);

}
