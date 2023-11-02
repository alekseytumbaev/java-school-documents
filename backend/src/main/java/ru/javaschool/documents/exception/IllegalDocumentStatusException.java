package ru.javaschool.documents.exception;

/**
 * Выбрасывается, если указан неверный статус документа
 */
public class IllegalDocumentStatusException extends RuntimeException {
    public IllegalDocumentStatusException(String message) {
        super(message);
    }
}
