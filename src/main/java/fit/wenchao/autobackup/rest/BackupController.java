package fit.wenchao.autobackup.rest;

import fit.wenchao.autobackup.concurrent.Locks;
import fit.wenchao.autobackup.dao.po.BackupLogPO;
import fit.wenchao.autobackup.dao.po.TaskPO;
import fit.wenchao.autobackup.dao.repo.BackupLogDao;
import fit.wenchao.autobackup.dao.repo.TaskDao;
import fit.wenchao.autobackup.model.JsonResult;
import fit.wenchao.autobackup.model.vo.TaskVO;
import fit.wenchao.autobackup.service.TaskService;
import fit.wenchao.autobackup.utils.DefaultSchedulingConfigurer;
import fit.wenchao.autobackup.utils.ResponseEntityUtils;
import fit.wenchao.autobackup.utils.ThreadLocalUtils;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.util.List;
import java.util.stream.Collectors;

import static fit.wenchao.autobackup.interceptor.HandlerMethodInterceptor.HANDLER_METHOD_NAME;


@Slf4j
@Validated
@RestController
public class BackupController {

    @Data
    @Accessors(chain = true)
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class NewTask {
        @NotNull
        @NotEmpty
        String name;
        @NotNull
        @NotEmpty
        String cron;

        @NotNull
        @NotEmpty
        String type;

        Long dbConnId;

        String dbName;
    }

    @Resource
    private DefaultSchedulingConfigurer defaultSchedulingConfigurer;

    @Autowired
    TaskDao taskDao;

    @Autowired
    TaskService taskService;

    @Autowired
    Locks locks;

    public static enum BackupType {
        MySQL
    }

    @PostMapping("/task")
    public ResponseEntity<JsonResult> newTask(@NotNull @Validated NewTask newTask
    ) {

        synchronized (locks.getNewTaskLock()) {
            taskService.newTask(newTask);
        }
        return ResponseEntityUtils.ok(JsonResult.ok());
    }

    @PutMapping("/cancel-task")
    public ResponseEntity<JsonResult> cancelTask(@NotNull Long taskId) {
        synchronized (locks.getCancelTaskLock()) {
            taskService.cancelTask(taskId);
        }
        return ResponseEntityUtils.ok(JsonResult.ok());
    }

    @DeleteMapping("/task/{taskId}")
    public ResponseEntity<JsonResult> removeTask(@PathVariable Long taskId) {
        synchronized ((Locks.getStringLock(ThreadLocalUtils.getThreadInfo(HANDLER_METHOD_NAME)
        ,taskId.toString()))) {
            taskService.removeTask(taskId);
        }
        return ResponseEntityUtils.ok(JsonResult.ok());
    }

   @GetMapping("/tasks")
   public ResponseEntity<JsonResult> getTaskList() {
       List<TaskPO> list = taskDao.list();
       List<TaskVO>  volist = list.stream().map(task ->{
           TaskVO vo = new TaskVO();
           BeanUtils.copyProperties(task,vo);
           return vo;
       }).collect(Collectors.toList());
       return ResponseEntityUtils.ok(JsonResult.ok(volist));
   }

    @Autowired
    BackupLogDao backupLogDao;

    @GetMapping("/logs")
    public ResponseEntity<JsonResult> getLogList(@NotNull Long taskId) {
        List<BackupLogPO> list = backupLogDao.listByTaskId(taskId);
        return ResponseEntityUtils.ok(JsonResult.ok(list));
    }

}
