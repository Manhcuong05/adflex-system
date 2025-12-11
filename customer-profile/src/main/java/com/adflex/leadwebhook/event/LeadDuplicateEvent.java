package com.adflex.leadwebhook.event;

/**
 * Event bắn ra khi phát hiện lead trùng (phone đã tồn tại).
 */
public record LeadDuplicateEvent(
        String leadId,
        String phone,
        String name
) { }
