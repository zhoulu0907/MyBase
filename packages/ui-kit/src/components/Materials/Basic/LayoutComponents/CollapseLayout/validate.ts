import { type XCollapseLayoutConfig } from './schema';

const XCollapseLayoutValidate = (props: XCollapseLayoutConfig): boolean => {
    // 标题
    if (props.label?.display && !props.label.text) {
        return false;
    }
    
    return true;
}

export default XCollapseLayoutValidate;