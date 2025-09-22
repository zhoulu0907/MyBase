import { PlatformInfoReq } from '../types/platformInfo';
import { systemService } from './clients';

// 获取平台信息
export const getPlatformInfoApi = () => systemService.get('/platform/get-platform-info')

// 获取平台信息 platforminfoList
export const getPlatFormInfoListApi = (params: PlatformInfoReq) => systemService.get(`/license/page?pageNo=${params.pageNo}&pageSize=${params.pageSize}`)

// 上传平台 License
export const uploadPlatformLicenseApi = (data: any) => systemService.post('/license/import', data, {
  headers: {
      // 不要手动设置Content-Type，让浏览器自动设置
      'Content-Type': 'multipart/form-data'
    },
});