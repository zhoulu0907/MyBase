package com.cmsr.onebase.module.system.dal.database;

import com.cmsr.onebase.framework.common.pojo.PageResult;
import com.cmsr.onebase.module.system.dal.dataobject.dict.DictTypeDO;
import com.cmsr.onebase.framework.orm.repo.BaseDataRepository;
import com.cmsr.onebase.module.system.dal.flex.mapper.SystemDictTypeMapper;
import com.cmsr.onebase.module.system.enums.dict.DictOwnerTypeEnum;
import com.cmsr.onebase.module.system.vo.dicttype.DictTypeListReqVO;
import com.cmsr.onebase.module.system.vo.dicttype.DictTypePageReqVO;
import com.mybatisflex.core.paginate.Page;
import com.mybatisflex.core.query.QueryWrapper;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Repository;

import java.util.List;

import static com.cmsr.onebase.framework.data.base.BaseDO.CREATE_TIME;
import static com.cmsr.onebase.framework.data.base.BaseDO.ID;
import static com.cmsr.onebase.module.system.dal.dataobject.dict.DictTypeDO.DICT_OWNER_ID;
import static com.cmsr.onebase.module.system.dal.dataobject.dict.DictTypeDO.DICT_OWNER_TYPE;
import static com.cmsr.onebase.module.system.dal.dataobject.dict.DictTypeDO.NAME;
import static com.cmsr.onebase.module.system.dal.dataobject.dict.DictTypeDO.STATUS;
import static com.cmsr.onebase.module.system.dal.dataobject.dict.DictTypeDO.TYPE;

/**
 * 字典类型数据访问层
 *
 * 负责字典类型相关的数据操作。
 *
 * @author matianyu
 * @date 2025-12-22
 */
@Repository
public class DictTypeRepository extends BaseDataRepository<SystemDictTypeMapper, DictTypeDO> {

    /**
     * 分页查询字典类型
     *
     * @param reqVO 分页查询条件
     * @return 分页结果
     */
    public PageResult<DictTypeDO> findPage(DictTypePageReqVO reqVO) {
        QueryWrapper queryWrapper = buildQueryWrapper(reqVO.getName(), reqVO.getType(), reqVO.getStatus(),
                reqVO.getCreateTime(), reqVO.getDictOwnerType(), reqVO.getDictOwnerId())
                .orderBy(ID, false);

        Page<DictTypeDO> pageResult = page(Page.of(reqVO.getPageNo(), reqVO.getPageSize()), queryWrapper);
        return new PageResult<>(pageResult.getRecords(), pageResult.getTotalRow());
    }

    /**
     * 根据字典类型查询字典类型对象
     *
     * @param type 字典类型
     * @return 字典类型对象
     */
    public DictTypeDO findOneByType(String type) {
        return getOne(query().eq(TYPE, type));
    }

    /**
     * 根据字典类型名称查询字典类型对象
     *
     * @param name 字典类型名称
     * @return 字典类型对象
     */
    public DictTypeDO findOneByName(String name) {
        return getOne(query().eq(NAME, name));
    }

    /**
     * 查询所有字典类型列表
     *
     * @return 字典类型列表
     */
    public List<DictTypeDO> findAllList() {
        return list();
    }

    /**
     * 根据条件查询字典类型列表（不分页）
     *
     * @param reqVO 查询条件
     * @return 字典类型列表
     */
    public List<DictTypeDO> findList(DictTypeListReqVO reqVO) {
        QueryWrapper queryWrapper = buildQueryWrapper(reqVO.getName(), reqVO.getType(), reqVO.getStatus(),
                reqVO.getCreateTime(), reqVO.getDictOwnerType(), reqVO.getDictOwnerId())
                .orderBy(ID, false);
        return list(queryWrapper);
    }

    private QueryWrapper buildQueryWrapper(String name, String type, Integer status, java.time.LocalDateTime[] createTime,
                                          String dictOwnerType, Long dictOwnerId) {
        QueryWrapper queryWrapper = query()
                .like(NAME, name, StringUtils.isNotBlank(name))
                .like(TYPE, type, StringUtils.isNotBlank(type))
                .eq(STATUS, status, status != null);

        if (createTime != null && createTime.length == 2) {
            queryWrapper.ge(CREATE_TIME, createTime[0], createTime[0] != null);
            queryWrapper.le(CREATE_TIME, createTime[1], createTime[1] != null);
        }

        // 字典所有者类型过滤条件
        if (StringUtils.isNotBlank(dictOwnerType)) {
            queryWrapper.eq(DICT_OWNER_TYPE, dictOwnerType);
            // 仅当字典所有者类型为 app 时才过滤字典所有者ID；tenant 类型字典为公共字典，不限制 dictOwnerId
            if (DictOwnerTypeEnum.isApp(dictOwnerType) && dictOwnerId != null) {
                queryWrapper.eq(DICT_OWNER_ID, dictOwnerId);
            }
        } else if (dictOwnerId != null) {
            // 未指定所有者类型但指定了所有者ID时，按所有者ID过滤
            queryWrapper.eq(DICT_OWNER_ID, dictOwnerId);
        }

        return queryWrapper;
    }
}
