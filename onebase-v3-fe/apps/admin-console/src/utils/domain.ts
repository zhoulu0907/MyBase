import { getPlatformFeURL } from '@onebase/common';

// 获取当前环境的域名前缀
export const getPlatformFeDomain = () => {
  // 检查全局配置
  try {
    const url = new URL(getPlatformFeURL());
    return url.toString();
  } catch (e) {
    console.error('解析PLATFORM_FE_URL失败:', e);
  }

  // 返回默认值
  return 'http://localhost:4399';
};
