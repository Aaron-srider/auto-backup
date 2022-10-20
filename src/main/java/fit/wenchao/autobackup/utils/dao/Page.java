package fit.wenchao.autobackup.utils.dao;

import lombok.Data;

import java.util.List;

@Data
public
class Page<T> {
    private Long pageSize;
    private Long pageNo;
    private List<T> records;
    private Long total;
}