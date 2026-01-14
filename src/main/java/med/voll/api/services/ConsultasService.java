package med.voll.api.services;

import med.voll.api.consulta.validacoes.agendamento.ValidadorAgendamentoDeConsulta;
import med.voll.api.consulta.validacoes.cancelamento.ValidadorCancelamentoDeConsulta;
import med.voll.api.dtos.DadosAgendamentoConsulta;
import med.voll.api.dtos.DadosCancelamentoConsulta;
import med.voll.api.dtos.DadosDetalhamentoConsulta;
import med.voll.api.entities.Consulta;
import med.voll.api.entities.Medico;
import med.voll.api.infra.exceptions.ValidacaoException;
import med.voll.api.repositories.ConsultaRepository;
import med.voll.api.repositories.MedicoRepository;
import med.voll.api.repositories.PacienteRepository;
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

    private final ConsultaRepository consultaRepository;
    private final MedicoRepository medicoRepository;
    private final PacienteRepository pacienteRepository;

    private final List<ValidadorAgendamentoDeConsulta> validadoresAgendamento;
    private final List<ValidadorCancelamentoDeConsulta> validadoresCancelamento;

    public ConsultasService(ConsultaRepository consultaRepository, MedicoRepository medicoRepository, PacienteRepository pacienteRepository, List<ValidadorAgendamentoDeConsulta> validadoresAgendamento, List<ValidadorCancelamentoDeConsulta> validadoresCancelamento) {
        this.consultaRepository = consultaRepository;
        this.medicoRepository = medicoRepository;
        this.pacienteRepository = pacienteRepository;
        this.validadoresAgendamento = validadoresAgendamento;
        this.validadoresCancelamento = validadoresCancelamento;
    }

    @Transactional
    public DadosDetalhamentoConsulta agendar(DadosAgendamentoConsulta dados) {
        log.info("Iniciando agendamento de consulta para o paciente com ID: {}", dados.idPaciente());

        if (!pacienteRepository.existsById(dados.idPaciente())) {
            log.error(PACIENTE_NAO_EXISTE);
            throw new ValidacaoException(PACIENTE_NAO_EXISTE);
        }

        if (dados.idMedico() != null && !medicoRepository.existsById(dados.idMedico())) {
            log.error(MEDICO_NAO_EXISTE);
            throw new ValidacaoException(MEDICO_NAO_EXISTE);
        }

        validadoresAgendamento.forEach(v -> v.validar(dados));

        var paciente = pacienteRepository.getReferenceById(dados.idPaciente());
        var medico = escolherMedico(dados);

        if (medico == null) {
            log.error(MEDICO_NAO_DISPONIVEL);
            throw new ValidacaoException(MEDICO_NAO_DISPONIVEL);
        }

        var consulta = new Consulta(null, medico, paciente, dados.data(), null);
        consultaRepository.save(consulta);

        log.info("Consulta agendada com sucesso para o paciente com ID: {} e médico com ID: {}",
                paciente.getId(), medico.getId());

        return new DadosDetalhamentoConsulta(consulta);
    }

    @Transactional
    public void cancelar(DadosCancelamentoConsulta dados) {
        log.info("Iniciando cancelamento de consulta com ID: {}", dados.idConsulta());

        if (!consultaRepository.existsById(dados.idConsulta())) {
            log.error(CONSULTA_NAO_EXISTE);
            throw new ValidacaoException(CONSULTA_NAO_EXISTE);
        }

        validadoresCancelamento.forEach(v -> v.validar(dados));

        var consulta = consultaRepository.getReferenceById(dados.idConsulta());
        consulta.cancelar(dados.motivo());

        log.info("Consulta com ID: {} foi cancelada. Motivo: {}", dados.idConsulta(), dados.motivo());
    }

    @Transactional(readOnly = true)
    public List<DadosDetalhamentoConsulta> listarTodas() {
        return consultaRepository.findAll()
                .stream()
                .map(DadosDetalhamentoConsulta::new)
                .toList();
    }

    private Medico escolherMedico(DadosAgendamentoConsulta dados) {
        log.debug("Selecionando médico para a consulta...");

        if (dados.idMedico() != null) {
            return medicoRepository.getReferenceById(dados.idMedico());
        }

        if (dados.especialidade() == null) {
            log.error(ESPECIALIDADE_OBRIGATORIA);
            throw new ValidacaoException(ESPECIALIDADE_OBRIGATORIA);
        }

        var medico = medicoRepository.escolherMedicoAleatorioLivreNaData(
                dados.especialidade(), dados.data()
        );

        if (medico != null) {
            log.debug("Médico selecionado com ID: {}", medico.getId());
        } else {
            log.warn("Nenhum médico disponível encontrado para a data: {}", dados.data());
        }

        return medico;
    }
}
