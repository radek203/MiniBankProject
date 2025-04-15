package me.radek203.branchservice.exception;

import lombok.Getter;
import org.springframework.http.HttpStatusCode;

@Getter
public class ClientException extends RuntimeException {

    private final ErrorDetails errorDetails;
    private final HttpStatusCode code;

    public ClientException(ErrorDetails errorDetails, HttpStatusCode code) {
        super();
        this.errorDetails = errorDetails;
        this.code = code;
    }
}
