package com.example.tracking_progress.scheduler;

import com.example.tracking_progress.entity.LeadProgress;
import com.example.tracking_progress.entity.MilestoneConfig;
import com.example.tracking_progress.enums.MilestoneStatus;
import com.example.tracking_progress.repository.LeadProgressRepository;
import com.example.tracking_progress.repository.MilestoneConfigRepository;
import com.example.tracking_progress.service.TelegramNotifierService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Slf4j
@Component
@RequiredArgsConstructor
public class SlaScanner {

    private final LeadProgressRepository progressRepo;
    private final MilestoneConfigRepository configRepo;
    private final TelegramNotifierService telegram;

    
    @Scheduled(fixedRate = 5000)
    public void scan() {

        var items = progressRepo.findByStatus(MilestoneStatus.IN_PROGRESS);

        for (LeadProgress lp : items) {

            MilestoneConfig cfg = configRepo.findByCode(lp.getMilestoneCode());
            if (cfg == null || cfg.getSlaHours() == null) continue;
            if (lp.getStartedAt() == null) continue;

            LocalDateTime due = lp.getStartedAt().plusHours(cfg.getSlaHours());

            if (due.isBefore(LocalDateTime.now())) {
                String msg = """
                        ⚠️ *SLA quá hạn!*
                        
                        • Lead: %s
                        • Step: %s
                        • Deadline: %s
                        """.formatted(lp.getLeadId(), lp.getMilestoneCode(), due);

                telegram.sendMessage(msg);
                log.warn("SLA overdue: {}", msg);
            }
        }
    }
}
