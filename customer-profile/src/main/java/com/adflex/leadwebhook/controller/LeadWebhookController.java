package com.adflex.leadwebhook.controller;

import com.adflex.leadwebhook.dto.request.WebhookRequest;
import com.adflex.leadwebhook.dto.response.LeadResponse;
import com.adflex.leadwebhook.entity.Lead;
import com.adflex.leadwebhook.service.LeadService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/webhooks")
@RequiredArgsConstructor
public class LeadWebhookController {

    private final LeadService leadService;

    @Value("${app.api-key}")
    private String apiKey;

    @PostMapping("/google-form")
    public ResponseEntity<?> receiveLead(
            @RequestHeader(value = "X-Api-Key", required = false) String headerKey,
            @RequestParam(value = "api_key", required = false) String queryKey,
            @Valid @RequestBody WebhookRequest body
    ) {
        // FR 1.1: Xác thực API Key – chấp nhận qua header hoặc query param để linh hoạt hơn
        String providedKey = headerKey != null ? headerKey : queryKey;
        if (providedKey == null || !providedKey.equals(apiKey)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body("Invalid API Key (thiếu X-Api-Key header hoặc query param api_key)");
        }


        if (body == null || body.getData() == null) {
            return ResponseEntity.badRequest().body("Missing data");
        }

        Lead lead = leadService.processIncomingLead(body.getData());


        LeadResponse res = new LeadResponse(lead.getId(), lead.getStatus());
        HttpStatus status = Boolean.TRUE.equals(lead.getIsDuplicate())
                ? HttpStatus.OK        // lead trùng
                : HttpStatus.CREATED;  // lead mới

        return ResponseEntity.status(status).body(res);
    }
}
