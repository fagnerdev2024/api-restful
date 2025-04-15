package med.voll.api.infra.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.time.Instant;

@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(DatabaseException.class)
    public ResponseEntity<ErroResposta> handleDatabaseException(DatabaseException ex) {
        ErroResposta erro = new ErroResposta(HttpStatus.INTERNAL_SERVER_ERROR.value(), ex.getMessage(), Instant.now().toEpochMilli());
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(erro);
    }

    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ErroResposta> handleResourceNotFoundException(ResourceNotFoundException ex) {
        ErroResposta erro = new ErroResposta(HttpStatus.NOT_FOUND.value(), ex.getMessage(), Instant.now().toEpochMilli());
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(erro);
    }

    @ExceptionHandler(IllegalStateException.class)
    public ResponseEntity<ErroResposta> handleIllegalStateException(IllegalStateException ex) {
        ErroResposta erro = new ErroResposta(HttpStatus.BAD_REQUEST.value(), ex.getMessage(), Instant.now().toEpochMilli());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(erro);
    }
}
