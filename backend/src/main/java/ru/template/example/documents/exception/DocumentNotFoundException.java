package ru.template.example.documents.exception;

/**
 * Выбрасывается если документ не найден в базе
 */
public class DocumentNotFoundException extends RuntimeException {
    public DocumentNotFoundException(String message) {
        super(message);
    }
}
