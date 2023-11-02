package ru.javaschool.documents.exception;

/**
 * Выбрасывается, если не удается сохранить сообщение в таблицу исходящих
 */
public class OutboxSavingException extends RuntimeException {
    public OutboxSavingException(String message, Throwable cause) {
        super(message, cause);
    }
}
