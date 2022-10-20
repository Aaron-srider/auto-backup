package fit.wenchao.autobackup.utils.dao;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;

public interface IBaseDao<T> extends IService<T> {

    T existsByPrimaryKey(T entity);

    <Q> Page<T> pageData(Q queryCondition, QueryWrapper<T> customCondition) ;
    <Q> Page<T> pageData(Q queryCondition, QueryWrapper<T> customCondition, BiConsumer<Integer, T> callback) ;

    <Q, R> Page<R> pageData(Q queryCondition, QueryWrapper<T> customCondition, BiFunction<Integer, T, R> callback) ;

    // getOne
    T getOneByMap(String col1, Object val1);

    T getOneByMap(String col1, Object val1, String col2, Object val2);

    T getOneByMap(String col1, Object val1, String col2, Object val2,
                  String col3, Object val3);

    T getOneByMap(String col1, Object val1, String col2, Object val2,
                  String col3, Object val3, String col4, Object val4);

    // getList
    List<T> listByMap(String col1, Object val1);

    List<T> listByMap(String col1, Object val1, String col2, Object val2);

    List<T> listByMap(String col1, Object val1, String col2, Object val2,
                      String col3, Object val3);

    List<T> listByMap(String col1, Object val1, String col2, Object val2,
                      String col3, Object val3, String col4, Object val4);

    // update

    Integer updateByMap(String setCol, Object setVal);

    Integer updateByMap(String col1, Object val1, String setCol, Object setVal);

    Integer updateByMap(String col1, Object val1, String col2, Object val2, String setCol, Object setVal);

    Integer updateByMap(String col1, Object val1, String col2, Object val2, String col3, Object val3, String setCol, Object setVal);

    Integer updateByMap(String col1, Object val1, String col2, Object val2, String col3, Object val3, String col4, Object val4, String setCol, Object setVal);

    // remove
    Integer removeByMap(String col1, Object val1);

    Integer removeByMap(String col1, Object val1, String col2, Object val2);

    Integer removeByMap(String col1, Object val1, String col2, Object val2, String col3, Object val3);

    Integer removeByMap(String col1, Object val1, String col2, Object val2, String col3, Object val3, String col4, Object val4);

    enum Like{
        RIGHT,
        LEFT
    }

    Boolean fieldUnique(Long id, String fieldName, Object value);

    // like right
    List<T> like(String col1, Object val1, Like like);

    List<T> like(String col1, Object val1, String col2, Object val2, Like like);

    List<T> like(String col1, Object val1, String col2, Object val2,
                      String col3, Object val3, Like like);

    List<T> like(String col1, Object val1, String col2, Object val2,
                      String col3, Object val3, String col4, Object val4, Like like);


}
