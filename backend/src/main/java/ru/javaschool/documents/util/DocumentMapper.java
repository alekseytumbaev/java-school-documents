package ru.javaschool.documents.util;

import ma.glasnost.orika.CustomMapper;
import ma.glasnost.orika.MapperFactory;
import ma.glasnost.orika.MappingContext;
import ma.glasnost.orika.impl.ConfigurableMapper;
import org.springframework.stereotype.Component;
import ru.javaschool.documents.controller.dto.DocumentDto;
import ru.javaschool.documents.controller.dto.Status;
import ru.javaschool.documents.exception.InvalidDocumentStatusCodeException;
import ru.javaschool.documents.repository.entity.Document;
import ru.javaschool.documents.repository.entity.StatusCode;

/**
 * Класс для маппинга {@link Document} и {@link DocumentDto}.
 * Если при маппинге в {@link Document} будет указан неверный статус, будет выброшено {@link InvalidDocumentStatusCodeException}
 */
@Component
public class DocumentMapper extends ConfigurableMapper {

    @Override
    protected void configure(MapperFactory factory) {
        factory.classMap(DocumentDto.class, Document.class)
                .exclude("status")
                .exclude("statusCode")
                .customize(new CustomMapper<>() {
                    @Override
                    public void mapAtoB(DocumentDto documentDto, Document document, MappingContext context) {
                        StatusCode status;
                        try {
                            status = StatusCode.valueOf(documentDto.getStatus().getCode());
                        } catch (IllegalArgumentException e) {
                            throw new InvalidDocumentStatusCodeException("Cannot map dto to document." +
                                    "Invalid status code: " + documentDto.getStatus().getCode());
                        }
                        document.setStatus(status);
                    }

                    @Override
                    public void mapBtoA(Document document, DocumentDto documentDto, MappingContext context) {
                        Status status = Status.of(document.getStatus().name(), document.getStatus().getDisplayName());
                        documentDto.setStatus(status);
                    }
                })
                .byDefault()
                .register();
    }
}
