package med.voll.api.controller;

import med.voll.api.dtos.DadosAutenticacao;
import med.voll.api.entities.Usuario;
import med.voll.api.infra.security.DadosTokenJWT;
import med.voll.api.infra.security.TokenService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentMatchers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AutenticacaoControllerTest {

    @Mock
    AuthenticationManager authenticationManager;

    @Mock
    TokenService tokenService;

    @InjectMocks
    AutenticacaoController controller;

    @Test
    void deveRetornarOkComTokenQuandoAutenticacaoForSucesso() {
        // arrange
        DadosAutenticacao dados = new DadosAutenticacao("usuario", "senha");
        Authentication authentication = mock(Authentication.class);
        Usuario usuario = mock(Usuario.class);
        when(authenticationManager.authenticate(ArgumentMatchers.any(UsernamePasswordAuthenticationToken.class)))
                .thenReturn(authentication);
        when(authentication.getPrincipal()).thenReturn(usuario);
        when(tokenService.gerarToken(usuario)).thenReturn("meu-token");

        // act
        ResponseEntity<?> response = controller.efetuarLogin(dados);

        // assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertTrue(response.getBody() instanceof DadosTokenJWT);
        DadosTokenJWT body = (DadosTokenJWT) response.getBody();
        assertEquals("meu-token", body.token());
        verify(authenticationManager).authenticate(ArgumentMatchers.any(UsernamePasswordAuthenticationToken.class));
        verify(tokenService).gerarToken(usuario);
    }

    @Test
    void deveRetornarBadRequestQuandoAutenticacaoFalhar() {
        // arrange
        DadosAutenticacao dados = new DadosAutenticacao("usuario", "senha");
        when(authenticationManager.authenticate(ArgumentMatchers.any(UsernamePasswordAuthenticationToken.class)))
                .thenThrow(new RuntimeException("Credenciais inválidas"));

        // act
        ResponseEntity<?> response = controller.efetuarLogin(dados);

        // assert
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals("Credenciais inválidas", response.getBody());
        verify(authenticationManager).authenticate(ArgumentMatchers.any(UsernamePasswordAuthenticationToken.class));
        verifyNoInteractions(tokenService);
    }
}