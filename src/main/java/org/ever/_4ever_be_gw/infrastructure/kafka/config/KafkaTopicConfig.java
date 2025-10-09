package org.ever._4ever_be_gw.infrastructure.kafka.config;

import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.TopicBuilder;

@Configuration
public class KafkaTopicConfig {

    // 서비스 토픽
    public static final String USER_EVENT_TOPIC = "user-event";
    public static final String SCM_EVENT_TOPIC = "scm-event";
    public static final String BUSINESS_EVENT_TOPIC = "business-event";
    public static final String ALARM_EVENT_TOPIC = "alarm-event";

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
