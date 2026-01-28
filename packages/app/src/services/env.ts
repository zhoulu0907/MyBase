import { flowService } from './clients';

/**
 * 获取环境列表
 * 用于连接器创建时"选择已有环境信息"功能
 */
export const getEnvList = () => {
  // TODO: 确认实际的后端接口路径
  // 临时使用 flowService，需要根据实际 API 调整
  return flowService.get('/env/list');
};
