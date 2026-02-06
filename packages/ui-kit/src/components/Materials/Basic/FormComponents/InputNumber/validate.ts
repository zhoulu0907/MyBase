import { type XInputNumberConfig } from './schema';

const XInputNumberValidate = (props: XInputNumberConfig): boolean => {
    // 标题
    if (props.label?.display && !props.label.text) {
        return false;
    }
    // 数据绑定
    if (!props.dataField || props.dataField.length === 0) {
        return false;
    }
    // 校验
    if (props.verify.numberLimit && !props.verify.min && props.verify.min !== 0 && !props.verify.max) {
        return false;
    }
    // 安全
    if (props.security?.display && !props.security.type) {
        return false;
    }
    return true;
}

export default XInputNumberValidate;