package com.cmsr.onebase.module.app.service.app;

import com.cmsr.onebase.framework.aynline.DataRepository;
import com.cmsr.onebase.framework.common.exception.util.ServiceExceptionUtil;
import com.cmsr.onebase.framework.common.util.object.BeanUtils;
import com.cmsr.onebase.module.app.controller.app.vo.ApplicationMenuCopyReqVO;
import com.cmsr.onebase.module.app.controller.app.vo.ApplicationMenuGroupCreateReqVO;
import com.cmsr.onebase.module.app.controller.app.vo.ApplicationMenuListRespVO;
import com.cmsr.onebase.module.app.controller.app.vo.ApplicationMenuOrderUpdateReqVO;
import com.cmsr.onebase.module.app.dal.dataobject.app.ApplicationMenuDO;
import com.cmsr.onebase.module.app.enums.app.AppErrorCodeConstants;
import com.cmsr.onebase.module.app.enums.app.ApplicationMenuTypeEnum;
import com.cmsr.onebase.module.app.enums.app.ApplicationMenuVisible;
import com.cmsr.onebase.module.app.util.MenuUtils;
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

    @Override
    public List<ApplicationMenuListRespVO> listApplicationMenu(Long applicationId) {
        ConfigStore configs = new DefaultConfigStore();
        configs.eq("application_id", applicationId);
        configs.order("menu_sort", Order.TYPE.ASC);
        List<ApplicationMenuDO> menuDOS = dataRepository.findAll(ApplicationMenuDO.class, configs);
        List<ApplicationMenuListRespVO> menuListRespList = new ArrayList<>();
        // 把第一层的菜单添加到列表中
        List<ApplicationMenuListRespVO> levelOneMenus = menuDOS.stream()
                .filter(menuDO -> StringUtils.equalsIgnoreCase(menuDO.getParentUuid(), MenuUtils.ROOT_MENU_UUID))
                .map(menuDO -> BeanUtils.toBean(menuDO, ApplicationMenuListRespVO.class))
                .toList();
        menuListRespList.addAll(levelOneMenus);
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

    @Override
    public Long createApplicationMenuGroup(ApplicationMenuGroupCreateReqVO createReqVO) {
        ApplicationMenuDO menuDO = new ApplicationMenuDO();
        menuDO.setApplicationId(createReqVO.getApplicationId());
        if (StringUtils.isNoneBlank(createReqVO.getParentUuid())) {
            menuDO.setParentUuid(createReqVO.getParentUuid());
        } else {
            menuDO.setParentUuid(MenuUtils.ROOT_MENU_UUID);
        }
        menuDO.setMenuType(ApplicationMenuTypeEnum.GROUP.getValue());
        menuDO.setMenuName(createReqVO.getMenuName());
        menuDO.setIsVisible(ApplicationMenuVisible.YES.getValue());
        dataRepository.insert(menuDO);
        return menuDO.getId();
    }

    @Override
    public void updateApplicationMenuName(Long id, String menuName) {
        ApplicationMenuDO menuDO = validateApplicationMenuExist(id);
        menuDO.setMenuName(menuName);
        dataRepository.update(menuDO);
    }

    @Override
    public void updateApplicationMenuOrder(ApplicationMenuOrderUpdateReqVO updateReqVO) {
        ApplicationMenuDO menuDO = validateApplicationMenuExist(updateReqVO.getId());
        menuDO.setParentUuid(updateReqVO.getParentUuid());
        dataRepository.update(menuDO);
        List<ApplicationMenuListRespVO> menuListRespList = listApplicationMenu(menuDO.getApplicationId());


    }

    @Override
    public void updateApplicationMenuVisible(Long id, Boolean visible) {
        ApplicationMenuDO menuDO = validateApplicationMenuExist(id);
        menuDO.setIsVisible(visible ? ApplicationMenuVisible.YES.getValue() : ApplicationMenuVisible.NO.getValue());
        dataRepository.update(menuDO);
    }

    @Override
    public void copyApplicationMenu(ApplicationMenuCopyReqVO copyReqVO) {
        ApplicationMenuDO menuDO = validateApplicationMenuExist(copyReqVO.getId());
        if (menuDO.getMenuType() == ApplicationMenuTypeEnum.GROUP.getValue()) {
            throw ServiceExceptionUtil.exception(AppErrorCodeConstants.APP_MENU_GROUP_NOT_ALLOW_COPY);
        }
        menuDO.setMenuName(copyReqVO.getMenuName());
        menuDO.setParentUuid(copyReqVO.getParentUuid());
        menuDO.setId(null);
        dataRepository.insert(menuDO);
    }

    @Override
    public void deleteApplicationMenu(Long id) {
        ApplicationMenuDO menuDO = validateApplicationMenuExist(id);
        if (menuDO.getMenuType() == ApplicationMenuTypeEnum.GROUP.getValue()
                && validateApplicationMenuGroupHasChildren(menuDO.getMenuUuid())) {
            throw ServiceExceptionUtil.exception(AppErrorCodeConstants.APP_MENU_GROUP_HAS_CHILDREN);
        }
        dataRepository.deleteById(ApplicationMenuDO.class, id);
    }

    private ApplicationMenuDO validateApplicationMenuExist(Long id) {
        ApplicationMenuDO menuDO = dataRepository.findById(ApplicationMenuDO.class, id);
        if (menuDO == null) {
            throw ServiceExceptionUtil.exception(AppErrorCodeConstants.APP_MENU_NOT_EXIST);
        }
        return menuDO;
    }

    private boolean validateApplicationMenuGroupHasChildren(String menuUuid) {
        ConfigStore configs = new DefaultConfigStore();
        configs.eq("parent_uuid", menuUuid);
        return dataRepository.countByConfig(ApplicationMenuDO.class, configs) > 0;
    }

}
