package med.voll.api.services;


import med.voll.api.dtos.DadosAtualizacaoMedico;
import med.voll.api.dtos.DadosCadastroMedico;
import med.voll.api.dtos.DadosDetalhamentoMedico;
import med.voll.api.dtos.DadosListagemMedico;
import med.voll.api.entities.Medico;
import med.voll.api.infra.exceptions.DatabaseException;
import med.voll.api.infra.exceptions.ResourceNotFoundException;
import med.voll.api.repositories.MedicoRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class MedicoService {

    private static final Logger log = LoggerFactory.getLogger(MedicoService.class);

    @Autowired
    private MedicoRepository medicoRepository;

    @Transactional
    public DadosDetalhamentoMedico cadastrar(DadosCadastroMedico dados) {
        Medico medico = new Medico(dados);
        try {
            medicoRepository.save(medico);
        } catch (Exception e) {
            throw new DatabaseException("Erro ao salvar o médico no banco de dados!", e);
        }
        return new DadosDetalhamentoMedico(medico);
    }


    @Transactional(readOnly = true)
    public Page<DadosListagemMedico> listar(Pageable paginacao) {
        return medicoRepository.findAllByAtivoTrue(paginacao).map(DadosListagemMedico::new);
    }


    @Transactional
    public DadosDetalhamentoMedico atualizar(DadosAtualizacaoMedico dadosAtualizacaoMedico) {
        var medico = medicoRepository.findById(dadosAtualizacaoMedico.id())
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Erro ao atualizar: Médico com ID " + dadosAtualizacaoMedico.id() + " não encontrado na base de dados."
                ));

        medico.atualizarInformacoes(dadosAtualizacaoMedico);
        return new DadosDetalhamentoMedico(medico);
    }


    @Transactional
    public void excluir(Long id) {
        var medico = medicoRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Médico com ID " + id + " não encontrado!"));

        if (!medico.isAtivo()) { // Verifica se já está inativo
            throw new IllegalStateException("Médico com ID " + id + " já está inativo!");
        }

        medico.excluir();
        log.info("Médico com ID {} foi excluído logicamente.", id);
    }


    @Transactional(readOnly = true)
    public DadosDetalhamentoMedico detalhar(Long id) {
        var medico = medicoRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Médico com ID " + id + " não encontrado!"));

        return new DadosDetalhamentoMedico(medico); // A Service retorna apenas o DTO.
    }


}
