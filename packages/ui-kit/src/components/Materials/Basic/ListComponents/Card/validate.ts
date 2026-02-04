import { type XCardConfig } from './schema';

const XCardValidate = (props: XCardConfig): boolean => {
    // 标题
    if (props.label?.display && !props.label.text) {
        return false;
    }
    // 数据绑定
    if (!props.metaData) {
        return false;
    }
    // 显示字段
    if (props.showFields && (!props.columns || props.columns.length == 0)) {
        return false;
    }
   
    return true;
}

export default XCardValidate;