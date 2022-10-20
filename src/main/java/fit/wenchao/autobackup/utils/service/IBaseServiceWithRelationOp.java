package fit.wenchao.autobackup.utils.service;

import java.io.Serializable;
import java.util.List;

public interface IBaseServiceWithRelationOp<T, L, R> {
    List<L> getLeftListByRightId(Serializable rightid);

    List<R> getRightListByLeftId(Serializable leftid);
}