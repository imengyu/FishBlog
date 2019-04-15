package com.dreamfish.fishblog.core.utils.response;

import java.io.Serializable;
import java.util.List;

public class IncludeSizeListResult<T> implements Serializable {

    private static final long serialVersionUID = 5653053017524768052L;

    public IncludeSizeListResult(List<T> list, int allCount) {
        this.list = list;
        this.allCount = allCount;
    }

    private List<T> list;
    private int allCount;

    public List<T> getList() {
        return list;
    }

    public void setList(List<T> list) {
        this.list = list;
    }

    public int getAllCount() {
        return allCount;
    }

    public void setAllCount(int allCount) {
        this.allCount = allCount;
    }
}
