import { type XSubTableConfig } from './schema';

const XSubTableValidate = (props: XSubTableConfig): boolean => {
    // 标题
    if (props.label?.display && !props.label.text) {
        return false;
    }
    
    return true;
}

export default XSubTableValidate;