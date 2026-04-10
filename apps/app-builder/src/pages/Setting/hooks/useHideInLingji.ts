import { getPlatform } from '@/products';

/**
 * 获取灵畿平台下需要隐藏的菜单 key 列表
 */
export function getHiddenMenuKeys(): string[] {
  const platform = getPlatform();
  if (platform !== 'lingji') return [];

  return [
    'security',      // 安全设置
    'agent',         // 智能体管理
    'copilotdoc',    // AI生成文档
    'wxmini'         // AI生成小程序
  ];
}

/**
 * 灵畿平台隐藏控制 Hook
 * 返回在灵畿平台下需要隐藏的元素状态
 */
export function useHideInLingji() {
  const platform = getPlatform();
  const isLingji = platform === 'lingji';

  return {
    // 是否隐藏菜单项
    hideSecurity: isLingji,           // 安全设置
    hideAgent: isLingji,              // 智能体管理
    hideCopilotdoc: isLingji,         // AI生成文档
    hideWxmini: isLingji,             // AI生成小程序
    // 是否隐藏空间信息的"企业数"
    hideCorpCount: isLingji,
    // 是否隐藏应用发布的外部登录链接
    hideExternalLoginLinks: isLingji,
  };
}