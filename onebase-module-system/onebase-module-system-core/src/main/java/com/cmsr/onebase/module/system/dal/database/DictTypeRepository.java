package com.cmsr.onebase.module.system.dal.database;

import com.cmsr.onebase.framework.aynline.DataRepository;
import com.cmsr.onebase.framework.common.pojo.PageResult;
import com.cmsr.onebase.module.system.vo.dicttype.DictTypePageReqVO;
import com.cmsr.onebase.module.system.dal.dataobject.dict.DictTypeDO;
import org.anyline.data.param.init.DefaultConfigStore;
import org.anyline.entity.Compare;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 字典类型数据访问层
 *
 * 负责字典类型相关的数据操作，继承DataRepositoryNew，提供标准CRUD能力。
 *
 * @author matianyu
 * @date 2025-08-07
 */
@Repository
public class DictTypeRepository extends DataRepository<DictTypeDO> {
    /**
     * 构造方法，指定默认实体类
     */
    public DictTypeRepository() {
        super(DictTypeDO.class);
    }

    /**
     * 分页查询字典类型
     *
     * @param reqVO 分页查询条件
     * @return 分页结果
     */
    public PageResult<DictTypeDO> findPage(DictTypePageReqVO reqVO) {
        DefaultConfigStore configs = new DefaultConfigStore();

        // 构建查询条件
        if (reqVO.getName() != null && !reqVO.getName().trim().isEmpty()) {
            configs.and(Compare.LIKE, DictTypeDO.NAME, reqVO.getName());
        }
        if (reqVO.getType() != null && !reqVO.getType().trim().isEmpty()) {
            configs.and(Compare.LIKE, DictTypeDO.TYPE, reqVO.getType());
        }
        if (reqVO.getStatus() != null) {
            configs.and(Compare.EQUAL, DictTypeDO.STATUS, reqVO.getStatus());
        }
        if (reqVO.getCreateTime() != null && reqVO.getCreateTime().length == 2) {
            LocalDateTime startTime = reqVO.getCreateTime()[0];
            LocalDateTime endTime = reqVO.getCreateTime()[1];
            if (startTime != null) {
                configs.and(Compare.GREAT_EQUAL, DictTypeDO.CREATE_TIME, startTime);
            }
            if (endTime != null) {
                configs.and(Compare.LESS_EQUAL, DictTypeDO.CREATE_TIME, endTime);
            }
        }

        // 字典所有者类型过滤条件
        if (reqVO.getDictOwnerType() != null && !reqVO.getDictOwnerType().trim().isEmpty()) {
            configs.and(Compare.EQUAL, DictTypeDO.DICT_OWNER_TYPE, reqVO.getDictOwnerType());
        }

        // 字典所有者ID过滤条件
        if (reqVO.getDictOwnerId() != null) {
            configs.and(Compare.EQUAL, DictTypeDO.DICT_OWNER_ID, reqVO.getDictOwnerId());
        }

        // 添加排序条件，按ID降序排列
        configs.order(DictTypeDO.ID, org.anyline.entity.Order.TYPE.DESC);

        return findPageWithConditions(configs, reqVO.getPageNo(), reqVO.getPageSize());
    }

    /**
     * 根据字典类型查询字典类型对象
     *
     * @param type 字典类型
     * @return 字典类型对象
     */
    public DictTypeDO findOneByType(String type) {
        DefaultConfigStore configs = new DefaultConfigStore();
        configs.and(Compare.EQUAL, DictTypeDO.TYPE, type);
        return findOne(configs);
    }

    /**
     * 根据字典类型名称查询字典类型对象
     *
     * @param name 字典类型名称
     * @return 字典类型对象
     */
    public DictTypeDO findOneByName(String name) {
        DefaultConfigStore configs = new DefaultConfigStore();
        configs.and(Compare.EQUAL, DictTypeDO.NAME, name);
        return findOne(configs);
    }

    /**
     * 查询所有字典类型列表
     *
     * @return 字典类型列表
     */
    public List<DictTypeDO> findAllList() {
        return findAll();
    }
}
