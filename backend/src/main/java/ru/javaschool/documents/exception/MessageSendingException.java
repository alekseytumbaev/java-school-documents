package ru.javaschool.documents.exception;

/**
 * Выбрасывается, когда произошла ошибка при отправке сообщения в кафку
 */
public class MessageSendingException extends RuntimeException {
    public MessageSendingException(String message, Throwable cause) {
        super(message, cause);
    }
}
