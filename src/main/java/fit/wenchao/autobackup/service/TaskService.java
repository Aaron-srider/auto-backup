package fit.wenchao.autobackup.service;

import fit.wenchao.autobackup.rest.BackupController;

/**
 * <p>
 * Task 服务类
 * </p>
 *
 * @author wc
 * @since 2022-09-13
 */
public interface TaskService {

    void newTask(BackupController.NewTask newTask);

    void startTask(Long newId, String type, String name, String cron, Long dbConnId, String dbName);

    void cancelTask(Long taskId);

    void removeTask(Long taskId);
}


