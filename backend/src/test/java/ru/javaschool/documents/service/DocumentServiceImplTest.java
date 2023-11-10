package ru.javaschool.documents.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.javaschool.documents.controller.dto.DocumentDto;
import ru.javaschool.documents.controller.dto.IdDto;
import ru.javaschool.documents.controller.dto.ProcessingResultDto;
import ru.javaschool.documents.controller.dto.Status;
import ru.javaschool.documents.exception.DocumentNotFoundException;
import ru.javaschool.documents.exception.IllegalDocumentStatusException;
import ru.javaschool.documents.repository.DocumentRepository;
import ru.javaschool.documents.repository.entity.Document;
import ru.javaschool.documents.repository.entity.Inbox;
import ru.javaschool.documents.repository.entity.Outbox;
import ru.javaschool.documents.repository.entity.StatusCode;
import ru.javaschool.documents.util.DocumentMapper;

import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class DocumentServiceImplTest {

    private DocumentRepository documentRepo;
    private OutboxService outboxService;
    private InboxService inboxService;

    private DocumentServiceImpl documentService;

    private DocumentMapper documentMapper;

    @BeforeEach
    public void setUp() {
        documentRepo = mock(DocumentRepository.class);
        outboxService = mock(OutboxService.class);
        inboxService = mock(InboxService.class);
        documentMapper = new DocumentMapper();
        documentService = new DocumentServiceImpl(documentRepo,
                outboxService,
                inboxService,
                documentMapper);
    }

    @Test
    public void testShouldSaveDocumentWithCurrentDateAndStatusNewAndReturnDto() {
        DocumentDto documentDto = new DocumentDto(
                null,
                "type",
                "organization",
                "description",
                "patient",
                new Date(3L),
                Status.of("random code", "random name")
        );

        when(documentRepo.save(any(Document.class)))
                .then(invocation -> {
                    Document document = (Document) invocation.getArguments()[0];
                    document.setId(1L);
                    return document;
                });

        DocumentDto savedDto = documentService.save(documentDto);

        verify(documentRepo, times(1)).save(any());
        assertEquals(documentDto.getType(), savedDto.getType(), "Type should be equal");
        assertEquals(documentDto.getOrganization(), savedDto.getOrganization(), "Organization should be equal");
        assertEquals(documentDto.getDescription(), savedDto.getDescription(), "Description should be equal");
        assertEquals(documentDto.getPatient(), savedDto.getPatient(), "Patient should be equal");

        Status status = Status.of(StatusCode.NEW.name(), StatusCode.NEW.getDisplayName());
        assertEquals(status, savedDto.getStatus());
        long oneMinute = 1000 * 60;
        assertTrue(calculateDifferenceInMillis(savedDto.getDate(), new Date()) < oneMinute,
                "Saved document should have current date");
    }

    private long calculateDifferenceInMillis(Date date1, Date date2) {
        return Math.abs(date1.getTime() - date2.getTime());
    }

    @Test
    public void testShouldChangeStatusWhenSentForProcessing() {
        long id = 1L;
        DocumentDto documentDto = new DocumentDto(
                id,
                "type",
                "organization",
                "description",
                "patient",
                new Date(),
                Status.of(StatusCode.NEW.name(), StatusCode.NEW.getDisplayName())
        );

        Document document = documentMapper.map(documentDto, Document.class);

        when(documentRepo.findById(documentDto.getId())).thenReturn(Optional.of(document));
        when(documentRepo.save(any(Document.class))).then(invocation -> invocation.getArguments()[0]);
        when(outboxService.addMessage(any(DocumentDto.class))).thenReturn(new Outbox(1L, "test"));
        DocumentDto sentForProcessingDto = documentService.sendForProcessing(new IdDto(id));

        verify(documentRepo, times(1)).findById(id);
        verify(documentRepo, times(1)).save(any());
        verify(outboxService, times(1)).addMessage(any(DocumentDto.class));
        assertEquals(
                Status.of(StatusCode.IN_PROCESS.name(), StatusCode.IN_PROCESS.getDisplayName()),
                sentForProcessingDto.getStatus(),
                "Status should be IN_PROCESS"
        );
        assertEquals(documentDto.getId(), sentForProcessingDto.getId(), "Id should be equal");
        assertEquals(documentDto.getType(), sentForProcessingDto.getType(), "Type should be equal");
        assertEquals(documentDto.getOrganization(), sentForProcessingDto.getOrganization(), "Organization should be equal");
        assertEquals(documentDto.getDescription(), sentForProcessingDto.getDescription(), "Description should be equal");
        assertEquals(documentDto.getPatient(), sentForProcessingDto.getPatient(), "Patient should be equal");
    }

    @Test
    public void testWhenNoneExistentDocumentIsSentForProcessingShouldThrowException() {
        DocumentDto documentDto = new DocumentDto(
                1L,
                "type",
                "organization",
                "description",
                "patient",
                new Date(),
                Status.of(StatusCode.NEW.name(), StatusCode.NEW.getDisplayName())
        );
        when(documentRepo.findById(documentDto.getId())).thenReturn(Optional.empty());

        IdDto idDto = new IdDto(documentDto.getId());
        assertThrows(DocumentNotFoundException.class, () -> documentService.sendForProcessing(idDto));
        verify(documentRepo, times(1)).findById(documentDto.getId());
    }

    @Test
    public void testWhenDocumentIsSentForProcessingTwiceShouldThrowException() {
        DocumentDto documentDto = new DocumentDto(
                1L,
                "type",
                "organization",
                "description",
                "patient",
                new Date(),
                Status.of(StatusCode.IN_PROCESS.name(), StatusCode.IN_PROCESS.getDisplayName())
        );
        when(documentRepo.findById(documentDto.getId()))
                .thenReturn(Optional.of(documentMapper.map(documentDto, Document.class)));

        IdDto idDto = new IdDto(documentDto.getId());
        assertThrows(IllegalDocumentStatusException.class, () -> documentService.sendForProcessing(idDto));
        verify(documentRepo, times(1)).findById(documentDto.getId());
    }

    @Test
    public void testReadInboxAndApplyProcessingResultsShouldWorkWithCorrectStatuses() {
        Inbox inbox = new Inbox(1L, new ProcessingResultDto(1L, StatusCode.ACCEPTED.name()));
        List<Inbox> inboxes = List.of(inbox);
        when(inboxService.getUnread()).thenReturn(inboxes);

        Document document = new Document(
                1L,
                "type",
                "organization",
                "description",
                new Date(),
                "patient",
                StatusCode.IN_PROCESS
        );
        List<Document> documents = Arrays.asList(document);
        when(documentRepo.findAllById(Set.of(document.getId()))).thenReturn(documents);

        when(documentRepo.saveAll(any())).then(invocation -> invocation.getArguments()[0]);
        when(inboxService.markAsRead(any())).thenReturn(inboxes);

        List<Document> documentsWithAppliedResults = documentService.readInboxAndApplyProcessingResult();

        assertEquals(1, documentsWithAppliedResults.size());
        assertEquals(StatusCode.ACCEPTED, documentsWithAppliedResults.get(0).getStatus());
        verify(documentRepo, times(1)).findAllById(any());
        verify(inboxService, times(1)).getUnread();
        verify(documentRepo, times(1)).saveAll(any());
        verify(inboxService, times(1)).markAsRead(any());
    }

    @Test
    public void testShouldNotApplyProcessingResultsWhenCurrentStatusIsNotInProcess() {
        Inbox inbox = new Inbox(1L, new ProcessingResultDto(1L, StatusCode.ACCEPTED.name()));
        List<Inbox> inboxes = List.of(inbox);
        when(inboxService.getUnread()).thenReturn(inboxes);

        Document document = new Document(
                1L,
                "type",
                "organization",
                "description",
                new Date(),
                "patient",
                StatusCode.REJECTED
        );
        List<Document> documents = Arrays.asList(document);
        when(documentRepo.findAllById(Set.of(document.getId()))).thenReturn(documents);

        when(documentRepo.saveAll(any())).then(invocation -> invocation.getArguments()[0]);
        when(inboxService.markAsRead(any())).thenReturn(inboxes);

        List<Document> documentsWithAppliedResults = documentService.readInboxAndApplyProcessingResult();

        assertEquals(1, documentsWithAppliedResults.size());
        assertEquals(StatusCode.REJECTED, documentsWithAppliedResults.get(0).getStatus());
        verify(documentRepo, times(1)).findAllById(any());
        verify(inboxService, times(1)).getUnread();
        verify(documentRepo, times(1)).saveAll(any());
        verify(inboxService, times(1)).markAsRead(any());
    }

    @Test
    public void testShouldNotApplyProcessingResultsWhenProvidedStatusIsIncorrect() {
        Inbox inbox = new Inbox(1L, new ProcessingResultDto(1L, StatusCode.NEW.name()));
        List<Inbox> inboxes = List.of(inbox);
        when(inboxService.getUnread()).thenReturn(inboxes);

        Document document = new Document(
                1L,
                "type",
                "organization",
                "description",
                new Date(),
                "patient",
                StatusCode.IN_PROCESS
        );
        List<Document> documents = Arrays.asList(document);
        when(documentRepo.findAllById(Set.of(document.getId()))).thenReturn(documents);

        when(documentRepo.saveAll(any())).then(invocation -> invocation.getArguments()[0]);
        when(inboxService.markAsRead(any())).thenReturn(inboxes);

        List<Document> documentsWithAppliedResults = documentService.readInboxAndApplyProcessingResult();

        assertEquals(1, documentsWithAppliedResults.size());
        assertEquals(StatusCode.IN_PROCESS, documentsWithAppliedResults.get(0).getStatus());
        verify(documentRepo, times(1)).findAllById(any());
        verify(inboxService, times(1)).getUnread();
        verify(documentRepo, times(1)).saveAll(any());
        verify(inboxService, times(1)).markAsRead(any());
    }

    @Test
    public void testShouldReturnDocumentWhenRetrievingExistingDocument() {
        DocumentDto documentDto = new DocumentDto(
                1L,
                "type",
                "organization",
                "description",
                "patient",
                new Date(),
                Status.of(StatusCode.NEW.name(), StatusCode.NEW.getDisplayName())
        );

        when(documentRepo.findById(documentDto.getId()))
                .thenReturn(Optional.of(documentMapper.map(documentDto, Document.class)));

        DocumentDto returnedDto = documentService.get(documentDto.getId());

        assertEquals(documentDto, returnedDto);
        verify(documentRepo, times(1)).findById(documentDto.getId());
    }

    @Test
    public void testShouldThrowExceptionWhenRetrievingNonExistingDocument() {
        when(documentRepo.findById(1L)).thenReturn(Optional.empty());
        assertThrows(DocumentNotFoundException.class, () -> documentService.get(1L));
        verify(documentRepo, times(1)).findById(1L);
    }
}