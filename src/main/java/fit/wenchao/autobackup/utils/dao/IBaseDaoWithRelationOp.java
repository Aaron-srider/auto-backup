package fit.wenchao.autobackup.utils.dao;

import java.util.List;

public interface IBaseDaoWithRelationOp<T, L, R> extends IBaseDao<T> {
    List<L> getLeftListByRightId(Long rightid);

    List<R> getRightListByLeftId(Long leftid);
}

