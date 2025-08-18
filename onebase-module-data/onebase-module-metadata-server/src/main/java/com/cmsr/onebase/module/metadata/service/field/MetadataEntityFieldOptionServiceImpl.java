package com.cmsr.onebase.module.metadata.service.field;

import com.cmsr.onebase.module.metadata.dal.dataobject.field.MetadataEntityFieldOptionDO;
import com.cmsr.onebase.module.metadata.dal.database.MetadataEntityFieldOptionRepository;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * 字段选项 Service 实现
 *
 * @author bty418
 * @date 2025-08-18
 */
@Service
public class MetadataEntityFieldOptionServiceImpl implements MetadataEntityFieldOptionService {

    @Resource
    private MetadataEntityFieldOptionRepository optionRepository;

    @Override
    public List<MetadataEntityFieldOptionDO> listByFieldId(Long fieldId) {
        return optionRepository.findAllByFieldId(fieldId);
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
        if (optionsInOrder == null) return;
        for (MetadataEntityFieldOptionDO it : optionsInOrder) {
            MetadataEntityFieldOptionDO upd = new MetadataEntityFieldOptionDO();
            upd.setId(it.getId());
            upd.setOptionOrder(it.getOptionOrder());
            optionRepository.update(upd);
        }
    }
}


