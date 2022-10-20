package fit.wenchao.autobackup.utils;

import fit.wenchao.autobackup.utils.BeanUtils;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.SchedulingException;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.SchedulingConfigurer;
import org.springframework.scheduling.config.ScheduledTask;
import org.springframework.scheduling.config.ScheduledTaskRegistrar;
import org.springframework.scheduling.config.TriggerTask;

import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ScheduledFuture;

@Configuration
@EnableScheduling
public class DefaultSchedulingConfigurer implements SchedulingConfigurer {

    private ScheduledTaskRegistrar taskRegistrar;
    private Set<ScheduledFuture<?>> scheduledFutures = null;
    private Map<String, ScheduledTask> tasks = new ConcurrentHashMap<>();
 
    @Override
    public void configureTasks(ScheduledTaskRegistrar taskRegistrar) {
        this.taskRegistrar = taskRegistrar;
    }
 
    @SuppressWarnings("unchecked")
    private Set<ScheduledFuture<?>> getScheduledFutures() {
        if (scheduledFutures == null) {
            try {
                // spring版本不同选用不同字段scheduledFutures
                scheduledFutures = (Set<ScheduledFuture<?>>) BeanUtils.getProperty(taskRegistrar, "scheduledTasks");
            } catch (NoSuchFieldException e) {
                throw new SchedulingException("not found scheduledFutures field.");
            }
        }
        return scheduledFutures;
    }
 
    /**
     * 添加任务
     */
    public void addTriggerTask(String taskId, TriggerTask triggerTask) {
        if (tasks.containsKey(taskId)) {
            throw new SchedulingException("the taskId[" + taskId + "] was added.");
        }
        ScheduledTask scheduledTask = taskRegistrar.scheduleTriggerTask(triggerTask);
        tasks.put(taskId, scheduledTask);
    }
 
    /**
     * 取消任务
     */
    public void cancelTriggerTask(String taskId) {
        ScheduledTask task = tasks.get(taskId);
        if (task != null) {
            task.cancel();
        }
        tasks.remove(taskId);
    }
 
    /**
     * 重置任务
     */
    public void resetTriggerTask(String taskId, TriggerTask triggerTask) {
        cancelTriggerTask(taskId);
        addTriggerTask(taskId, triggerTask);
    }
 
    /**
     * 任务编号
     */
    public Set<String> taskIds() {
        return tasks.keySet();
    }
 
    /**
     * 是否存在任务
     */
    public boolean hasTask(String taskId) {
        return this.tasks.containsKey(taskId);
    }
 
    /**
     * 任务调度是否已经初始化完成
     */
    public boolean inited() {
        return this.taskRegistrar != null && this.taskRegistrar.getScheduler() != null;
    }
}
