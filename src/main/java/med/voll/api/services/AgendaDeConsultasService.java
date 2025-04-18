package med.voll.api.services;

import med.voll.api.consulta.validacoes.agendamento.ValidadorAgendamentoDeConsulta;
import med.voll.api.entities.Consulta;
import med.voll.api.infra.exceptions.ValidacaoException;
import med.voll.api.consulta.validacoes.cancelamento.ValidadorCancelamentoDeConsulta;
import med.voll.api.entities.Medico;
import med.voll.api.repositories.MedicoRepository;
import med.voll.api.repositories.PacienteRepository;
import med.voll.api.dtos.DadosAgendamentoConsulta;
import med.voll.api.dtos.DadosCancelamentoConsulta;
import med.voll.api.dtos.DadosDetalhamentoConsulta;
import med.voll.api.repositories.ConsultaRepository;
import med.voll.api.repositories.RepositoryFacade;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class AgendaDeConsultasService {

    @Autowired
    private RepositoryFacade repositoryFacade;

    @Autowired
    private List<ValidadorAgendamentoDeConsulta> validadorAgendamentoDeConsultas;

    @Autowired
    private List<ValidadorCancelamentoDeConsulta> validadorCancelamentoDeConsultas;


    public AgendaDeConsultasService(List<ValidadorAgendamentoDeConsulta> validadorAgendamentoDeConsultas, List<ValidadorCancelamentoDeConsulta> validadorCancelamentoDeConsultas) {
        this.validadorAgendamentoDeConsultas = validadorAgendamentoDeConsultas;
        this.validadorCancelamentoDeConsultas = validadorCancelamentoDeConsultas;
    }




    @Transactional
    public DadosDetalhamentoConsulta agendar(DadosAgendamentoConsulta dadosAgendamentoConsulta) {
        if (!repositoryFacade.getPacienteRepository().existsById(dadosAgendamentoConsulta.idPaciente())) {
            throw new ValidacaoException("Id do paciente informado não existe!");
        }

        if (dadosAgendamentoConsulta.idMedico() != null && !repositoryFacade.getMedicoRepository().existsById(dadosAgendamentoConsulta.idMedico())) {
            throw new ValidacaoException("Id do médico informado não existe!");
        }

        validadorAgendamentoDeConsultas.forEach(v -> v.validar(dadosAgendamentoConsulta));

        var paciente = repositoryFacade.getPacienteRepository().getReferenceById(dadosAgendamentoConsulta.idPaciente());
        var medico = escolherMedico(dadosAgendamentoConsulta);
        if (medico == null) {
            throw new ValidacaoException("Não existe médico disponível nessa data!");
        }

        var consulta = new Consulta(null, medico, paciente, dadosAgendamentoConsulta.data(), null);
        repositoryFacade.getConsultaRepository().save(consulta);

        return new DadosDetalhamentoConsulta(consulta);
    }


    @Transactional
    public void cancelar(DadosCancelamentoConsulta dadosCancelamentoConsulta) {
        if (!repositoryFacade.getConsultaRepository().existsById(dadosCancelamentoConsulta.idConsulta())) {
            throw new ValidacaoException("Id da consulta informado não existe!");
        }

        validadorCancelamentoDeConsultas.forEach(v -> v.validar(dadosCancelamentoConsulta));

        var consulta = repositoryFacade.getConsultaRepository().getReferenceById(dadosCancelamentoConsulta.idConsulta());
        consulta.cancelar(dadosCancelamentoConsulta.motivo());
    }


    @Transactional
    private Medico escolherMedico(DadosAgendamentoConsulta dadosAgendamentoConsulta) {
        if (dadosAgendamentoConsulta.idMedico() != null) {
            return repositoryFacade.getMedicoRepository().getReferenceById(dadosAgendamentoConsulta.idMedico());
        }

        if (dadosAgendamentoConsulta.especialidade() == null) {
            throw new ValidacaoException("Especialidade é obrigatória quando médico não for escolhido!");
        }

        return repositoryFacade.getMedicoRepository().escolherMedicoAleatorioLivreNaData(dadosAgendamentoConsulta.especialidade(), dadosAgendamentoConsulta.data());
    }

}
