package ru.javaschool.documents.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.javaschool.documents.controller.dto.ProcessingResultDto;
import ru.javaschool.documents.exception.InboxAlreadyExistsException;
import ru.javaschool.documents.repository.entity.Inbox;
import ru.javaschool.documents.repository.entity.StatusCode;

import javax.validation.ConstraintViolation;
import javax.validation.Validator;
import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.internal.verification.VerificationModeFactory.times;


public class KafkaInboxReceiverTest {

    private InboxService inboxService;
    private Validator validator;
    private KafkaInboxReceiver kafkaInboxReceiver;

    @BeforeEach
    public void setUp() {
        inboxService = mock(InboxService.class);
        validator = mock(Validator.class);
        kafkaInboxReceiver = new KafkaInboxReceiver(inboxService, validator);
    }

    @Test
    public void testReceiveNoneExistentInboxWithValidMessage() {
        Long key = 1L;
        ProcessingResultDto resultDto = new ProcessingResultDto(key, StatusCode.ACCEPTED.name());
        Inbox inbox = new Inbox(key, resultDto);

        when(validator.validate(resultDto)).thenReturn(Set.of());
        when(inboxService.addIfNotExistsById(inbox)).thenReturn(inbox);

        assertEquals(inbox, kafkaInboxReceiver.receiveInbox(resultDto, key).get());
        verify(inboxService, times(1)).addIfNotExistsById(inbox);
    }

    @Test
    public void testReceiveInboxInvalidMessageShouldReturnEmpty() {
        Long key = 1L;
        ProcessingResultDto resultDto = new ProcessingResultDto(key, "invalid");

        ConstraintViolation<ProcessingResultDto> constraintViolation = mock(ConstraintViolation.class);
        when(validator.validate(resultDto)).thenReturn(Set.of(constraintViolation));

        assertEquals(Optional.empty(), kafkaInboxReceiver.receiveInbox(resultDto, key));
        verify(inboxService, never()).addIfNotExistsById(any());
    }

    @Test
    public void testReceiveInboxInboxAlreadyExistsShouldReturnEmpty() {
        Long key = 1L;
        ProcessingResultDto resultDto = new ProcessingResultDto(key, StatusCode.ACCEPTED.name());
        Inbox inbox = new Inbox(key, resultDto);

        when(validator.validate(resultDto)).thenReturn(Set.of());
        when(inboxService.addIfNotExistsById(inbox)).thenThrow(new InboxAlreadyExistsException("inbox already exists"));

        assertEquals(Optional.empty(), kafkaInboxReceiver.receiveInbox(resultDto, key));
        verify(inboxService, times(1)).addIfNotExistsById(inbox);
    }
}
