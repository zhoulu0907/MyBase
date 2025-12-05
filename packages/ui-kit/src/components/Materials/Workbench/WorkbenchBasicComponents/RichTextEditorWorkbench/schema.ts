import {
  baseConfig,
  baseDefault,
  dataFieldConfig,
  defaultValueConfig,
  type ICommonBaseType,
  type TWidthSelectKeyType,
} from '../../../common';
import {
  WIDTH_OPTIONS,
  WIDTH_VALUES,
  DEFAULT_VALUE_TYPES
} from '../../../constants';
import type {
  IDataFieldConfigType,
  ILabelConfigType,
  TBooleanDefaultType,
  TRadioDefaultType,
  TSelectDefaultType,
  TTextDefaultType,
  IDefaultValueConfigType
} from '../../../types';
import type {TWbColorDefaultType} from '../../types';
import { WORKBENCH_CONFIG_TYPES, WORKBENCH_STATUS_OPTIONS, WORKBENCH_STATUS_VALUES } from '../../constants';
import type { TWorkbenchStatusSelectKeyType } from '../../workbenchShared';

export interface XRichTextSchema {
  editData: TXRichTextEditData;
  config: XRichTextConfig;
}

export type TXRichTextEditData = Array<
  | ILabelConfigType
  | IDataFieldConfigType
  | IDefaultValueConfigType
>;

export interface XRichTextConfig extends ICommonBaseType {
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
   * 背景颜色
   */
  WbColor: TWbColorDefaultType;

  /**
   * 富文本内容
   */
  WbRichTextContent: string;

  /**
   * 默认值
   */
  defaultValueConfig?: any;

  /**
   * 字段宽度
   */
  width: TSelectDefaultType<TWidthSelectKeyType>;

  /**
   * 组件状态：可用、隐藏、只读
   * 可选值: 'default' | 'hidden' | 'readonly'
   */
  status?: TRadioDefaultType<TWorkbenchStatusSelectKeyType>;
}

const XRichText: XRichTextSchema = {
  editData: [
    ...baseConfig,
    {
      key: 'label',
      name: '标题名称',
      type: WORKBENCH_CONFIG_TYPES.LABEL_INPUT
    },
    {
      key: 'WbColor',
      name: '背景颜色',
      type: WORKBENCH_CONFIG_TYPES.WB_COLOR
    },
    {
      key: 'WbRichTextContent',
      name: '富文本内容',
      type: WORKBENCH_CONFIG_TYPES.WB_RICH_TEXT_CONTENT
    },
    //  数据绑定
    ...dataFieldConfig,
    // 默认值
    defaultValueConfig
  ],
  config: {
    ...baseDefault,
    label: {
      text: '富文本',
      display: true
    },
    width: WIDTH_VALUES[WIDTH_OPTIONS.FULL],
    defaultValueConfig: {
      type: DEFAULT_VALUE_TYPES.CUSTOM,
      customValue: ''
    },
    WbColor: '#ffffff',
    WbRichTextContent: '',
    status: WORKBENCH_STATUS_VALUES[WORKBENCH_STATUS_OPTIONS.DEFAULT]
  }
};

export default XRichText;
