package org.ever._4ever_be_gw.infrastructure.kafka.config;

import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.TopicBuilder;

@Configuration
public class KafkaTopicConfig {

    // 결제 서비스 토픽
    public static final String PAYMENT_REQUEST_TOPIC = "payment-request";
    public static final String PAYMENT_COMPLETE_TOPIC = "payment-complete";
    public static final String PAYMENT_CANCEL_TOPIC = "payment-cancel";
    public static final String PAYMENT_FAILED_TOPIC = "payment-failed";

    // 다른 서비스 토픽
    public static final String USER_EVENT_TOPIC = "user-event";
    public static final String SCM_EVENT_TOPIC = "scm-event";
    public static final String BUSINESS_EVENT_TOPIC = "business-event";
    public static final String ALARM_EVENT_TOPIC = "alarm-event";

    @Bean
    public NewTopic paymentRequestTopic() {
        return TopicBuilder.name(PAYMENT_REQUEST_TOPIC)
            .partitions(3)
            .replicas(1)
            .build();
    }

    @Bean
    public NewTopic paymentCompleteTopic() {
        return TopicBuilder.name(PAYMENT_COMPLETE_TOPIC)
            .partitions(3)
            .replicas(1)
            .build();
    }

    @Bean
    public NewTopic paymentCancelTopic() {
        return TopicBuilder.name(PAYMENT_CANCEL_TOPIC)
            .partitions(3)
            .replicas(1)
            .build();
    }

    @Bean
    public NewTopic paymentFailedTopic() {
        return TopicBuilder.name(PAYMENT_FAILED_TOPIC)
            .partitions(3)
            .replicas(1)
            .build();
    }

    @Bean
    public NewTopic userEventTopic() {
        return TopicBuilder.name(USER_EVENT_TOPIC)
            .partitions(3)
            .replicas(1)
            .build();
    }

    @Bean
    public NewTopic scmEventTopic() {
        return TopicBuilder.name(SCM_EVENT_TOPIC)
            .partitions(3)
            .replicas(1)
            .build();
    }

    @Bean
    public NewTopic businessEventTopic() {
        return TopicBuilder.name(BUSINESS_EVENT_TOPIC)
            .partitions(3)
            .replicas(1)
            .build();
    }

    @Bean
    public NewTopic alarmEventTopic() {
        return TopicBuilder.name(ALARM_EVENT_TOPIC)
            .partitions(3)
            .replicas(1)
            .build();
    }
}
