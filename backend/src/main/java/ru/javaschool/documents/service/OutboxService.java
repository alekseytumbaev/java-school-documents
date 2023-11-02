package ru.javaschool.documents.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.javaschool.documents.repository.OutboxRepository;
import ru.javaschool.documents.repository.entity.Outbox;
import ru.javaschool.documents.exception.OutboxSavingException;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class OutboxService {
    private final OutboxRepository outboxRepo;
    private final ObjectMapper objectMapper;
    private final KafkaOutboxProducer kafkaProducer;

    /**
     * Добавляет сообщение в таблицу исходящих.
     *
     * @param payload тело сообщения
     * @return сохраненное исходящее сообщение
     */
    @Transactional
    public Outbox addMessage(Object payload) {
        String payloadJson;
        try {
            payloadJson = objectMapper.writeValueAsString(payload);
        } catch (JsonProcessingException e) {
            throw new OutboxSavingException(
                    String.format("Failed to save message, payload: %s", payload), e);
        }
        Outbox outbox = outboxRepo.save(new Outbox(null, payloadJson));
        log.info("Saved outbox: {}", outbox);
        return outbox;
    }

    /**
     * Отправляет все сообщения, которые есть в таблице исходящих, в кафку. Удаляет успешно отправленные сообщения.
     * <br/>
     * Ключ сообщения - первичный ключ в таблице исходящих.
     */
    @Scheduled(fixedDelay = 3000)
    @Transactional
    public void sendFromOutbox() {
        List<Outbox> outboxes = outboxRepo.findAll();
        for (Outbox outbox : outboxes) {
            kafkaProducer.sendSync(outbox);
        }
        outboxRepo.deleteAll(outboxes);
    }
}
