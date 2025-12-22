export interface statusProps {
  label: string;
  value: string;
  status: number;
}

/**
 * 全部0, 启用1，禁用2, 过期 3
 */
export enum StatusEnum {
  ALL = 0,
  ENABLE = 1,
  DISABLE = 2,
  EXPIRED = 3
}

export enum StatusEnumLabel {
  ALL = "全部",
  ENABLE = "已启用",
  DISABLE = "已禁用",
  EXPIRED = "已过期"
}

export enum StatusValue {
  ALL = "all",
  ENABLE = "started",
  DISABLE = "disabled",
  EXPIRED = "expired"
}


export const statusMapping: statusProps[] = [
  {label:StatusEnumLabel.ALL, value: StatusValue.ALL, status: StatusEnum.ALL},
  {label:StatusEnumLabel.ENABLE, value: StatusValue.ENABLE, status:StatusEnum.ENABLE},
  {label:StatusEnumLabel.DISABLE, value: StatusValue.DISABLE, status: StatusEnum.DISABLE},
  {label:StatusEnumLabel.EXPIRED, value: StatusValue.EXPIRED, status: StatusEnum.EXPIRED},
]

export const statusOptions = [
  {
    label: '全部状态',
    value: ''
  },
  {
    label: '开发中',
    value: 0
  },
  {
    label: '已发布',
    value: 1
  }
];

export const statusColorMap = {
  [StatusEnum.ENABLE]: 'green',
  [StatusEnum.DISABLE]: 'danger',
  [StatusEnum.ALL]:'',
  [StatusEnum.EXPIRED]:'gray',
};

export enum StatusLabelEnum {
  ENABLE = '启用',
  DISABLE = '禁用',
  EXPIRED = '过期'
}

export enum ThirdLoginType {
  PASSWORD = 'password',
  VERIFYCODE = 'verifycode'
}

export enum ThirdLoginTypeLabel {
  PASSWORD = '密码登录',
  VERIFYCODE = '验证码登录'
}

export const ThirdLoginMap = [
  {label: ThirdLoginTypeLabel.VERIFYCODE, value: ThirdLoginType.VERIFYCODE},
  {label: ThirdLoginTypeLabel.PASSWORD, value: ThirdLoginType.PASSWORD}
]