package med.voll.api.infra.exceptions;

import java.time.Instant;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;

public class ErroResposta {

    private int status;
    private String mensagem;
    private String timestamp;

    public ErroResposta(int status, String mensagem, long timestamp) {
        this.status = status;
        this.mensagem = mensagem;
        this.timestamp = DateTimeFormatter.ISO_INSTANT.withZone(ZoneOffset.UTC).format(Instant.ofEpochMilli(timestamp));
    }


    public int getStatus() {
        return status;
    }

    public String getMensagem() {
        return mensagem;
    }

    public String getTimestamp() {
        return timestamp;
    }
}

