package com.cmsr.onebase.module.metadata.runtime.service.field;

import com.cmsr.onebase.framework.common.util.object.BeanUtils;
import com.cmsr.onebase.module.metadata.runtime.controller.app.entity.vo.FieldOptionBatchSortReqVO;
import com.cmsr.onebase.module.metadata.runtime.controller.app.entity.vo.FieldOptionRespVO;
import com.cmsr.onebase.module.metadata.runtime.controller.app.entity.vo.FieldOptionSaveReqVO;
import com.cmsr.onebase.module.metadata.core.dal.dataobject.field.MetadataEntityFieldOptionDO;
import com.cmsr.onebase.module.metadata.core.dal.database.MetadataEntityFieldOptionRepository;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 字段选项 Runtime Service 实现类
 *
 * @author bty418
 * @date 2025-10-30
 */
@Service
public class MetadataEntityFieldOptionRuntimeServiceImpl implements MetadataEntityFieldOptionRuntimeService {

    @Resource
    private MetadataEntityFieldOptionRepository optionRepository;

    @Override
    public List<MetadataEntityFieldOptionDO> listByFieldUuid(String fieldUuid) {
        return optionRepository.findAllByFieldUuid(fieldUuid);
    }

    @Override
    public Map<String, List<MetadataEntityFieldOptionDO>> listByFieldUuids(List<String> fieldUuids) {
        List<MetadataEntityFieldOptionDO> all = optionRepository.findAllByFieldUuids(fieldUuids);
        Map<String, java.util.List<MetadataEntityFieldOptionDO>> map = new HashMap<>();
        if (all != null) {
            for (MetadataEntityFieldOptionDO o : all) {
                if (o == null || o.getFieldUuid() == null) continue;
                map.computeIfAbsent(o.getFieldUuid(), k -> new ArrayList<>()).add(o);
            }
        }
        return map;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Long create(MetadataEntityFieldOptionDO option) {
        optionRepository.insert(option);
        return option.getId();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void update(MetadataEntityFieldOptionDO option) {
        optionRepository.update(option);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteById(Long id) {
        optionRepository.deleteById(id);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteByFieldId(Long fieldId) {
        optionRepository.deleteByFieldId(fieldId);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void batchSort(Long fieldId, List<MetadataEntityFieldOptionDO> optionsInOrder) {
        if (optionsInOrder == null) {
            return;
        }
        for (MetadataEntityFieldOptionDO it : optionsInOrder) {
            MetadataEntityFieldOptionDO upd = new MetadataEntityFieldOptionDO();
            upd.setId(it.getId());
            upd.setOptionOrder(it.getOptionOrder());
            optionRepository.update(upd);
        }
    }

    @Override
    public List<FieldOptionRespVO> getFieldOptionList(String fieldUuid) {
        List<MetadataEntityFieldOptionDO> list = listByFieldUuid(fieldUuid);
        return list.stream().map(this::convertToRespVO).collect(Collectors.toList());
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Long createFieldOption(FieldOptionSaveReqVO req) {
        MetadataEntityFieldOptionDO option = convertToDO(req);
        return create(option);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateFieldOption(FieldOptionSaveReqVO req) {
        MetadataEntityFieldOptionDO option = convertToDO(req);
        if (req.getId() != null && !req.getId().trim().isEmpty()) {
            try {
                option.setId(Long.valueOf(req.getId()));
            } catch (NumberFormatException e) {
                throw new IllegalArgumentException("选项ID格式不正确: " + req.getId());
            }
        }
        update(option);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void batchSortFieldOptions(FieldOptionBatchSortReqVO req) {
        List<MetadataEntityFieldOptionDO> options = req.getItems().stream().map(item -> {
            MetadataEntityFieldOptionDO option = new MetadataEntityFieldOptionDO();
            option.setId(item.getId());
            option.setOptionOrder(item.getOptionOrder());
            return option;
        }).collect(Collectors.toList());
        batchSort(req.getFieldId(), options);
    }

    /**
     * 转换为响应VO
     *
     * @param option DO对象
     * @return 响应VO
     */
    private FieldOptionRespVO convertToRespVO(MetadataEntityFieldOptionDO option) {
        return BeanUtils.toBean(option, FieldOptionRespVO.class, respVO -> {
            respVO.setId(option.getId() != null ? String.valueOf(option.getId()) : null);
        });
    }

    /**
     * 转换为DO对象
     *
     * @param req 请求VO
     * @return DO对象
     */
    private MetadataEntityFieldOptionDO convertToDO(FieldOptionSaveReqVO req) {
        return BeanUtils.toBean(req, MetadataEntityFieldOptionDO.class);
    }
}
