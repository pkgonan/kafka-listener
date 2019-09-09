package io.kafka.service;

import io.kafka.core.KafkaListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.config.KafkaListenerContainerFactory;
import org.springframework.kafka.listener.MessageListener;
import org.springframework.stereotype.Service;

import java.util.Set;

@Service
public class OrderConsumeService {

    private static final Logger LOG = LoggerFactory.getLogger(OrderConsumeService.class.getName());

    private final KafkaListener kafkaListener;

    OrderConsumeService(final KafkaListenerContainerFactory kafkaListenerContainerFactory) {
        this.kafkaListener = new KafkaListener(kafkaListenerContainerFactory);
    }

    public void start() {
        kafkaListener.start();
    }

    public void stop() {
        kafkaListener.stop();
    }

    public void registerListener(final Set<String> topic) {
        kafkaListener.register(() -> topic, () -> messageListener());
    }

    public void deRegisterListener(final Set<String> topic) {
        kafkaListener.deRegister(() -> topic);
    }

    private MessageListener<Object, Object> messageListener() {
        return (record) -> LOG.info("Order listened : {}", record.toString());
    }
}
