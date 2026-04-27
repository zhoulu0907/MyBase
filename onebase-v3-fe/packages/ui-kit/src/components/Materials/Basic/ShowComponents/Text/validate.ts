import { type XTextConfig } from './schema';

const XTextValidate = (props: XTextConfig): boolean => {
    // 文本内容
    if (!props.content) {
        return false;
    }
   
    return true;
}

export default XTextValidate;