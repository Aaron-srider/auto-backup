package fit.wenchao.autobackup.dao.repo.impl;

import fit.wenchao.autobackup.dao.po.DbConnPO;
import fit.wenchao.autobackup.dao.mapper.DbConnMapper;
import fit.wenchao.autobackup.dao.repo.DbConnDao;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;

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
public class DbConnDaoImpl extends BaseDao<DbConnMapper, DbConnPO> implements DbConnDao {

}
