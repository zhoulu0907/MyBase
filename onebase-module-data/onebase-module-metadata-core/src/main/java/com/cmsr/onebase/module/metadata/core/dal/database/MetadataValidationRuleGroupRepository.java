package com.cmsr.onebase.module.metadata.core.dal.database;

import com.cmsr.onebase.framework.aynline.DataRepository;
import com.cmsr.onebase.framework.common.pojo.PageResult;
// TODO: Repository层不应该直接依赖VO，应该使用DO或基础类型参数
// import com.cmsr.onebase.module.metadata.controller.admin.validation.vo.ValidationRuleGroupPageReqVO;
import com.cmsr.onebase.module.metadata.core.dal.dataobject.validation.MetadataValidationRuleGroupDO;
import lombok.extern.slf4j.Slf4j;
import org.anyline.data.param.ConfigStore;
import org.anyline.data.param.init.DefaultConfigStore;
import org.anyline.entity.Order;
import org.springframework.stereotype.Repository;
import org.springframework.util.StringUtils;

/**
 * 校验规则分组仓储类
 * <p>
 * 提供校验规则分组相关的数据库操作接口，继承自DataRepositoryNew获得基础的CRUD能力
 *
 * @author bty418
 * @date 2025-01-25
 */
@Repository
@Slf4j
public class MetadataValidationRuleGroupRepository extends DataRepository<MetadataValidationRuleGroupDO> {

    /**
     * 构造方法，指定默认实体类
     */
    public MetadataValidationRuleGroupRepository() {
        super(MetadataValidationRuleGroupDO.class);
    }

    /**
     * 分页查询校验规则分组
     *
     * @param reqVO 分页查询条件
     * @return 分页结果
     */
    // TODO: Repository层方法应该使用基础类型参数而不是VO
    // public PageResult<MetadataValidationRuleGroupDO> selectPage(ValidationRuleGroupPageReqVO reqVO) {
    public PageResult<MetadataValidationRuleGroupDO> selectPage(int pageNum, int pageSize, String name) {
        ConfigStore configStore = new DefaultConfigStore();

        // 添加查询条件
        if (StringUtils.hasText(name)) {
            configStore.like("rg_name", name);
        }

        // 添加排序
        configStore.order("create_time", Order.TYPE.DESC);

        // 设置分页参数
        configStore.limit(pageNum, pageSize);

        // 执行分页查询
        return findPageWithConditions(configStore, pageNum, pageSize);
    }

    /**
     * 根据规则组名称查询（用于校验名称唯一性）
     *
     * @param rgName 规则组名称
     * @param excludeId 排除的ID（用于修改时排除自身）
     * @return 校验规则分组对象
     */
    public MetadataValidationRuleGroupDO selectByRgName(String rgName, Long excludeId) {
        ConfigStore configStore = new DefaultConfigStore();
        configStore.eq("rg_name", rgName);
        if (excludeId != null) {
            configStore.ne("id", excludeId);
        }
        return findOne(configStore);
    }

}
