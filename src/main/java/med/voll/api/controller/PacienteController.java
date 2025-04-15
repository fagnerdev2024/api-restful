package med.voll.api.controller;

import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid;
import med.voll.api.dtos.*;
import med.voll.api.repositories.PacienteRepository;
import med.voll.api.services.PacienteService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;

@RestController
@RequestMapping("/pacientes")
@SecurityRequirement(name = "bearer-key")
public class PacienteController {

    @Autowired
    private PacienteRepository pacienteRepository;

    @Autowired
    private final PacienteService pacienteService;

    private static final Logger log = LoggerFactory.getLogger(PacienteService.class);

    public PacienteController(PacienteService pacienteService) {
        this.pacienteService = pacienteService;
    }

    @PostMapping
    public ResponseEntity<DadosDetalhamentoPaciente> cadastrarPaciente(
            @RequestBody @Valid DadosCadastroPaciente dados, UriComponentsBuilder uriBuilder) {
        log.info("Recebida solicitação para cadastrar paciente: {}", dados.nome());

        DadosDetalhamentoPaciente detalhes = pacienteService.cadastrar(dados);
        var uri = uriBuilder.path("/pacientes/{id}").buildAndExpand(detalhes.id()).toUri();
        return ResponseEntity.created(uri).body(detalhes);
    }

    @GetMapping
    public ResponseEntity<Page<DadosListagemPaciente>> listarPaciente(
            @PageableDefault(page = 0, size = 10, sort = {"nome"}) Pageable paginacao) {
        log.info("Recebida solicitação para listar pacientes com paginação: {}", paginacao);

        Page<DadosListagemPaciente> page = pacienteService.listar(paginacao);

        if (page.isEmpty()) {
            return ResponseEntity.noContent()
                    .header("X-Info", "Nenhum paciente ativo encontrado.")
                    .build(); // Retorna 204 se não houver pacientes ativos
        }

        return ResponseEntity.ok(page);
    }


    @PutMapping
    public ResponseEntity<DadosDetalhamentoPaciente> atualizarPaciente(
            @RequestBody @Valid DadosAtualizacaoPaciente dadosAtualizacaoPaciente) {
        log.info("Recebida solicitação para atualizar paciente com ID: {}", dadosAtualizacaoPaciente.id());

        DadosDetalhamentoPaciente detalhesAtualizados = pacienteService.atualizar(dadosAtualizacaoPaciente);
        return ResponseEntity.ok(detalhesAtualizados);
    }


    @DeleteMapping("/{id}")
    public ResponseEntity<Void> excluirPaciente(@PathVariable Long id) {
        log.info("Recebida solicitação para excluir paciente com ID: {}", id);

        pacienteService.excluir(id);
        return ResponseEntity.noContent().build();
    }


    @GetMapping("/{id}")
    public ResponseEntity<DadosDetalhamentoPaciente> detalharPaciente(@PathVariable Long id) {
        log.info("Recebida solicitação para detalhar paciente com ID: {}", id);

        DadosDetalhamentoPaciente detalhesPaciente = pacienteService.detalhar(id);
        return ResponseEntity.ok(detalhesPaciente); // A Controller é responsável por formatar a resposta HTTP.
    }

}
