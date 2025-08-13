import { PlatformInfoReq } from '../types/platformInfo';
import { systemService } from './clients';

// 获取平台信息
export const getPlatformInfoApi = () => systemService.get('/platform/get-platform-info')

// 获取平台信息 platforminfoList
export const getPlatFormInfoListApi = (params: PlatformInfoReq) => systemService.get(`/license/page?pageNo=${params.pageNo}&pageSize=${params.pageSize}`)

// 上传平台 License
export const uploadPlatformLicenseApi = (data: any) => systemService.post('/license/upload', data);

// 下载平台 license 
export const downloadPlatformLicenseApi = (id: number = 1) => {
  return systemService.get(`/license/export?id=${id}`), {
    responseType: 'blob'
  }
}