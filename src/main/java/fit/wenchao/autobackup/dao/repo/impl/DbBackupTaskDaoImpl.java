package fit.wenchao.autobackup.dao.repo.impl;

import fit.wenchao.autobackup.dao.po.DbBackupTaskPO;
import fit.wenchao.autobackup.dao.mapper.DbBackupTaskMapper;
import fit.wenchao.autobackup.dao.repo.DbBackupTaskDao;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;

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
public class DbBackupTaskDaoImpl extends ServiceImpl<DbBackupTaskMapper, DbBackupTaskPO> implements DbBackupTaskDao {

}
