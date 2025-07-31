import { baseConfig, baseDefault, statusConfig, widthConfig, type ICommonBaseType, type TStatusSelectKeyType, type TWidthSelectKeyType } from "@/components/Materials/common";
import { CONFIG_TYPES, STATUS_OPTIONS, STATUS_VALUES, WIDTH_OPTIONS, WIDTH_VALUES } from "../../../constants";
import type { IDescriptionConfigType, ILabelConfigType, INumberConfigType, IPlaceholderConfigType, IStatusConfigType, ITextAreaConfigType, ITextConfigType, ITooltipConfigType, IWidthConfigType, TRadioDefaultType, TTextAreaDefaultType, TTextDefaultType } from "../../../types";


// 输入框组件的schema
export interface XInputTextSchema {
    // 可配置项
    editData: TXInputTextEditData;
    // 默认配置
    config: XInputTextConfig;
}

// 输入框组件的可配置项
export type TXInputTextEditData = Array<
  ITextConfigType |
  ILabelConfigType |
  IPlaceholderConfigType |
  IDescriptionConfigType |
  ITooltipConfigType |
  IStatusConfigType<TStatusSelectKeyType> |
  IWidthConfigType<TWidthSelectKeyType> |
  INumberConfigType |
  ITextAreaConfigType
>;


export interface XInputTextConfig extends ICommonBaseType {
    /**
     * 输入框标题
     */
    label: TTextDefaultType;

    /**
     * 占位符
     */
    placeholder: TTextDefaultType;

    /**
     * 描述信息（显示在输入框下方，辅助说明）
     */
    description: TTextAreaDefaultType;

    /**
     * 提示文字（鼠标悬浮时显示）
     */
    tooltip?: TTextDefaultType;

    /**
     * 组件状态：可用、隐藏、只读
     * 可选值: 'default' | 'hidden' | 'readonly'
     */
    status?: TRadioDefaultType<TStatusSelectKeyType>;

    /**
     * 默认值
     */
    defaultValue?: TTextDefaultType;

    /**
     * 字段宽度
     */
    width: TRadioDefaultType<TWidthSelectKeyType>;
}



const XInputText: XInputTextSchema = {
    editData: [
        ...baseConfig,
        {
            key: 'label',
            name: '标题',
            type: CONFIG_TYPES.LABEL_INPUT,
        },
        {
            key: 'placeholder',
            name: '占位符',
            type: CONFIG_TYPES.PLACEHOLDER_INPUT,
        },
        {
            key: 'description',
            name: '描述信息',
            type: CONFIG_TYPES.DESCRIPTION_INPUT,
        },
        {
            key: 'tooltip',
            name: '提示文字',
            type: CONFIG_TYPES.TOOLTIP_INPUT,
        },
        statusConfig,
        widthConfig
    ],
    config: {
        ...baseDefault,
        label: '标题',
        placeholder: '请输入文字',
        description: '',
        tooltip: '',
        width: WIDTH_VALUES[WIDTH_OPTIONS.HALF],
        status: STATUS_VALUES[STATUS_OPTIONS.DEFAULT],
        defaultValue: '',
    }
};

export default XInputText;