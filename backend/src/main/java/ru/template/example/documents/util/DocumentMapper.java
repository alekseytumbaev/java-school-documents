package ru.template.example.documents.util;

import lombok.experimental.UtilityClass;
import ru.template.example.documents.controller.dto.DocumentDto;
import ru.template.example.documents.controller.dto.Status;
import ru.template.example.documents.exception.InvalidDocumentStatusCodeException;
import ru.template.example.documents.repository.entity.Document;
import ru.template.example.documents.repository.entity.StatusCode;

/**
 * Класс для маппинга {@link Document} и {@link DocumentDto}
 */
@UtilityClass
public class DocumentMapper {
    public Document toEntity(DocumentDto documentDto) {
        StatusCode status;
        try {
            status = StatusCode.valueOf(documentDto.getStatus().getCode());
        } catch (IllegalArgumentException e) {
            throw new InvalidDocumentStatusCodeException("Invalid status code: " + documentDto.getStatus().getCode());
        }

        return new Document(
                documentDto.getId(),
                documentDto.getType(),
                documentDto.getOrganization(),
                documentDto.getDescription(),
                documentDto.getDate(),
                documentDto.getPatient(),
                status
        );
    }

    public DocumentDto toDto(Document document, String statusName) {
        return DocumentDto.builder()
                .id(document.getId())
                .type(document.getType())
                .organization(document.getOrganization())
                .description(document.getDescription())
                .date(document.getDate())
                .patient(document.getPatient())
                .status(Status.of(document.getStatus().name(), statusName))
                .build();
    }
}
