package fit.wenchao.autobackup.dao.repo.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import fit.wenchao.autobackup.dao.po.BackupLogPO;
import fit.wenchao.autobackup.dao.mapper.BackupLogMapper;
import fit.wenchao.autobackup.dao.repo.BackupLogDao;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;

import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * <p>
 *  dao实现类
 * </p>
 *
 * @author wc
 * @since 2022-09-13
 */
@Repository
public class BackupLogDaoImpl extends ServiceImpl<BackupLogMapper, BackupLogPO> implements BackupLogDao {

    @Override
    public List<BackupLogPO> listByTaskId(Long taskId) {
        QueryWrapper<BackupLogPO> query = new QueryWrapper<BackupLogPO>().eq("task_id", taskId);
        return this.list(query);
    }
}
