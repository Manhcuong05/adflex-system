package com.adflex.leadwebhook.event;

/**
 * Event bắn ra khi thanh toán đã được xác nhận.
 */
public record PaymentConfirmedEvent(
        String leadId,
        String phone,
        String name,
        String packageCode
) { }
