package ru.javaschool.documents.controller;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import ru.javaschool.controller.AbstractWebMvcTest;
import ru.javaschool.documents.controller.dto.DocumentDto;
import ru.javaschool.documents.controller.dto.IdDto;
import ru.javaschool.documents.controller.dto.IdsDto;
import ru.javaschool.documents.controller.dto.Status;
import ru.javaschool.documents.exception.DocumentNotFoundException;
import ru.javaschool.documents.exception.IllegalDocumentStatusException;
import ru.javaschool.documents.repository.entity.StatusCode;
import ru.javaschool.documents.service.DocumentServiceImpl;

import java.util.Date;
import java.util.List;
import java.util.Set;

import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(DocumentController.class)
public class DocumentControllerTest extends AbstractWebMvcTest {

    private static final String ROUTE = "/documents";
    private static final String SEND_ROUTE = ROUTE + "/send";

    @MockBean
    private DocumentServiceImpl service;

    @Test
    public void testShouldSaveDocument() throws Exception {
        DocumentDto documentDto = new DocumentDto(
                1L,
                "type",
                "organization",
                "description",
                "patient",
                new Date(),
                Status.of(StatusCode.NEW.name(), StatusCode.NEW.getDisplayName())
        );

        when(service.save(documentDto)).thenReturn(documentDto);

        mvc.perform(postAction(ROUTE, documentDto))
                .andExpect(status().isOk())
                .andExpect(content().json(mapper.writeValueAsString(documentDto)));
    }

    @Test
    public void testShouldGetAllDocuments() throws Exception {
        DocumentDto documentDto = new DocumentDto(
                1L,
                "type",
                "organization",
                "description",
                "patient",
                new Date(),
                Status.of(StatusCode.NEW.name(), StatusCode.NEW.getDisplayName())
        );

        when(service.findAll()).thenReturn(List.of(documentDto));

        mvc.perform(get(ROUTE))
                .andExpect(status().isOk())
                .andExpect(content().json(mapper.writeValueAsString(List.of(documentDto))));
    }

    @Test
    public void testSend() throws Exception {
        DocumentDto documentDto = new DocumentDto(
                1L,
                "type",
                "organization",
                "description",
                "patient",
                new Date(),
                Status.of(StatusCode.NEW.name(), StatusCode.NEW.getDisplayName())
        );

        IdDto idDto = new IdDto(1L);

        when(service.sendForProcessing(idDto)).thenReturn(documentDto);

        mvc.perform(postAction(SEND_ROUTE, idDto))
                .andExpect(status().isOk())
                .andExpect(content().json(mapper.writeValueAsString(documentDto)));
    }

    @Test
    public void testStatusShouldBeNotFoundWhenSendingIdOfNoneExistentDocument() throws Exception {
        IdDto idDto = new IdDto(-1L);

        when(service.sendForProcessing(idDto))
                .thenThrow(new DocumentNotFoundException("Document not found"));

        mvc.perform(postAction(SEND_ROUTE, idDto)).andExpect(status().isNotFound());
    }

    @Test
    public void testStatusShouldBeBadRequestWhenDocumentStatusIsNotNew() throws Exception {
        IdDto idDto = new IdDto(1L);

        when(service.sendForProcessing(idDto))
                .thenThrow(new IllegalDocumentStatusException("Document status is not NEW"));

        mvc.perform(postAction(SEND_ROUTE, idDto)).andExpect(status().isBadRequest());
    }

    @Test
    public void testDelete() throws Exception {
        Long id = 1L;
        doNothing().when(service).delete(id);
        mvc.perform(delete(ROUTE + "/{id}", id)).andExpect(status().isOk());
    }

    @Test
    public void testDeleteAll() throws Exception {
        IdsDto idsDto = new IdsDto();
        idsDto.setIds(Set.of(1L, 2L));
        doNothing().when(service).deleteAll(idsDto.getIds());
        mvc.perform(deleteAction(ROUTE, idsDto)).andExpect(status().isOk());
    }
}
