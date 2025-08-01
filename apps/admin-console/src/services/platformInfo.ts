// /system/platforminfo/simple-list
import { httpGet } from '@/utils/http';

// 获取认证记录列表
export const getPlatformInfoListApi = async () => {
  const response = await httpGet('/system/platforminfo/simple-list');
  return response;
};