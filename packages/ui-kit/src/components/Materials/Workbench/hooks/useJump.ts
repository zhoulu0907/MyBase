// hooks/useJump.ts 或类似文件
import { useNavigate, useLocation, useParams } from 'react-router-dom';
import { menuCacheManager } from '../utils/jump';
import { type ApplicationMenu, menuSignal } from '@onebase/app';

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
  const { setCurMenu } = menuSignal;

  const handleJump = async (options: JumpOptions) => {
    const { linkAddress, menuUuid, runtime = true } = options;

    if (!runtime) return;

    // 外部链接
    if (linkAddress) {
      if (linkAddress.startsWith('http')) {
        window.open(linkAddress, '_blank');
      } else {
        navigate(linkAddress);
      }
      return;
    }

    // 内部菜单跳转
    if (menuUuid && appId) {
      const appRuntimeMenu = await menuCacheManager.getMenuList(appId);
      const targetMenu = appRuntimeMenu.find((menu: ApplicationMenu) => menu.menuUuid === menuUuid);

      if (targetMenu && targetMenu.id) {
        // 获取当前URL的查询参数, 更新或添加 curMenu 参数
        const searchParams = new URLSearchParams(location.search);
        searchParams.set('curMenu', targetMenu.id);
        const newPath = `${location.pathname}?${searchParams.toString()}`;
        navigate(newPath);
        setCurMenu(targetMenu);
      } else {
        console.warn('未找到对应菜单或菜单未配置 id', menuUuid);
      }
    }
  };

  return { handleJump };
}