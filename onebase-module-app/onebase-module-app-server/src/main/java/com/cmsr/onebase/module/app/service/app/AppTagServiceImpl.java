package com.cmsr.onebase.module.app.service.app;

import com.cmsr.onebase.framework.aynline.DataRepository;
import com.cmsr.onebase.framework.common.exception.util.ServiceExceptionUtil;
import com.cmsr.onebase.framework.common.util.object.BeanUtils;
import com.cmsr.onebase.module.app.controller.admin.app.vo.TagListRespVO;
import com.cmsr.onebase.module.app.dal.dataobject.app.TagDO;
import com.cmsr.onebase.module.app.enums.app.AppErrorCodeConstants;
import jakarta.annotation.Resource;
import lombok.Setter;
import org.anyline.data.param.ConfigStore;
import org.anyline.data.param.init.DefaultConfigStore;
import org.anyline.entity.Order;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import java.util.List;

/**
 * @ClassName AppTagServiceImpl
 * @Description TODO
 * @Author mickey
 * @Date 2025/7/31 08:31
 */
@Setter
@Service
@Validated
public class AppTagServiceImpl implements AppTagService {

    @Resource
    private DataRepository dataRepository;

    @Resource
    private AppCommonService appCommonService;

    @Override
    public List<TagListRespVO> listTags(String tagName) {
        ConfigStore configs = new DefaultConfigStore();
        configs.like("tag_name", tagName);
        configs.order("tag_name", Order.TYPE.DESC);
        List<TagDO> tagDOS = dataRepository.findAll(TagDO.class, configs);
        return tagDOS.stream().map(tagDO -> BeanUtils.toBean(tagDO, TagListRespVO.class)).toList();
    }

    @Override
    public void createTag(String tagName) {
        ConfigStore configs = new DefaultConfigStore();
        configs.eq("tag_name", tagName);
        long count = dataRepository.countByConfig(TagDO.class, configs);
        if (count > 0) {
            throw ServiceExceptionUtil.exception(AppErrorCodeConstants.APP_TAG_EXIST);
        }
        TagDO tagDO = new TagDO();
        tagDO.setTagName(tagName);
        dataRepository.insert(tagDO);
    }


}
