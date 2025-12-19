import {
  baseConfig,
  baseDefault,
  dataFieldConfig,
  layoutConfig,
  statusConfig,
  widthConfig,
  buttonNameConfig,
  uploadButtonTypeConfig,
  uploadMethodConfig,
  showDownloadConfig,
  labelConfig,
  tooltipConfig,
  verifyConfig,
  type ICommonBaseType,
  type TLayoutSelectKeyType,
  type TStatusSelectKeyType,
  type TUploadSelectKeyType,
  type TUploadButtonType,
  type TWidthSelectKeyType
} from '../../../common';
import {
  LAYOUT_OPTIONS,
  LAYOUT_VALUES,
  STATUS_OPTIONS,
  STATUS_VALUES,
  UPLOAD_OPTIONS,
  UPLOAD_VALUES,
  WIDTH_OPTIONS,
  WIDTH_VALUES,
  UPLOAD_BUTTON_TYPES
} from '../../../constants';
import type {
  IBooleanConfigType,
  ICommonConfigType,
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
  | ICommonConfigType
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
    labelConfig,
    tooltipConfig,
    ...dataFieldConfig,
    uploadMethodConfig,
    buttonNameConfig,
    uploadButtonTypeConfig,
    showDownloadConfig,
    verifyConfig,
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
      maxCount: 1,
      maxSize: 10,
      fileFormat: ''
    },
    status: STATUS_VALUES[STATUS_OPTIONS.DEFAULT],
    layout: LAYOUT_VALUES[LAYOUT_OPTIONS.VERTICAL],
    width: WIDTH_VALUES[WIDTH_OPTIONS.HALF],
  }
};

export default XFileUpload;
