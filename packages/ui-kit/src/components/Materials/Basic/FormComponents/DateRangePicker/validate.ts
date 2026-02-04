import { type XInputDateRangePickerConfig } from './schema';
import { DATE_EXTREME_TYPE } from '../../../constants'

const XDateRangePickerValidate = (props: XInputDateRangePickerConfig): boolean => {
    // 标题
    if (props.label?.display && !props.label.text) {
        return false;
    }
    // 数据绑定
    if (!props.dataField || props.dataField.length === 0) {
        return false;
    }
    // 最早可选日期
    if (props.dateRange.earliestLimit) {
        // 静态值
        if (props.dateRange.earliestType === DATE_EXTREME_TYPE.STATIC && !props.dateRange.earliestStaticValue) {
            return false;
        }
        // 动态值
        if (props.dateRange.earliestType === DATE_EXTREME_TYPE.DYNAMIC && !props.dateRange.earliestDynamicValue) {
            return false;
        }
        // 变量
        if (props.dateRange.earliestType === DATE_EXTREME_TYPE.VARIABLE && !props.dateRange.earliestVariableValue) {
            return false;
        }
    }
    // 最晚可选日期
    if (props.dateRange.latestLimit) {
        // 静态值
        if (props.dateRange.latestType === DATE_EXTREME_TYPE.STATIC && !props.dateRange.latestStaticValue) {
            return false;
        }
        // 动态值
        if (props.dateRange.latestType === DATE_EXTREME_TYPE.DYNAMIC && !props.dateRange.latestDynamicValue) {
            return false;
        }
        // 变量
        if (props.dateRange.latestType === DATE_EXTREME_TYPE.VARIABLE && !props.dateRange.latestVariableValue) {
            return false;
        }
    }
    return true;
}

export default XDateRangePickerValidate;