package ru.template.example.documents.exception;

/**
 * Выбрасывается, если id документа было null, хотя не должно было присутствовать
 */
public class NullDocumentIdException extends RuntimeException {
    public NullDocumentIdException(String message) {
        super(message);
    }
}
