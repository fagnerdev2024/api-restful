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
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/pacientes")
@SecurityRequirement(name = "bearer-key")
public class PacienteController {

    private static final Logger log = LoggerFactory.getLogger(PacienteController.class);

    @Autowired
    private PacienteService pacienteService;



    @PostMapping
    public ResponseEntity<DadosDetalhamentoPaciente> cadastrarPaciente(@RequestBody @Valid DadosCadastroPaciente dadosCadastroPaciente, UriComponentsBuilder uriComponentsBuilder) {
        log.info("Recebida solicitação para cadastrar paciente: {}", dadosCadastroPaciente.nome());
        DadosDetalhamentoPaciente dadosDetalhamentoPaciente = pacienteService.cadastrar(dadosCadastroPaciente);
        var uri = uriComponentsBuilder.path("/pacientes/{id}").buildAndExpand(dadosDetalhamentoPaciente.id()).toUri();
        return ResponseEntity.created(uri).body(dadosDetalhamentoPaciente);
    }

    @GetMapping
    public ResponseEntity<?> listarPaciente(@PageableDefault(page = 0, size = 10, sort = {"nome"}) Pageable paginacao) {
        log.info("Recebida solicitação para listar pacientes com paginação: {}", paginacao);
        Page<DadosListagemPaciente> dadosListagemPacientes = pacienteService.listar(paginacao);

        if (dadosListagemPacientes.isEmpty()) {
            Map<String, String> response = new HashMap<>();
            response.put("message", "Nenhum paciente ativo encontrado");
            return ResponseEntity.status(HttpStatus.OK).body(response);
        }
        return ResponseEntity.ok(dadosListagemPacientes);
    }

    @PutMapping
    public ResponseEntity<DadosDetalhamentoPaciente> atualizarPaciente(@RequestBody @Valid DadosAtualizacaoPaciente dadosAtualizacaoPaciente) {
        log.info("Recebida solicitação para atualizar paciente com ID: {}", dadosAtualizacaoPaciente.id());
        DadosDetalhamentoPaciente dadosDetalhamentoPaciente = pacienteService.atualizar(dadosAtualizacaoPaciente);
        return ResponseEntity.ok(dadosDetalhamentoPaciente);
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
        DadosDetalhamentoPaciente dadosDetalhamentoPaciente = pacienteService.detalhar(id);
        return ResponseEntity.ok(dadosDetalhamentoPaciente);
    }
}
