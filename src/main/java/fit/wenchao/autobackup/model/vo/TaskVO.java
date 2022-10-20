package fit.wenchao.autobackup.model.vo;


import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import fit.wenchao.autobackup.dao.po.TaskPO;

@JsonNaming(value = PropertyNamingStrategy.SnakeCaseStrategy.class)
public class TaskVO extends TaskPO {

}
