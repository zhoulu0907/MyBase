import { type XInputUserSelectConfig } from './schema';

const XUserSelectValidate = (props: XInputUserSelectConfig): boolean => {
    // 标题
    if (props.label?.display && !props.label.text) {
        return false;
    }
    // 数据绑定
    if (!props.dataField || props.dataField.length === 0) {
        return false;
    }
    // 可选范围
    if (props.isSelectScope && (!props.selectScope && props.selectScope === 0)) {
        return false;
    }
   
    return true;
}

export default XUserSelectValidate;