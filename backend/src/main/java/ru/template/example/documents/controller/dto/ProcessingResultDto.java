package ru.template.example.documents.controller.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.io.Serial;
import java.io.Serializable;

/**
 * Результат обработки документа из кафки
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProcessingResultDto implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @NotNull
    private Long documentId;

    @NotBlank
    private String statusCode;
}
