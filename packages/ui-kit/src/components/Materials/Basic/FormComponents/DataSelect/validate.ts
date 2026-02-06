import { type XDataSelectConfig } from './schema';

const XDataSelectValidate = (props: XDataSelectConfig): boolean => {
    // 标题
    if (props.label?.display && !props.label.text) {
        return false;
    }
    // 数据绑定
    if (!props.dataField || props.dataField.length === 0) {
        return false;
    }
    // 数据源
    if (!props.selectedDataSource || !props.selectedDataSource.entityUuid) {
        return false;
    }
    // 回显字段
    if (!props.displayFields || props.displayFields.length === 0) {
        return false;
    }
    
    return true;
}

export default XDataSelectValidate;