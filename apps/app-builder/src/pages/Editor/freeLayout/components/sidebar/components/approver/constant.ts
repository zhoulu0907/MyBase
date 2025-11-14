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

export const approverConfigVar:any = {
  approvalMode: {
    a: 'counter_sign',
    b: 'any_sign',
    c: 'c_sign',
    d: 'd_sign'
  }
};
export const approvalConfigVar:any = {
  approvalMode: {
    'counter_sign': '会签（所有人同意才通过）',
    'any_sign': '或签（一人同意即通过）',
    'c_sign': '依次审批（按顺序依次审批）',
    'd_sign': '投票（按投票比例决定是否通过）'
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

export interface ApproverConfigDataType {
  approverConfig?: ApproverConfigType;
  buttonConfigs?: ButtonConfigType[];
  fieldPermConfig?: FieldPermConfigType;
  name?: string;
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
  ckOptions:any
}
