package fit.wenchao.autobackup;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.conditions.query.QueryChainWrapper;
import fit.wenchao.autobackup.dao.po.DbBackupTaskPO;
import fit.wenchao.autobackup.dao.po.TaskPO;
import fit.wenchao.autobackup.dao.repo.DbBackupTaskDao;
import fit.wenchao.autobackup.dao.repo.TaskDao;
import fit.wenchao.autobackup.exception.BackendException;
import fit.wenchao.autobackup.service.TaskService;
import fit.wenchao.autobackup.utils.DefaultSchedulingConfigurer;
import fit.wenchao.mybatisCodeGen.codegen.MybatisCodeGenerator;
import lombok.extern.slf4j.Slf4j;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.annotation.DependsOn;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.List;

@SpringBootApplication
public class AutoBackupApplication  implements ApplicationContextAware {



    static ApplicationContext applicationContext;



    public static void main(String[] args) {
        SpringApplication.run(AutoBackupApplication.class, args);
        MybatisCodeGenerator.generateStructureCode();

        StartProcessor startProcessor = applicationContext.getBean("startProcessor", StartProcessor.class);
        startProcessor.start();
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
this.applicationContext = applicationContext;
    }
}


@Component
@Slf4j
class StartProcessor   {

    @Autowired
    private DefaultSchedulingConfigurer defaultSchedulingConfigurer;

    @Autowired
    TaskDao taskDao;

    @Autowired
    TaskService taskService;

    @Autowired
    DbBackupTaskDao dbBackupTaskDao;

    public void start() {
        List<TaskPO> taskList = getAllTasks();
        for (TaskPO taskPO : taskList) {
            Long taskId = taskPO.getId();
            boolean scheduled = taskScheduled(taskId);
            if (!scheduled) {
                scheduleTask(taskPO);
            }
        }
    }


    private void scheduleTask(TaskPO task) {
        DbBackupTaskPO dbBackupTaskPO = dbBackupTaskDao.getOne(new QueryWrapper<DbBackupTaskPO>()
                .eq("task_id", task.getId()), false);
        if (dbBackupTaskPO == null) {
            log.warn("启动任务失败：taskId: {} taskName:{}", task.getId(), task.getName());
            return;
        }
        try {
            taskService.startTask(task.getId(), "MySQL",
                    task.getName(),
                    task.getCron(),
                    dbBackupTaskPO.getDbConnId(),
                    dbBackupTaskPO.getDbName());
        } catch (BackendException ex) {
            log.warn("启动任务失败：taskId: {} taskName:{}", task.getId(), task.getName());
        }
    }

    private boolean taskScheduled(Long taskId) {
        return defaultSchedulingConfigurer.hasTask(taskId.toString());
    }

    private List<TaskPO> getAllTasks() {
        return taskDao.list();
    }
}

