package com.cmsr.onebase.framework.orm.data;


import com.mybatisflex.annotation.Column;
import lombok.Data;

/**
 * 字段：ID、创建人、创建时间、更新人、更新时间、删除标识、乐观锁、租户ID、应用ID、版本ID、版本状态；
 * <p>
 * 适用场景：需要参与应用发布及版本管理的相关实体；
 */
@Data
public class BaseBizEntity extends BaseTenantEntity {

    @Column(value = "version_tag", comment = "版本标签")
    private Long versionTag;

}
