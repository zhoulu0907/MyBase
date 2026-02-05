import { type XInputImgUploadConfig } from './schema';

const XImgUploadValidate = (props: XInputImgUploadConfig): boolean => {
    // 标题
    if (props.label?.display && !props.label.text) {
        return false;
    }
    // 数据绑定
    if (!props.dataField || props.dataField.length === 0) {
        return false;
    }
    
    // 校验 上传数量限制
    if (props.verify.maxCountLimit && !props.verify.maxCount) {
        return false;
    }
    // 校验 大小限制
    if (props.verify.maxSizeLimit && !props.verify.maxSize) {
        return false;
    }
    // 校验 格式限制
    if (props.verify.fileFormatLimit && !props.verify.fileFormat) {
        return false;
    }

    return true;
}

export default XImgUploadValidate;