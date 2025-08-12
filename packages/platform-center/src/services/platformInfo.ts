import { PlatformInfoReq } from '../types/platformInfo';
import { systemService } from './clients';

// 获取平台信息
export const getPlatformInfoApi = () => systemService.get('/platforminfo/get-platform-info')

// 获取平台信息 platforminfoList
export const getPlatFormInfoListApi = (params: PlatformInfoReq) => systemService.get(`/license/page?pageNo=${params.pageNo}&pageSize=${params.pageSize}`)

// 上传平台 License /platforminfo/upload
export const uploadPlatformLicenseApi = (data: any) => systemService.post('/platforminfo/upload', data);

// 下载平台 license /platforminfo/export/{id} get
export const downloadPlatformLicenseApi = (id: number) => systemService.get(`/platforminfo/export/${id}`)