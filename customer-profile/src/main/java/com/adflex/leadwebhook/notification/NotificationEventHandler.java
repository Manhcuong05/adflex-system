package com.adflex.leadwebhook.notification;

import com.adflex.leadwebhook.event.*;
import lombok.RequiredArgsConstructor;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import java.util.Map;


@Component
@RequiredArgsConstructor
public class NotificationEventHandler {

    private final NotificationService notificationService;

    @EventListener
    public void onLeadCreated(LeadCreatedEvent e) {
        notificationService.notify(
                NotificationEvent.builder()
                        .type(NotificationType.LEAD_NEW)
                        .leadId(e.leadId())
                        .phone(e.phone())
                        .name(e.name())
                        .email(e.email())
                        .build()
        );
    }

    @EventListener
    public void onLeadDuplicate(LeadDuplicateEvent e) {
        notificationService.notify(
                NotificationEvent.builder()
                        .type(NotificationType.LEAD_DUPLICATE)
                        .leadId(e.leadId())
                        .phone(e.phone())
                        .name(e.name())
                        .build()
        );
    }

    @EventListener
    public void onMilestoneSlaExceeded(MilestoneSlaExceededEvent e) {
        notificationService.notify(
                NotificationEvent.builder()
                        .type(NotificationType.TRACKING_SLA_EXCEEDED)
                        .leadId(e.leadId())
                        .phone(e.phone())
                        .name(e.name())
                        .extra(Map.of(
                                "milestone", e.milestoneCode(),
                                "deadline", e.deadline().toString()
                        ))
                        .build()
        );
    }

    @EventListener
    public void onPaymentWaiting(PaymentWaitingEvent e) {
        notificationService.notify(
                NotificationEvent.builder()
                        .type(NotificationType.PAYMENT_WAITING)
                        .leadId(e.leadId())
                        .phone(e.phone())
                        .name(e.name())
                        .extra(Map.of(
                                "package", e.packageCode()
                        ))
                        .build()
        );
    }

    @EventListener
    public void onPaymentConfirmed(PaymentConfirmedEvent e) {
        notificationService.notify(
                NotificationEvent.builder()
                        .type(NotificationType.PAYMENT_CONFIRMED)
                        .leadId(e.leadId())
                        .phone(e.phone())
                        .name(e.name())
                        .extra(Map.of(
                                "package", e.packageCode()
                        ))
                        .build()
        );
    }
}
