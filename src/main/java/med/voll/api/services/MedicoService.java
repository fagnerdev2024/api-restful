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

    @Autowired
    private MedicoRepository medicoRepository;



    @Transactional
    public DadosDetalhamentoMedico cadastrar(DadosCadastroMedico dadosCadastroMedico) {
        log.info("Iniciando o método cadastrar para o médico: {}", dadosCadastroMedico.nome());
        Medico medico = new Medico(dadosCadastroMedico);
        try {
            medicoRepository.save(medico);
            log.info("Médico salvo com sucesso: {}", medico.getId());
        } catch (Exception e) {
            log.error("Erro ao salvar o médico no banco de dadosCadastroMedico!", e);
            throw new DatabaseException("Erro ao salvar o médico no banco de dadosCadastroMedico!", e);
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
            if(medicos.isEmpty()){
                return Page.empty();
            }
            return medicos.map(DadosListagemMedico::new);
        } catch (DataAccessException e) {
            log.error("Erro de acesso ao banco de dados.", e);
            throw new ServiceException("Falha ao acessar o banco de dados.", e);
        } catch (Exception e) {
            log.error("Erro desconhecido ao listar médicos ativos.", e);
            throw new ServiceException("Falha ao buscar médicos ativos.", e);
        }
    }


    @Transactional
    public DadosDetalhamentoMedico atualizar(DadosAtualizacaoMedico dadosAtualizacaoMedico) {
        log.info("Iniciando atualização para o médico com ID: {}", dadosAtualizacaoMedico.id());
        var medico = medicoRepository.findById(dadosAtualizacaoMedico.id())
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Erro ao atualizar: Médico com ID " + dadosAtualizacaoMedico.id() + " não encontrado na base de dados."
                ));

        medico.atualizarInformacoes(dadosAtualizacaoMedico);

        try {
            medicoRepository.save(medico);
            log.info("Médico atualizado com sucesso: {}", medico.getId());
        } catch (Exception e) {
            log.error("Erro ao atualizar o médico no banco de dados!", e);
            throw new DatabaseException("Erro ao atualizar o médico no banco de dados!", e);
        }

        return new DadosDetalhamentoMedico(medico);
    }



    @Transactional
    public void excluir(Long id) {
        log.info("Iniciando exclusão para o médico com ID: {}", id);

        var medico = medicoRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Médico com ID " + id + " não encontrado!"));

        if (!medico.isAtivo()) {
            throw new IllegalStateException("Médico com ID " + id + " já está inativo!");
        }

        try {
            medico.excluir();
            medicoRepository.save(medico);
            log.info("Médico com ID {} foi excluído logicamente.", id);
        } catch (Exception e) {
            log.error("Erro ao excluir o médico com ID {} no banco de dados!", id, e);
            throw new DatabaseException("Erro ao excluir o médico no banco de dados!", e);
        }
    }


    @Transactional(readOnly = true)
    public DadosDetalhamentoMedico detalhar(Long id) {
        log.info("Iniciando detalhamento para o médico com ID: {}", id);

        var medico = medicoRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Médico com ID " + id + " não encontrado!"));
        if(!medico.isAtivo()){
            log.info("Médico com ID {} está inativo", id);
            throw new ResourceNotFoundException("Médico com ID " + id + " está inativo.");

        }
        try {
            return new DadosDetalhamentoMedico(medico);
        } catch (Exception e) {
            log.error("Erro ao detalhar o médico com ID {} no banco de dados!", id, e);
            throw new ServiceException("Erro ao detalhar o médico no banco de dados!", e);
        }
    }
}
