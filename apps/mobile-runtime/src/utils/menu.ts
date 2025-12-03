import { MenuType, type ApplicationMenu } from "@onebase/app";


export const addParentIdToChildren = (menuItems: ApplicationMenu[], parentId?: string): ApplicationMenu[] => {
    // 只保留 menuType 为 2（分组）的菜单项用于生成父级页面选择下拉框
    return menuItems
      .filter((menu) => menu.menuType == MenuType.GROUP)
      .map((menu) => ({
        ...menu,
        parentId: parentId,
        children: menu.children ? addParentIdToChildren(menu.children, menu.id) : []
      }));
  };