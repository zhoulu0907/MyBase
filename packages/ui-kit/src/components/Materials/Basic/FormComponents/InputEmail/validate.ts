import { type XInputEmailConfig } from './schema';

const XInputEmailValidate = (props: XInputEmailConfig): boolean => {
    // 标题
    if (props.label?.display && !props.label.text) {
        return false;
    }
    // 数据绑定
    if (!props.dataField || props.dataField.length === 0) {
        return false;
    }
    // 安全
    if (props.security?.display && !props.security.type) {
        return false;
    }
    return true;
}

export default XInputEmailValidate;