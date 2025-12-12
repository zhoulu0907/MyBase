export interface statusProps {
  label: string;
  value: string;
  status: number;
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
  ALL = 0,
  ENABLE = 1,
  DISABLE = 2,
  EXPIRED = 3
}

export const statusMapping: statusProps[] = [
  { label: StatusEnumLabel.ALL, value: StatusValue.ALL, status: StatusEnum.ALL },
  { label: StatusEnumLabel.ENABLE, value: StatusValue.ENABLE, status: StatusEnum.ENABLE },
  { label: StatusEnumLabel.DISABLE, value: StatusValue.DISABLE, status: StatusEnum.DISABLE }
];
