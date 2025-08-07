package com.cmsr.onebase.module.app.service.tag;

import com.cmsr.onebase.framework.common.exception.util.ServiceExceptionUtil;
import com.cmsr.onebase.framework.common.util.object.BeanUtils;
import com.cmsr.onebase.module.app.controller.admin.tag.vo.TagListRespVO;
import com.cmsr.onebase.module.app.dal.database.tag.AppTagRepository;
import com.cmsr.onebase.module.app.dal.dataobject.tag.TagDO;
import com.cmsr.onebase.module.app.enums.app.AppErrorCodeConstants;
import jakarta.annotation.Resource;
import lombok.Setter;
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
    private AppTagRepository tagRepository;


    @Override
    public List<TagListRespVO> listTags(String tagName) {
        List<TagDO> tagDOS = tagRepository.findByTagNameLike(tagName);
        return tagDOS.stream().map(tagDO -> BeanUtils.toBean(tagDO, TagListRespVO.class)).toList();
    }

    @Override
    public void createTag(String tagName) {
        long count = tagRepository.countByTagName(tagName);
        if (count > 0) {
            throw ServiceExceptionUtil.exception(AppErrorCodeConstants.APP_TAG_EXIST);
        }
        TagDO tagDO = new TagDO();
        tagDO.setTagName(tagName);
        tagRepository.insert(tagDO);
    }


}
