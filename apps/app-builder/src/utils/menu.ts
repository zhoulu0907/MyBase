import { MenuType, type ApplicationMenu } from "@onebase/app";

 /**
   * 递归为菜单项补充parentCode字段
   * @param menuItems 菜单项数组
   * @param parentCode 父级Code
   * @returns 处理后的菜单项数组
   */
export const addParentCodeToChildren = (menuItems: ApplicationMenu[], parentCode?: string): ApplicationMenu[] => {
    // 只保留 menuType 为 2（分组）的菜单项用于生成父级页面选择下拉框
    return menuItems
      .filter((menu) => menu.menuType == MenuType.GROUP)
      .map((menu) => ({
        ...menu,
        parentCode: parentCode,
        children: menu.children ? addParentCodeToChildren(menu.children, menu.menuCode) : []
      }));
  };