package com.cmsr.onebase.framework.data.base;

/**
 *
 * 用于定义基础实体对象的接口，作为实现类的基础接口。
 *
 * @author liyang
 * @date 2025/10/10
 */
public interface BaseDOInterface {

    /**
     *
     * 需要实现该接口，返回实体的id，用于更新等操作。
     *
     * @return 实体的id
     */
    Long getId();
}
