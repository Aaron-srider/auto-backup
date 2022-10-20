package fit.wenchao.autobackup.dao.repo.impl;

import fit.wenchao.autobackup.dao.po.TaskPO;
import fit.wenchao.autobackup.dao.mapper.TaskMapper;
import fit.wenchao.autobackup.dao.repo.TaskDao;

import fit.wenchao.autobackup.utils.dao.BaseDao;
import org.springframework.stereotype.Repository;

/**
 * <p>
 *  dao实现类
 * </p>
 *
 * @author wc
 * @since 2022-09-13
 */
@Repository
public class TaskDaoImpl extends BaseDao<TaskMapper, TaskPO> implements TaskDao {

}
