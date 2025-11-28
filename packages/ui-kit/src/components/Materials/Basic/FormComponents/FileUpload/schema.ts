import {
  baseConfig,
  baseDefault,
  dataFieldConfig,
  layoutConfig,
  statusConfig,
  widthConfig,
  type ICommonBaseType,
  type TLayoutSelectKeyType,
  type TStatusSelectKeyType,
  type TUploadSelectKeyType,
  type TUploadButtonType,
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
  UPLOAD_TYPE_OPTIONS,
  WIDTH_OPTIONS,
  WIDTH_VALUES,
  UPLOAD_BUTTON_TYPES
} from '../../../constants';
import type {
  IBooleanConfigType,
  IDataFieldConfigType,
  ILabelConfigType,
  ILayoutConfigType,
  IStatusConfigType,
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
  TTextDefaultType,
} from '../../../types';

export interface XInputFileUploadSchema {
  editData: TXInputFileUploadEditData;
  config: XInputFileUploadConfig;
}

export type TXInputFileUploadEditData = Array<
  | ILabelConfigType
  | ITooltipConfigType
  | IDataFieldConfigType
  | IStatusConfigType<TUploadSelectKeyType>
  | ITextConfigType
  | IStatusConfigType<TUploadButtonType>
  | IBooleanConfigType
  | IVerifyConfigType
  | IUploadSizeConfigType
  | IUploadLimitConfigType
  | IStatusConfigType<TStatusSelectKeyType>
  | ILayoutConfigType<TLayoutSelectKeyType>
  | IWidthConfigType<TWidthSelectKeyType>
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
   * 描述信息（鼠标悬浮时显示）
   */
  tooltip?: TTextAreaDefaultType;

  /**
   * 数据字段
   */
  dataField: TTextDefaultType[];

  // 上传方式
  uploadType?: TSelectDefaultType<TUploadSelectKeyType>;

  // 按钮名称
  buttonName?: string;

  // 按钮类型
  buttonType?: TSelectDefaultType<TUploadButtonType>;

  // 列表页支持下载
  showDownload?: TBooleanDefaultType;

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
   * 组件状态：可用、隐藏、只读
   * 可选值: 'default' | 'hidden' | 'readonly'
   */
  status?: TSelectDefaultType<TStatusSelectKeyType>;

  /**
   * 表单的布局：水平、垂直（默认）
   * 可选值: 'vertical' | 'horizontal'
   */
  layout?: TLayoutSelectKeyType;

  /**
   * 字段宽度
   */
  width: TSelectDefaultType<TWidthSelectKeyType>;
}

const XFileUpload: XInputFileUploadSchema = {
  editData: [
    ...baseConfig,
    {
      key: 'label',
      name: '标题',
      type: CONFIG_TYPES.LABEL_INPUT
    },
    {
      key: 'tooltip',
      name: '描述信息',
      type: CONFIG_TYPES.TOOLTIP_INPUT
    },
    ...dataFieldConfig,
    {
      key: 'uploadType',
      name: '上传方式',
      type: CONFIG_TYPES.STATUS_RADIO,
      range: [
        {
          key: UPLOAD_OPTIONS.TEXT,
          text: UPLOAD_TYPE_OPTIONS.TEXT,
          value: UPLOAD_VALUES[UPLOAD_OPTIONS.TEXT]
        },
        {
          key: UPLOAD_OPTIONS.LIST,
          text: UPLOAD_TYPE_OPTIONS.LIST,
          value: UPLOAD_VALUES[UPLOAD_OPTIONS.LIST]
        },
      ]
    },
    {
      key: 'buttonName',
      name: '按钮名称',
      type: CONFIG_TYPES.TEXT_INPUT
    },
    {
      key: 'buttonType',
      name: '按钮类型',
      type: CONFIG_TYPES.STATUS_RADIO,
      range: [
        {
          key: UPLOAD_BUTTON_TYPES.PRIMARY,
          text: '主要按钮',
          value: UPLOAD_BUTTON_TYPES.PRIMARY
        },
        {
          key: UPLOAD_BUTTON_TYPES.SECONDARY,
          text: '次要按钮',
          value: UPLOAD_BUTTON_TYPES.SECONDARY
        },
        {
          key: UPLOAD_BUTTON_TYPES.OUTLINE,
          text: '线框按钮',
          value: UPLOAD_BUTTON_TYPES.OUTLINE
        }
      ]
    },
    {
      key: 'showDownload',
      name: '列表页支持下载',
      type: CONFIG_TYPES.SWITCH_INPUT
    },
    {
      key: 'verify',
      name: '校验',
      type: CONFIG_TYPES.VERIFY
    },
    statusConfig,
    layoutConfig,
    widthConfig
  ],
  config: {
    ...baseDefault,
    label: {
      text: '文件上传',
      display: true
    },
    tooltip: '',
    dataField: [],
    uploadType: UPLOAD_VALUES[UPLOAD_OPTIONS.TEXT],
    buttonName:'点击上传',
    buttonType: UPLOAD_BUTTON_TYPES.PRIMARY,
    showDownload: false,
     verify: {
      required: false,
      maxCount: -1,
      maxSize: 10,
      fileFormat: ''
    },
    status: STATUS_VALUES[STATUS_OPTIONS.DEFAULT],
    layout: LAYOUT_VALUES[LAYOUT_OPTIONS.VERTICAL],
    width: WIDTH_VALUES[WIDTH_OPTIONS.HALF],
  }
};

export default XFileUpload;
