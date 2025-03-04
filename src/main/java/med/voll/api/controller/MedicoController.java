package med.voll.api.controller;

import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid;
import med.voll.api.dtos.DadosAtualizacaoMedico;
import med.voll.api.dtos.DadosCadastroMedico;
import med.voll.api.dtos.DadosDetalhamentoMedico;
import med.voll.api.dtos.DadosListagemMedico;
import med.voll.api.services.MedicoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;

@RestController
@RequestMapping("/medicos")
@SecurityRequirement(name = "bearer-key")
public class MedicoController {


    @Autowired
    private MedicoService medicoService;


    @PostMapping
    public ResponseEntity<DadosDetalhamentoMedico> cadastrarMedico(@RequestBody @Valid DadosCadastroMedico dados, UriComponentsBuilder uriBuilder) {
        DadosDetalhamentoMedico detalhes = medicoService.cadastrar(dados);
        var uri = uriBuilder.path("/medicos/{id}").buildAndExpand(detalhes.id()).toUri();
        return ResponseEntity.created(uri).body(detalhes);
    }


    @GetMapping
    public ResponseEntity<Page<DadosListagemMedico>> listarMedicos(@PageableDefault(page = 0, size = 10, sort = {"nome"}) Pageable paginacao) {
        Page<DadosListagemMedico> page = medicoService.listar(paginacao);

        if (page.isEmpty()) {
            return ResponseEntity.noContent().build(); // Retorna 204 se não houver médicos ativos
        }

        return ResponseEntity.ok(page);
    }

    @PutMapping
    public ResponseEntity<DadosDetalhamentoMedico> atualizarMedicos(@RequestBody @Valid DadosAtualizacaoMedico dadosAtualizacaoMedico) {
        DadosDetalhamentoMedico detalhesAtualizados = medicoService.atualizar(dadosAtualizacaoMedico);
        return ResponseEntity.ok(detalhesAtualizados);
    }


    @DeleteMapping("/{id}")
    public ResponseEntity<Void> excluirMedicos(@PathVariable Long id) {
        medicoService.excluir(id);
        return ResponseEntity.noContent().build();
    }


    @GetMapping("/{id}")
    public ResponseEntity<DadosDetalhamentoMedico> detalharMedicos(@PathVariable Long id) {
        DadosDetalhamentoMedico detalhesMedico = medicoService.detalhar(id);
        return ResponseEntity.ok(detalhesMedico); // A Controller é responsável por formatar a resposta HTTP.
    }

}
