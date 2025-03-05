package med.voll.api.services;

import med.voll.api.dtos.DadosAtualizacaoPaciente;
import med.voll.api.dtos.DadosCadastroPaciente;
import med.voll.api.dtos.DadosDetalhamentoPaciente;
import med.voll.api.dtos.DadosListagemPaciente;
import med.voll.api.entities.Paciente;
import med.voll.api.infra.exceptions.ResourceNotFoundException;
import med.voll.api.repositories.PacienteRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class PacienteService {

    @Autowired
    private final PacienteRepository pacienteRepository;

    public PacienteService(PacienteRepository pacienteRepository) {
        this.pacienteRepository = pacienteRepository;
    }



    @Transactional
    public Paciente cadastrarPaciente(DadosCadastroPaciente dados) {
        var paciente = new Paciente(dados);
        return pacienteRepository.save(paciente);
    }

    @Transactional(readOnly = true)
    public Page<DadosListagemPaciente> listarPacientesAtivos(Pageable paginacao) {
        return pacienteRepository.findAllByAtivoTrue(paginacao).map(DadosListagemPaciente::new);
    }

    @Transactional
    public Paciente atualizarPaciente(DadosAtualizacaoPaciente dados) {
        var paciente = pacienteRepository.getReferenceById(dados.id());
        paciente.atualizarInformacoes(dados);
        return paciente;
    }

    @Transactional
    public void excluirPaciente(Long id) {
        var paciente = pacienteRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Paciente com ID " + id + " não encontrado!"));

        if (!paciente.isAtivo()) {
            throw new IllegalStateException("O paciente com ID " + id + " já está inativo!");
        }

        paciente.excluir(); // Realiza a exclusão lógica
    }


    @Transactional(readOnly = true)
    public DadosDetalhamentoPaciente detalharPaciente(Long id) {
        var paciente = pacienteRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Paciente com ID " + id + " não encontrado!"));
        return new DadosDetalhamentoPaciente(paciente);
    }
}
