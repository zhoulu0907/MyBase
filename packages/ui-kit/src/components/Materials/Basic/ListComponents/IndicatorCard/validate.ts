import { type XIndicatorCardConfig } from './schema';

const XIndicatorCardValidate = (props: XIndicatorCardConfig): boolean => {
    // 标题
    if (props.label?.display && !props.label.text) {
        return false;
    }
    // 指标配置 indicatorList
   
    return true;
}

export default XIndicatorCardValidate;