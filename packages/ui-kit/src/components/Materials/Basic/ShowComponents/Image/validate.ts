import { type XImageConfig } from './schema';

const XImageValidate = (props: XImageConfig): boolean => {
    // 图片配置
    if (!props.imageConfig) {
        return false;
    }
    
    return true;
}

export default XImageValidate;