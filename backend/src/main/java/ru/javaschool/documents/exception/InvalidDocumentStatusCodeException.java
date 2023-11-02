package ru.javaschool.documents.exception;

import ru.javaschool.documents.repository.entity.StatusCode;

/**
 * Выбрасывается, если передан несуществующий код документа
 * @see StatusCode
 */
public class InvalidDocumentStatusCodeException extends RuntimeException {
    public InvalidDocumentStatusCodeException(String message) {
        super(message);
    }
}
