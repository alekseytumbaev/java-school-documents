package ru.javaschool.documents.repository.entity;

public enum StatusCode {
    NEW("Новый"),
    IN_PROCESS("В обработке"),
    ACCEPTED("Принят"),
    REJECTED("Отклонен");

    /**
     * Название статуса, которе будет выведено пользователю
     */
    private final String displayName;

    StatusCode(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }
}
