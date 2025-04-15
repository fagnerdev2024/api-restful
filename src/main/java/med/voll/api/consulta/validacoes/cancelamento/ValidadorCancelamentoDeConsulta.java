package med.voll.api.consulta.validacoes.cancelamento;

import med.voll.api.dtos.DadosCancelamentoConsulta;

public interface ValidadorCancelamentoDeConsulta {

    void validar(DadosCancelamentoConsulta dados);

}
