import { type XRelatedFormConfig } from './schema';

const XRelatedFormValidate = (props: XRelatedFormConfig): boolean => {
    // 标题
    if (props.label?.display && !props.label.text) {
        return false;
    }
    // 数据绑定
    if (!props.dataField || props.dataField.length === 0) {
        return false;
    }
    
    return true;
}

export default XRelatedFormValidate;