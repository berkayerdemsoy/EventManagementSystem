package com.example.event_service_app.configs;

import com.example.event_service_app.scheduler.EventReminderJob;
import com.example.event_service_app.scheduler.EventStatusUpdateJob;
import org.quartz.*;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class QuartzConfig {

    private static final String REMINDER_JOB_NAME = "eventReminderJob";
    private static final String REMINDER_JOB_GROUP = "reminderGroup";
    private static final String REMINDER_TRIGGER_NAME = "eventReminderTrigger";

    private static final String STATUS_JOB_NAME = "eventStatusUpdateJob";
    private static final String STATUS_JOB_GROUP = "statusGroup";
    private static final String STATUS_TRIGGER_NAME = "eventStatusUpdateTrigger";

    @Bean
    public JobDetail eventReminderJobDetail() {
        return JobBuilder.newJob(EventReminderJob.class)
                .withIdentity(REMINDER_JOB_NAME, REMINDER_JOB_GROUP)
                .withDescription("24 saat içinde başlayacak etkinlikler için Kafka'ya hatırlatıcı event gönderir")
                .storeDurably()   // trigger olmasa da silinmesin
                .requestRecovery() // node crash sonrası yeniden çalıştır
                .build();
    }

    @Bean
    public Trigger eventReminderTrigger(JobDetail eventReminderJobDetail) {
        // Her saat başı çalışır
        return TriggerBuilder.newTrigger()
                .forJob(eventReminderJobDetail)
                .withIdentity(REMINDER_TRIGGER_NAME, REMINDER_JOB_GROUP)
                .withDescription("Saatlik reminder trigger")
                .withSchedule(
                        CronScheduleBuilder.cronSchedule("0 0 * * * ?")  // her saat başı
                                .withMisfireHandlingInstructionFireAndProceed()
                )
                .build();
    }

    @Bean
    public JobDetail eventStatusUpdateJobDetail() {
        return JobBuilder.newJob(EventStatusUpdateJob.class)
                .withIdentity(STATUS_JOB_NAME, STATUS_JOB_GROUP)
                .withDescription("Event status'lerini otomatik olarak günceller (UPCOMING -> ONGOING -> COMPLETED)")
                .storeDurably()
                .requestRecovery()
                .build();
    }

    @Bean
    public Trigger eventStatusUpdateTrigger(JobDetail eventStatusUpdateJobDetail) {
        // Her 5 dakikada bir çalışır
        return TriggerBuilder.newTrigger()
                .forJob(eventStatusUpdateJobDetail)
                .withIdentity(STATUS_TRIGGER_NAME, STATUS_JOB_GROUP)
                .withDescription("5 dakikalık event status update trigger")
                .withSchedule(
                        CronScheduleBuilder.cronSchedule("0 0/5 * * * ?")  // her 5 dakika
                                .withMisfireHandlingInstructionFireAndProceed()
                )
                .build();
    }
}

