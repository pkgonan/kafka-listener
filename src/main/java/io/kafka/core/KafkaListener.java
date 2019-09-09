package io.kafka.core;

import org.springframework.kafka.config.KafkaListenerContainerFactory;
import org.springframework.kafka.listener.MessageListener;
import org.springframework.kafka.listener.MessageListenerContainer;
import org.springframework.util.Assert;

import java.util.Collection;
import java.util.Collections;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Supplier;

/**
 * Kafka runtime listener
 *
 * {@link org.springframework.kafka.annotation.KafkaListener} not provide new topic listening at runtime.
 * It provides new topic listening, start, stop at runtime.
 *
 * @author Minkiu Kim
 */
public class KafkaListener {

    private final KafkaListenerContainerFactory kafkaListenerContainerFactory;
    private final Map<String, MessageListenerContainer> registeredTopicMap;
    private final Object lock;

    public KafkaListener(final KafkaListenerContainerFactory kafkaListenerContainerFactory) {
        Assert.notNull(kafkaListenerContainerFactory, "kafkaListenerContainerFactory must be not null.");

        this.kafkaListenerContainerFactory = kafkaListenerContainerFactory;
        this.registeredTopicMap = new ConcurrentHashMap<>();
        this.lock = new Object();
    }

    /** Kafka listener registration at runtime.**/
    public void register(final Supplier<Set<String>> topicSupplier, final Supplier<MessageListener> messageListenerSupplier) {
        Assert.notNull(topicSupplier, "topicSupplier must be not null.");
        Assert.notNull(messageListenerSupplier, "messageListenerSupplier must be not null.");

        synchronized (lock) {
            final Set<String> registeredTopics = getRegisteredTopics();
            final Set<String> topics = topicSupplier.get();

            if (topics.isEmpty()) {
                return;
            }

            topics.stream()
                    .filter(topic -> !registeredTopics.contains(topic))
                    .forEach(topic -> doRegister(topic, messageListenerSupplier.get()));
        }
    }

    /** Kafka listener de-registration at runtime. **/
    public void deRegister(final Supplier<Set<String>> topicSupplier) {
        Assert.notNull(topicSupplier, "topicSupplier must be not null.");

        synchronized (lock) {
            final Set<String> registeredTopics = getRegisteredTopics();
            final Set<String> topics = topicSupplier.get();

            if (topics.isEmpty()) {
                return;
            }

            topics.stream()
                    .filter(registeredTopics::contains)
                    .forEach(this::doDeregister);
        }
    }

    /** Kafka listener start at runtime **/
    public void start() {
        synchronized (lock) {
            final Collection<MessageListenerContainer> registeredMessageListenerContainers = registeredTopicMap.values();
            registeredMessageListenerContainers.forEach(container -> {
                if (container.isRunning()) {
                    return;
                }
                container.start();
            });
        }
    }

    /** Kafka listener stop at runtime **/
    public void stop() {
        synchronized (lock) {
            final Collection<MessageListenerContainer> registeredMessageListenerContainers = registeredTopicMap.values();
            registeredMessageListenerContainers.forEach(container -> {
                if (!container.isRunning()) {
                    return;
                }
                container.stop();
            });
        }
    }

    private void doRegister(final String topic, final MessageListener messageListener) {
        Assert.hasLength(topic, "topic must be not empty.");
        Assert.notNull(messageListener, "messageListener must be not null.");

        final MessageListenerContainer messageListenerContainer = kafkaListenerContainerFactory.createContainer(topic);
        messageListenerContainer.setupMessageListener(messageListener);
        messageListenerContainer.start();

        registeredTopicMap.put(topic, messageListenerContainer);
    }

    private void doDeregister(final String topic) {
        Assert.hasLength(topic, "topic must be not empty.");

        final MessageListenerContainer messageListenerContainer = registeredTopicMap.get(topic);
        messageListenerContainer.stop();

        registeredTopicMap.remove(topic);
    }

    private Set<String> getRegisteredTopics() {
        return Collections.unmodifiableSet(registeredTopicMap.keySet());
    }
}