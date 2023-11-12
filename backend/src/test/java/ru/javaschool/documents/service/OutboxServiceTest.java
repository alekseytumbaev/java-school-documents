package ru.javaschool.documents.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.javaschool.configuration.JacksonConfiguration;
import ru.javaschool.documents.controller.dto.DocumentDto;
import ru.javaschool.documents.controller.dto.Status;
import ru.javaschool.documents.exception.OutboxSavingException;
import ru.javaschool.documents.repository.OutboxRepository;
import ru.javaschool.documents.repository.entity.Outbox;
import ru.javaschool.documents.repository.entity.StatusCode;

import java.util.Date;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.internal.verification.VerificationModeFactory.times;

public class OutboxServiceTest {

    private OutboxRepository outboxRepo;
    private ObjectMapper objectMapper;
    private KafkaOutboxProducer kafkaProducer;
    private OutboxService outboxService;

    @BeforeEach
    public void setUp() {
        outboxRepo = mock(OutboxRepository.class);
        kafkaProducer = mock(KafkaOutboxProducer.class);
        objectMapper = spy(new JacksonConfiguration().objectMapper());
        outboxService = new OutboxService(outboxRepo, objectMapper, kafkaProducer);
    }

    @Test
    public void testAddMessage() throws JsonProcessingException {
        DocumentDto payload = new DocumentDto(
                1L,
                "type",
                "organization",
                "description",
                "patient",
                new Date(),
                Status.of(StatusCode.NEW.name(), StatusCode.NEW.getDisplayName())
        );
        when(outboxRepo.save(any(Outbox.class)))
                .then(invocation -> {
                    Outbox outbox = (Outbox) invocation.getArguments()[0];
                    outbox.setId(1L);
                    return outbox;
                });

        when(objectMapper.writeValueAsString(payload)).thenCallRealMethod();

        outboxService.addMessage(payload);
        verify(outboxRepo, times(1)).save(any());
    }

    @Test
    public void testShouldThrowExceptionWhenCanNotParsePayloadToJson() throws JsonProcessingException {
        DocumentDto payload = new DocumentDto(
                1L,
                "incorrect payload",
                "organization",
                "description",
                "patient",
                new Date(),
                Status.of(StatusCode.NEW.name(), StatusCode.NEW.getDisplayName())
        );

        doThrow(new CustomJsonProcessingException("")).when(objectMapper).writeValueAsString(payload);

        assertThrows(OutboxSavingException.class, () -> outboxService.addMessage(payload));
        verify(objectMapper, times(1)).writeValueAsString(payload);
        verify(outboxRepo, never()).save(any());
    }
}

/**
 * У JsonProcessingException protected конструктор, поэтому используем его наследника
 */
class CustomJsonProcessingException extends JsonProcessingException {
    public CustomJsonProcessingException(String msg) {
        super(msg);
    }
}

