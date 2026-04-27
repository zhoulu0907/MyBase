import type { statusProps } from "../types/appItem";

// 步骤配置
export const steps = [
    { title: '基本信息' },
    { title: '管理员信息' },
    { title: '授权应用' },
    { title: '完成' }
];

// 联系地址选项（省/市层级示例）
export const addressOptions = [
    { label: '请选择', value: '' },
    { label: '北京市', value: 'beijing' },
    { label: '上海市', value: 'shanghai' },
    { label: '广东省', value: 'guangdong' },
];

export const statusMapping:statusProps[] = [
    {label:"全部", value: "all", status: 3},
    {label:"已启用", value: "started", status:0},
    {label:"已禁用", value: "disabled", status: 1},
    {label:"已过期", value: "expired", status: 2},
]

export enum StatusEnum {
    ALL = 2,
    ENABLE = 1,
    DISABLE = 0
}

export enum StatusEnumLabel {
    ALl = "全部状态",
    ENABLE = "已启用",
    DISABLE = "已禁用"
}

export const statusOptions = [
    {label: StatusEnumLabel.ALl, value: StatusEnum.ALL },
    { label: StatusEnumLabel.ENABLE, value: StatusEnum.ENABLE },
    { label: StatusEnumLabel.DISABLE, value: StatusEnum.DISABLE },
]

// 允许的文件格式列表
export const allowedFormats = ['image/jpeg', 'image/png', 'image/gif'];

