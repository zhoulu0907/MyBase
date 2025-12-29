import { PlatformTenantStatus } from '@onebase/platform-center';
import type { CreateSourceType, SelectOptions } from './type';

export enum CreateSourceValue {
    BACK = 'back',
    SELF = 'self'
}
export const CreateSource: CreateSourceType = {
  back: '后台注册',
  self: '自主注册'
};

export const statusOptions: SelectOptions[] = [
  {
    label: '全部状态',
    value: ''
  },
  {
    label: '已启用',
    value: PlatformTenantStatus.enabled
  },
  {
    label: '已禁用',
    value: PlatformTenantStatus.disabled
  }
];
