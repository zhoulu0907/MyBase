export interface BatchUpdateSecurityConfigsParams {
  configs: SecurityConfigItem[];
}

export interface SecurityConfigItem {
  configKey: string;
  configValue: string;
}

export interface GetTenantSecurityConfigParams {
  appId?: string;
  tenantId?: string;
  categoryCode?: string[];
}
