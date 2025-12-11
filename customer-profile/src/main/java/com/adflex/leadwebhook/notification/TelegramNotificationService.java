package com.adflex.leadwebhook.notification;

import com.adflex.leadwebhook.integration.telegram.TelegramNotifier;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;


@Service
@RequiredArgsConstructor
public class TelegramNotificationService implements NotificationService {

    private final TelegramNotifier telegramNotifier;

    @Override
    public void notify(NotificationEvent event) {
        String message = buildMessage(event);
        if (message != null && !message.isBlank()) {
            telegramNotifier.sendMessage(message);
        }
    }

    private String buildMessage(NotificationEvent e) {
        if (e == null || e.getType() == null) {
            return null;
        }

        return switch (e.getType()) {
            case LEAD_NEW -> formatLeadNew(e);
            case LEAD_DUPLICATE -> formatLeadDuplicate(e);
            case TRACKING_SLA_EXCEEDED -> formatSlaExceeded(e);
            case PAYMENT_WAITING -> formatPaymentWaiting(e);
            case PAYMENT_CONFIRMED -> formatPaymentConfirmed(e);
        };
    }

    private String formatLeadNew(NotificationEvent e) {
        return """
                :clipboard: Lead mới
                Tên: %s
                SĐT: %s
                Email: %s
                """.formatted(n(e.getName()), n(e.getPhone()), n(e.getEmail()));
    }

    private String formatLeadDuplicate(NotificationEvent e) {
        return """
                🔔: Lead trùng lặp
                Tên: %s
                SĐT: %s
                """.formatted(n(e.getName()), n(e.getPhone()));
    }

    private String formatSlaExceeded(NotificationEvent e) {
        Object milestone = e.getExtra() != null ? e.getExtra().get("milestone") : null;
        Object deadline  = e.getExtra() != null ? e.getExtra().get("deadline")  : null;

        return """
                ⏰ Quá hạn SLA
                Bước: %s
                Lead: %s (%s)
                Deadline: %s
                """.formatted(
                n(milestone),
                n(e.getName()),
                n(e.getPhone()),
                n(deadline)
        );
    }

    private String formatPaymentWaiting(NotificationEvent e) {
        Object pkg = e.getExtra() != null ? e.getExtra().get("package") : null;

        return """
                💰 Chờ thanh toán
                Lead: %s (%s)
                Gói: %s
                """.formatted(
                n(e.getName()),
                n(e.getPhone()),
                n(pkg)
        );
    }

    private String formatPaymentConfirmed(NotificationEvent e) {
        Object pkg = e.getExtra() != null ? e.getExtra().get("package") : null;

        return """
                ✅ Đã thanh toán
                Lead: %s (%s)
                Gói: %s
                """.formatted(
                n(e.getName()),
                n(e.getPhone()),
                n(pkg)
        );
    }

    private String n(Object v) {
        return v == null ? "" : v.toString();
    }
}
