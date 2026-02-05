import { type XCarouselConfig } from './schema';

const XCarouselFormValidate = (props: XCarouselConfig): boolean => {
    // 标题
    if (props.label?.display && !props.label.text) {
        return false;
    }
    // 图片配置
    if (!props.carouselConfig || props.carouselConfig.length === 0) {
        return false;
    }
    // 轮播间隔
    if (props.autoplay && !props.interval) {
        return false;
    }

    return true;
}

export default XCarouselFormValidate;