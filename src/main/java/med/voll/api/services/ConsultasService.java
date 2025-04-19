package med.voll.api.services;

import med.voll.api.consulta.validacoes.agendamento.ValidadorAgendamentoDeConsulta;
import med.voll.api.consulta.validacoes.cancelamento.ValidadorCancelamentoDeConsulta;
import med.voll.api.dtos.DadosAgendamentoConsulta;
import med.voll.api.dtos.DadosCancelamentoConsulta;
import med.voll.api.dtos.DadosDetalhamentoConsulta;
import med.voll.api.entities.Consulta;
import med.voll.api.entities.Medico;
import med.voll.api.infra.exceptions.ValidacaoException;
import med.voll.api.repositories.RepositoryFacade;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class ConsultasService {

    private static final Logger log = LoggerFactory.getLogger(ConsultasService.class);

    private static final String PACIENTE_NAO_EXISTE = "Id do paciente informado não existe!";
    private static final String MEDICO_NAO_EXISTE = "Id do médico informado não existe!";
    private static final String MEDICO_NAO_DISPONIVEL = "Não existe médico disponível nessa data!";
    private static final String CONSULTA_NAO_EXISTE = "Id da consulta informado não existe!";
    private static final String ESPECIALIDADE_OBRIGATORIA = "Especialidade é obrigatória quando médico não for escolhido!";

    private final RepositoryFacade repositoryFacade;
    private final List<ValidadorAgendamentoDeConsulta> validadorAgendamentoDeConsultas;
    private final List<ValidadorCancelamentoDeConsulta> validadorCancelamentoDeConsultas;

    public ConsultasService(
            RepositoryFacade repositoryFacade,
            List<ValidadorAgendamentoDeConsulta> validadorAgendamentoDeConsultas,
            List<ValidadorCancelamentoDeConsulta> validadorCancelamentoDeConsultas) {
        this.repositoryFacade = repositoryFacade;
        this.validadorAgendamentoDeConsultas = validadorAgendamentoDeConsultas;
        this.validadorCancelamentoDeConsultas = validadorCancelamentoDeConsultas;
    }

    @Transactional
    public DadosDetalhamentoConsulta agendar(DadosAgendamentoConsulta dadosAgendamentoConsulta) {
        log.info("Iniciando agendamento de consulta para o paciente com ID: {}", dadosAgendamentoConsulta.idPaciente());

        if (!repositoryFacade.getPacienteRepository().existsById(dadosAgendamentoConsulta.idPaciente())) {
            log.error(PACIENTE_NAO_EXISTE);
            throw new ValidacaoException(PACIENTE_NAO_EXISTE);
        }

        if (dadosAgendamentoConsulta.idMedico() != null &&
                !repositoryFacade.getMedicoRepository().existsById(dadosAgendamentoConsulta.idMedico())) {
            log.error(MEDICO_NAO_EXISTE);
            throw new ValidacaoException(MEDICO_NAO_EXISTE);
        }

        validadorAgendamentoDeConsultas.forEach(validador -> validador.validar(dadosAgendamentoConsulta));

        var paciente = repositoryFacade.getPacienteRepository().getReferenceById(dadosAgendamentoConsulta.idPaciente());
        var medico = escolherMedico(dadosAgendamentoConsulta);

        if (medico == null) {
            log.error(MEDICO_NAO_DISPONIVEL);
            throw new ValidacaoException(MEDICO_NAO_DISPONIVEL);
        }

        var consulta = new Consulta(null, medico, paciente, dadosAgendamentoConsulta.data(), null);
        repositoryFacade.getConsultaRepository().save(consulta);

        log.info("Consulta agendada com sucesso para o paciente com ID: {} e médico com ID: {}",
                paciente.getId(), medico.getId());

        return new DadosDetalhamentoConsulta(consulta);
    }

    @Transactional
    public void cancelar(DadosCancelamentoConsulta dadosCancelamentoConsulta) {
        log.info("Iniciando cancelamento de consulta com ID: {}", dadosCancelamentoConsulta.idConsulta());

        if (!repositoryFacade.getConsultaRepository().existsById(dadosCancelamentoConsulta.idConsulta())) {
            log.error(CONSULTA_NAO_EXISTE);
            throw new ValidacaoException(CONSULTA_NAO_EXISTE);
        }

        validadorCancelamentoDeConsultas.forEach(validador -> validador.validar(dadosCancelamentoConsulta));

        var consulta = repositoryFacade.getConsultaRepository().getReferenceById(dadosCancelamentoConsulta.idConsulta());
        consulta.cancelar(dadosCancelamentoConsulta.motivo());

        log.info("Consulta com ID: {} foi cancelada. Motivo: {}",
                dadosCancelamentoConsulta.idConsulta(), dadosCancelamentoConsulta.motivo());
    }

    @Transactional
    private Medico escolherMedico(DadosAgendamentoConsulta dadosAgendamentoConsulta) {
        log.debug("Selecionando médico para a consulta...");

        if (dadosAgendamentoConsulta.idMedico() != null) {
            return repositoryFacade.getMedicoRepository().getReferenceById(dadosAgendamentoConsulta.idMedico());
        }

        if (dadosAgendamentoConsulta.especialidade() == null) {
            log.error(ESPECIALIDADE_OBRIGATORIA);
            throw new ValidacaoException(ESPECIALIDADE_OBRIGATORIA);
        }

        var medico = repositoryFacade.getMedicoRepository()
                .escolherMedicoAleatorioLivreNaData(dadosAgendamentoConsulta.especialidade(), dadosAgendamentoConsulta.data());

        if (medico != null) {
            log.debug("Médico selecionado com ID: {}", medico.getId());
        } else {
            log.warn("Nenhum médico disponível encontrado para a data: {}", dadosAgendamentoConsulta.data());
        }

        return medico;
    }
}