package me.radek203.apigateway.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

public class ResourceInvalidException extends RuntimeException {

    static final String ERROR_CODE = "RESOURCE_INVALID";
    static final HttpStatus HTTP_STATUS = HttpStatus.BAD_REQUEST;

    @Getter
    private String data = "";

    public ResourceInvalidException(final String message) {
        super(message);
    }

    public ResourceInvalidException(final String message, final String data) {
        super(message);
        this.data = data;
    }

}
