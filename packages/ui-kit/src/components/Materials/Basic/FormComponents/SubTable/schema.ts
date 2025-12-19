import {
  baseConfig,
  baseDefault,
  layoutConfig,
  statusConfig,
  subTableConfig,
  widthConfig,
  type ICommonBaseType,
  type TLayoutSelectKeyType,
  type TStatusSelectKeyType,
  type TWidthSelectKeyType
} from '../../../common';
import {
  COLUMN_COUNT_OPTIONS,
  CONFIG_TYPES,
  STATUS_OPTIONS,
  STATUS_VALUES,
  WIDTH_OPTIONS,
  WIDTH_VALUES
} from '../../../constants';
import type {
  IColumnCountConfigType,
  ILabelConfigType,
  ILayoutConfigType,
  INumberConfigType,
  ISelectConfigType,
  IStatusConfigType,
  ISubTableConfigType,
  ITextConfigType,
  ITooltipConfigType,
  IVerifyConfigType,
  IWidthConfigType,
  TBooleanDefaultType,
  TSelectDefaultType,
  TTextAreaDefaultType,
  TTextDefaultType
} from '../../../types';

export type TColumnCountSelectKeyType = (typeof COLUMN_COUNT_OPTIONS)[keyof typeof COLUMN_COUNT_OPTIONS];
export type TColumnLayouTWidthSelectKeyType = (typeof WIDTH_VALUES)[keyof typeof WIDTH_VALUES];

export type TColumnLayoutEditData = Array<
  | ITextConfigType
  | IColumnCountConfigType<TColumnCountSelectKeyType>
  | IWidthConfigType<TColumnLayouTWidthSelectKeyType>
  | ILabelConfigType
  | ISelectConfigType<TColumnLayouTWidthSelectKeyType | TColumnCountSelectKeyType>
  | IStatusConfigType<TStatusSelectKeyType>
  | IVerifyConfigType
  | ILayoutConfigType<TLayoutSelectKeyType>
  | ITooltipConfigType
  | INumberConfigType
  | ISubTableConfigType
>;

export interface XSubTableConfig extends ICommonBaseType {
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
   * required：是否必填，未填写时提交报错
   * noRepeat: 不允许重复
   * maxLength：子字段长度
   * allowNull：子字段空行校验
   */
  verify: {
    required: TBooleanDefaultType;
  };
  subTableConfig?: {
    showIndex: boolean;       // 显示序号列
    showOperate: boolean;     // 显示操作列
    editRow: boolean;         // 可编辑已有数据
    deleteRow: boolean;       // 可删除已有数据
    operateFixed: boolean;    // 操作列冻结
    pageSize: number;         // 分页条数
    columnFixed: number;      // 左侧列冻结
  },
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

  pageType?: string;
}

export interface XSubTableSchema {
  editData: TColumnLayoutEditData;
  config: XSubTableConfig;
}

const XSubTable: XSubTableSchema = {
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
    {
      key: 'verify',
      name: '校验',
      type: CONFIG_TYPES.VERIFY
    },
    subTableConfig,
    // 显示状态
    statusConfig,
    // layoutConfig,
    widthConfig
  ],
  config: {
    ...baseDefault,
    label: {
      text: '子表单',
      display: true
    },
    tooltip: '',
    verify: {
      required: false,
    },
    subTableConfig: {
      showIndex: true,
      showOperate: true,
      editRow: true,
      deleteRow: true,
      operateFixed: true,
      pageSize: 5,
      columnFixed: 0
    },
    status: STATUS_VALUES[STATUS_OPTIONS.DEFAULT],
    width: WIDTH_VALUES[WIDTH_OPTIONS.FULL],
  }
};

export default XSubTable;
