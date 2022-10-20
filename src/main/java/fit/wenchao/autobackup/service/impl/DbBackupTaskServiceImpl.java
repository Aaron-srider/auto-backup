package fit.wenchao.autobackup.service.impl;

import fit.wenchao.autobackup.dao.repo.DbBackupTaskDao;
import fit.wenchao.autobackup.service.DbBackupTaskService;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * <p>
 * DbBackupTask 服务类
 * </p>
 *
 * @author wc
 * @since 2022-09-13
 */
@Service
@Slf4j
public class DbBackupTaskServiceImpl implements DbBackupTaskService{

    @Autowired
    DbBackupTaskDao dbBackupTaskDao;

}


