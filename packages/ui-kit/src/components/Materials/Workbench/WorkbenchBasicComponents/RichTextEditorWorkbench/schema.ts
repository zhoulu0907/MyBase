import {
  workbenchBaseConfig,
  workbenchBaseDefault,
  workbenchDataFieldConfig,
  workbenchDefaultValueConfig,
  type ICommonBaseWorkbenchType,
  type TWorkbenchWidthSelectKeyType,
} from '../../config/workbenchShared';
import {
  WIDTH_OPTIONS,
  WIDTH_VALUES,
  DEFAULT_VALUE_TYPES
} from '../../core/constants';
import type {
  IDataFieldConfigType,
  ILabelConfigType,
  TBooleanDefaultType,
  TRadioDefaultType,
  TSelectDefaultType,
  TTextDefaultType,
  IDefaultValueConfigType
} from '../../core/types';
import type {TWbColorDefaultType} from '../../core/types';
import { WORKBENCH_CONFIG_TYPES, WORKBENCH_STATUS_OPTIONS, WORKBENCH_STATUS_VALUES } from '../../core/constants';
import type { TWorkbenchStatusSelectKeyType } from '../../config/workbenchShared';

export interface XRichTextSchema {
  editData: TXRichTextEditData;
  config: XRichTextConfig;
}

export type TXRichTextEditData = Array<
  | ILabelConfigType
  | IDataFieldConfigType
  | IDefaultValueConfigType
>;

export interface XRichTextConfig extends ICommonBaseWorkbenchType {
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
  Wb_Color: TWbColorDefaultType;

  /**
   * 富文本内容
   */
  Wb_RichTextContent: string;

  /**
   * 默认值
   */
  defaultValueConfig?: any;

  /**
   * 字段宽度
   */
  width: TSelectDefaultType<TWorkbenchWidthSelectKeyType>;

  /**
   * 组件状态：可用、隐藏、只读
   * 可选值: 'default' | 'hidden' | 'readonly'
   */
  status?: TRadioDefaultType<TWorkbenchStatusSelectKeyType>;
}

const XRichText: XRichTextSchema = {
  editData: [
    ...workbenchBaseConfig,
    {
      key: 'label',
      name: '标题名称',
      type: WORKBENCH_CONFIG_TYPES.LABEL_INPUT
    },
    {
      key: 'Wb_Color',
      name: '背景颜色',
      type: WORKBENCH_CONFIG_TYPES.WB_COLOR
    },
    {
      key: 'Wb_RichTextContent',
      name: '富文本内容',
      type: WORKBENCH_CONFIG_TYPES.WB_RICH_TEXT_CONTENT
    },
    //  数据绑定
    ...workbenchDataFieldConfig,
    // 默认值
    workbenchDefaultValueConfig
  ],
  config: {
    ...workbenchBaseDefault,
    label: {
      text: '富文本',
      display: true
    },
    width: WIDTH_VALUES[WIDTH_OPTIONS.FULL],
    defaultValueConfig: {
      type: DEFAULT_VALUE_TYPES.CUSTOM,
      customValue: ''
    },
    Wb_Color: '#ffffff',
    Wb_RichTextContent: '',
    status: WORKBENCH_STATUS_VALUES[WORKBENCH_STATUS_OPTIONS.DEFAULT]
  }
};

export default XRichText;
