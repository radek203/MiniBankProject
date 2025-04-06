package me.radek203.headquarterservice.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.CONFLICT)
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
