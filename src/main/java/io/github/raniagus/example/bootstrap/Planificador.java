package io.github.raniagus.example.bootstrap;

import java.util.Map;
import java.util.UUID;
import org.quartz.CronScheduleBuilder;
import org.quartz.Job;
import org.quartz.JobBuilder;
import org.quartz.JobDataMap;
import org.quartz.JobDetail;
import org.quartz.JobExecutionContext;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.Trigger;
import org.quartz.TriggerBuilder;
import org.quartz.impl.StdSchedulerFactory;

public class Planificador {
  private final Scheduler scheduler;

  public Planificador() {
    try {
      scheduler = new StdSchedulerFactory().getScheduler();
    } catch (SchedulerException e) {
      throw new PlanificadorException("Error al crear el planificador", e);
    }
  }

  public Planificador agregarTarea(Runnable runnable, String cron) {
    try {
      scheduler.scheduleJob(crearJobDetail(runnable), crearTrigger(cron));
    } catch (SchedulerException e) {
      throw new PlanificadorException("Error al agregar la tarea", e);
    }
    return this;
  }

  public void iniciar() {
    try {
      scheduler.start();
    } catch (SchedulerException e) {
      throw new PlanificadorException("Error al iniciar el planificador", e);
    }
  }

  private Trigger crearTrigger(String cron) {
    return TriggerBuilder.newTrigger()
        .withIdentity(UUID.randomUUID().toString())
        .withSchedule(CronScheduleBuilder.cronSchedule(cron))
        .build();
  }

  private JobDetail crearJobDetail(Runnable runnable) {
    return JobBuilder.newJob(Tarea.class)
        .withIdentity(runnable.getClass().getName(), "Application")
        .usingJobData(new JobDataMap(Map.of("runnable", runnable)))
        .build();
  }

  public static class Tarea implements Job {
    @Override
    public void execute(JobExecutionContext context) {
      ((Runnable) context
          .getJobDetail()
          .getJobDataMap()
          .get("runnable"))
          .run();
    }
  }
}
