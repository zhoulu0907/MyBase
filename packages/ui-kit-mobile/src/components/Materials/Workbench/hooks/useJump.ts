// hooks/useJump.ts 或类似文件
import { useNavigate, useLocation, useParams } from 'react-router-dom';
import { menuCacheManager } from '@onebase/ui-kit';
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
      const targetMenu = appRuntimeMenu.find((menu: ApplicationMenu) => menu.menuUuid === menuUuid);

      if (targetMenu && targetMenu.id) {
        const searchParams = new URLSearchParams(location.search);
        searchParams.set('curMenu', targetMenu.id);
        const to = `${location.pathname}?${searchParams.toString()}`;

        navigate(to, { replace: true });

        const { setCurMenu } = menuSignal;
        setCurMenu({
          id: targetMenu.id || '',
          menuCode: targetMenu.menuCode || '',
          menuSort: targetMenu.menuSort || 1,
          menuType: targetMenu.menuType || 1,
          menuName: targetMenu.menuName || '',
          menuIcon: targetMenu.menuIcon || '',
          isVisible: targetMenu.isVisible || 1,
          pagesetType: targetMenu.pagesetType,
          children: targetMenu.children || [],
        });
      } else {
        console.warn('未找到对应菜单或菜单未配置 id', menuUuid);
      }
    }
  };

  return { handleJump };
}