package ru.javaschool.documents.service;

import org.junit.jupiter.api.Test;
import ru.javaschool.documents.controller.dto.ProcessingResultDto;
import ru.javaschool.documents.exception.InboxAlreadyExistsException;
import ru.javaschool.documents.repository.InboxRepository;
import ru.javaschool.documents.repository.entity.Inbox;
import ru.javaschool.documents.repository.entity.StatusCode;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.internal.verification.VerificationModeFactory.times;

class InboxServiceTest {

    private final InboxRepository inboxRepo = mock(InboxRepository.class);
    private final InboxService inboxService = new InboxService(inboxRepo);


    @Test
    public void testShouldAddInboxIfNotExistsById() {
        Inbox inbox = new Inbox(1L, new ProcessingResultDto(1L, StatusCode.ACCEPTED.name()));

        when(inboxRepo.existsById(inbox.getId())).thenReturn(false);
        when(inboxRepo.save(inbox)).thenReturn(inbox);

        assertEquals(inbox, inboxService.addIfNotExistsById(inbox));
        verify(inboxRepo, times(1)).save(inbox);
    }

    @Test
    public void testShouldThrowExceptionWhenAddingInboxIfAlreadyExistsByID() {
        Inbox inbox = new Inbox(1L, new ProcessingResultDto(1L, StatusCode.ACCEPTED.name()));

        when(inboxRepo.existsById(inbox.getId())).thenReturn(true);

        assertThrows(InboxAlreadyExistsException.class, () -> inboxService.addIfNotExistsById(inbox));
        verify(inboxRepo, times(1)).existsById(inbox.getId());
        verify(inboxRepo, times(0)).save(inbox);
    }

    @Test
    public void testShouldMarkAsRead() {
        Inbox inbox = new Inbox(1L, new ProcessingResultDto(1L, StatusCode.ACCEPTED.name()));
        Inbox inbox2 = new Inbox(2L, new ProcessingResultDto(2L, StatusCode.ACCEPTED.name()));
        List<Inbox> inboxes = Arrays.asList(inbox, inbox2);
        List<Long> ids = List.of(inbox.getId(), inbox2.getId());

        when(inboxRepo.findAllByIdIn(ids)).thenReturn(inboxes);
        when(inboxRepo.saveAll(any())).then(invocation -> invocation.getArguments()[0]);

        List<Inbox> readInboxes = inboxService.markAsRead(ids);
        assertEquals(2, readInboxes.size());
        assertTrue(readInboxes.get(0).isRead());
        assertTrue(readInboxes.get(1).isRead());
        verify(inboxRepo, times(1)).findAllByIdIn(ids);
        verify(inboxRepo, times(1)).saveAll(any());
    }
}