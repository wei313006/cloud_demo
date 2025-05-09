package common.core.entity;

import lombok.Data;

import java.util.List;

@Data
public class DataByPaging<T> {

    private long count;

    private List<T> data;
}
