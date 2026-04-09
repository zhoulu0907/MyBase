// 从平台选择器导出平台判断函数（推荐）
export { isTiangongPlatform, isLingjiPlatform, getPlatform } from '@/products';

// 兼容旧的导出（已废弃，建议使用 isTiangongPlatform）
// 使用此导出会在控制台显示警告
export { isArtifexDomain } from './domain';

export * from './hooks';
export * from './menuData';
export * from './useMenuSelection';
export * from '../pages/Application/utils/useApplicationPage';
