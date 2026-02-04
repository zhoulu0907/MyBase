import { type XFileConfig } from './schema';

const XFileValidate = (props: XFileConfig): boolean => {
    // 文件配置
    if (!props.fileConfig || props.fileConfig.length === 0) {
        return false;
    }
   
    return true;
}

export default XFileValidate;