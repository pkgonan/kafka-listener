package io.kafka.controller;

import io.kafka.service.OrderConsumeService;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Size;
import java.util.Set;

@Validated
@RestController
public class OrderConsumerController {

    private final OrderConsumeService orderConsumeService;

    OrderConsumerController(final OrderConsumeService orderConsumeService) {
        this.orderConsumeService = orderConsumeService;
    }

    @PostMapping("/consumers/order/start")
    public void start() {
        orderConsumeService.start();
    }

    @PostMapping("/consumers/order/stop")
    public void stop() {
        orderConsumeService.stop();
    }

    @PostMapping("/consumers/order/register")
    public void register(@RequestBody @Valid Payload payload) {
        orderConsumeService.registerListener(payload.getTopics());
    }

    @PostMapping("/consumers/order/de-register")
    public void deRegister(@RequestBody @Valid Payload payload) {
        orderConsumeService.deRegisterListener(payload.getTopics());
    }

    private static class Payload {

        @NotEmpty @Size(min = 1)
        private Set<@Valid @NotEmpty String> topics;

        private Payload() {}

        public Payload(final Set<String> topics) {
            this.topics = topics;
        }

        public Set<String> getTopics() {
            return topics;
        }
    }
}