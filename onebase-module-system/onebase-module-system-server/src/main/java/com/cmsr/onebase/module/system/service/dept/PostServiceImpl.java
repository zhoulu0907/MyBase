package com.cmsr.onebase.module.system.service.dept;

import cn.hutool.core.collection.CollUtil;
import com.cmsr.onebase.framework.aynline.DataRepository;
import com.cmsr.onebase.framework.common.enums.CommonStatusEnum;
import com.cmsr.onebase.framework.common.pojo.PageResult;
import com.cmsr.onebase.framework.common.util.object.BeanUtils;
import com.cmsr.onebase.module.system.controller.admin.dept.vo.post.PostPageReqVO;
import com.cmsr.onebase.module.system.controller.admin.dept.vo.post.PostSaveReqVO;
import com.cmsr.onebase.module.system.dal.dataobject.dept.PostDO;
import lombok.extern.slf4j.Slf4j;
import org.anyline.data.param.ConfigStore;
import org.anyline.data.param.init.DefaultConfigStore;
import org.anyline.entity.Compare;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import jakarta.annotation.Resource;
import java.util.*;

import static com.cmsr.onebase.framework.common.exception.util.ServiceExceptionUtil.exception;
import static com.cmsr.onebase.framework.common.util.collection.CollectionUtils.convertMap;
import static com.cmsr.onebase.module.system.enums.ErrorCodeConstants.*;

/**
 * 岗位 Service 实现类
 *
 */
@Service
@Validated
@Slf4j
public class PostServiceImpl implements PostService {

    //@Resource
    //private PostMapper postMapper;

    @Resource
    private DataRepository dataRepository;

    @Override
    public Long createPost(PostSaveReqVO createReqVO) {
        // 校验正确性
        validatePostForCreateOrUpdate(null, createReqVO.getName(), createReqVO.getCode());

        // 插入岗位
        PostDO post = BeanUtils.toBean(createReqVO, PostDO.class);
        dataRepository.insert(post);
        return post.getId();
    }

    @Override
    public void updatePost(PostSaveReqVO updateReqVO) {
        // 校验正确性
        validatePostForCreateOrUpdate(updateReqVO.getId(), updateReqVO.getName(), updateReqVO.getCode());

        // 更新岗位
        PostDO updateObj = BeanUtils.toBean(updateReqVO, PostDO.class);
        dataRepository.save(updateObj);
    }

    @Override
    public void deletePost(Long id) {
        // 校验是否存在
        validatePostExists(id);
        // 删除部门
        dataRepository.deleteById(PostDO.class, id);
    }

    private void validatePostForCreateOrUpdate(Long id, String name, String code) {
        // 校验自己存在
        validatePostExists(id);
        // 校验岗位名的唯一性
        validatePostNameUnique(id, name);
        // 校验岗位编码的唯一性
        validatePostCodeUnique(id, code);
    }

    private void validatePostNameUnique(Long id, String name) {
        ConfigStore cs = new DefaultConfigStore()
                .and(Compare.EQUAL, "name", name)
                .and(Compare.EQUAL, "deleted", false);
        PostDO post = dataRepository.findOne(PostDO.class, cs);
        if (post == null) {
            return;
        }
        // 如果 id 为空，说明不用比较是否为相同 id 的岗位
        if (id == null) {
            throw exception(POST_NAME_DUPLICATE);
        }
        if (!post.getId().equals(id)) {
            throw exception(POST_NAME_DUPLICATE);
        }
    }

    private void validatePostCodeUnique(Long id, String code) {
        ConfigStore cs = new DefaultConfigStore()
                .and(Compare.EQUAL, "code", code)
                .and(Compare.EQUAL, "deleted", false);
        PostDO post = dataRepository.findOne(PostDO.class, cs);
        if (post == null) {
            return;
        }
        // 如果 id 为空，说明不用比较是否为相同 id 的岗位
        if (id == null) {
            throw exception(POST_CODE_DUPLICATE);
        }
        if (!post.getId().equals(id)) {
            throw exception(POST_CODE_DUPLICATE);
        }
    }

    private void validatePostExists(Long id) {
        if (id == null) {
            return;
        }
        if (dataRepository.findById(PostDO.class, id) == null) {
            throw exception(POST_NOT_FOUND);
        }
    }

    @Override
    public List<PostDO> getPostList(Collection<Long> ids) {
        if (CollUtil.isEmpty(ids)) {
            return Collections.emptyList();
        }
        return dataRepository.findAllById(PostDO.class, new ArrayList<>(ids));
    }

    @Override
    public List<PostDO> getPostList(Collection<Long> ids, Collection<Integer> statuses) {
        ConfigStore cs = new DefaultConfigStore()
                .and(Compare.EQUAL, "deleted", false);
        if (CollUtil.isNotEmpty(ids)) {
            cs.in("id", ids);
        }
        if (CollUtil.isNotEmpty(statuses)) {
            cs.in("status", statuses);
        }
        return dataRepository.findAll(PostDO.class, cs);
    }

    @Override
    public PageResult<PostDO> getPostPage(PostPageReqVO reqVO) {
        try {
            ConfigStore cs = new DefaultConfigStore()
                    .and(Compare.EQUAL, "deleted", false);
            
            // 构建查询条件
            if (cn.hutool.core.util.StrUtil.isNotBlank(reqVO.getCode())) {
                cs.and(Compare.LIKE, "code", reqVO.getCode());
            }
            if (cn.hutool.core.util.StrUtil.isNotBlank(reqVO.getName())) {
                cs.and(Compare.LIKE, "name", reqVO.getName());
            }
            if (reqVO.getStatus() != null) {
                cs.and(Compare.EQUAL, "status", reqVO.getStatus());
            }
            
            // 添加排序条件，按ID降序排列
            cs.order("id", "DESC");
            
            return dataRepository.findPageWithConditions(
                    PostDO.class, 
                    cs, 
                    reqVO.getPageNo(), 
                    reqVO.getPageSize()
            );
        } catch (Exception e) {
            log.error("分页查询岗位失败", e);
            throw new RuntimeException("分页查询岗位失败", e);
        }
    }

    @Override
    public PostDO getPost(Long id) {
        return dataRepository.findById(PostDO.class, id);
    }

    @Override
    public void validatePostList(Collection<Long> ids) {
        if (CollUtil.isEmpty(ids)) {
            return;
        }
        // 获得岗位信息
        List<PostDO> posts = dataRepository.findAllById(PostDO.class, new ArrayList<>(ids));
        Map<Long, PostDO> postMap = convertMap(posts, PostDO::getId);
        // 校验
        ids.forEach(id -> {
            PostDO post = postMap.get(id);
            if (post == null) {
                throw exception(POST_NOT_FOUND);
            }
            if (!CommonStatusEnum.ENABLE.getStatus().equals(post.getStatus())) {
                throw exception(POST_NOT_ENABLE, post.getName());
            }
        });
    }
}
