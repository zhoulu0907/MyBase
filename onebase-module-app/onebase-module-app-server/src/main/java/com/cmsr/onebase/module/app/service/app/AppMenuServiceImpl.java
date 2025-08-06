package com.cmsr.onebase.module.app.service.app;

import com.cmsr.onebase.framework.common.exception.util.ServiceExceptionUtil;
import com.cmsr.onebase.framework.common.util.object.BeanUtils;
import com.cmsr.onebase.module.app.controller.admin.app.vo.MenuCopyReqVO;
import com.cmsr.onebase.module.app.controller.admin.app.vo.MenuCreateReqVO;
import com.cmsr.onebase.module.app.controller.admin.app.vo.MenuListRespVO;
import com.cmsr.onebase.module.app.controller.admin.app.vo.MenuOrderUpdateReqVO;
import com.cmsr.onebase.module.app.dal.database.app.AppMenuRepository;
import com.cmsr.onebase.module.app.dal.dataobject.app.MenuDO;
import com.cmsr.onebase.module.app.enums.app.AppErrorCodeConstants;
import com.cmsr.onebase.module.app.enums.app.MenuTypeEnum;
import com.cmsr.onebase.module.app.enums.app.MenuVisibleEnum;
import com.cmsr.onebase.module.app.util.MenuUtils;
import jakarta.annotation.Resource;
import lombok.Setter;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * @Author：huangjie
 * @Date：2025/7/23 13:40
 */
@Setter
@Service
@Validated
public class AppMenuServiceImpl implements AppMenuService {

    @Resource
    private AppCommonService appCommonService;

    @Resource
    private AppMenuRepository appMenuRepository;

    @Override
    public List<MenuListRespVO> listApplicationMenu(Long applicationId) {
        List<MenuDO> menuDOS = appMenuRepository.findByApplicationId(applicationId);
        List<MenuListRespVO> menuListRespList = new ArrayList<>();
        // 把第一层的菜单添加到列表中
        List<MenuListRespVO> levelOneMenus = menuDOS.stream()
                .filter(v -> Objects.equals(v.getParentId(), MenuUtils.ROOT_MENU_ID))
                .map(v -> BeanUtils.toBean(v, MenuListRespVO.class))
                .toList();
        menuListRespList.addAll(levelOneMenus);
        //递归实现每个菜单的子菜单
        for (MenuListRespVO respVO : menuListRespList) {
            List<MenuListRespVO> children = recursiveGetChildren(respVO, menuDOS);
            respVO.setChildren(children);
        }
        return menuListRespList;
    }

    private List<MenuListRespVO> recursiveGetChildren(MenuListRespVO parent, List<MenuDO> menuDOS) {
        List<MenuListRespVO> children = new ArrayList<>();
        for (MenuDO menuDO : menuDOS) {
            if (Objects.equals(menuDO.getParentId(), parent.getId())) {
                // 只有父菜单的uuid等于当前菜单的父菜单的uuid时，才添加子菜单，继续递归
                MenuListRespVO child = BeanUtils.toBean(menuDO, MenuListRespVO.class);
                child.setChildren(recursiveGetChildren(child, menuDOS));
                children.add(child);
            }
        }
        return children.isEmpty() ? null : children;
    }

    @Override
    public Long createApplicationMenu(MenuCreateReqVO createReqVO) {
        MenuTypeEnum.validate(createReqVO.getMenuType());
        appCommonService.validateApplicationExist(createReqVO.getApplicationId());
        MenuDO menuDO = new MenuDO();
        menuDO.setApplicationId(createReqVO.getApplicationId());

        if (createReqVO.getParentId() != null && createReqVO.getParentId() != MenuUtils.ROOT_MENU_ID) {
            validateApplicationMenuExist(createReqVO.getParentId());
            menuDO.setParentId(createReqVO.getParentId());
        } else {
            menuDO.setParentId(MenuUtils.ROOT_MENU_ID);
        }
        menuDO.setMenuCode(MenuUtils.generateMenuCode());
        menuDO.setMenuType(createReqVO.getMenuType());
        menuDO.setMenuName(createReqVO.getMenuName());
        menuDO.setMenuIcon(createReqVO.getMenuIcon());
        menuDO.setIsVisible(MenuVisibleEnum.YES.getValue());
        appMenuRepository.insert(menuDO);
        return menuDO.getId();
    }

    @Override
    public void updateApplicationMenuName(Long id, String menuName) {
        MenuDO menuDO = validateApplicationMenuExist(id);
        menuDO.setMenuName(menuName);
        appMenuRepository.update(menuDO);
    }

    @Override
    public void updateApplicationMenuOrder(MenuOrderUpdateReqVO updateReqVO) {
        MenuDO menuDO = validateApplicationMenuExist(updateReqVO.getId());
        menuDO.setParentId(updateReqVO.getId());
        appMenuRepository.update(menuDO);
        List<MenuListRespVO> menuListRespList = listApplicationMenu(menuDO.getApplicationId());


    }

    @Override
    public void updateApplicationMenuVisible(Long id, Boolean visible) {
        MenuDO menuDO = validateApplicationMenuExist(id);
        menuDO.setIsVisible(visible);
        appMenuRepository.update(menuDO);
    }

    @Override
    public void copyApplicationMenu(MenuCopyReqVO copyReqVO) {
        MenuDO menuDO = validateApplicationMenuExist(copyReqVO.getId());
        if (menuDO.getMenuType() == MenuTypeEnum.GROUP.getValue()) {
            throw ServiceExceptionUtil.exception(AppErrorCodeConstants.APP_MENU_GROUP_NOT_ALLOW_COPY);
        }
        menuDO.setMenuName(copyReqVO.getMenuName());
        menuDO.setParentId(copyReqVO.getParentId());
        menuDO.setId(null);
        appMenuRepository.insert(menuDO);
        //TODO 菜单相关的资源都要复制

    }

    @Override
    public void deleteApplicationMenu(Long id) {
        MenuDO menuDO = validateApplicationMenuExist(id);
        if (menuDO.getMenuType() == MenuTypeEnum.GROUP.getValue()
                && validateApplicationMenuGroupHasChildren(menuDO.getId())) {
            throw ServiceExceptionUtil.exception(AppErrorCodeConstants.APP_MENU_GROUP_HAS_CHILDREN);
        }
        appMenuRepository.deleteById(id);
        //TODO 菜单相关资源都要删除
    }


    private MenuDO validateApplicationMenuExist(Long id) {
        MenuDO menuDO = appMenuRepository.findById(id);
        if (menuDO == null) {
            throw ServiceExceptionUtil.exception(AppErrorCodeConstants.APP_MENU_NOT_EXIST);
        }
        return menuDO;
    }

    private boolean validateApplicationMenuGroupHasChildren(Long id) {
        return appMenuRepository.countByParentId(id) > 0;
    }

}
