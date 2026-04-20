package com.example.event_service_app.configs;

import com.example.event_service_app.scheduler.EventReminderJob;
import org.quartz.*;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class QuartzConfig {

    private static final String JOB_NAME = "eventReminderJob";
    private static final String JOB_GROUP = "reminderGroup";
    private static final String TRIGGER_NAME = "eventReminderTrigger";

    @Bean
    public JobDetail eventReminderJobDetail() {
        return JobBuilder.newJob(EventReminderJob.class)
                .withIdentity(JOB_NAME, JOB_GROUP)
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
                .withIdentity(TRIGGER_NAME, JOB_GROUP)
                .withDescription("Saatlik reminder trigger")
                .withSchedule(
                        CronScheduleBuilder.cronSchedule("0 0 * * * ?")  // her saat başı
                                .withMisfireHandlingInstructionFireAndProceed()
                )
                .build();
    }
}

