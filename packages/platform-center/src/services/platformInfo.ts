import { PlatformInfoReq } from '../types/platformInfo'
import platformClient from './clients/platform'

// 获取平台信息 platforminfo/page
export const getPlatFormInfoListApi = (params: PlatformInfoReq) => platformClient.get(`/platforminfo/page?pageNum=${params.pageNum}&pageSize=${params.pageSize}`)

// 上传平台 License /platforminfo/upload
export const uploadPlatformLicenseApi = (data: any) => platformClient.post('/platforminfo/upload', data)

// 下载平台 license /system/platforminfo/export/{id} get
export const downloadPlatformLicenseApi = (id: number) => platformClient.get(`/system/platforminfo/export/${id}`)