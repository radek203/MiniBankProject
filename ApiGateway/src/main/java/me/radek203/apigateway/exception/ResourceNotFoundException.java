package me.radek203.apigateway.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

public class ResourceNotFoundException extends RuntimeException {

    static final String ERROR_CODE = "RESOURCE_NOT_FOUND";
    static final HttpStatus HTTP_STATUS = HttpStatus.NOT_FOUND;

    @Getter
    private String data = "";

    public ResourceNotFoundException(final String message) {
        super(message);
    }

    public ResourceNotFoundException(final String message, final String data) {
        super(message);
        this.data = data;
    }

    public ResourceNotFoundException(final String message, final Integer data) {
        this(message, String.valueOf(data));
    }
}
