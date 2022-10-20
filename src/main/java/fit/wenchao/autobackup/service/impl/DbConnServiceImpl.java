package fit.wenchao.autobackup.service.impl;

import fit.wenchao.autobackup.dao.repo.DbConnDao;
import fit.wenchao.autobackup.service.DbConnService;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * <p>
 * DbConn 服务类
 * </p>
 *
 * @author wc
 * @since 2022-09-13
 */
@Service
@Slf4j
public class DbConnServiceImpl implements DbConnService{

    @Autowired
    DbConnDao dbConnDao;

}


