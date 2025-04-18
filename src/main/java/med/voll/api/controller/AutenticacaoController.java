package med.voll.api.controller;

import jakarta.validation.Valid;
import med.voll.api.dtos.DadosAutenticacao;
import med.voll.api.entities.Usuario;
import med.voll.api.infra.security.DadosTokenJWT;
import med.voll.api.infra.security.TokenService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/login")
public class AutenticacaoController {

    private final AuthenticationManager authenticationManager;
    private final TokenService tokenService;


    public AutenticacaoController(AuthenticationManager authenticationManager, TokenService tokenService) {
        this.authenticationManager = authenticationManager;
        this.tokenService = tokenService;
    }

    @PostMapping
    public ResponseEntity<?> efetuarLogin(@RequestBody @Valid DadosAutenticacao dadosAutenticacao) {
        try {
            var authenticationToken = new UsernamePasswordAuthenticationToken(
                    dadosAutenticacao.login(), dadosAutenticacao.senha()
            );
            var authentication = authenticationManager.authenticate(authenticationToken);
            var usuario = (Usuario) authentication.getPrincipal();
            var tokenJWT = tokenService.gerarToken(usuario);

            return ResponseEntity.ok(new DadosTokenJWT(tokenJWT));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
}