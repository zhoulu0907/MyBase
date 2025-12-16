import {
  baseConfig,
  baseDefault,
  dataFieldConfig,
  layoutConfig,
  listTypeConfig,
  uploadTypeConfig,
  statusConfig,
  widthConfig,
  imageHandleConfig,
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
  IDataFieldConfigType,
  ILabelConfigType,
  ILayoutConfigType,
  IStatusConfigType,
  ITooltipConfigType,
  IVerifyConfigType,
  IWidthConfigType,
  IImageHandleConfigType,
  TBooleanDefaultType,
  TNumberDefaultType,
  TSelectDefaultType,
  TTextAreaDefaultType,
  TTextDefaultType
} from '../../../types';

export interface XInputImgUploadSchema {
  editData: TXInputImgUploadEditData;
  config: XInputImgUploadConfig;
}

export type TXInputImgUploadEditData = Array<
  | ILabelConfigType
  | ITooltipConfigType
  | IDataFieldConfigType
  | IStatusConfigType<TUploadSelectKeyType>
  | IImageHandleConfigType
  | IVerifyConfigType
  | IStatusConfigType<TStatusSelectKeyType>
  | ILayoutConfigType<TLayoutSelectKeyType>
  | IWidthConfigType<TWidthSelectKeyType>
>;

interface IMAGE_HANDLE {
  autoCompress: boolean, // 自动压缩图片
  addWatermark: boolean, //添加水印
  watermarkText?: string // 水印文案
}

export interface XInputImgUploadConfig extends ICommonBaseType {
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

  /**
   * 文件/图片展示样式：文本、平铺、列表
   * 可选值: 'text' | 'picture-card' | 'picture-list'
   */
  listType?: TSelectDefaultType<TUploadSelectKeyType>;

  // 图片处理 
  imageHandle?: IMAGE_HANDLE;

  /**
   * required：是否必填，未填写时提交报错
   * maxCount：最大上传数量，默认：-1 不限制
   * maxSize：最大图片大小单位：MB，默认：10，最大100
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

const XImgUpload: XInputImgUploadSchema = {
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
    uploadTypeConfig,
    listTypeConfig,
    imageHandleConfig,
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
      text: '图片上传',
      display: true
    },
    tooltip: '',
    dataField: [],
    uploadType: UPLOAD_VALUES[UPLOAD_OPTIONS.TEXT],
    listType: UPLOAD_VALUES[UPLOAD_OPTIONS.CARD],
    imageHandle: {
      autoCompress: false, // 自动压缩图片
      addWatermark: false, //添加水印
      watermarkText: '' // 水印文案
    },
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

export default XImgUpload;
