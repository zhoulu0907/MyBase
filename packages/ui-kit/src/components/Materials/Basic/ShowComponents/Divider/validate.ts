import { type XDividerConfig } from './schema';

const XDividerValidate = (props: XDividerConfig): boolean => {
    // 标题
    if (props.label?.display && !props.label.text) {
        return false;
    }
    // 字段描述
    if (props.tooltip?.display && !props.tooltip.text) {
        return false;
    }
   
    return true;
}

export default XDividerValidate;