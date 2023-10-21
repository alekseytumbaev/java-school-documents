package ru.template.example.documents.exception;

/**
 * Выбрасывается, если передан несуществующий код документа
 * @see ru.template.example.documents.repository.entity.StatusCode
 */
public class InvalidDocumentStatusCodeException extends RuntimeException {
    public InvalidDocumentStatusCodeException(String message) {
        super(message);
    }
}
