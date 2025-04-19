package med.voll.api.services;

import med.voll.api.dtos.DadosAtualizacaoPaciente;
import med.voll.api.dtos.DadosCadastroPaciente;
import med.voll.api.dtos.DadosDetalhamentoPaciente;
import med.voll.api.dtos.DadosListagemPaciente;
import med.voll.api.entities.Paciente;
import med.voll.api.infra.exceptions.DatabaseException;
import med.voll.api.infra.exceptions.ResourceNotFoundException;
import med.voll.api.repositories.PacienteRepository;
import org.hibernate.service.spi.ServiceException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class PacienteService {

    private static final Logger log = LoggerFactory.getLogger(PacienteService.class);

    private static final int MAX_PAGE_SIZE = 100;

    private static final String ERRO_SALVAR_PACIENTE = "Erro ao salvar o paciente no banco de dados!";
    private static final String PACIENTE_NAO_ENCONTRADO = "Paciente com ID %d não encontrado!";
    private static final String PACIENTE_JA_INATIVO = "Paciente com ID %d já está inativo!";
    private static final String ERRO_EXCLUIR_PACIENTE = "Erro ao excluir o paciente com ID %d no banco de dados!";
    private static final String ERRO_ATUALIZAR_PACIENTE = "Erro ao atualizar o paciente no banco de dados!";
    private static final String ERRO_LISTAR_PACIENTES = "Erro ao listar pacientes ativos.";

    @Autowired
    private PacienteRepository pacienteRepository;

    @Transactional
    public DadosDetalhamentoPaciente cadastrar(DadosCadastroPaciente dados) {
        log.info("Iniciando o método cadastrar para o paciente: {}", dados.nome());
        Paciente paciente = new Paciente(dados);
        try {
            pacienteRepository.save(paciente);
            log.info("Paciente salvo com sucesso: {}", paciente.getId());
        } catch (DataAccessException e) {
            log.error(ERRO_SALVAR_PACIENTE, e);
            throw new DatabaseException(ERRO_SALVAR_PACIENTE, e);
        }
        return new DadosDetalhamentoPaciente(paciente);
    }

    @Transactional(readOnly = true)
    public Page<DadosListagemPaciente> listar(Pageable paginacao) {
        if (paginacao.getPageSize() > MAX_PAGE_SIZE) {
            throw new IllegalArgumentException("O tamanho da página não pode exceder " + MAX_PAGE_SIZE + " registros.");
        }
        log.debug("Listando pacientes ativos com paginação: {}", paginacao);
        try {
            return pacienteRepository.findAllByAtivoTrue(paginacao)
                    .map(DadosListagemPaciente::new);
        } catch (DataAccessException e) {
            log.error("Erro de acesso ao banco de dados.", e);
            throw new ServiceException(ERRO_LISTAR_PACIENTES, e);
        } catch (Exception e) {
            log.error("Erro desconhecido ao listar pacientes ativos.", e);
            throw new ServiceException(ERRO_LISTAR_PACIENTES, e);
        }
    }

    @Transactional
    public DadosDetalhamentoPaciente atualizar(DadosAtualizacaoPaciente dadosAtualizacaoPaciente) {
        log.info("Iniciando atualização para o paciente com ID: {}", dadosAtualizacaoPaciente.id());

        var paciente = pacienteRepository.findById(dadosAtualizacaoPaciente.id())
                .orElseThrow(() -> new ResourceNotFoundException(
                        String.format(PACIENTE_NAO_ENCONTRADO, dadosAtualizacaoPaciente.id())
                ));

        paciente.atualizarInformacoes(dadosAtualizacaoPaciente);

        try {
            pacienteRepository.save(paciente);
            log.info("Paciente atualizado com sucesso: {}", paciente.getId());
        } catch (DataAccessException e) {
            log.error(ERRO_ATUALIZAR_PACIENTE, e);
            throw new DatabaseException(ERRO_ATUALIZAR_PACIENTE, e);
        }

        return new DadosDetalhamentoPaciente(paciente);
    }

    @Transactional
    public void excluir(Long id) {
        log.info("Iniciando exclusão para o paciente com ID: {}", id);

        var paciente = pacienteRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(
                        String.format(PACIENTE_NAO_ENCONTRADO, id)
                ));

        if (!paciente.isAtivo()) {
            throw new IllegalStateException(String.format(PACIENTE_JA_INATIVO, id));
        }

        try {
            paciente.excluir();
            pacienteRepository.save(paciente);
            log.info("Paciente com ID {} foi excluído logicamente.", id);
        } catch (DataAccessException e) {
            log.error(String.format(ERRO_EXCLUIR_PACIENTE, id), e);
            throw new DatabaseException(String.format(ERRO_EXCLUIR_PACIENTE, id), e);
        }
    }

    @Transactional(readOnly = true)
    public DadosDetalhamentoPaciente detalhar(Long id) {
        log.info("Iniciando detalhamento para o paciente com ID: {}", id);

        var paciente = pacienteRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(
                        String.format(PACIENTE_NAO_ENCONTRADO, id)
                ));

        if (!paciente.isAtivo()) {
            log.info("Paciente com ID {} está inativo.", id);
            throw new IllegalStateException("Paciente com ID " + id + " está inativo!");
        }

        return new DadosDetalhamentoPaciente(paciente);
    }
}