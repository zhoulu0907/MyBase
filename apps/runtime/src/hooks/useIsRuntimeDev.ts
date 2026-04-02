import { useLocation } from 'react-router-dom';

const RUNTIME_DEV_PATH = '/runtime-dev';

/** 0
 * 根据当前路由判断是否为运行时开发模式（路径包含 /runtime-dev/）
 */
export function useIsRuntimeDev(): boolean {
  const location = useLocation();
  return location.pathname.includes(RUNTIME_DEV_PATH) || location.hash.includes(RUNTIME_DEV_PATH);
}
