package com.cmsr.onebase.framework.orm.entity;

import com.mybatisflex.annotation.Column;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * 适用场景：需要参与应用发布及版本管理的相关实体；
 *
 * @author liyang
 * @date 2025-11-29
 */
@NoArgsConstructor
@AllArgsConstructor
public class WarmFlowBizEntity extends WarmFlowBaseEntity {

    @Getter
    @Column(value = "application_id", comment = "应用ID")
    protected Long applicationId;

    @Getter
    @Column(value = "version_tag", comment = "版本标签")
    protected Long versionTag;
}

