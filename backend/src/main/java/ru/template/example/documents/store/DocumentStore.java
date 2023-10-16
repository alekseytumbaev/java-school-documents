package ru.template.example.documents.store;

import ru.template.example.documents.controller.dto.DocumentDto;
import ru.template.example.documents.controller.dto.Status;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;

/**
 * Хранилище документов. Заменить на базу данных
 */
public class DocumentStore {
    private static DocumentStore INSTANCE;
    private static List<DocumentDto> documentDtos = new ArrayList<>();

    private DocumentStore() {
        documentDtos.addAll(createBaseList());
    }

    private Collection<? extends DocumentDto> createBaseList() {
        List<DocumentDto> dtos = new ArrayList<>();
        dtos.add(DocumentDto.builder()
                .id(223L)
                .type("Направление на медико-социальную экспертизу организацией, оказывающей медицинскую помощь")
                .organization("ОГБУЗ Саянская городская больница")
                .date(new Date())
                .patient("Иванов Иван Иванович")
                .description("Направление")
                .status(Status.of("NEW", "Новый"))
                .build());
        dtos.add(DocumentDto.builder()
                .id(224L)
                .type("Направление на медико-социальную экспертизу организацией, оказывающей медицинскую помощь")
                .organization("ОГБУЗ Саянская городская больница")
                .date(new Date())
                .patient("Иванов Иван Михайлович")
                .description("Направление")
                .status(Status.of("NEW", "Новый"))
                .build());
        dtos.add(DocumentDto.builder()
                .id(225L)
                .type("Направление на медико-социальную экспертизу организацией, оказывающей медицинскую помощь")
                .organization("ОГБУЗ Саянская городская больница")
                .date(new Date())
                .patient("Иванов Иван Андреевич")
                .description("Направление")
                .status(Status.of("IN_PROCESS", "В обработке"))
                .build());
        return dtos;
    }

    public static DocumentStore getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new DocumentStore();
        }
        return INSTANCE;
    }

    public List<DocumentDto> getDocumentDtos() {
        return documentDtos;
    }
}
