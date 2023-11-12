package ru.javaschool.documents.service;

import org.apache.kafka.clients.producer.ProducerRecord;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.util.concurrent.SettableListenableFuture;
import ru.javaschool.documents.exception.MessageSendingException;
import ru.javaschool.documents.repository.entity.Outbox;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class KafkaOutboxProducerTest {

    private final String topic = "testTopic";
    private KafkaTemplate<Long, Outbox> kafkaTemplate;
    private KafkaOutboxProducer kafkaOutboxProducer;


    @BeforeEach
    public void setUp() {
        kafkaTemplate = mock(KafkaTemplate.class);
        kafkaOutboxProducer = new KafkaOutboxProducer(kafkaTemplate);
        ReflectionTestUtils.setField(kafkaOutboxProducer, "topic", topic);
    }

    @Test
    public void testSendSync() {
        Outbox outbox = new Outbox(1L, "test");
        SettableListenableFuture<SendResult<Long, Outbox>> future = new SettableListenableFuture<>();
        SendResult<Long, Outbox> sendResult = new SendResult<>(new ProducerRecord<>(topic, outbox.getId(), outbox), null);
        future.set(sendResult);

        when(kafkaTemplate.send(topic, outbox.getId(), outbox)).thenReturn(future);

        ProducerRecord<Long, Outbox> result = kafkaOutboxProducer.sendSync(outbox);
        assertEquals(outbox.getId(), result.key());
        assertEquals(outbox, result.value());
    }

    @Test
    public void testSendSyncThrowsException() {
        Outbox outbox = new Outbox(1L, "test");
        ReflectionTestUtils.setField(kafkaOutboxProducer, "topic", topic);

        SettableListenableFuture<SendResult<Long, Outbox>> future = new SettableListenableFuture<>();
        future.setException(new RuntimeException("Kafka exception"));

        Mockito.when(kafkaTemplate.send(topic, outbox.getId(), outbox)).thenReturn(future);

        assertThrows(MessageSendingException.class, () -> kafkaOutboxProducer.sendSync(outbox));
    }

}
