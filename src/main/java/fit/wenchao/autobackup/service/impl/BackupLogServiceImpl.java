package fit.wenchao.autobackup.service.impl;

import fit.wenchao.autobackup.dao.repo.BackupLogDao;
import fit.wenchao.autobackup.service.BackupLogService;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * <p>
 * BackupLog 服务类
 * </p>
 *
 * @author wc
 * @since 2022-09-13
 */
@Service
@Slf4j
public class BackupLogServiceImpl implements BackupLogService{

    @Autowired
    BackupLogDao backupLogDao;

}


