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
  EXPIRES = 3
}

export enum StatusEnumLabel {
  ALL = "全部",
  ENABLE = "已启用",
  DISABLE = "已禁用",
  EXPIRES = "已过期"
}

export enum StatusValue {
  ALL = "all",
  ENABLE = "started",
  DISABLE = "disabled",
  EXPIRES = "expired"
}


export const statusMapping: statusProps[] = [
  {label:StatusEnumLabel.ALL, value: StatusValue.ALL, status: StatusEnum.ALL},
  {label:StatusEnumLabel.ENABLE, value: StatusValue.ENABLE, status:StatusEnum.ENABLE},
  {label:StatusEnumLabel.DISABLE, value: StatusValue.DISABLE, status: StatusEnum.DISABLE},
  {label:StatusEnumLabel.EXPIRES, value: StatusValue.EXPIRES, status: StatusEnum.EXPIRES},
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