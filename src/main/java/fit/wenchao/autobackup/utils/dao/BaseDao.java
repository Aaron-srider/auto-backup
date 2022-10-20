package fit.wenchao.autobackup.utils.dao;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import fit.wenchao.autobackup.exception.BackendException;
import fit.wenchao.autobackup.model.RespCode;
import fit.wenchao.autobackup.utils.VarCaseConvertUtils;
import lombok.extern.slf4j.Slf4j;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;


@Slf4j
public class BaseDao<M extends BaseMapper<T>, T> extends ServiceImpl<M, T> implements IBaseDao<T> {

    @Override
    public T existsByPrimaryKey(T entity) {
        Class<?> entityClass = entity.getClass();
        Field[] declaredFields = entityClass.getDeclaredFields();
        Map<String, Object> primaryKeyValues = new HashMap<>();
        Object priKeyValue;
        for (Field declaredField : declaredFields) {
            log.debug("检查类：{} 的 {} 字段是否是主键字段", entityClass.getName(), declaredField.getName());
            declaredField.setAccessible(true);
            PrimaryKey primaryKeyAnno = declaredField.getAnnotation(PrimaryKey.class);
            if (primaryKeyAnno != null) {
                log.debug("类：{} 的 {} 字段是主键字段", entityClass.getName(), declaredField.getName());
                try {
                    priKeyValue = declaredField.get(entity);
                } catch (IllegalAccessException e) {
                    throw new RuntimeException(e);
                }
                log.debug("获取主键字段的值为：{}", priKeyValue);
                String dbFieldName = VarCaseConvertUtils.lowerCamel2LowerUnderScore(declaredField.getName());
                primaryKeyValues.put(dbFieldName, priKeyValue);
                log.debug("参数：{} => {} 放入map中", dbFieldName, priKeyValue);
            }
        }
        log.debug("实体类：{} 所有主键字段的值集合：{}", entityClass.getName(), primaryKeyValues);

        log.debug("构造查询条件");
        QueryWrapper<T> queryWrapper = constructQueryConditionByMap(primaryKeyValues);
        log.debug("执行查询");

        T oneEntityFromDB = this.getOne(queryWrapper, false);
        log.debug("主键查询操作结果：{}", oneEntityFromDB);
        return oneEntityFromDB;
    }

    private QueryWrapper<T> constructQueryConditionByMap(Map<String, Object> primaryKeyValues) {
        QueryWrapper<T> queryWrapper = new QueryWrapper<>();
        primaryKeyValues.forEach((dbFieldName, priKeyValue) -> {
            if (priKeyValue == null) {
                log.warn("主键字段：{} 的值为null，将不作为查询条件", dbFieldName);
            } else {
                log.debug("添加查询条件：{}={}", dbFieldName, priKeyValue);
                queryWrapper.eq(dbFieldName, priKeyValue);
            }
        });
        log.debug("最终查询条件：{}", queryWrapper);
        return queryWrapper;
    }

    public enum ConditionEnum {
        EQ,
        GT,
        LT, GE, LE, LIKE, RIGHT_LIKE, LEFT_LIKE, BETWEEN
    }

    public void condition(QueryWrapper<T> queryWrapper, String col, ConditionEnum condition, Object value1, Object value2) {
        if (condition == null) {
            return;
        }
        log.debug("Use \" {} {} {} \" as query condition", col, condition, value1);
        switch (condition) {
            case EQ:
                queryWrapper.eq(col, value1);
                break;
            case GT:
                queryWrapper.gt(col, value1);
                break;
            case LT:
                queryWrapper.lt(col, value1);
                break;
            case GE:
                queryWrapper.ge(col, value1);
                break;
            case LE:
                queryWrapper.le(col, value1);
                break;
            case LIKE:
                queryWrapper.like(col, value1);
                break;
            case RIGHT_LIKE:
                queryWrapper.likeRight(col, value1);
                break;
            case LEFT_LIKE:
                queryWrapper.likeLeft(col, value1);
                break;
            // 暂时用不上
            case BETWEEN:
                queryWrapper.between(col, value1, value2);
                break;
        }
    }


    private <Q> Page<T> doPage(Q queryCondition, QueryWrapper<T> customCondition) {

        if (customCondition == null) {
            customCondition = new QueryWrapper<>();
        }

        Page<T> tPage = new Page<>();
        constructQueryCondition(queryCondition, customCondition, tPage);

        Class<?> aClass = queryCondition.getClass();
        PageConfig annotation = aClass.getAnnotation(PageConfig.class);
        if (annotation == null) {
            log.error("分页实体类：{}， 没有标注PageConfig注解", aClass.getName());
            throw new BackendException(null, RespCode.GENERAL_ERROR);
        }

        long pageNo = tPage.getPageNo();
        long pageSize = tPage.getPageSize();

        com.baomidou.mybatisplus.extension.plugins.pagination.Page<T> mybatisPage = new com.baomidou.mybatisplus.extension.plugins.pagination.Page<>();
        mybatisPage.setCurrent(pageNo);
        mybatisPage.setSize(pageSize);
        this.page(mybatisPage, customCondition);

        List<T> records = mybatisPage.getRecords();
        tPage.setTotal(mybatisPage.getTotal());
        tPage.setRecords(records);
        return tPage;
    }

    private <Q> QueryWrapper<T> constructQueryCondition(Q queryCondition, QueryWrapper<T> customCondition, Page<T> page) {

        Class<?> aClass = queryCondition.getClass();
        Field[] declaredFields = aClass.getDeclaredFields();
        for (Field declaredField : declaredFields) {
            declaredField.setAccessible(true);

            boolean conditionField = true;

            Object conditionValue = null;
            try {
                conditionValue = declaredField.get(queryCondition);
            } catch (IllegalAccessException e) {
                throw new RuntimeException(e);
            }

            // 如果该字段标注了PageSize注解且不为空，那么该字段的值作为pageSize
            PageSize pageSizeAnno = declaredField.getAnnotation(PageSize.class);
            if (pageSizeAnno != null) {
                if (conditionValue != null && declaredField.getType().equals(Long.class)) {
                    page.setPageSize((Long) conditionValue);
                    log.debug("检测到分页大小：{}", conditionValue);
                    conditionField = false;
                }
            }

            // 如果该字段标注了PageSize注解且不为空，那么该字段的值作为pageSize
            PageNo pageNoAnno = declaredField.getAnnotation(PageNo.class);
            if (pageNoAnno != null) {
                if (conditionValue != null && declaredField.getType().equals(Long.class)) {
                    page.setPageNo((Long) conditionValue);
                    log.debug("检测到分页页数：{}", conditionValue);
                    conditionField = false;
                }
            }

            if (conditionField) {
                if (conditionValue != null) {
                    Condition conditionAnno = declaredField.getAnnotation(Condition.class);
                    String underScoreName = null;
                    if (conditionAnno == null) {
                        underScoreName = VarCaseConvertUtils.lowerCamel2LowerUnderScore(declaredField.getName());
                        // 默认使用 = 作为查询条件
                        customCondition.eq(underScoreName, conditionValue);
                    } else {
                        // 否则使用注解指定的查询条件
                        if (conditionAnno.dbFieldName().equals("")) {
                            underScoreName = VarCaseConvertUtils.lowerCamel2LowerUnderScore(declaredField.getName());
                        } else {
                            underScoreName = conditionAnno.dbFieldName();
                        }
                        condition(customCondition, underScoreName, conditionAnno.con(), conditionValue, null);
                    }
                }
            }

        }

        // 如果没有标注PageSize和PageNo的字段，使用默认值
        if (page.getPageSize() == null) {
            page.setPageSize(10L);
        }
        if (page.getPageNo() == null) {
            page.setPageNo(1L);
        }
        return customCondition;
    }

    @Override
    public <Q> Page<T> pageData(Q queryCondition, QueryWrapper<T> customCondition) {
        return this.pageData(queryCondition, customCondition, (idx, row) -> {
        });
    }

    @Override
    public <Q> Page<T> pageData(Q queryCondition, QueryWrapper<T> customCondition, BiConsumer<Integer, T> callback) {
        Page<T> page = this.doPage(queryCondition, customCondition);
        List<T> records = page.getRecords();
        if (callback != null) {
            for (int i = 0; i < records.size(); i++) {
                callback.accept(i, records.get(i));
            }
        }
        return page;
    }

    @Override
    public <Q, R> Page<R> pageData(Q queryCondition, QueryWrapper<T> customCondition, BiFunction<Integer, T, R> callback) {
        Page<T> page = this.doPage(queryCondition, customCondition);
        List<T> records = page.getRecords();
        List<R> mapRecords = new ArrayList<>();
        if (callback != null) {
            for (int i = 0; i < records.size(); i++) {
                mapRecords.add(callback.apply(i, records.get(i)));
            }
        }
        Page<R> rPage = new Page<>();
        rPage.setRecords(mapRecords);
        rPage.setTotal(page.getTotal());
        return rPage;
    }

    // getOne
    @Override
    public T getOneByMap(String col1, Object val1) {
        QueryWrapper<T> tQueryWrapper = new QueryWrapper<>();
        QueryWrapper<T> eq = tQueryWrapper.eq(col1, val1);
        T one = this.getOne(eq, false);
        return one;
    }

    @Override
    public T getOneByMap(String col1, Object val1, String col2, Object val2) {
        QueryWrapper<T> tQueryWrapper = new QueryWrapper<>();
        QueryWrapper<T> eq = tQueryWrapper.eq(col1, val1).eq(col2, val2);
        T one = this.getOne(eq, false);
        return one;
    }

    @Override
    public T getOneByMap(String col1, Object val1, String col2, Object val2,
                         String col3, Object val3) {
        QueryWrapper<T> tQueryWrapper = new QueryWrapper<>();
        QueryWrapper<T> eq = tQueryWrapper.eq(col1, val1).eq(col2, val2).eq(col3, val3);
        T one = this.getOne(eq, false);
        return one;
    }

    @Override
    public T getOneByMap(String col1, Object val1, String col2, Object val2,
                         String col3, Object val3, String col4, Object val4) {
        QueryWrapper<T> tQueryWrapper = new QueryWrapper<>();
        QueryWrapper<T> eq = tQueryWrapper.eq(col1, val1).eq(col2, val2).eq(col3, val3).eq(col4, val4);
        T one = this.getOne(eq, false);
        return one;
    }

    // getList
    @Override
    public List<T> listByMap(String col1, Object val1) {
        QueryWrapper<T> tQueryWrapper = new QueryWrapper<>();
        QueryWrapper<T> eq = tQueryWrapper.eq(col1, val1);
        List<T> list = this.list(eq);
        return list;
    }

    @Override
    public List<T> listByMap(String col1, Object val1, String col2, Object val2) {
        QueryWrapper<T> tQueryWrapper = new QueryWrapper<>();
        QueryWrapper<T> eq = tQueryWrapper.eq(col1, val1).eq(col2, val2);
        List<T> list = this.list(eq);
        return list;
    }

    @Override
    public List<T> listByMap(String col1, Object val1, String col2, Object val2,
                             String col3, Object val3) {
        QueryWrapper<T> tQueryWrapper = new QueryWrapper<>();
        QueryWrapper<T> eq = tQueryWrapper.eq(col1, val1).eq(col2, val2).eq(col3, val3);
        List<T> list = this.list(eq);
        return list;
    }

    @Override
    public List<T> listByMap(String col1, Object val1, String col2, Object val2,
                             String col3, Object val3, String col4, Object val4) {
        QueryWrapper<T> tQueryWrapper = new QueryWrapper<>();
        QueryWrapper<T> eq = tQueryWrapper.eq(col1, val1).eq(col2, val2).eq(col3, val3).eq(col4, val4);
        List<T> list = this.list(eq);
        return list;
    }

    @Override
    public Integer updateByMap(String setCol, Object setVal) {
        UpdateWrapper<T> updateWrapper = new UpdateWrapper<>();
        UpdateWrapper<T> eq = updateWrapper.set(setCol, setVal);
        return this.baseMapper.update(null, eq);
    }

    // update
    @Override
    public Integer updateByMap(String col1, Object val1, String setCol, Object setVal) {
        UpdateWrapper<T> updateWrapper = new UpdateWrapper<>();
        UpdateWrapper<T> eq = updateWrapper.eq(col1, val1).set(setCol, setVal);
        return this.baseMapper.update(null, eq);
    }

    @Override
    public Integer updateByMap(String col1, Object val1, String col2, Object val2, String setCol, Object setVal) {
        UpdateWrapper<T> updateWrapper = new UpdateWrapper<>();
        UpdateWrapper<T> eq = updateWrapper.eq(col1, val1).eq(col2, val2).set(setCol, setVal);
        return this.baseMapper.update(null, eq);
    }

    @Override
    public Integer updateByMap(String col1, Object val1, String col2, Object val2, String col3, Object val3, String setCol, Object setVal) {
        UpdateWrapper<T> updateWrapper = new UpdateWrapper<>();
        UpdateWrapper<T> eq = updateWrapper.eq(col1, val1).eq(col2, val2).eq(col3, val3).set(setCol, setVal);
        return this.baseMapper.update(null, eq);
    }

    @Override
    public Integer updateByMap(String col1, Object val1, String col2, Object val2, String col3, Object val3, String col4, Object val4, String setCol, Object setVal) {
        UpdateWrapper<T> updateWrapper = new UpdateWrapper<>();
        UpdateWrapper<T> eq = updateWrapper.eq(col1, val1).eq(col2, val2).eq(col3, val3).eq(col4, val4).set(setCol, setVal);
        return this.baseMapper.update(null, eq);
    }

    // remove
    @Override
    public Integer removeByMap(String col1, Object val1) {
        Map<String, Object> map = new HashMap<>();
        map.put(col1, val1);
        return this.baseMapper.deleteByMap(map);
    }

    @Override
    public Integer removeByMap(String col1, Object val1, String col2, Object val2) {
        Map<String, Object> map = new HashMap<>();
        map.put(col1, val1);
        map.put(col2, val2);
        return this.baseMapper.deleteByMap(map);
    }

    @Override
    public Integer removeByMap(String col1, Object val1, String col2, Object val2, String col3, Object val3) {
        Map<String, Object> map = new HashMap<>();
        map.put(col1, val1);
        map.put(col2, val2);
        map.put(col3, val3);
        return this.baseMapper.deleteByMap(map);
    }

    @Override
    public Integer removeByMap(String col1, Object val1, String col2, Object val2, String col3, Object val3, String col4, Object val4) {
        Map<String, Object> map = new HashMap<>();
        map.put(col1, val1);
        map.put(col2, val2);
        map.put(col3, val3);
        map.put(col4, val4);
        return this.baseMapper.deleteByMap(map);
    }

    @Override
    public Boolean fieldUnique(Long id, String fieldName, Object value) {
        QueryWrapper<T> queryWrapper = new QueryWrapper<>();
        queryWrapper.ne("id", id).eq(fieldName, value);
        T one = this.getOne(queryWrapper);
        if (one != null) {
            return false;
        }
        return true;
    }

    @Override
    public List<T> like(String col1, Object val1, Like like) {
        QueryWrapper<T> tQueryWrapper = new QueryWrapper<>();
        QueryWrapper<T> eq = null;
        if (like.equals(Like.RIGHT)) {
            eq = tQueryWrapper.likeRight(col1, val1);
        } else {
            eq = tQueryWrapper.likeLeft(col1, val1);
        }

        return this.list(tQueryWrapper);
    }

    @Override
    public List<T> like(String col1, Object val1, String col2, Object val2, Like like) {
        QueryWrapper<T> tQueryWrapper = new QueryWrapper<>();
        QueryWrapper<T> eq = null;
        if (like.equals(Like.RIGHT)) {
            eq = tQueryWrapper.likeRight(col1, val1).likeRight(col2, val2);
        } else {
            eq = tQueryWrapper.likeLeft(col1, val1).likeLeft(col2, val2);
        }

        return this.list(tQueryWrapper);
    }

    @Override
    public List<T> like(String col1, Object val1, String col2, Object val2,
                        String col3, Object val3, Like like) {
        QueryWrapper<T> tQueryWrapper = new QueryWrapper<>();
        QueryWrapper<T> eq = null;
        if (like.equals(Like.RIGHT)) {
            eq = tQueryWrapper.likeRight(col1, val1).likeRight(col2, val2).likeRight(col3, val3);
        } else {
            eq = tQueryWrapper.likeLeft(col1, val1).likeLeft(col2, val2).likeLeft(col3, val3);
        }

        return this.list(tQueryWrapper);
    }

    @Override
    public List<T> like(String col1, Object val1, String col2, Object val2,
                        String col3, Object val3, String col4, Object val4, Like like) {
        QueryWrapper<T> tQueryWrapper = new QueryWrapper<>();
        QueryWrapper<T> eq = null;
        if (like.equals(Like.RIGHT)) {
            eq = tQueryWrapper.likeRight(col1, val1).likeRight(col2, val2).likeRight(col3, val3).likeRight(col4, val4);
        } else {
            eq = tQueryWrapper.likeLeft(col1, val1).likeLeft(col2, val2).likeLeft(col3, val3).likeLeft(col4, val4);
        }

        return this.list(tQueryWrapper);
    }

}