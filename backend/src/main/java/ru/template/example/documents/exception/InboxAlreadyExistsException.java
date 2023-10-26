package ru.template.example.documents.exception;

/**
 * Выбрасывается, если сообщение в таблице входящих уже существует
 */
public class InboxAlreadyExistsException extends RuntimeException {
    public InboxAlreadyExistsException(String message) {
        super(message);
    }
}
