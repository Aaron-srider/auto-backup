package fit.wenchao.autobackup.model;

public enum RespCode {

    SUCCESS("成功"), FRONT_END_PARAMS_ERROR("前端参数错误"), TASK_NAME_DUPLICATED("任务名不能相同"), GENERAL_ERROR("未知错误"), INVALID_CRON("cron 表达式格式错误"), NO_TASK("任务不存在"),
    CONN_NAME_DUPLICATED("数据库连接重名"), START_DB_BACKUP_FAILED("启动数据库备份任务失败"),
    BACKUP_GENERAL_ERROR("备份错误"),
    BACKUP_TYPE_NOT_SUPPORTED("不支持的备份类型"), NO_DBCONN("数据库连接信息不存在");


    private String msg;


    public String getMsg() {
        return msg;
    }

    private RespCode(String msg) {
        this.msg = msg;
    }


}
