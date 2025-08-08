import { PlatformInfoReq } from '../types/platformInfo';
import systemClient from './clients/system';

// 获取平台信息
export const getPlatformInfoApi = () => systemClient.get('/platforminfo/get-platform-info')

// 获取平台信息 platforminfoList
export const getPlatFormInfoListApi = (params: PlatformInfoReq) => systemClient.get(`/license/page?pageNum=${params.pageNum}&pageSize=${params.pageSize}`)

// 上传平台 License /platforminfo/upload
export const uploadPlatformLicenseApi = (data: any) => systemClient.post('/platforminfo/upload', data);

// 下载平台 license /platforminfo/export/{id} get
export const downloadPlatformLicenseApi = (id: number) => systemClient.get(`/platforminfo/export/${id}`)