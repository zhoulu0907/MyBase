/**
 * @deprecated 此文件已废弃，请使用 @/products 中的平台判断函数
 * import { isTiangongPlatform, isLingjiPlatform, getPlatform } from '@/products';
 */

console.warn(
  '[DEPRECATED] utils/domain.ts 已废弃，请使用 @/products 中的函数。\n' +
  '新用法: import { isTiangongPlatform } from "@/products";'
);

export const isArtifexDomain = (): boolean => {
  if (typeof window === 'undefined') return false;
  return window.location.hostname.includes('artifex-cmcc');
};
