package ru.javaschool.documents.controller.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.Length;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
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
    @NotNull
    @Length(max = 255)
    private String type;

    /**
     * Организация
     */
    @NotNull
    @Length(max = 128)
    private String organization;

    /**
     * Описание
     */
    @NotNull
    @Length(max = 512)
    private String description;

    /**
     * Пациент
     */
    @NotNull
    @Length(max = 128)
    private String patient;

    /**
     * Дата создания документа
     */
    @NotNull
    private Date date;

    /**
     * Статус
     */
    @Valid
    private Status status;

}
