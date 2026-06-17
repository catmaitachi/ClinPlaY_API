package dev.clinplay.api.modules.scheduling.services;

import org.quartz.JobDetail;
import org.quartz.JobKey;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.Trigger;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AgendamentoService {

    private final Scheduler scheduler;

    public void agendar(JobDetail job, Trigger trigger) {
        try {
            if (scheduler.checkExists(job.getKey())) scheduler.deleteJob(job.getKey());
            scheduler.scheduleJob(job, trigger);
        } catch (SchedulerException e) {
            throw new RuntimeException("Erro ao agendar job " + job.getKey(), e);
        }
    }

    public void cancelar(JobKey chave) {
        try {
            scheduler.deleteJob(chave);
        } catch (SchedulerException e) {
            throw new RuntimeException("Erro ao cancelar job " + chave, e);
        }
    }

    public boolean existe(JobKey chave) {
        try {
            return scheduler.checkExists(chave);
        } catch (SchedulerException e) {
            throw new RuntimeException("Erro ao verificar job " + chave, e);
        }
    }

}
