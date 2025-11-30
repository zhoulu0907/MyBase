export interface BatchUpdateSecurityConfigsParams {
  configs: SecurityConfigItem[];
}

export interface SecurityConfigItem {
  configKey: string;
  configValue: string;
}
