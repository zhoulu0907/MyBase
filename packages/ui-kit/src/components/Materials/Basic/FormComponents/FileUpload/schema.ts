import {
  baseConfig,
  baseDefault,
  dataFieldConfig,
  labelColSpanConfig,
  layoutConfig,
  listTypeConfig,
  statusConfig,
  widthConfig,
  type ICommonBaseType,
  type TLayoutSelectKeyType,
  type TStatusSelectKeyType,
  type TUploadSelectKeyType,
  type TWidthSelectKeyType
} from '../../../common';
import {
  CONFIG_TYPES,
  LAYOUT_OPTIONS,
  LAYOUT_VALUES,
  STATUS_OPTIONS,
  STATUS_VALUES,
  UPLOAD_OPTIONS,
  UPLOAD_VALUES,
  WIDTH_OPTIONS,
  WIDTH_VALUES
} from '../../../constants';
import type {
  IBooleanConfigType,
  IDataFieldConfigType,
  ILabelConfigType,
  ILayoutConfigType,
  INumberConfigType,
  IPlaceholderConfigType,
  ISelectConfigType,
  IStatusConfigType,
  ITextAreaConfigType,
  ITextConfigType,
  ITooltipConfigType,
  IUploadLimitConfigType,
  IUploadSizeConfigType,
  IVerifyConfigType,
  IWidthConfigType,
  TBooleanDefaultType,
  TNumberDefaultType,
  TSelectDefaultType,
  TTextAreaDefaultType,
  TTextDefaultType
} from '../../../types';

export interface XInputFileUploadSchema {
  editData: TXInputFileUploadEditData;
  config: XInputFileUploadConfig;
}

export type TXInputFileUploadEditData = Array<
  | ITextConfigType
  | ILabelConfigType
  | IPlaceholderConfigType
  | ITooltipConfigType
  | IStatusConfigType<TStatusSelectKeyType>
  | IWidthConfigType<TWidthSelectKeyType>
  | INumberConfigType
  | ISelectConfigType<TWidthSelectKeyType | TStatusSelectKeyType>
  | ITextAreaConfigType
  | IUploadSizeConfigType
  | IUploadLimitConfigType
  | IBooleanConfigType
  | IStatusConfigType<TUploadSelectKeyType>
  | ILayoutConfigType<TLayoutSelectKeyType>
  | IVerifyConfigType
  | IDataFieldConfigType
>;

export interface XInputFileUploadConfig extends ICommonBaseType {
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
   * 数据字段
   */
  dataField: TTextDefaultType[];

  /**
   * 描述信息（鼠标悬浮时显示）
   */
  tooltip?: TTextAreaDefaultType;

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
  width: TSelectDefaultType<TWidthSelectKeyType>;

  /**
   * required：是否必填，未填写时提交报错
   * maxCount：最大上传数量，默认：-1 不限制
   * maxSize：最大图片大小单位：MB，默认：10，最大100
   * fileFormat：支持的文件类型，多个类型用逗号分隔，默认不限制
   */
  verify: {
    required: TBooleanDefaultType;
    maxCount: TNumberDefaultType;
    maxSize: TNumberDefaultType;
    fileFormat: TTextDefaultType;
  };

  /**
   * 表单的布局：水平、垂直（默认）
   * 可选值: 'vertical' | 'horizontal'
   */
  layout?: TLayoutSelectKeyType;

  /**
   * 标题宽度
   */
  labelColSpan?: TNumberDefaultType;

  /**
   * 是否允许预览文件
   */
  // showPreview?: TBooleanDefaultType;

  /**
   * 是否允许下载文件
   */
  // showDownload?: TBooleanDefaultType;

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
      type: CONFIG_TYPES.LABEL_INPUT
    },
    ...dataFieldConfig,
    {
      key: 'tooltip',
      name: '描述信息',
      type: CONFIG_TYPES.TOOLTIP_INPUT
    },
    layoutConfig,
    labelColSpanConfig,
    // {
    //   key: 'showPreview',
    //   name: '允许预览文件',
    //   type: CONFIG_TYPES.SWITCH_INPUT
    // },
    // {
    //   key: 'showDownload',
    //   name: '允许下载文件',
    //   type: CONFIG_TYPES.SWITCH_INPUT
    // },
    // {
    //   key: 'saveWithHidden',
    //   name: '隐藏时提交数据',
    //   type: CONFIG_TYPES.SWITCH_INPUT
    // },
    {
      key: 'verify',
      name: '校验',
      type: CONFIG_TYPES.VERIFY
    },
    listTypeConfig,
    statusConfig,
    widthConfig
  ],
  config: {
    ...baseDefault,
    label: {
      text: '文件上传',
      display: true
    },
    dataField: [],
    tooltip: '',
    width: WIDTH_VALUES[WIDTH_OPTIONS.HALF],
    status: STATUS_VALUES[STATUS_OPTIONS.DEFAULT],
    defaultValue: '',
    // showPreview: false,
    // showDownload: false,
    listType: UPLOAD_VALUES[UPLOAD_OPTIONS.TEXT],
    layout: LAYOUT_VALUES[LAYOUT_OPTIONS.VERTICAL],
    saveWithHidden: false,
    labelColSpan: 200,
    verify: {
      required: false,
      maxCount: -1,
      maxSize: 10,
      fileFormat: ''
    }
  }
};

export default XFileUpload;
