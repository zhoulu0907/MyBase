import { type XWebViewConfig } from './schema';

const XWebViewValidate = (props: XWebViewConfig): boolean => {
    // 网页链接必填
    if (!props.webViewUrl) {
        return false;
    }
   
    return true;
}

export default XWebViewValidate;