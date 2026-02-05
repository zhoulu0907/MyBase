import { type XInputFileUploadConfig } from './schema';
import { UPLOAD_OPTIONS, UPLOAD_VALUES } from '../../../constants';
const XFileUploadValidate = (props: XInputFileUploadConfig): boolean => {
    // 标题
    if (props.label?.display && !props.label.text) {
        return false;
    }
    // 数据绑定
    if (!props.dataField || props.dataField.length === 0) {
        return false;
    }
    // 按钮名称
    if (props.uploadType === UPLOAD_VALUES[UPLOAD_OPTIONS.TEXT] && !props.buttonName) {
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

export default XFileUploadValidate;