import { type XTableConfig } from './schema';

const XTableValidate = (props: XTableConfig): boolean => {
    // 标题
    if (props.label?.display && !props.label.text) {
        return false;
    }
    // 数据绑定
    if (!props.metaData) {
        return false;
    }
    // 表头配置
    if (!props.columns || props.columns.length == 0) {
        return false;
    }
    return true;
}

export default XTableValidate;