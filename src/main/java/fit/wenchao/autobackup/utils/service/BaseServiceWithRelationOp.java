package fit.wenchao.autobackup.utils.service;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.google.common.base.CaseFormat;
import fit.wenchao.autobackup.exception.BackendException;
import fit.wenchao.autobackup.model.RespCode;
import fit.wenchao.autobackup.utils.ClassUtils;
import fit.wenchao.autobackup.utils.VarCaseConvertUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.PostConstruct;
import java.io.Serializable;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 * <p>
 * 简介：Service类的一个工具基类，用于简化关系表之间的查询（目前只支持两张关系表的查询）。目前简化的操作有：根据左表中的一条数据查询右表中的
 * 多条数据，根据右表中的一条数据查询左表中的多条数据，查询字段均为主键，且为类型为Long。
 * </p>
 *
 * <p>
 * 实现原理： 使用反射自动根据关系实体类名推断出关联的左表和右表表名、关系表中关系字段的名称，从而达到自动查询的目的。
 * </p>
 *
 * <p>
 * 适用条件：关系表名和关系实体类有两部分组成：Left和Right，Left表示关系表关联的左表名，Right表示右表名。比如左表：user，右表：dept，
 * 则关系表名应该为：user_dept，关系表中的关系字段应该为：user_id、dept_id，实体类表名应该为：UserDeptXX，Service类名应该为
 * ：UserDeptService。
 * </p>
 *
 * @param <M>  关系表Mapper
 * @param <T>  关系实体
 * @param <LM> 左表Mapper
 * @param <RM> 右表Mapper
 * @param <L>  左表实体
 * @param <R>  右表实体
 */
@Slf4j
public class BaseServiceWithRelationOp<M extends BaseMapper<T>, T, LM extends BaseMapper<L>, L, RM extends BaseMapper<R>, R>
        implements IBaseServiceWithRelationOp<T, L, R> {

    Class<T> relationPOClass;

    @PostConstruct
    public void construct() {
        RelationPOClass fieldAnnotation = ClassUtils.getFieldAnnotation(RelationPOClass.class, this.getClass());
        if (fieldAnnotation == null) {
            log.warn("Service ：{} 配置错误，请使用RelationPOClass注解标注关系实体类字段", this.getClass().getName());
            throw new BackendException(null, RespCode.GENERAL_ERROR);
        }
        Field fieldAnnotatedBy = ClassUtils.getFieldAnnotatedBy(RelationPOClass.class, this.getClass());
        fieldAnnotatedBy.setAccessible(true);
        Object o = null;
        try {
            o = fieldAnnotatedBy.get(this);
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }
        if (o instanceof Class) {
            this.relationPOClass = (Class<T>) o;
        } else {
            log.warn("Service ：{} 配置错误，请使用RelationPOClass注解标注关系实体类字段", this.getClass().getName());
            throw new BackendException(null, RespCode.GENERAL_ERROR);
        }
    }


    // 字段报错正常，使用时容器中的Service继承该类，可以出发autowired自动装配
    @Autowired
    M relationMapper;

    @Autowired
    LM leftMapper;

    @Autowired
    RM rightMapper;

    @Transactional
    public List<L> getLeftListByRightId(Serializable rightid) {

        if (relationPOClass == null) {
            log.warn("Service ：{} 配置错误，请在构造该对象时传入正确的关系实体Class对象", this.getClass().getName());
            throw new BackendException(null, RespCode.GENERAL_ERROR);
        }

        // 实体类中对应的左表字段名，驼峰
        String leftIdFieldName = parseLeft(relationPOClass.getSimpleName());

        // 实体类中对应的右表字段名，驼峰
        String rightFieldIdName = parseRight(relationPOClass.getSimpleName());

        R rightPO = rightMapper.selectById(rightid);

        if (rightPO == null) {
            return new ArrayList<L>();
        }

        Map<String, Object> map = new HashMap<>();
        map.put(CaseFormat.LOWER_CAMEL.to(CaseFormat.LOWER_UNDERSCORE, rightFieldIdName), rightid);

        List<T> relationPOs = relationMapper.selectByMap(map);

        // 初始化列表，避免返回空值
        List<L> result = new ArrayList<>();
        relationPOs.forEach((relationPO) -> {
            Serializable leftid = null;
            try {
                leftid = getFieldValueFromRelationPO(relationPO, leftIdFieldName);
            } catch (NoSuchFieldException e) {
                log.warn("关系表查询失败，在实体类：{} 中 未找到字段：{}，请检查您的实体类：{} 字段：{} 的命名是否符合 {} 类的要求",
                        relationPO.getClass(), leftIdFieldName, relationPO.getClass(), leftIdFieldName, this.getClass());
            } catch (IllegalAccessException e) {
                log.warn("关系表查询中出现：{}异常", e.getMessage());
            }
            L leftPO = leftMapper.selectById(leftid);
            if (leftPO != null) {
                result.add(leftPO);
            }
        });

        return result;
    }

    @Override
    public List<R> getRightListByLeftId(Serializable leftid) {

        if (relationPOClass == null) {
            log.warn("Service ：{} 配置错误，请在构造该对象时传入正确的关系实体Class对象", this.getClass().getName());
            throw new BackendException(null, RespCode.GENERAL_ERROR);
        }

        String leftIdFieldName = parseLeft(relationPOClass.getSimpleName());
        String rightFieldIdName = parseRight(relationPOClass.getSimpleName());

        L leftPO = leftMapper.selectById(leftid);

        if (leftPO == null) {
            return new ArrayList<R>();
        }

        Map<String, Object> map = new HashMap<>();
        map.put(CaseFormat.LOWER_CAMEL.to(CaseFormat.LOWER_UNDERSCORE, leftIdFieldName), leftid);
        List<T> relationPOs = relationMapper.selectByMap(map);

        List<R> result = new ArrayList<>();
        relationPOs.forEach((relationPO) -> {
            Serializable rightid = null;
            try {
                rightid = getFieldValueFromRelationPO(relationPO, rightFieldIdName);
            } catch (NoSuchFieldException e) {
                log.warn("关系表查询失败，在实体类：{} 中 未找到字段：{}，请检查您的实体类：{} 字段：{} 的命名是否符合 {} 类的要求",
                        relationPO.getClass(), rightFieldIdName, relationPO.getClass(), rightFieldIdName, this.getClass());
            } catch (IllegalAccessException e) {
                log.warn("关系表查询中出现：{}异常", e.getMessage());
            }
            R rightPO = rightMapper.selectById(rightid);
            if (rightPO != null) {
                result.add(rightPO);
            }
        });

        return result;
    }

    private Serializable getFieldValueFromRelationPO(T relationPO, String fieldName) throws NoSuchFieldException, IllegalAccessException {
        Class<?> aClass = relationPO.getClass();
        Field leftIdField = null;
        try {
            leftIdField = aClass.getDeclaredField(fieldName);
        } catch (NoSuchFieldException noSuchFieldException) {
            log.error("获取字段:{}值失败，目标实体类:{}中无该字段", fieldName, aClass.getName());
            throw noSuchFieldException;
        }

        leftIdField.setAccessible(true);
        Object leftIdValue = leftIdField.get(relationPO);
        if (leftIdValue == null) {
            return null;
        }

        if (leftIdValue instanceof Integer) {
            return ((Integer) leftIdValue).longValue();
        }

        if (leftIdValue instanceof Long) {
            return (Long) leftIdValue;
        }

        if (leftIdValue instanceof String) {
            return (String) leftIdValue;
        }

        // 暂时不支持其他的id类型
        log.warn("获取字段:{}值失败，目标字段类型是:{}，暂时只支持Integer和Long类型", fieldName, leftIdField.getType().getName());
        return null;
    }

    private String parseLeft(String relationPOSimpleName) {

        RelationLeft relationLeftAnnotation = ClassUtils.getFieldAnnotation(RelationLeft.class, this.relationPOClass);
        if (relationLeftAnnotation != null) {
            String value = relationLeftAnnotation.value();
            return VarCaseConvertUtils.lowerUnderScore2LowerCamel(value);
        }

        String lowerCamel = VarCaseConvertUtils.lowerCamel2LowerUnderScore(relationPOSimpleName);
        if (!lowerCamel.contains("_")) {
            throw new RuntimeException("类名:" + relationPOSimpleName + "不规范, 驼峰转下划线失败");
        }

        String[] s = lowerCamel.split("_");
        if (s.length < 2) {
            throw new RuntimeException("实体类" + relationPOSimpleName + "命名不规范，获取左右表名失败");
        }
        String left = s[0];
        String right = s[1];
        return left + "Id";
    }

    private String parseRight(String relationPOSimpleName) {

        RelationRight relationRightAnnotation = ClassUtils.getFieldAnnotation(RelationRight.class, this.relationPOClass);
        if (relationRightAnnotation != null) {
            String value = relationRightAnnotation.value();
            return VarCaseConvertUtils.lowerUnderScore2LowerCamel(value);
        }

        String lowerCamel = VarCaseConvertUtils.lowerCamel2LowerUnderScore(relationPOSimpleName);
        if (!lowerCamel.contains("_")) {
            throw new RuntimeException("类名:" + relationPOSimpleName + "不规范, 驼峰转下划线失败");
        }
        String[] s = lowerCamel.split("_");
        if (s.length < 2) {
            throw new RuntimeException("实体类" + relationPOSimpleName + "命名不规范，获取左右表名失败");

        }
        String left = s[0];
        String right = s[1];
        return right + "Id";
    }

    public static void main(String[] args) {
        ArrayList<String> arrayList = new ArrayList<>();
        ArrayList<Integer> arrayList1 = new ArrayList<>();

        System.out.println(arrayList.getClass());
        System.out.println(arrayList1.getClass());
        System.out.println(arrayList.getClass().equals(arrayList1.getClass()));
    }


}