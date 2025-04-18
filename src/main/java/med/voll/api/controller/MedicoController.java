package med.voll.api.controller;

import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid;
import med.voll.api.dtos.DadosAtualizacaoMedico;
import med.voll.api.dtos.DadosCadastroMedico;
import med.voll.api.dtos.DadosDetalhamentoMedico;
import med.voll.api.dtos.DadosListagemMedico;
import med.voll.api.services.MedicoService;
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
@RequestMapping("/medicos")
@SecurityRequirement(name = "bearer-key")
public class MedicoController {

    private static final Logger log = LoggerFactory.getLogger(MedicoController.class);


    @Autowired
    private MedicoService medicoService;


    @PostMapping
    public ResponseEntity<DadosDetalhamentoMedico> cadastrarMedico(@RequestBody @Valid DadosCadastroMedico dadosCadastroMedico, UriComponentsBuilder uriComponentsBuilder) {
        log.info("Recebida solicitação para cadastrar médico: {}", dadosCadastroMedico.nome());
        DadosDetalhamentoMedico dadosDetalhamentoMedico = medicoService.cadastrar(dadosCadastroMedico);
        var uri = uriComponentsBuilder.path("/medicos/{id}").buildAndExpand(dadosDetalhamentoMedico.id()).toUri();
        return ResponseEntity.created(uri).body(dadosDetalhamentoMedico);
    }


    @GetMapping
    public ResponseEntity<?> listarMedicos(@PageableDefault(page = 0, size = 10, sort = {"nome"}) Pageable paginacao) {
        log.info("Recebida solicitação para listar médicos com paginação: {}", paginacao);
        Page<DadosListagemMedico> page = medicoService.listar(paginacao);

        if (page.isEmpty()) {
            Map<String, String> response = new HashMap<>();
            response.put("message", "Nenhum médico ativo encontrado");
            return ResponseEntity.status(HttpStatus.OK).body(response);
        }
        return ResponseEntity.ok(page);
    }


    @PutMapping
    public ResponseEntity<DadosDetalhamentoMedico> atualizarMedico(@RequestBody @Valid DadosAtualizacaoMedico dadosAtualizacaoMedico) {
        log.info("Recebida solicitação para atualizar médico com ID: {}", dadosAtualizacaoMedico.id());
        DadosDetalhamentoMedico dadosDetalhamentoMedico = medicoService.atualizar(dadosAtualizacaoMedico);
        return ResponseEntity.ok(dadosDetalhamentoMedico);
    }


    @DeleteMapping("/{id}")
    public ResponseEntity<Void> excluirMedico(@PathVariable Long id) {
        log.info("Recebida solicitação para excluir médico com ID: {}", id);
        medicoService.excluir(id);
        return ResponseEntity.noContent().build();
    }


    @GetMapping("/{id}")
    public ResponseEntity<DadosDetalhamentoMedico> detalharMedico(@PathVariable Long id) {
        log.info("Recebida solicitação para detalhar médico com ID: {}", id);
        DadosDetalhamentoMedico dadosDetalhamentoMedico = medicoService.detalhar(id);
        return ResponseEntity.ok(dadosDetalhamentoMedico);
    }
}
