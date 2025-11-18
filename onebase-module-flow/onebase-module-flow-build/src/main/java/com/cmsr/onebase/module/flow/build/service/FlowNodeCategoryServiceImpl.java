package com.cmsr.onebase.module.flow.build.service;

import com.cmsr.onebase.module.flow.build.vo.NodeCategoryVO;
import com.cmsr.onebase.module.flow.core.dal.database.FlowNodeCategoryRepository;
import com.cmsr.onebase.module.flow.core.dal.dataobject.FlowNodeCategoryDO;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * @Author：huangjie
 * @Date：2025/11/17 16:04
 */
@Slf4j
@Setter
@Service
public class FlowNodeCategoryServiceImpl implements FlowNodeCategoryService {

    @Autowired
    private FlowNodeCategoryRepository flowNodeCategoryRepository;

    @Override
    public List<NodeCategoryVO> getNodeCategoryList() {
        List<FlowNodeCategoryDO> all = flowNodeCategoryRepository.findAllCategories();
        if (all == null || all.isEmpty()) {
            return new ArrayList<>();
        }

        List<NodeCategoryVO> result = new ArrayList<>();

        // 1. 创建所有 level1 分类
        for (FlowNodeCategoryDO categoryDO : all) {
            if (categoryDO != null && categoryDO.getLevel1Code() != null && !categoryDO.getLevel1Code().isEmpty()) {
                NodeCategoryVO level1VO = new NodeCategoryVO();
                level1VO.setName(categoryDO.getLevel1Name());
                level1VO.setCode(categoryDO.getLevel1Code());
                level1VO.setSubNodeCategories(new ArrayList<>());
                if (!exist(result, level1VO)) {
                    result.add(level1VO);
                }
            }
        }

        // 2. 为每个 level1 分类添加 level2 子分类
        for (NodeCategoryVO level1VO : result) {
            // 从所有数据中查找属于当前level1的level2分类
            for (FlowNodeCategoryDO categoryDO : all) {
                if (categoryDO != null &&
                        categoryDO.getLevel2Code() != null && !categoryDO.getLevel2Code().isEmpty() &&
                        level1VO.getCode().equals(categoryDO.getLevel1Code())) {

                    NodeCategoryVO level2VO = new NodeCategoryVO();
                    level2VO.setName(categoryDO.getLevel2Name());
                    level2VO.setCode(categoryDO.getLevel2Code());
                    level2VO.setSubNodeCategories(new ArrayList<>());

                    if (!exist(level1VO.getSubNodeCategories(), level2VO)) {
                        level1VO.getSubNodeCategories().add(level2VO);
                    }
                }
            }
        }

        // 3. 为每个 level2 分类添加 level3 子分类
        for (NodeCategoryVO level1VO : result) {
            for (NodeCategoryVO level2VO : level1VO.getSubNodeCategories()) {
                // 从所有数据中查找属于当前level1和level2的level3分类
                for (FlowNodeCategoryDO categoryDO : all) {
                    if (categoryDO != null &&
                            categoryDO.getLevel3Code() != null && !categoryDO.getLevel3Code().isEmpty() &&
                            level1VO.getCode().equals(categoryDO.getLevel1Code()) &&
                            level2VO.getCode().equals(categoryDO.getLevel2Code())) {

                        NodeCategoryVO level3VO = new NodeCategoryVO();
                        level3VO.setName(categoryDO.getLevel3Name());
                        level3VO.setCode(categoryDO.getLevel3Code());
                        level3VO.setSubNodeCategories(new ArrayList<>()); // 可以为空列表

                        if (!exist(level2VO.getSubNodeCategories(), level3VO)) {
                            level2VO.getSubNodeCategories().add(level3VO);
                        }
                    }
                }
            }
        }
        return result;
    }


    /**
     * 检查分类是否存在于列表中
     *
     * @param list 分类列表
     * @param vo   要检查的分类对象
     * @return 是否存在
     */
    private boolean exist(List<NodeCategoryVO> list, NodeCategoryVO vo) {
        if (list == null || list.isEmpty() || vo == null || vo.getCode() == null) {
            return false;
        }
        return list.stream().anyMatch(item -> item != null && vo.getCode().equals(item.getCode()));
    }

}
