package ru.template.example.configuration;

import lombok.Data;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.FieldError;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;
import ru.template.example.documents.exception.DocumentNotFoundException;
import ru.template.example.documents.exception.InvalidDocumentStatusCodeException;
import ru.template.example.documents.exception.NullDocumentIdException;

import java.util.ArrayList;
import java.util.List;

import static org.springframework.http.HttpStatus.BAD_REQUEST;
import static org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR;
import static org.springframework.http.HttpStatus.NOT_FOUND;

@ControllerAdvice
public class CustomRestExceptionHandler extends ResponseEntityExceptionHandler {

    /**
     * Обработчик исключения, при котором id документа должен был быть указан, но был равен null
     */
    @ExceptionHandler({NullDocumentIdException.class})
    public ResponseEntity<RestApiError> handleNullDocumentIdException(final NullDocumentIdException ex) {
        logger.error("Null document id", ex);
        RestApiError restApiError = new RestApiError("Null document id", List.of(ex.getLocalizedMessage()));
        return new ResponseEntity<>(restApiError, new HttpHeaders(), BAD_REQUEST);
    }

    /**
     * Обработчик ошибки при ненайденном в базе документе
     */
    @ExceptionHandler({DocumentNotFoundException.class})
    public ResponseEntity<RestApiError> handleDocumentNotFoundException(final DocumentNotFoundException ex) {
        logger.error("Document not found", ex);
        RestApiError restApiError = new RestApiError("Document not found", List.of(ex.getLocalizedMessage()));
        return new ResponseEntity<>(restApiError, new HttpHeaders(), NOT_FOUND);
    }

    /**
     * Обработчик неверного кода статуса документа
     */
    @ExceptionHandler({InvalidDocumentStatusCodeException.class})
    public ResponseEntity<RestApiError> handleInvalidDocumentStatusCodeException(final InvalidDocumentStatusCodeException ex) {
        logger.error("Invalid document status code", ex);
        RestApiError restApiError = new RestApiError("Invalid document status code", List.of(ex.getLocalizedMessage()));
        return new ResponseEntity<>(restApiError, new HttpHeaders(), BAD_REQUEST);
    }

    /**
     * 400
     */
    @Override
    protected ResponseEntity<Object> handleMethodArgumentNotValid(final MethodArgumentNotValidException ex,
                                                                  final HttpHeaders headers, final HttpStatus status,
                                                                  final WebRequest request) {
        logger.error("Validation error", ex);

        List<String> errors = new ArrayList<>();
        for (final FieldError error : ex.getBindingResult().getFieldErrors()) {
            errors.add(error.getField() + ": " + error.getDefaultMessage());
        }
        for (final ObjectError error : ex.getBindingResult().getGlobalErrors()) {
            errors.add(error.getObjectName() + ": " + error.getDefaultMessage());
        }
        RestApiError restApiError = new RestApiError("Validation failed", errors);
        return handleExceptionInternal(ex, restApiError, headers, BAD_REQUEST, request);
    }

    @Override
    protected ResponseEntity<Object> handleHttpMessageNotReadable(HttpMessageNotReadableException ex,
                                                                  HttpHeaders headers, HttpStatus status,
                                                                  WebRequest request) {
        logger.error("Http message is not readable", ex);
        List<String> errors = List.of(ex.getRootCause() == null ? ex.getMessage() : ex.getRootCause().getMessage());
        RestApiError restApiError = new RestApiError("Http message is not readable", errors);
        return handleExceptionInternal(ex, restApiError, headers, BAD_REQUEST, request);
    }

    /**
     * 500
     */
    @ExceptionHandler({Exception.class})
    public ResponseEntity<RestApiError> handleAll(final Exception ex) {
        logger.error("Internal server error", ex);
        RestApiError restApiError = new RestApiError("Internal server error", List.of(ex.getLocalizedMessage()));
        return new ResponseEntity<>(restApiError, new HttpHeaders(), INTERNAL_SERVER_ERROR);
    }

    @Data
    public static class RestApiError {

        private String message;

        private List<String> errors;

        public RestApiError(String message, List<String> errors) {
            this.message = message;
            this.errors = errors;
        }
    }
}
