import { type XRateConfig } from './schema';

const XRateValidate = (props: XRateConfig): boolean => {
    // 标题
    if (props.label?.display && !props.label.text) {
        return false;
    }
    // 数据绑定
    if (!props.dataField || props.dataField.length === 0) {
        return false;
    }
    // 自定义评分文案
    if (props.rateConfig.showCustomTooltips) {
        const flag = props.rateConfig.tooltips.every(ele => !!ele)
        if (!flag) {
            return false;
        }
    }

    return true;
}

export default XRateValidate;