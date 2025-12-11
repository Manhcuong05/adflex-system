package com.example.tracking_progress.service;

import com.example.tracking_progress.dto.LeadMilestoneDto;
import com.example.tracking_progress.entity.LeadProgress;
import com.example.tracking_progress.entity.MilestoneConfig;
import com.example.tracking_progress.enums.MilestoneStatus;
import com.example.tracking_progress.repository.LeadProgressRepository;
import com.example.tracking_progress.repository.MilestoneConfigRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class SlaService {

    private final MilestoneConfigRepository configRepo;
    private final LeadProgressRepository progressRepo;

    public List<LeadMilestoneDto> getOverdueSteps() {

        LocalDateTime now = LocalDateTime.now();

        // Load config map
        Map<String, MilestoneConfig> configMap =
                configRepo.findAll().stream()
                        .collect(Collectors.toMap(MilestoneConfig::getCode, c -> c));

        return progressRepo.findByStatus(MilestoneStatus.IN_PROGRESS).stream()
                .filter(lp -> {
                    MilestoneConfig cfg = configMap.get(lp.getMilestoneCode());
                    if (cfg == null || cfg.getSlaHours() == null || lp.getStartedAt() == null)
                        return false;

                    LocalDateTime due = lp.getStartedAt().plusHours(cfg.getSlaHours());
                    return due.isBefore(now); 
                })
                .map(lp -> convert(lp, configMap))
                .toList();
    }

    private LeadMilestoneDto convert(
            LeadProgress lp,
            Map<String, MilestoneConfig> configMap
    ) {
        MilestoneConfig cfg = configMap.get(lp.getMilestoneCode());

        Integer slaHours = cfg != null ? cfg.getSlaHours() : null;

        LocalDateTime dueAt = (slaHours != null)
                ? lp.getStartedAt().plusHours(slaHours)
                : null;

        return LeadMilestoneDto.builder()
                .leadId(lp.getLeadId())
                .milestoneCode(lp.getMilestoneCode())
                .milestoneName(cfg != null ? cfg.getName() : null)
                .milestoneType(cfg != null ? cfg.getType().name() : null)
                .status(lp.getStatus())
                .sequenceOrder(cfg != null ? cfg.getSequenceOrder() : null)
                .requiredProof(cfg != null ? cfg.getRequiredProof() : null)
                .paymentRequired(cfg != null ? cfg.getPaymentRequired() : null)
                .slaHours(slaHours)
                .startedAt(lp.getStartedAt())
                .completedAt(lp.getCompletedAt())
                .createdAt(lp.getCreatedAt())
                .updatedAt(lp.getUpdatedAt())
                .proofDocId(lp.getProofDocId())
                .note(lp.getNote())
                .dueAt(dueAt)
                .build();
    }
}
