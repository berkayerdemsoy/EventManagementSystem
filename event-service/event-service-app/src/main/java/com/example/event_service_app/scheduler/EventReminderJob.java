package com.example.event_service_app.scheduler;

import com.example.ems_common.dto.NotificationEvent;
import com.example.ems_common.dto.NotificationEventType;
import com.example.event_service_app.entity.Participation;
import com.example.event_service_app.kafka.NotificationEventProducer;
import com.example.event_service_app.repository.ParticipationRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.quartz.DisallowConcurrentExecution;
import org.quartz.JobExecutionContext;
import org.quartz.PersistJobDataAfterExecution;
import org.springframework.scheduling.quartz.QuartzJobBean;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@Slf4j
@Component
@RequiredArgsConstructor
@DisallowConcurrentExecution          // Aynı anda birden fazla node'un çalıştırmasını engelle (DB kilit ile birlikte çalışır)
@PersistJobDataAfterExecution
public class EventReminderJob extends QuartzJobBean {

    private final ParticipationRepository participationRepository;
    private final NotificationEventProducer notificationEventProducer;

    @Override
    @Transactional
    protected void executeInternal(JobExecutionContext context) {
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime from = now.plusHours(23);
        LocalDateTime to = now.plusHours(25);

        log.info("[ReminderJob] Taranıyor: {} - {}", from, to);

        List<Participation> pending = participationRepository.findPendingReminders(from, to);

        if (pending.isEmpty()) {
            log.info("[ReminderJob] Gönderilecek hatırlatıcı yok.");
            return;
        }

        for (Participation p : pending) {
            try {
                notificationEventProducer.send(NotificationEvent.builder()
                        .eventType(NotificationEventType.EVENT_REMINDER)
                        .recipientEmail(p.getParticipantEmail())
                        .payload(Map.of(
                                "eventTitle", p.getEvent().getTitle(),
                                "eventDate", p.getEvent().getStartDate().toString(),
                                "eventCity", p.getEvent().getCity(),
                                "eventId", String.valueOf(p.getEvent().getId())
                        ))
                        .build());

                p.setReminderSent(true);
                participationRepository.save(p);

                log.info("[ReminderJob] Hatırlatıcı gönderildi: participantEmail={}, event={}",
                        p.getParticipantEmail(), p.getEvent().getTitle());
            } catch (Exception e) {
                log.error("[ReminderJob] Hatırlatıcı gönderilemedi: participantId={}, error={}",
                        p.getParticipantId(), e.getMessage());
            }
        }

        log.info("[ReminderJob] Toplam {} hatırlatıcı gönderildi.", pending.size());
    }
}

