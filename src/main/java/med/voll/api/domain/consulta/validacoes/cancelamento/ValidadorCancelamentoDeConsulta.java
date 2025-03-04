package med.voll.api.domain.consulta.validacoes.cancelamento;

import med.voll.api.dtos.DadosCancelamentoConsulta;

public interface ValidadorCancelamentoDeConsulta {

    void validar(DadosCancelamentoConsulta dados);

}
