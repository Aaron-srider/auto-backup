package fit.wenchao.autobackup.service.impl;

import fit.wenchao.autobackup.dao.po.BackupLogPO;
import fit.wenchao.autobackup.dao.po.DbBackupTaskPO;
import fit.wenchao.autobackup.dao.po.DbConnPO;
import fit.wenchao.autobackup.dao.po.TaskPO;
import fit.wenchao.autobackup.dao.repo.BackupLogDao;
import fit.wenchao.autobackup.dao.repo.DbBackupTaskDao;
import fit.wenchao.autobackup.dao.repo.DbConnDao;
import fit.wenchao.autobackup.dao.repo.TaskDao;
import fit.wenchao.autobackup.exception.BackendException;
import fit.wenchao.autobackup.model.RespCode;
import fit.wenchao.autobackup.rest.BackupController;
import fit.wenchao.autobackup.service.TaskService;

import fit.wenchao.autobackup.utils.DbOperate;
import fit.wenchao.autobackup.utils.DefaultSchedulingConfigurer;
import fit.wenchao.utils.dateUtils.DateUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.config.TriggerTask;
import org.springframework.scheduling.support.CronTrigger;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * <p>
 * Task 服务类
 * </p>
 *
 * @author wc
 * @since 2022-09-13
 */

@Component
class BackupService {

    @Autowired
    BackupLogDao backupLogDao;

    @Autowired
    DefaultSchedulingConfigurer defaultSchedulingConfigurer;

    @Autowired
    TaskDao taskDao;

    @Autowired
    DbConnDao dbConnDao;

    @Transactional(propagation = Propagation.NEVER)
    public void backup(Long newId, String type, Long dbConnId, String dbName) {
        String startTime = DateUtils.formatDate(new Date());
        String endTime = null;
        Long startTimeLong = new Date().getTime();
        Long endTimeLong = null;
        String msg;
        try {
            System.out.println("任务开始执行");
            msg = doBackup(newId, type, dbConnId, dbName);
            System.out.println("任务执行结束");
        } catch (BackendException ex) {
            // 取消任务，需要首先取消任务，避免不断循环抛异常
            defaultSchedulingConfigurer.cancelTriggerTask(newId.toString());
            // 任务出问题，记录日志
            endTimeLong = new Date().getTime();
            backupLogDao.save(BackupLogPO.builder().taskId(newId).msg(ex.getMsg()).time(startTime).
                    duration(endTimeLong - startTimeLong).status("failed").build());

            TaskPO task = taskDao.getById(newId);
            if (task != null) {
                task.setStatus("Stopped");
                taskDao.updateById(task);
            }

            return ;
        }

        // 成功，记录日志
        endTimeLong = new Date().getTime();
        backupLogDao.save(BackupLogPO.builder().taskId(newId).msg(msg).time(startTime).
                duration(endTimeLong - startTimeLong).status("success").build());
    }



    @Transactional
    public String doBackup(Long newId, String type, Long dbConnId, String dbName) {

        TaskPO thisTask = taskDao.getById(newId);
        thisTask.setExeTimes(thisTask.getExeTimes() + 1);
        taskDao.updateById(thisTask);

        if ("MySQL".equals(type)) {
            if (StringUtils.isAnyEmpty(dbName) || dbConnId == null) {
                throw new BackendException(null, RespCode.START_DB_BACKUP_FAILED);
            }
            DbConnPO dbConnPO = dbConnDao.getById(dbConnId);
            if (dbConnPO == null) {
                throw new BackendException(null, RespCode.START_DB_BACKUP_FAILED);
            }
            String username = dbConnPO.getUsername();
            String passwd = dbConnPO.getPasswd();
            String host = dbConnPO.getHost();
            Integer port = dbConnPO.getPort();

            String backName = new SimpleDateFormat("yyyy-MM-dd-HH-mm-ss").format(new Date()) + ".sql";

            try {
               String msg= DbOperate.dbBackUp(username, passwd, host, port, dbName, "/home/chaowen/test/db-backup/" + backName);
            return msg;
            } catch (Exception e) {
                if (e instanceof BackendException) {
                    throw (BackendException) e;
                }
                throw new BackendException(null, RespCode.BACKUP_GENERAL_ERROR.name(), e.getMessage());
            }
        }

       throw new BackendException(null, RespCode.BACKUP_TYPE_NOT_SUPPORTED);

    }
}
@Service
@Slf4j
public class TaskServiceImpl implements TaskService {

    @Autowired
    BackupService backupService;
    @Autowired
    TaskDao taskDao;

    @Resource
    private DefaultSchedulingConfigurer defaultSchedulingConfigurer;


    @Autowired
    DbBackupTaskDao dbBackupTaskDao;


    @Autowired
    DbConnDao dbConnDao;




    public Long persistNewTask(String type, String name, String cron, Long dbConnId, String dbName) {
        TaskPO task = taskDao.getOneByMap("name", name);
        if (task != null) {
            throw new BackendException(null, RespCode.TASK_NAME_DUPLICATED);
        }

        task = TaskPO.builder().exeTimes(0)
                .status("Running")
                .cron(cron)
                .name(name)
                .startTime(DateUtils.formatDate(new Date()))
                .build();
        taskDao.save(task);

        Long newId = task.getId();

        if ("MySQL".equals(type)) {
            if (StringUtils.isAnyEmpty(dbName) || dbConnId == null) {
                throw new BackendException(null, RespCode.FRONT_END_PARAMS_ERROR);
            }
            dbBackupTaskDao.save(DbBackupTaskPO.builder().dbName(dbName)
                    .taskId(newId).dbConnId(dbConnId).build());
        }

        return newId;
    }

    @Autowired
    BackupLogDao backupLogDao;

    @Override
    @Transactional
    public void newTask(BackupController.NewTask newTask) {
        String name = newTask.getName();
        String cron = newTask.getCron();
        Long dbConnId = newTask.getDbConnId();
        String dbName = newTask.getDbName();
        String type = newTask.getType();
        Long newId = persistNewTask(newTask.getType(), name, cron, newTask.getDbConnId(), newTask.getDbName());
        startTask(newId, type, name, cron, dbConnId, dbName);
    }


    @Override
    public void startTask(Long newId, String type, String name, String cron, Long dbConnId, String dbName) {
        try {
            defaultSchedulingConfigurer.addTriggerTask(newId.toString(),
                    new TriggerTask(
                            () -> backupService.backup(newId, type, dbConnId, dbName), new CronTrigger(cron)));
            log.info("启动任务：{} - {}", newId, name);
        } catch (IllegalArgumentException ex) {
            // cron表达式格式错误，任务启动失败
            throw new BackendException(null, RespCode.INVALID_CRON);
        }
    }


    @Override
    @Transactional
    public void cancelTask(Long taskId) {
        TaskPO task = taskDao.getById(taskId);
        if (task == null) {
            defaultSchedulingConfigurer.cancelTriggerTask(taskId.toString());
            throw new BackendException(null, RespCode.NO_TASK);
        }

        task.setStatus("已取消");
        taskDao.updateById(task);
        defaultSchedulingConfigurer.cancelTriggerTask(taskId.toString());
    }

    @Override
    @Transactional
    public void removeTask(Long taskId) {

        TaskPO task = taskDao.getById(taskId);
        if (task == null) {
            defaultSchedulingConfigurer.cancelTriggerTask(taskId.toString());
            return;
        }


        taskDao.removeById(task);
        defaultSchedulingConfigurer.cancelTriggerTask(taskId.toString());
    }
}


