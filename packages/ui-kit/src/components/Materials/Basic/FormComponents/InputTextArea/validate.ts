import { type XInputTextAreaConfig } from './schema';

const XInputTextAreaValidate = (props: XInputTextAreaConfig): boolean => {
    // 标题
    if (props.label?.display && !props.label.text) {
        return false;
    }
    // 数据绑定
    if (!props.dataField || props.dataField.length === 0) {
        return false;
    }
    // 校验
    if (props.verify.lengthLimit && !props.verify.minLength && props.verify.minLength !== 0 && !props.verify.maxLength) {
        return false;
    }
    // 安全
    if (props.security?.display && !props.security.type) {
        return false;
    }
    return true;
}

export default XInputTextAreaValidate;