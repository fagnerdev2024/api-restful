package med.voll.api.infra.exceptions;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class ExceptionHandlerAdviceTest {

    // Instância da classe a ser testada
    private final ExceptionHandlerAdvice exceptionHandlerAdvice = new ExceptionHandlerAdvice();

    @Test
    @DisplayName("Deve retornar status 400 BAD REQUEST e o corpo de erro correto ao lidar com ValidacaoException")
    void handleValidacaoExceptionCenario1() {
        // ARRANGE
        // 1. Define a mensagem de erro que será encapsulada na exceção
        String mensagemDeErro = "O campo 'data' é obrigatório e não pode ser nulo.";

        // 2. Cria a instância da exceção
        ValidacaoException validacaoException = new ValidacaoException(mensagemDeErro);

        // ACT
        // 3. Chama o método a ser testado
        ResponseEntity<Map<String, String>> resposta =
                exceptionHandlerAdvice.handleValidacaoException(validacaoException);

        // ASSERT
        // 4. Verifica se o status HTTP é o esperado (400 Bad Request)
        assertEquals(HttpStatus.BAD_REQUEST, resposta.getStatusCode(),
                "O status HTTP deve ser 400 BAD REQUEST.");

        // 5. Verifica se o corpo da resposta não é nulo
        assertNotNull(resposta.getBody(), "O corpo da resposta não deve ser nulo.");

        // 6. Verifica se o corpo contém a chave "error"
        assertNotNull(resposta.getBody().get("error"),
                "O corpo da resposta deve conter a chave 'error'.");

        // 7. Verifica se a mensagem de erro no corpo é a mesma da exceção
        assertEquals(mensagemDeErro, resposta.getBody().get("error"),
                "A mensagem de erro no corpo deve ser a mesma da exceção.");
    }
}