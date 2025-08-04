import { baseConfig, baseDefault, labelColSpanConfig, layoutConfig, /* widthConfig, */ listTypeConfig, statusConfig, type ICommonBaseType, type TLayoutSelectKeyType, type TStatusSelectKeyType, type TUploadSelectKeyType, type TWidthSelectKeyType } from "@/components/Materials/common";
import { CONFIG_TYPES, /* WIDTH_OPTIONS, WIDTH_VALUES, */ LAYOUT_OPTIONS, LAYOUT_VALUES, STATUS_OPTIONS, STATUS_VALUES, UPLOAD_OPTIONS, UPLOAD_VALUES } from "@/components/Materials/constants";
import type { IBooleanConfigType, IDescriptionConfigType, ILabelConfigType, ILayoutConfigType, INumberConfigType, IPlaceholderConfigType, ISelectConfigType, IStatusConfigType, ITextAreaConfigType, ITextConfigType, ITooltipConfigType, IUploadLimitConfigType, IUploadSizeConfigType, IWidthConfigType, TBooleanDefaultType, TNumberDefaultType, TSelectDefaultType, TTextAreaDefaultType, TTextDefaultType } from "@/components/Materials/types";



export interface XInputFileUploadSchema {
    editData: TXInputFileUploadEditData;
    config: XInputFileUploadConfig;
}


export type TXInputFileUploadEditData = Array<
  ITextConfigType |
  ILabelConfigType |
  IPlaceholderConfigType |
  IDescriptionConfigType |
  ITooltipConfigType |
  IStatusConfigType<TStatusSelectKeyType> |
  IWidthConfigType<TWidthSelectKeyType> |
  INumberConfigType |
  ISelectConfigType<TWidthSelectKeyType | TStatusSelectKeyType > |
  ITextAreaConfigType|
  IUploadSizeConfigType|
  IUploadLimitConfigType|
  IBooleanConfigType|
  IStatusConfigType<TUploadSelectKeyType>|
  ILayoutConfigType<TLayoutSelectKeyType>
>;


export interface XInputFileUploadConfig extends ICommonBaseType {
    /**
     * 输入框标题
     */
    label: TTextDefaultType;

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
    status?: TSelectDefaultType<TStatusSelectKeyType>;

    /**
     * 默认值
     */
    defaultValue?: TTextDefaultType;

    /**
     * 字段宽度
     */
    // width: TSelectDefaultType<TWidthSelectKeyType>;

    /**
     * 是否必填，未填写时提交报错
     */
    required: TBooleanDefaultType;

    /**
     * 表单的布局：水平、垂直（默认）
     * 可选值: 'vertical' | 'horizontal'
     */
    layout?: TLayoutSelectKeyType;

    /**
     * 标签宽度
     */
    labelColSpan?: TNumberDefaultType;

    /**
     * 单个文件大小限制（MB），最大 100, 默认10
     */
    uploadSize?: TNumberDefaultType;

    /**
     * 上传数量限制，默认无限制
     */
    uploadLimit?: TNumberDefaultType;

    /**
     * 是否允许预览文件
     */
    showPreview?: TBooleanDefaultType;

    /**
     * 是否允许下载文件
     */
    showDownload?: TBooleanDefaultType;

    /**
     * 文件/图片展示样式：文本、平铺、列表
     * 可选值: 'text' | 'picture-card' | 'picture-list'
     */
    listType?: TSelectDefaultType<TUploadSelectKeyType>;

    /**
     * 隐藏时是否提交数据，开启后隐藏状态仍会保存值
     */
    saveWithHidden?: TBooleanDefaultType;
}


const XFileUpload: XInputFileUploadSchema = {
    editData: [
        ...baseConfig,
        {
            key: 'label',
            name: '标题',
            type: CONFIG_TYPES.LABEL_INPUT,
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
        layoutConfig,
        labelColSpanConfig,
        {
            key: 'required',
            name: '开启必填',
            type: CONFIG_TYPES.SWITCH_INPUT,
        },
        {
            key: 'uploadSize',
            name: '文件大小限制',
            type: CONFIG_TYPES.UPLOAD_SIZE,
        },
        {
            key: 'uploadLimit',
            name: '上传数量限制',
            type: CONFIG_TYPES.UPLOAD_LIMIT,
        },
        {
            key: 'showPreview',
            name: '允许预览文件',
            type: CONFIG_TYPES.SWITCH_INPUT,
        },
        {
            key: 'showDownload',
            name: '允许下载文件',
            type: CONFIG_TYPES.SWITCH_INPUT,
        },
        {
            key: 'saveWithHidden',
            name: '隐藏时提交数据',
            type: CONFIG_TYPES.SWITCH_INPUT,
        },
        statusConfig,
        // widthConfig,
        listTypeConfig,
    ],
    config: {
        ...baseDefault,
        label: '标题',
        description: '',
        tooltip: '',
        // width: WIDTH_VALUES[WIDTH_OPTIONS.QUARTER],
        status: STATUS_VALUES[STATUS_OPTIONS.DEFAULT],
        defaultValue: '',
        required: false,
        uploadSize: 10,
        uploadLimit: -1,
        showPreview: false,
        showDownload: false,
        listType: UPLOAD_VALUES[UPLOAD_OPTIONS.CARD],
        layout: LAYOUT_VALUES[LAYOUT_OPTIONS.HORIZONTAL],
        saveWithHidden: false,
        labelColSpan: 5,
    }
};

export default XFileUpload;