package com.cmsr.onebase.module.system.service.dept;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.ObjectUtil;
import com.cmsr.onebase.framework.aynline.DataRepository;
import com.cmsr.onebase.framework.common.enums.CommonStatusEnum;
import com.cmsr.onebase.framework.common.util.object.BeanUtils;
import com.cmsr.onebase.framework.datapermission.core.annotation.DataPermission;
import com.cmsr.onebase.module.system.controller.admin.dept.vo.dept.DeptListReqVO;
import com.cmsr.onebase.module.system.controller.admin.dept.vo.dept.DeptSaveReqVO;
import com.cmsr.onebase.module.system.dal.dataobject.dept.DeptDO;
import com.cmsr.onebase.module.system.dal.redis.RedisKeyConstants;
import com.google.common.annotations.VisibleForTesting;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.anyline.data.param.ConfigStore;
import org.anyline.data.param.init.DefaultConfigStore;
import org.anyline.entity.Compare;
import org.anyline.entity.Order;
import org.anyline.entity.generator.PrimaryGenerator;
import org.anyline.util.ConfigTable;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import java.util.*;

import static com.cmsr.onebase.framework.common.exception.util.ServiceExceptionUtil.exception;
import static com.cmsr.onebase.framework.common.util.collection.CollectionUtils.convertSet;
import static com.cmsr.onebase.module.system.enums.ErrorCodeConstants.*;

/**
 * 部门 Service 实现类
 */
@Service
@Validated
@Slf4j
public class DeptServiceImpl implements DeptService {

    //@Resource
    //private DeptMapper deptMapper;

    @Resource
    private DataRepository dataRepository;

    @Override
    @CacheEvict(cacheNames = RedisKeyConstants.DEPT_CHILDREN_ID_LIST,
            allEntries = true) // allEntries 清空所有缓存，因为操作一个部门，涉及到多个缓存
    public Long createDept(DeptSaveReqVO createReqVO) {
        if (createReqVO.getParentId() == null) {
            createReqVO.setParentId(DeptDO.PARENT_ID_ROOT);
        }
        // 校验父部门的有效性
        validateParentDept(null, createReqVO.getParentId());
        // 校验部门名的唯一性
        validateDeptNameUnique(null, createReqVO.getParentId(), createReqVO.getName());

        // 插入部门
        DeptDO dept = BeanUtils.toBean(createReqVO, DeptDO.class);
        dataRepository.insert(dept);

        return dept.getId();
    }

    @Override
    @CacheEvict(cacheNames = RedisKeyConstants.DEPT_CHILDREN_ID_LIST,
            allEntries = true) // allEntries 清空所有缓存，因为操作一个部门，涉及到多个缓存
    public void updateDept(DeptSaveReqVO updateReqVO) {
        if (updateReqVO.getParentId() == null) {
            updateReqVO.setParentId(DeptDO.PARENT_ID_ROOT);
        }
        // 校验自己存在
        validateDeptExists(updateReqVO.getId());
        // 校验父部门的有效性
        validateParentDept(updateReqVO.getId(), updateReqVO.getParentId());
        // 校验部门名的唯一性
        validateDeptNameUnique(updateReqVO.getId(), updateReqVO.getParentId(), updateReqVO.getName());

        // 更新部门
        DeptDO updateObj = BeanUtils.toBean(updateReqVO, DeptDO.class);
        dataRepository.update(updateObj);
    }

    @Override
    @CacheEvict(cacheNames = RedisKeyConstants.DEPT_CHILDREN_ID_LIST,
            allEntries = true) // allEntries 清空所有缓存，因为操作一个部门，涉及到多个缓存
    public void deleteDept(Long id) {
        // 校验是否存在
        validateDeptExists(id);
        // 校验是否有子部门
        if (getChildDeptCount(id) > 0) {
            throw exception(DEPT_EXITS_CHILDREN);
        }
        // 删除部门
        dataRepository.deleteById(DeptDO.class, id);
    }

    /**
     * 获取子部门数量
     */
    private long getChildDeptCount(Long parentId) {
        try {
            ConfigStore configs = new DefaultConfigStore();
            configs.and(Compare.EQUAL, "parent_id", parentId);
            configs.and(Compare.EQUAL, "deleted", false);
            return dataRepository.findAll(DeptDO.class, configs).size();
        } catch (Exception e) {
            log.error("获取子部门数量失败: parentId={}", parentId, e);
            return 0;
        }
    }

    @VisibleForTesting
    void validateDeptExists(Long id) {
        if (id == null) {
            return;
        }
        DeptDO dept = dataRepository.findById(DeptDO.class, id);
        if (dept == null) {
            throw exception(DEPT_NOT_FOUND);
        }
    }

    @VisibleForTesting
    void validateParentDept(Long id, Long parentId) {
        if (parentId == null || DeptDO.PARENT_ID_ROOT.equals(parentId)) {
            return;
        }
        // 1. 不能设置自己为父部门
        if (Objects.equals(id, parentId)) {
            throw exception(DEPT_PARENT_ERROR);
        }
        // 2. 父部门不存在
        DeptDO parentDept = dataRepository.findById(DeptDO.class, parentId);
        if (parentDept == null) {
            throw exception(DEPT_PARENT_NOT_EXITS);
        }
        // 3. 递归校验父部门，如果父部门是自己的子部门，则报错，避免形成环路
        if (id == null) { // id 为空，说明新增，不需要考虑环路
            return;
        }
        for (int i = 0; i < Short.MAX_VALUE; i++) {
            // 3.1 校验环路
            parentId = parentDept.getParentId();
            if (Objects.equals(id, parentId)) {
                throw exception(DEPT_PARENT_IS_CHILD);
            }
            // 3.2 继续递归下一级父部门
            if (parentId == null || DeptDO.PARENT_ID_ROOT.equals(parentId)) {
                break;
            }
            parentDept = dataRepository.findById(DeptDO.class, parentId);
            if (parentDept == null) {
                break;
            }
        }
    }

    @VisibleForTesting
    void validateDeptNameUnique(Long id, Long parentId, String name) {
        try {
            ConfigStore configs = new DefaultConfigStore();
            configs.and(Compare.EQUAL, "parent_id", parentId);
            configs.and(Compare.EQUAL, "name", name);
            DeptDO dept = dataRepository.findOne(DeptDO.class, configs);
            if (dept == null) {
                return;
            }
            // 如果 id 为空，说明不用比较是否为相同 id 的部门
            if (id == null) {
                throw exception(DEPT_NAME_DUPLICATE);
            }
            if (ObjectUtil.notEqual(dept.getId(), id)) {
                throw exception(DEPT_NAME_DUPLICATE);
            }
        } catch (Exception e) {
            if (e instanceof com.cmsr.onebase.framework.common.exception.ServiceException) {
                throw e;
            }
            log.error("验证部门名称唯一性失败: parentId={}, name={}", parentId, name, e);
            throw exception(DEPT_NAME_DUPLICATE);
        }
    }

    @Override
    public DeptDO getDept(Long id) {
        return dataRepository.findById(DeptDO.class, id);
    }

    @Override
    public List<DeptDO> getDeptList(Collection<Long> ids) {
        if (CollUtil.isEmpty(ids)) {
            return Collections.emptyList();
        }
        return dataRepository.findAllByIds(DeptDO.class, new ArrayList<>(ids));
    }

    @Override
    public List<DeptDO> getDeptList(DeptListReqVO reqVO) {
        try {
            ConfigStore configs = new DefaultConfigStore();

            // 构建查询条件
            if (reqVO.getName() != null) {
                configs.and(Compare.LIKE, "name", reqVO.getName());
            }
            if (reqVO.getStatus() != null) {
                configs.and(Compare.EQUAL, "status", reqVO.getStatus());
            }
            configs.order("sort", Order.TYPE.ASC);
            List<DeptDO> list = dataRepository.findAll(DeptDO.class, configs);
            return list;
        } catch (Exception e) {
            log.error("查询部门列表失败", e);
            return Collections.emptyList();
        }
    }

    @Override
    public List<DeptDO> getChildDeptList(Collection<Long> ids) {
        List<DeptDO> children = new LinkedList<>();
        // 遍历每一层
        Collection<Long> parentIds = ids;
        for (int i = 0; i < Short.MAX_VALUE; i++) { // 使用 Short.MAX_VALUE 避免 bug 场景下，存在死循环
            // 查询当前层，所有的子部门
            List<DeptDO> depts = getDeptListByParentIds(parentIds);
            // 1. 如果没有子部门，则结束遍历
            if (CollUtil.isEmpty(depts)) {
                break;
            }
            // 2. 如果有子部门，继续遍历
            children.addAll(depts);
            parentIds = convertSet(depts, DeptDO::getId);
        }
        return children;
    }

    /**
     * 根据父部门ID列表查询子部门
     */
    private List<DeptDO> getDeptListByParentIds(Collection<Long> parentIds) {
        try {
            ConfigStore configs = new DefaultConfigStore();
            configs.and(Compare.IN, "parent_id", parentIds);
            return dataRepository.findAll(DeptDO.class, configs);
        } catch (Exception e) {
            log.error("根据父部门ID列表查询子部门失败: parentIds={}", parentIds, e);
            return Collections.emptyList();
        }
    }

    @Override
    public List<DeptDO> getDeptListByLeaderUserId(Long id) {
        try {
            ConfigStore configs = new DefaultConfigStore();
            configs.and(Compare.EQUAL, "leader_user_id", id);
            return dataRepository.findAll(DeptDO.class, configs);
        } catch (Exception e) {
            log.error("根据负责人用户ID查询部门列表失败: id={}", id, e);
            return Collections.emptyList();
        }
    }

    @Override
    @DataPermission(enable = false) // 禁用数据权限，避免建立不正确的缓存
    @Cacheable(cacheNames = RedisKeyConstants.DEPT_CHILDREN_ID_LIST, key = "#id")
    public Set<Long> getChildDeptIdListFromCache(Long id) {
        List<DeptDO> children = getChildDeptList(id);
        return convertSet(children, DeptDO::getId);
    }

    @Override
    public void validateDeptList(Collection<Long> ids) {
        if (CollUtil.isEmpty(ids)) {
            return;
        }
        // 获得科室信息
        Map<Long, DeptDO> deptMap = getDeptMap(ids);
        // 校验
        ids.forEach(id -> {
            DeptDO dept = deptMap.get(id);
            if (dept == null) {
                throw exception(DEPT_NOT_FOUND);
            }
            if (!CommonStatusEnum.ENABLE.getStatus().equals(dept.getStatus())) {
                throw exception(DEPT_NOT_ENABLE, dept.getName());
            }
        });
    }

}
