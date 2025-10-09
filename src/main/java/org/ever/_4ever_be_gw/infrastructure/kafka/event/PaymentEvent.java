package org.ever._4ever_be_gw.infrastructure.kafka.event;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.math.BigDecimal;

@Getter
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public class PaymentEvent extends BaseEvent {

    private String orderId;
    private String userId;
    private BigDecimal amount;
    private String paymentMethod;
    private PaymentStatus status;
    private PaymentAction action;

    public enum PaymentAction {
        REQUESTED,
        COMPLETED,
        CANCELED,
        FAILED
    }

    public enum PaymentStatus {
        PENDING,
        PROCESSING,
        SUCCESS,
        FAILED,
        CANCELED
    }
}
