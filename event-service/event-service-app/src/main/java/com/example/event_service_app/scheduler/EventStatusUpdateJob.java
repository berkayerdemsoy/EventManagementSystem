package com.example.event_service_app.scheduler;

import com.example.event_service_app.entity.Event;
import com.example.event_service_app.repository.EventRepository;
import com.example.event_service_client.enums.EventStatus;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.quartz.DisallowConcurrentExecution;
import org.quartz.JobExecutionContext;
import org.springframework.scheduling.quartz.QuartzJobBean;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
@DisallowConcurrentExecution
public class EventStatusUpdateJob extends QuartzJobBean {

    private final EventRepository eventRepository;

    @Override
    @Transactional
    protected void executeInternal(JobExecutionContext context) {
        LocalDateTime now = LocalDateTime.now();

        // UPCOMING -> ONGOING (startDate <= now < endDate)
        List<Event> upcomingToOngoing = eventRepository.findByStatusAndStartDateBeforeAndEndDateAfter(
                EventStatus.UPCOMING, now);

        for (Event event : upcomingToOngoing) {
            event.setStatus(EventStatus.ONGOING);
            eventRepository.save(event);
            log.info("[EventStatusUpdateJob] Event {} status updated from UPCOMING to ONGOING", event.getId());
        }

        // ONGOING -> COMPLETED (endDate < now)
        List<Event> ongoingToCompleted = eventRepository.findByStatusAndEndDateBefore(
                EventStatus.ONGOING, now);

        for (Event event : ongoingToCompleted) {
            event.setStatus(EventStatus.COMPLETED);
            eventRepository.save(event);
            log.info("[EventStatusUpdateJob] Event {} status updated from ONGOING to COMPLETED", event.getId());
        }

        // UPCOMING -> COMPLETED (endDate < now, missed ONGOING window)
        List<Event> upcomingToCompleted = eventRepository.findByStatusAndEndDateBefore(
                EventStatus.UPCOMING, now);

        for (Event event : upcomingToCompleted) {
            event.setStatus(EventStatus.COMPLETED);
            eventRepository.save(event);
            log.info("[EventStatusUpdateJob] Event {} status updated from UPCOMING to COMPLETED", event.getId());
        }

        int total = upcomingToOngoing.size() + ongoingToCompleted.size() + upcomingToCompleted.size();
        log.info("[EventStatusUpdateJob] Total {} events updated", total);
    }
}
