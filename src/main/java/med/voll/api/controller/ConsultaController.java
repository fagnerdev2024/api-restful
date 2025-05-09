package med.voll.api.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid;
import med.voll.api.services.ConsultasService;
import med.voll.api.dtos.DadosAgendamentoConsulta;
import med.voll.api.dtos.DadosCancelamentoConsulta;
import med.voll.api.dtos.DadosDetalhamentoConsulta;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/consultas")
@SecurityRequirement(name = "bearer-key")
public class ConsultaController {

    private final ConsultasService consultasService;

    public ConsultaController(ConsultasService consultasService) {
        this.consultasService = consultasService;
    }

    @PostMapping
    @Operation(summary = "Agenda uma nova consulta")
    public ResponseEntity<DadosDetalhamentoConsulta> agendar(@RequestBody @Valid DadosAgendamentoConsulta dadosAgendamentoConsulta) {
        var dto = consultasService.agendar(dadosAgendamentoConsulta);
        return ResponseEntity.ok(dto);
    }

    @DeleteMapping
    @Operation(summary = "Cancela uma consulta existente")
    public ResponseEntity<Void> cancelar(@RequestBody @Valid DadosCancelamentoConsulta dadosCancelamentoConsulta) {
        consultasService.cancelar(dadosCancelamentoConsulta);
        return ResponseEntity.noContent().build();
    }


    @GetMapping
    @Operation(summary = "Lista todas as consultas agendadas")
    public ResponseEntity<List<DadosDetalhamentoConsulta>> listar() {
        var consultas = consultasService.listarTodas();
        return ResponseEntity.ok(consultas);
    }
}
