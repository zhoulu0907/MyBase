package com.cmsr.onebase.module.app.service.app;

import com.cmsr.onebase.framework.aynline.DataRepository;
import com.cmsr.onebase.framework.common.util.object.BeanUtils;
import com.cmsr.onebase.module.app.controller.app.vo.ApplicationMenuCreateReqVO;
import com.cmsr.onebase.module.app.controller.app.vo.ApplicationMenuListRespVO;
import com.cmsr.onebase.module.app.dal.dataobject.app.ApplicationMenuDO;
import jakarta.annotation.Resource;
import lombok.Setter;
import org.anyline.data.param.ConfigStore;
import org.anyline.data.param.init.DefaultConfigStore;
import org.anyline.entity.Order;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import java.util.ArrayList;
import java.util.List;

/**
 * @Author：huangjie
 * @Date：2025/7/23 13:40
 */
@Setter
@Service
@Validated
public class ApplicationMenuServiceImpl implements ApplicationMenuService {

    @Resource
    private DataRepository dataRepository;

    @Resource
    private AppCommonService appCommonService;


    public List<ApplicationMenuListRespVO> listApplicationMenu(Long applicationId) {
        ConfigStore configs = new DefaultConfigStore();
        configs.eq("application_id", applicationId);
        configs.order("menu_sort", Order.TYPE.ASC);
        List<ApplicationMenuDO> menuDOS = dataRepository.findAll(ApplicationMenuDO.class, configs);
        List<ApplicationMenuListRespVO> menuListRespList = new ArrayList<>();
        // 把第一层的菜单添加到列表中 TODO 顶层菜单的判断逻辑，钉钉是一个特殊值：NAV-SYSTEM-PARENT-UUID
        menuListRespList.addAll(menuDOS.stream()
                .filter(menuDO -> StringUtils.equalsIgnoreCase(menuDO.getParentUuid(), "ROOT-UUID"))
                .map(menuDO -> BeanUtils.toBean(menuDO, ApplicationMenuListRespVO.class))
                .toList());
        //递归实现每个菜单的子菜单
        for (ApplicationMenuListRespVO respVO : menuListRespList) {
            List<ApplicationMenuListRespVO> children = recursiveGetChildren(respVO, menuDOS);
            respVO.setChildren(children);
        }
        return menuListRespList;
    }

    private List<ApplicationMenuListRespVO> recursiveGetChildren(ApplicationMenuListRespVO parent, List<ApplicationMenuDO> menuDOS) {
        List<ApplicationMenuListRespVO> children = new ArrayList<>();
        for (ApplicationMenuDO menuDO : menuDOS) {
            if (StringUtils.equalsIgnoreCase(menuDO.getParentUuid(), parent.getMenuUuid())) {
                // 只有父菜单的uuid等于当前菜单的父菜单的uuid时，才添加子菜单，继续递归
                ApplicationMenuListRespVO child = BeanUtils.toBean(menuDO, ApplicationMenuListRespVO.class);
                child.setChildren(recursiveGetChildren(child, menuDOS));
                children.add(child);
            }
        }
        return children.isEmpty() ? null : children;
    }

    public Long createApplicationMenu(ApplicationMenuCreateReqVO applicationMenuCreateReqVO) {
        return null;
    }

    public void deleteApplicationMenu(Long applicationId, String menuUuid) {
    }

    public void updateApplicationMenuName(Long applicationId, String menuUuid, String menuName) {

    }
}
