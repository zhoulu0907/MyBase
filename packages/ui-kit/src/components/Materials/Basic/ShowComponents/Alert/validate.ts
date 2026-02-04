import { type XAlertConfig } from './schema';

const XAlertValidate = (props: XAlertConfig): boolean => {
    // 标题
    if (props.label?.display && !props.label.text) {
        return false;
    }
    // 文本内容 content
    if (props.content?.display && !props.content.text) {
        return false;
    }
   
    return true;
}

export default XAlertValidate;