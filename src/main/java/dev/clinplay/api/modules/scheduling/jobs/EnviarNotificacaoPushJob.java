package dev.clinplay.api.modules.scheduling.jobs;

import org.quartz.Job;
import org.quartz.JobDataMap;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.springframework.stereotype.Component;

import dev.clinplay.api.modules.notifications.services.NotificacaoService;
import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class EnviarNotificacaoPushJob implements Job {

    private final NotificacaoService notificacaoService;

    @Override
    public void execute(JobExecutionContext ctx) throws JobExecutionException {
        JobDataMap data = ctx.getMergedJobDataMap();
        notificacaoService.enviar(
                data.getString("fcmToken"),
                data.getString("titulo"),
                data.getString("corpo")
        );
    }

}
