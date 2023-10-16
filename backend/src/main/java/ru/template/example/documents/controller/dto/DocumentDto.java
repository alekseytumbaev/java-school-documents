package ru.template.example.documents.controller.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DocumentDto {
    /**
     * Номер
     */
    private Long id;
    /**
     * Вид документа
     */
    private String type;
    /**
     * Организация
     */
    private String organization;
    /**
     * Описание
     */
    private String description;
    /**
     * Пациент
     */
    private String patient;
    /**
     * Дата документа
     */
    private Date date;
    /**
     * Статус
     */
    private Status status;

}
