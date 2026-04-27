import { PlatformInfoReq } from '../types/platformInfo';
import { platformService } from './clients';

// 获取平台信息
export const getPlatformInfoApi = () => platformService.get('/platform/get-platform-info');

// 获取平台信息 platforminfoList
export const getPlatFormInfoListApi = (params: PlatformInfoReq) =>
  platformService.get(`/license/page?pageNo=${params.pageNo}&pageSize=${params.pageSize}`);

// 上传平台 License
export const uploadPlatformLicenseApi = (data: any) =>
  platformService.post('/license/import', data, {
    headers: {
      // 不要手动设置Content-Type，让浏览器自动设置
      'Content-Type': 'multipart/form-data'
    }
  });
