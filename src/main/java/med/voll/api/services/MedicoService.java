package med.voll.api.services;

import med.voll.api.dtos.DadosAtualizacaoMedico;
import med.voll.api.dtos.DadosCadastroMedico;
import med.voll.api.dtos.DadosDetalhamentoMedico;
import med.voll.api.dtos.DadosListagemMedico;
import med.voll.api.entities.Medico;
import med.voll.api.infra.exceptions.DatabaseException;
import med.voll.api.infra.exceptions.ResourceNotFoundException;
import med.voll.api.repositories.MedicoRepository;
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
public class MedicoService {

    private static final Logger log = LoggerFactory.getLogger(MedicoService.class);

    private static final int MAX_PAGE_SIZE = 100;

    private static final String ERRO_SALVAR_MEDICO = "Erro ao salvar o médico no banco de dados!";
    private static final String ERRO_LISTAR_MEDICOS = "Erro ao listar médicos ativos.";
    private static final String ERRO_ATUALIZAR_MEDICO = "Erro ao atualizar o médico no banco de dados!";
    private static final String ERRO_EXCLUIR_MEDICO = "Erro ao excluir o médico no banco de dados!";
    private static final String MEDICO_NAO_ENCONTRADO = "Médico com ID %d não encontrado!";
    private static final String MEDICO_INATIVO = "Médico com ID %d já está inativo!";

    @Autowired
    private MedicoRepository medicoRepository;

    @Transactional
    public DadosDetalhamentoMedico cadastrar(DadosCadastroMedico dadosCadastroMedico) {
        log.info("Iniciando o método cadastrar para o médico: {}", dadosCadastroMedico.nome());
        Medico medico = new Medico(dadosCadastroMedico);
        try {
            medicoRepository.save(medico);
            log.info("Médico salvo com sucesso: {}", medico.getId());
        } catch (DataAccessException e) {
            log.error(ERRO_SALVAR_MEDICO, e);
            throw new DatabaseException(ERRO_SALVAR_MEDICO, e);
        }
        return new DadosDetalhamentoMedico(medico);
    }

    @Transactional(readOnly = true)
    public Page<DadosListagemMedico> listar(Pageable paginacao) {
        if (paginacao.getPageSize() > MAX_PAGE_SIZE) {
            throw new IllegalArgumentException("O tamanho da página não pode exceder " + MAX_PAGE_SIZE + " registros.");
        }
        log.debug("Listando médicos ativos com paginação: {}", paginacao);
        try {
            Page<Medico> medicos = medicoRepository.findAllByAtivoTrue(paginacao);
            if (medicos.isEmpty()) {
                log.debug("Nenhum médico ativo encontrado.");
                return Page.empty();
            }
            return medicos.map(DadosListagemMedico::new);
        } catch (DataAccessException e) {
            log.error(ERRO_LISTAR_MEDICOS, e);
            throw new ServiceException(ERRO_LISTAR_MEDICOS, e);
        }
    }

    @Transactional
    public DadosDetalhamentoMedico atualizar(DadosAtualizacaoMedico dadosAtualizacaoMedico) {
        log.info("Iniciando atualização para o médico com ID: {}", dadosAtualizacaoMedico.id());
        var medico = medicoRepository.findById(dadosAtualizacaoMedico.id())
                .orElseThrow(() -> new ResourceNotFoundException(
                        String.format(MEDICO_NAO_ENCONTRADO, dadosAtualizacaoMedico.id())
                ));

        medico.atualizarInformacoes(dadosAtualizacaoMedico);
        try {
            medicoRepository.save(medico);
            log.info("Médico atualizado com sucesso: {}", medico.getId());
        } catch (DataAccessException e) {
            log.error(ERRO_ATUALIZAR_MEDICO, e);
            throw new DatabaseException(ERRO_ATUALIZAR_MEDICO, e);
        }
        return new DadosDetalhamentoMedico(medico);
    }

    @Transactional
    public void excluir(Long id) {
        log.info("Iniciando exclusão para o médico com ID: {}", id);
        var medico = medicoRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(
                        String.format(MEDICO_NAO_ENCONTRADO, id)
                ));

        if (!medico.isAtivo()) {
            log.info(String.format(MEDICO_INATIVO, id));
            throw new IllegalStateException(String.format(MEDICO_INATIVO, id));
        }

        try {
            medico.excluir();
            medicoRepository.save(medico);
            log.info("Médico com ID {} foi excluído logicamente.", id);
        } catch (DataAccessException e) {
            log.error(String.format(ERRO_EXCLUIR_MEDICO, id), e);
            throw new DatabaseException(String.format(ERRO_EXCLUIR_MEDICO, id), e);
        }
    }

    @Transactional(readOnly = true)
    public DadosDetalhamentoMedico detalhar(Long id) {
        log.info("Iniciando detalhamento para o médico com ID: {}", id);
        var medico = medicoRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(
                        String.format(MEDICO_NAO_ENCONTRADO, id)
                ));

        if (!medico.isAtivo()) {
            log.info("Médico com ID {} está inativo.", id);
            throw new ResourceNotFoundException(String.format(MEDICO_INATIVO, id));
        }

        return new DadosDetalhamentoMedico(medico);
    }
}