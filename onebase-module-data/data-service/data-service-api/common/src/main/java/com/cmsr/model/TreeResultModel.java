package com.cmsr.model;

import java.util.List;

public interface TreeResultModel<T extends TreeResultModel<T>> {

    void setChildren(List<T> children);
}
