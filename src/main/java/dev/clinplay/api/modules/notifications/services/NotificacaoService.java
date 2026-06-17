package dev.clinplay.api.modules.notifications.services;

import java.time.Instant;
import java.util.Date;
import java.util.UUID;

import org.quartz.JobBuilder;
import org.quartz.JobDetail;
import org.quartz.JobKey;
import org.quartz.TriggerBuilder;
import org.springframework.stereotype.Service;

import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.Message;
import com.google.firebase.messaging.Notification;

import dev.clinplay.api.modules.scheduling.jobs.EnviarNotificacaoPushJob;
import dev.clinplay.api.modules.scheduling.services.AgendamentoService;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class NotificacaoService {

    private final AgendamentoService agendamentoService;

    public void enviar(String fcmToken, String titulo, String corpo) {
        Message msg = Message.builder()
                .setToken(fcmToken)
                .setNotification(Notification.builder().setTitle(titulo).setBody(corpo).build())
                .build();
        FirebaseMessaging.getInstance().sendAsync(msg);
    }

    public String agendarPush(String fcmToken, String titulo, String corpo, Instant quando) {
        String jobId = UUID.randomUUID().toString();
        JobDetail job = JobBuilder.newJob(EnviarNotificacaoPushJob.class)
                .withIdentity(jobId, "push")
                .usingJobData("fcmToken", fcmToken)
                .usingJobData("titulo", titulo)
                .usingJobData("corpo", corpo)
                .build();
        org.quartz.Trigger trigger = TriggerBuilder.newTrigger()
                .startAt(Date.from(quando))
                .build();
        agendamentoService.agendar(job, trigger);
        return jobId;
    }

    public void cancelarPush(String jobId) {
        agendamentoService.cancelar(JobKey.jobKey(jobId, "push"));
    }

}
