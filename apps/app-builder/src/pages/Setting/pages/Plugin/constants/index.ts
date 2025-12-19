export interface statusProps {
  label: string;
  value: string;
}

export enum StatusEnumLabel {
  ALL = '全部',
  ENABLE = '已启用',
  DISABLE = '未启用'
}

export enum StatusValue {
  ALL = 'all',
  ENABLE = 'started',
  DISABLE = 'disabled'
}

export enum StatusEnum {
  ALL = 2,
  ENABLE = 1,
  DISABLE = 0,
}

export const statusMapping: statusProps[] = [
  { label: StatusEnumLabel.ALL, value: StatusValue.ALL},
  { label: StatusEnumLabel.ENABLE, value: StatusValue.ENABLE},
  { label: StatusEnumLabel.DISABLE, value: StatusValue.DISABLE}
];
