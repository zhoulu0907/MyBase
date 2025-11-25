package com.cmsr.onebase.module.app.build.service.tag;

import com.cmsr.onebase.framework.common.exception.util.ServiceExceptionUtil;
import com.cmsr.onebase.framework.common.util.object.BeanUtils;
import com.cmsr.onebase.module.app.build.util.AuthUtils;
import com.cmsr.onebase.module.app.build.vo.tag.TagRespVO;
import com.cmsr.onebase.module.app.core.dal.database.AppApplicationTagRepository;
import com.cmsr.onebase.module.app.core.dal.database.AppTagRepository;
import com.cmsr.onebase.module.app.core.dal.dataobject.TagDO;
import com.cmsr.onebase.module.app.core.enums.AppErrorCodeConstants;
import com.cmsr.onebase.module.app.core.vo.tag.TagGroupCountVO;
import jakarta.annotation.Resource;
import lombok.Setter;
import org.apache.commons.lang3.tuple.Pair;
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
    private AppTagRepository appTagRepository;

    @Resource
    private AppApplicationTagRepository appApplicationTagRepository;


    @Override
    public List<TagRespVO> listTags(String tagName) {
        List<TagDO> tagDOS = appTagRepository.findByTagNameLike(tagName);
        return BeanUtils.toBean(tagDOS, TagRespVO.class);
    }

    @Override
    public void createTag(String tagName) {
        long count = appTagRepository.countByTagName(tagName);
        if (count > 0) {
            throw ServiceExceptionUtil.exception(AppErrorCodeConstants.APP_TAG_EXIST);
        }
        TagDO tagDO = new TagDO();
        tagDO.setTagName(tagName);
        appTagRepository.save(tagDO);
    }

    @Override
    public void deleteTag(Long tagId) {
        appTagRepository.removeById(tagId);
    }

    @Override
    public List<TagGroupCountVO> groupCount() {
        return appTagRepository.groupCount();
    }

    @Override
    public void updateTags(List<TagRespVO> tagRespVOS) {
        List<TagDO> tagDOS = appTagRepository.findAllTags();
        List<Pair<TagDO, TagRespVO>> pairs = AuthUtils.fullOuterJoin(tagDOS, tagRespVOS, (tagDO, tagRespVO) -> tagDO.getId().equals(tagRespVO.getId()));
        for (Pair<TagDO, TagRespVO> pair : pairs) {
            TagDO tagDO = pair.getLeft();
            TagRespVO tagRespVO = pair.getRight();
            if (tagDO == null) {
                tagDO = new TagDO();
                tagDO.setTagName(tagRespVO.getTagName());
                appTagRepository.save(tagDO);
            } else if (tagRespVO == null) {
                appTagRepository.removeById(tagDO.getId());
                appApplicationTagRepository.deleteByTagId(tagDO.getId());
            } else {
                tagDO.setTagName(tagRespVO.getTagName());
                appTagRepository.updateById(tagDO);
            }
        }
    }

}
