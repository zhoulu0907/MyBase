import { type XInputCheckboxConfig } from './schema';

const XCheckboxValidate = (props: XInputCheckboxConfig): boolean => {
    // 标题
    if (props.label?.display && !props.label.text) {
        return false;
    }
    // 数据绑定
    if (!props.dataField || props.dataField.length === 0) {
        return false;
    }
    // 校验
    if (props.verify.checkedLimit && !props.verify.minChecked && props.verify.minChecked !== 0 && !props.verify.maxChecked) {
        return false;
    }
    return true;
}

export default XCheckboxValidate;