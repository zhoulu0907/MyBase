package com.cmsr.onebase.module.system.service.dict;

import cn.hutool.core.util.StrUtil;
import com.cmsr.onebase.framework.aynline.DataRepository;
import com.cmsr.onebase.framework.common.pojo.PageResult;
import com.cmsr.onebase.framework.common.util.date.LocalDateTimeUtils;
import com.cmsr.onebase.framework.common.util.object.BeanUtils;
import com.cmsr.onebase.module.system.controller.admin.dict.vo.type.DictTypePageReqVO;
import com.cmsr.onebase.module.system.controller.admin.dict.vo.type.DictTypeSaveReqVO;
import com.cmsr.onebase.module.system.dal.dataobject.dict.DictTypeDO;
import com.cmsr.onebase.module.system.dal.mysql.dict.DictTypeMapper;
import com.google.common.annotations.VisibleForTesting;
import lombok.extern.slf4j.Slf4j;
import org.anyline.data.param.ConfigStore;
import org.anyline.data.param.init.DefaultConfigStore;
import org.anyline.entity.Compare;
import org.springframework.stereotype.Service;

import jakarta.annotation.Resource;
import java.time.LocalDateTime;
import java.util.List;

import static com.cmsr.onebase.framework.common.exception.util.ServiceExceptionUtil.exception;
import static com.cmsr.onebase.module.system.enums.ErrorCodeConstants.*;

/**
 * 字典类型 Service 实现类
 *
 */
@Service
@Slf4j
public class DictTypeServiceImpl implements DictTypeService {

    @Resource
    private DictDataService dictDataService;

    @Resource
    private DictTypeMapper dictTypeMapper;

    @Resource
    private DataRepository dataRepository;

    @Override
    public PageResult<DictTypeDO> getDictTypePage(DictTypePageReqVO pageReqVO) {
        try {
            ConfigStore configs = new DefaultConfigStore();
            
            // 构建查询条件
            if (StrUtil.isNotBlank(pageReqVO.getName())) {
                configs.and(Compare.LIKE, "name", pageReqVO.getName());
            }
            if (StrUtil.isNotBlank(pageReqVO.getType())) {
                configs.and(Compare.LIKE, "type", pageReqVO.getType());
            }
            if (pageReqVO.getStatus() != null) {
                configs.and(Compare.EQUAL, "status", pageReqVO.getStatus());
            }
            if (pageReqVO.getCreateTime() != null && pageReqVO.getCreateTime().length == 2) {
                LocalDateTime startTime = pageReqVO.getCreateTime()[0];
                LocalDateTime endTime = pageReqVO.getCreateTime()[1];
                if (startTime != null) {
                    configs.and(Compare.GREAT_EQUAL, "create_time", startTime);
                }
                if (endTime != null) {
                    configs.and(Compare.LESS_EQUAL, "create_time", endTime);
                }
            }
            
            // 添加排序条件，按ID降序排列
            configs.order("id", "DESC");
            
            return dataRepository.findPageWithConditions(
                    DictTypeDO.class, 
                    configs, 
                    pageReqVO.getPageNo(), 
                    pageReqVO.getPageSize()
            );
        } catch (Exception e) {
            log.error("分页查询字典类型失败", e);
            throw new RuntimeException("分页查询字典类型失败", e);
        }
    }

    @Override
    public DictTypeDO getDictType(Long id) {
        return dataRepository.findById(DictTypeDO.class, id);
    }

    @Override
    public DictTypeDO getDictType(String type) {
        try {
            ConfigStore configs = new DefaultConfigStore();
            configs.and(Compare.EQUAL, "type", type);
            return dataRepository.findOne(DictTypeDO.class, configs);
        } catch (Exception e) {
            log.error("根据类型查询字典类型失败: type={}", type, e);
            return null;
        }
    }

    @Override
    public Long createDictType(DictTypeSaveReqVO createReqVO) {
        // 校验字典类型的名字的唯一性
        validateDictTypeNameUnique(null, createReqVO.getName());
        // 校验字典类型的类型的唯一性
        validateDictTypeUnique(null, createReqVO.getType());

        // 插入字典类型
        DictTypeDO dictType = BeanUtils.toBean(createReqVO, DictTypeDO.class);
        dictType.setDeletedTime(LocalDateTimeUtils.EMPTY); // 唯一索引，避免 null 值
        dataRepository.insert(dictType);
        return dictType.getId();
    }

    @Override
    public void updateDictType(DictTypeSaveReqVO updateReqVO) {
        // 校验自己存在
        validateDictTypeExists(updateReqVO.getId());
        // 校验字典类型的名字的唯一性
        validateDictTypeNameUnique(updateReqVO.getId(), updateReqVO.getName());
        // 校验字典类型的类型的唯一性
        validateDictTypeUnique(updateReqVO.getId(), updateReqVO.getType());

        // 更新字典类型
        DictTypeDO updateObj = BeanUtils.toBean(updateReqVO, DictTypeDO.class);
        dataRepository.update(updateObj);
    }

    @Override
    public void deleteDictType(Long id) {
        // 校验是否存在
        DictTypeDO dictType = validateDictTypeExists(id);
        // 校验是否有字典数据
        if (dictDataService.getDictDataCountByDictType(dictType.getType()) > 0) {
            throw exception(DICT_TYPE_HAS_CHILDREN);
        }
        // 删除字典类型（软删除）
        dataRepository.deleteById(DictTypeDO.class, id);
    }

    @Override
    public List<DictTypeDO> getDictTypeList() {
        return dataRepository.findAll(DictTypeDO.class);
    }

    @VisibleForTesting
    void validateDictTypeNameUnique(Long id, String name) {
        try {
            ConfigStore configs = new DefaultConfigStore();
            configs.and(Compare.EQUAL, "name", name);
            DictTypeDO dictType = dataRepository.findOne(DictTypeDO.class, configs);
            if (dictType == null) {
                return;
            }
            // 如果 id 为空，说明不用比较是否为相同 id 的字典类型
            if (id == null) {
                throw exception(DICT_TYPE_NAME_DUPLICATE);
            }
            if (!dictType.getId().equals(id)) {
                throw exception(DICT_TYPE_NAME_DUPLICATE);
            }
        } catch (Exception e) {
            if (e instanceof com.cmsr.onebase.framework.common.exception.ServiceException) {
                throw e;
            }
            log.error("验证字典类型名称唯一性失败: name={}", name, e);
            throw exception(DICT_TYPE_NAME_DUPLICATE);
        }
    }

    @VisibleForTesting
    void validateDictTypeUnique(Long id, String type) {
        if (StrUtil.isEmpty(type)) {
            return;
        }
        try {
            ConfigStore configs = new DefaultConfigStore();
            configs.and(Compare.EQUAL, "type", type);
            DictTypeDO dictType = dataRepository.findOne(DictTypeDO.class, configs);
            if (dictType == null) {
                return;
            }
            // 如果 id 为空，说明不用比较是否为相同 id 的字典类型
            if (id == null) {
                throw exception(DICT_TYPE_TYPE_DUPLICATE);
            }
            if (!dictType.getId().equals(id)) {
                throw exception(DICT_TYPE_TYPE_DUPLICATE);
            }
        } catch (Exception e) {
            if (e instanceof com.cmsr.onebase.framework.common.exception.ServiceException) {
                throw e;
            }
            log.error("验证字典类型唯一性失败: type={}", type, e);
            throw exception(DICT_TYPE_TYPE_DUPLICATE);
        }
    }

    @VisibleForTesting
    DictTypeDO validateDictTypeExists(Long id) {
        if (id == null) {
            return null;
        }
        DictTypeDO dictType = dataRepository.findById(DictTypeDO.class, id);
        if (dictType == null) {
            throw exception(DICT_TYPE_NOT_EXISTS);
        }
        return dictType;
    }

}
