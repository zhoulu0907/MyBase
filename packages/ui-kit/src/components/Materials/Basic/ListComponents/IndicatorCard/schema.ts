import type {
    TTextDefaultType,
    TBooleanDefaultType,
    ICommonConfigType,
    TRadioDefaultType,
    TSelectDefaultType,
    IStatusConfigType,
    IWidthConfigType
} from '../../../types';
import {
    baseConfig,
    baseDefault,
    statusConfig,
    cardWidthConfig,
    indicatorCardStyleConfig,
    indicatorCardConfig,
    type ICommonBaseType,
    type TStatusSelectKeyType,
    type TWidthSelectKeyType,
} from '../../../common';
import {
    STATUS_OPTIONS,
    STATUS_VALUES,
    WIDTH_OPTIONS,
    WIDTH_VALUES,
    INDICATOR_CARD_STYLE_TYPE
} from '../../../constants';

export interface XIndicatorCardSchema {
    editData: TXIndicatorCardEditData;
    config: XIndicatorCardConfig;
}

export type TXIndicatorCardEditData = Array<
    | ICommonConfigType
    | IStatusConfigType<TStatusSelectKeyType>
    | IWidthConfigType<TWidthSelectKeyType>
>;

export interface XIndicatorCardConfig extends ICommonBaseType {
    /**
     * 输入框标题
     * text：标题
     * display：是否显示
     */
    label: {
        text: TTextDefaultType;
        display: TBooleanDefaultType;
    };
    /**
     * 样式库
     */
    styleType: string;

    /**
     * 指标配置
     */
    indicatorList: any[];

    /**
     * 显示状态：可用、隐藏、只读
     * 可选值: 'default' | 'hidden' | 'readonly'
     */
    status?: TRadioDefaultType<TStatusSelectKeyType>;
    /**
     * 卡片宽度
     */
    cardWidth: TSelectDefaultType<TWidthSelectKeyType>;
}

const XIndicatorCard: XIndicatorCardSchema = {
    editData: [
        ...baseConfig,
        indicatorCardStyleConfig,
        indicatorCardConfig,
        // 显示状态
        statusConfig,
        // 宽度
        cardWidthConfig
    ],
    config: {
        ...baseDefault,
        label: {
            text: '卡片',
            display: false
        },
        styleType: INDICATOR_CARD_STYLE_TYPE.ONE,
        indicatorList: [
            {name:'指标一'},
            {name:'指标二'},
            {name:'指标三'},
            {name:'指标四'},
        ],
        status: STATUS_VALUES[STATUS_OPTIONS.DEFAULT],
        cardWidth: WIDTH_VALUES[WIDTH_OPTIONS.QUARTER]
    }
}

export default XIndicatorCard;