export const btnConfigVar = {
  buttonName: {
    agree: 'agree',
    reject: 'reject',
    save: 'save',
    transfer: 'transfer',
    signature: 'signature',
    returnBack: 'returnBack',
    rollBack: 'rollBack',
    abstention: 'abstention'
  }
};

export const approverConfigVar: any = {
  approvalMode: {
    a: 'counter_sign',
    b: 'any_sign',
    c: 'c_sign',
    d: 'd_sign'
  }
};

export enum ApproveDrawerTab {
  APPROVER = 'approver',
  APPROVER_BTN = 'approverBtn',
  FIELD_PERMISSIONS = 'fieldPermissions',
  ADVANCED_SETTINGS = 'advancedSettings'
}

interface User {
  userId?: string;
  name?: string;
  roleId?: string;
  roleName?: string;
}

export interface ApproverConfigType {
  approverType?: string;
  users?: User[];
  roles?: User[];
  approvalMode?: string;
}

export interface ButtonConfigType {
  buttonName?: string;
  buttonType?: string;
  displayName?: string;
  defaultApprovalComment?: string;
  approvalCommentRequired?: boolean;
  enabled?: boolean;
  batchApproval?: boolean;
}

interface FieldConfig {
  fieldId?: string;
  fieldName?: string;
  fieldPermType?: string;
}

export interface FieldPermConfigType {
  useNodeConfig?: boolean;
  fieldConfigs?: FieldConfig[];
}
export interface AdvancedConfigType {
  autoApproveCfg?: any;
  emptyApproverCfg?: any;
}

export interface ApproverConfigDataType {
  approverConfig?: ApproverConfigType;
  buttonConfigs?: ButtonConfigType[];
  fieldPermConfig?: FieldPermConfigType;
  advancedConfig?: AdvancedConfigType;
  name?: string;
  errorMsg?: string;
}

export interface ApproveDrawerProps {
  configData?: ApproverConfigDataType;
  handleConfigSubmit: (data: ApproverConfigDataType, name: string) => void;
}

type ConfigKey = 'approverConfig' | 'buttonConfigs' | 'fieldPermConfig';

export interface BaseConfig<T> {
  setApprovalConfigData: (key: ConfigKey, data: T) => void;
}

export interface ApproverConfig extends BaseConfig<ApproverConfigType> {
  approverConfig: ApproverConfigType;
}

export interface BtnConfig extends BaseConfig<ButtonConfigType[]> {
  buttonConfigs: ButtonConfigType[];
}

export interface FieldConfigType extends BaseConfig<FieldPermConfigType> {
  fieldPermConfig: FieldPermConfigType;
  ckOptions: any;
}
export interface AdvancedConfig extends BaseConfig<AdvancedConfigType> {
  advancedConfig: AdvancedConfigType;
}

export function getDefaultBtnConfig() {
  const btns:any[] = [
    {
      key: '1',
      buttonType: 'approve',
      buttonName: '同意',
      displayName: '同意',
      name: '同意',
      defaultApprovalComment: '同意',
      approvalCommentRequired: false,
      batchApproval: false,
      enabled: false
    },
    {
      key: '2',
      buttonType: 'reject',
      buttonName: '拒绝',
      displayName: '拒绝',
      name: '拒绝',
      defaultApprovalComment: '拒绝',
      approvalCommentRequired: false,
      batchApproval: false,
      enabled: false
    },
    {
      key: '3',
      buttonType: 'save',
      buttonName: '保存',
      displayName: '保存',
      name: '保存',
      approvalCommentRequired: false,
      batchApproval: false,
      enabled: false
    },
    {
      key: '4',
      buttonType: 'transfer',
      buttonName: '转交',
      displayName: '转交',
      name: '转交',
      defaultApprovalComment: '转交',
      approvalCommentRequired: false,
      batchApproval: false,
      enabled: false
    },
    {
      key: '5',
      buttonType: 'add_sign',
      buttonName: '加签',
      displayName: '加签',
      name: '加签',
      defaultApprovalComment: '加签',
      approvalCommentRequired: false,
      batchApproval: false,
      enabled: false
    },
    {
      key: '6',
      buttonType: 'return',
      buttonName: '退回',
      displayName: '退回',
      name: '退回',
      defaultApprovalComment: '退回',
      approvalCommentRequired: false,
      batchApproval: false,
      enabled: false
    },
    {
      key: '7',
      buttonType: 'withdraw',
      buttonName: '撤回',
      displayName: '撤回',
      name: '撤回',
      defaultApprovalComment: '撤回',
      approvalCommentRequired: false,
      batchApproval: false,
      enabled: false
    },
    {
      key: '8',
      buttonType: 'abstain',
      buttonName: '弃权',
      displayName: '弃权',
      name: '弃权',
      defaultApprovalComment: '弃权',
      approvalCommentRequired: false,
      batchApproval: false,
      enabled: false
    }
  ];
  return btns
}