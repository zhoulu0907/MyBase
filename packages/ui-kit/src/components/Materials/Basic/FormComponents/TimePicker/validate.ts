import { type XInputTimePickerConfig } from './schema';
import { DATE_EXTREME_TYPE } from '../../../constants'

const XTimePickerValidate = (props: XInputTimePickerConfig): boolean => {
    // 标题
    if (props.label?.display && !props.label.text) {
        return false;
    }
    // 数据绑定
    if (!props.dataField || props.dataField.length === 0) {
        return false;
    }
   
    // 最早可选时间
    if (props.timeRange.earliestLimit && !props.timeRange.earliestValue) {
        return false;
    }
    // 最晚可选时间
    if (props.timeRange.latestLimit && !props.timeRange.latestValue) {
        return false;
    }
    return true;
}

export default XTimePickerValidate;