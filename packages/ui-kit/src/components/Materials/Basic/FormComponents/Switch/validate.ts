import { type XInputSwitchConfig } from './schema';

const XSwitchValidate = (props: XInputSwitchConfig): boolean => {
    // 标题
    if (props.label?.display && !props.label.text) {
        return false;
    }
    // 数据绑定
    if (!props.dataField || props.dataField.length === 0) {
        return false;
    }
    // 填充文本
    if (props.fillText?.display && (!props.fillText.checkedText || !props.fillText.uncheckedText)) {
        return false;
    }
    
    return true;
}

export default XSwitchValidate;