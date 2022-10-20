package fit.wenchao.autobackup.dao.repo;

import fit.wenchao.autobackup.dao.po.BackupLogPO;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

/**
 * <p>
 *  dao类
 * </p>
 *
 * @author wc
 * @since 2022-09-13
 */
public interface BackupLogDao extends IService<BackupLogPO> {

    List<BackupLogPO> listByTaskId(Long taskId);
}
