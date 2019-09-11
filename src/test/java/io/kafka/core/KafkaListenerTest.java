package io.kafka.core;

import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.kafka.config.KafkaListenerContainerFactory;
import org.springframework.kafka.listener.MessageListener;
import org.springframework.kafka.listener.MessageListenerContainer;

import java.util.Set;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class KafkaListenerTest {

    @Test
    void register() {
        KafkaListenerContainerFactory factory = mock(KafkaListenerContainerFactory.class);
        KafkaListener kafkaListener = new KafkaListener(factory);

        Set<String> topics = Stream.of("A").collect(Collectors.toSet());
        Supplier<Set<String>> topicSupplier = () -> topics;
        MessageListener messageListener = (record) -> record.toString();
        Supplier<MessageListener> messageListenerSupplier = () -> messageListener;

        MessageListenerContainer messageListenerContainer = mock(MessageListenerContainer.class);
        doReturn(messageListenerContainer).when(factory).createContainer(topics);

        kafkaListener.register(topicSupplier, messageListenerSupplier);

        Set<String> registeredTopics = kafkaListener.getRegisteredTopics();

        assertTrue(registeredTopics.contains("A"));
        assertFalse(registeredTopics.contains("B"));

        verify(factory, times(1)).createContainer(topics);
        verify(messageListenerContainer, times(1)).setupMessageListener(messageListener);
        verify(messageListenerContainer, times(1)).start();
    }

    @Test
    void deRegister() {
    }

    @Test
    void start() {
    }

    @Test
    void stop() {
    }
}