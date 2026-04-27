export interface TenantSecurityConfig {
  categoryCode: string;
  securityConfigItemRespVO: TenantSecurityConfigItem[];
}

export interface TenantSecurityConfigItem {
  id: string;
  configKey: string;
  configName: string;
  dataType: string;
  configValue: string;
  description: string;
  sortOrder: number;
  categoryId: string;
  options: string;
  maxValue?: number;
  minValue?: number;
  required: string;
  widgetType: string;
}
