// 步骤配置
export const steps = [
    { title: '基本信息' },
    { title: '管理员信息' },
    { title: '授权应用' },
    { title: '完成' }
];

// 行业类型选项
export const industryOptions = [
    { label: '工业', value: 'industry' },
    { label: '金融', value: 'finance' },
    { label: '教育', value: 'education' },
    { label: '医疗', value: 'medical' },
];


// 联系地址选项（省/市层级示例）
export const addressOptions = [
    { label: '请选择', value: '' },
    { label: '北京市', value: 'beijing' },
    { label: '上海市', value: 'shanghai' },
    { label: '广东省', value: 'guangdong' },
];

export const noLabelLayout = {
    wrapperCol: {
        span: 19,
        offset: 5,
    },
};