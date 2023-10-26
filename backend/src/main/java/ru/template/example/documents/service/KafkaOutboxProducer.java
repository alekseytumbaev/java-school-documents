package ru.template.example.documents.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Service;
import org.springframework.util.concurrent.ListenableFuture;
import org.springframework.util.concurrent.ListenableFutureCallback;
import ru.template.example.documents.exception.MessageSendingException;
import ru.template.example.documents.repository.entity.Outbox;

import java.util.concurrent.ExecutionException;

@Service
@RequiredArgsConstructor
@Slf4j
public class KafkaOutboxProducer {
    private final KafkaTemplate<Long, Outbox> kafkaTemplate;

    @Value("${kafka.topic-names.documents-in}")
    private String topic;

    /**
     * Отправляет сообщение в кафку и ждет от нее ответ.
     *
     * @param outbox тело сообщение, id используется в качестве ключа сообщения
     * @return результат отправки
     * @throws MessageSendingException при ошибке отправки
     */
    public ProducerRecord<Long, Outbox> sendSync(Outbox outbox) {
        ListenableFuture<SendResult<Long, Outbox>> future = kafkaTemplate.send(topic, outbox.getId(), outbox);
        future.addCallback(new ListenableFutureCallback<>() {
            @Override
            public void onSuccess(SendResult<Long, Outbox> result) {
                log.info("Message '{}' sent to topic '{}'", outbox, result.getRecordMetadata().topic());
            }

            @Override
            public void onFailure(Throwable ex) {
                throw new MessageSendingException("Failed to send message", ex);
            }
        });

        try {
            SendResult<Long, Outbox> result = future.get();
            return result.getProducerRecord();
        } catch (InterruptedException | ExecutionException e) {
            throw new MessageSendingException("Failed to send message", e);
        }
    }
}
