import { type XIndicatorCardConfig } from './schema';

const XIndicatorCardValidate = (props: XIndicatorCardConfig): boolean => {
    // 标题
    if (props.label?.display && !props.label.text) {
        return false;
    }
   
    return true;
}

export default XIndicatorCardValidate;