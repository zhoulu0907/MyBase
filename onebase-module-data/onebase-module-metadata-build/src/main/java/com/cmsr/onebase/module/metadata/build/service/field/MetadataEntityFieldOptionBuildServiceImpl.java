package com.cmsr.onebase.module.metadata.build.service.field;

import com.cmsr.onebase.framework.common.util.object.BeanUtils;
import com.cmsr.onebase.module.metadata.build.controller.admin.entity.vo.FieldOptionBatchSortReqVO;
import com.cmsr.onebase.module.metadata.build.controller.admin.entity.vo.FieldOptionRespVO;
import com.cmsr.onebase.module.metadata.build.controller.admin.entity.vo.FieldOptionSaveReqVO;
import com.cmsr.onebase.module.metadata.core.dal.dataobject.field.MetadataEntityFieldOptionDO;
import com.cmsr.onebase.module.metadata.core.dal.database.MetadataEntityFieldOptionRepository;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

/**
 * 字段选项 Service 实现
 *
 * @author bty418
 * @date 2025-08-18
 */
@Service
public class MetadataEntityFieldOptionBuildServiceImpl implements MetadataEntityFieldOptionBuildService {

    @Resource
    private MetadataEntityFieldOptionRepository optionRepository;

    @Override
    public List<MetadataEntityFieldOptionDO> listByFieldId(String fieldUuid) {
        return optionRepository.findAllByFieldUuid(fieldUuid);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Long create(MetadataEntityFieldOptionDO option) {
        optionRepository.save(option);
        return option.getId();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void update(MetadataEntityFieldOptionDO option) {
        optionRepository.updateById(option);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteById(Long id) {
        optionRepository.removeById(id);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteByFieldId(String fieldUuid) {
        optionRepository.deleteByFieldUuid(fieldUuid);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void batchSort(String fieldUuid, List<MetadataEntityFieldOptionDO> optionsInOrder) {
        if (optionsInOrder == null) return;
        for (MetadataEntityFieldOptionDO it : optionsInOrder) {
            MetadataEntityFieldOptionDO upd = new MetadataEntityFieldOptionDO();
            upd.setId(it.getId());
            upd.setOptionOrder(it.getOptionOrder());
            optionRepository.updateById(upd);
        }
    }

    @Override
    public List<FieldOptionRespVO> getFieldOptionList(String fieldUuid) {
        List<MetadataEntityFieldOptionDO> list = listByFieldId(fieldUuid);
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
        batchSort(req.getFieldUuid(), options);
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


