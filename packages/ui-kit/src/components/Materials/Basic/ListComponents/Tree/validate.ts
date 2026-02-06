import { type XTreeConfig } from './schema';

const XTreeValidate = (props: XTreeConfig): boolean => {
    // 标题
    if (props.label?.display && !props.label.text) {
        return false;
    }
    // 数据绑定
    if (!props.metaData) {
        return false;
    }
    // 高度设置
    if (props.enableMinHeight && !props.minHeight) {
        return false;
    }
    if (props.enableMaxHeight && !props.maxHeight) {
        return false;
    }
    return true;
}

export default XTreeValidate;