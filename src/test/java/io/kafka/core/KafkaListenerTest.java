package io.kafka.core;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.kafka.config.KafkaListenerContainerFactory;
import org.springframework.kafka.listener.MessageListener;
import org.springframework.kafka.listener.MessageListenerContainer;

import java.util.Set;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class KafkaListenerTest {

    private static String TOPIC = "test-topic";
    private static Supplier<Set<String>> topicSupplier = () -> Stream.of(TOPIC).collect(Collectors.toSet());
    private static Supplier<MessageListener> messageListenerSupplier = () -> (record) -> record.toString();

    @Autowired
    private KafkaListenerContainerFactory factory;

    private KafkaListener kafkaListener;

    @BeforeEach
    void init() {
        kafkaListener = new KafkaListener(factory);
    }

    @Test
    void register() {
        kafkaListener.register(topicSupplier, messageListenerSupplier);

        assertEquals(1, getRegisteredTopics().size());
        assertTrue(getRegisteredTopics().contains(TOPIC));
        assertTrue(getRegisteredMessageListenerContainerByTopic().isRunning());
    }

    @Test
    void deRegister() {
        kafkaListener.register(topicSupplier, messageListenerSupplier);

        assertEquals(1, getRegisteredTopics().size());
        assertTrue(getRegisteredTopics().contains(TOPIC));

        kafkaListener.deRegister(topicSupplier);

        assertTrue(getRegisteredTopics().isEmpty());
    }

    @Test
    void start() {
        kafkaListener.register(topicSupplier, messageListenerSupplier);
        kafkaListener.stop();
        kafkaListener.start();

        assertTrue(getRegisteredMessageListenerContainerByTopic().isRunning());
    }

    @Test
    void stop() {
        kafkaListener.register(topicSupplier, messageListenerSupplier);
        kafkaListener.stop();

        assertFalse(getRegisteredMessageListenerContainerByTopic().isRunning());
    }

    private Set<String> getRegisteredTopics() {
        return kafkaListener.getRegisteredTopicMap().keySet();
    }

    private MessageListenerContainer getRegisteredMessageListenerContainerByTopic() {
        return kafkaListener.getRegisteredTopicMap().get(TOPIC);
    }
}