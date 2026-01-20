// hooks/useJump.ts 或类似文件
import { useNavigate, useLocation, useParams } from 'react-router-dom';
import { menuCacheManager, DATA_CONFIG_NAME_MAP } from '@onebase/ui-kit';
import { menuSignal, type ApplicationMenu } from '@onebase/app';

/**
 * 跳转选项
 */
export interface JumpOptions {
  /** 应用ID（用于菜单查询） */
  appId?: string;
  /** 外部链接地址 */
  linkAddress?: string;
  /** 菜单UUID（用于内部菜单跳转） */
  menuUuid?: string;
  /** 是否在运行时（非编辑态） */
  runtime?: boolean;
}

export function useJump() {
  const navigate = useNavigate();
  const location = useLocation();
  const { appId } = useParams<{ appId?: string }>();

  // 递归查找菜单（包括子菜单）
  const findMenuByUuid = (menus: ApplicationMenu[], uuid: string): ApplicationMenu | null => {
    for (const menu of menus) {
      if (menu.menuUuid === uuid) {
        return menu;
      }
      if (menu.children && menu.children.length > 0) {
        const found = findMenuByUuid(menu.children, uuid);
        if (found) return found;
      }
    }
    return null;
  };

  // 根据菜单名称递归查找菜单
  const findMenuByName = (menus: ApplicationMenu[], name: string): ApplicationMenu | null => {
    for (const menu of menus) {
      if (menu.menuName === name) {
        return menu;
      }
      if (menu.children && menu.children.length > 0) {
        const found = findMenuByName(menu.children, name);
        if (found) return found;
      }
    }
    return null;
  }; 

  const handleJump = async (options: JumpOptions) => {
    const { linkAddress, menuUuid, runtime = true } = options;

    if (!runtime) return;

    // 外部链接
    if (linkAddress) {
      if (linkAddress.startsWith('http://') || linkAddress.startsWith('https://')) {
        window.open(linkAddress, '_blank');
      } else {
        navigate(linkAddress);
      }
      return;
    }

    // 内部菜单跳转
    if (menuUuid && appId) {
      const appRuntimeMenu = await menuCacheManager.getMenuList(appId);
      const targetMenu = findMenuByUuid(appRuntimeMenu, menuUuid);

      if (targetMenu && targetMenu.id) {
        const searchParams = new URLSearchParams(location.search);
        searchParams.set('curMenu', targetMenu.id);
        const to = `${location.pathname}?${searchParams.toString()}`;

        navigate(to, { replace: true });

        const { setCurMenu } = menuSignal;
        setCurMenu(targetMenu);
      } else {
        console.warn('未找到对应菜单或菜单未配置 id', menuUuid);
      }
    }
  };

  // 根据配置键跳转到BPM菜单
  const handleBPMJump = async (configKey: string, runtime = true) => {
    if (!runtime || !appId) return;

    const menuName = DATA_CONFIG_NAME_MAP[configKey];
    if (!menuName) {
      console.warn('未找到对应的菜单名称', configKey);
      return;
    }

    const bpmMenuList = await menuCacheManager.getBPMMenuList(appId);
    
    const targetMenu = findMenuByName(bpmMenuList, menuName);

    if (!targetMenu) {
      console.warn('未找到对应的 BPM 菜单', menuName);
      return;
    }

    // 根据BPM菜单的menuCode进行跳转
    if (targetMenu.menuCode) {
      const searchParams = new URLSearchParams(location.search);
      searchParams.set('curMenu', targetMenu.menuCode);
      const to = `${location.pathname}?${searchParams.toString()}`;

      navigate(to, { replace: true });

      const { setCurMenu } = menuSignal;
      setCurMenu(targetMenu);
      return;
    }
  };

  return { handleJump, handleBPMJump };
}