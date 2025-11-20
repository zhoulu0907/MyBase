export interface statusProps {
  label: string;
  value: string;
  status: number;
}

export const statusMapping: statusProps[] = [
    {label:"全部", value: "all", status: 3},
    {label:"已启用", value: "started", status:0},
    {label:"已禁用", value: "disabled", status: 1},
    {label:"已过期", value: "expired", status: 2},
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