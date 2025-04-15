package me.radek203.headquarterservice.exception;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Global exception handler for the application.
 * This class handles various exceptions that may occur during the execution of the application.
 * It provides a centralized way to handle exceptions and return appropriate error responses.
 */
@ControllerAdvice
public class GlobalExceptionHandler extends ResponseEntityExceptionHandler {

    @ExceptionHandler(ResourceNotFoundException.class)
    public final ResponseEntity<ErrorDetails> handleResourceNotFoundException(final ResourceNotFoundException exception, final WebRequest webRequest) {
        final ErrorDetails errorDetails = new ErrorDetails(LocalDateTime.now(), exception.getMessage(), exception.getData(), webRequest.getDescription(false), ResourceNotFoundException.ERROR_CODE);

        return new ResponseEntity<>(errorDetails, ResourceNotFoundException.HTTP_STATUS);
    }

    @ExceptionHandler(ResourceAlreadyExistsException.class)
    public final ResponseEntity<ErrorDetails> handleResourceAlreadyExistsException(final ResourceAlreadyExistsException exception, final WebRequest webRequest) {
        final ErrorDetails errorDetails = new ErrorDetails(LocalDateTime.now(), exception.getMessage(), exception.getData(), webRequest.getDescription(false), ResourceAlreadyExistsException.ERROR_CODE);

        return new ResponseEntity<>(errorDetails, ResourceAlreadyExistsException.HTTP_STATUS);
    }

    @ExceptionHandler(ResourceInvalidException.class)
    public final ResponseEntity<ErrorDetails> handleResourceInvalidException(final ResourceInvalidException exception, final WebRequest webRequest) {
        final ErrorDetails errorDetails = new ErrorDetails(LocalDateTime.now(), exception.getMessage(), exception.getData(), webRequest.getDescription(false), ResourceInvalidException.ERROR_CODE);

        return new ResponseEntity<>(errorDetails, ResourceInvalidException.HTTP_STATUS);
    }

    @ExceptionHandler(ClientException.class)
    public final ResponseEntity<ErrorDetails> handleClientException(final ClientException exception, final WebRequest webRequest) {
        return new ResponseEntity<>(exception.getErrorDetails(), exception.getCode());
    }

    /**
     * Handles MethodArgumentNotValidException.
     *
     * @param ex      the exception
     * @param headers the headers
     * @param status  the status
     * @param request the web request
     * @return the response entity
     */
    @Override
    protected ResponseEntity<Object> handleMethodArgumentNotValid(final MethodArgumentNotValidException ex, final HttpHeaders headers, final HttpStatusCode status, final WebRequest request) {
        final StringBuilder errorMessage = new StringBuilder();
        final List<ObjectError> errorList = ex.getBindingResult().getAllErrors();
        errorList.forEach((error) -> {
            final String message = error.getDefaultMessage();
            errorMessage.append(message).append(";");
        });
        final String message = errorMessage.toString();
        final String trimmedMessage = !message.isEmpty() ? message.substring(0, message.length() - 1) : message;

        final ErrorDetails errorDetails = new ErrorDetails(LocalDateTime.now(), trimmedMessage, "", request.getDescription(false), ResourceInvalidException.ERROR_CODE);

        return new ResponseEntity<>(errorDetails, HttpStatus.BAD_REQUEST);
    }
}
