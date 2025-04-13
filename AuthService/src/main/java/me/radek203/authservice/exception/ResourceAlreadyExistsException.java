package me.radek203.authservice.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

public class ResourceAlreadyExistsException extends RuntimeException {

    static final String ERROR_CODE = "RESOURCE_ALREADY_EXISTS";
    static final HttpStatus HTTP_STATUS = HttpStatus.CONFLICT;

    @Getter
    private String data = "";

    public ResourceAlreadyExistsException(final String message) {
        super(message);
    }

    public ResourceAlreadyExistsException(final String message, final String data) {
        super(message);
        this.data = data;
    }

}
