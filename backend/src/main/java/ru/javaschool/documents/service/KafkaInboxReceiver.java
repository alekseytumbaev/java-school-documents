package ru.javaschool.documents.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Service;
import ru.javaschool.documents.controller.dto.ProcessingResultDto;
import ru.javaschool.documents.repository.entity.Inbox;
import ru.javaschool.documents.exception.InboxAlreadyExistsException;

import javax.validation.ConstraintViolation;
import javax.validation.Validator;
import java.util.Set;

@Service
@Slf4j
@RequiredArgsConstructor
public class KafkaInboxReceiver {

    private final InboxService inboxService;
    private final Validator validator;

    /**
     * Читает из кафки сообщения о результатах обработки документа.
     * <br/>
     * Если сообщения с таким ключом не было - сохраняет его, если было - игнорирует.
     *
     * @param resultDto сообщение о результатах обработки
     * @param key       ключ сообщения, используется как id в таблице входящих
     */
    @KafkaListener(topics = "${kafka.topic-names.documents-out}")
    public void receiveInbox(@Payload ProcessingResultDto resultDto,
                             @Header(KafkaHeaders.RECEIVED_MESSAGE_KEY) Long key) {
        if (messageIsInvalid(resultDto, key)) {
            return;
        }
        try {
            Inbox inbox = inboxService.addIfNotExistsById(new Inbox(key, resultDto));
            log.info("Added inbox with id={} and result={}", inbox.getId(), inbox.getPayload());
        } catch (InboxAlreadyExistsException e) {
            log.warn("Cannot add inbox with id={}, because it already exists", key);
        }
    }

    /**
     * Проверяет, что resultDto валиден (с помощью JSR 303) и что key не равен null.
     * Пишет лог, если валидация не прошла.
     *
     * @param resultDto тело сообщения
     * @param key       ключ
     * @return true, если валидация не прошла
     */
    private boolean messageIsInvalid(ProcessingResultDto resultDto, Long key) {
        Set<ConstraintViolation<ProcessingResultDto>> violations = validator.validate(resultDto);
        if (!violations.isEmpty() || key == null) {
            log.warn("Cannot add inbox, validation failed. Key: {}, message: {}, violations: {}",
                    key, resultDto, violations);
            return true;
        }
        return false;
    }
}
