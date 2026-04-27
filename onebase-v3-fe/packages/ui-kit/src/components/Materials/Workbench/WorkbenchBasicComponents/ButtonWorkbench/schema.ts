import {
  type ILabelConfigType,
  type INumberConfigType,
  type ITextConfigType,
  type IStatusConfigType,
  type IAlignConfigType,
  type IWbColorConfigType,
  type IColorConfigType,
  type TBooleanDefaultType,
  type TRadioDefaultType,
  type TSelectDefaultType,
  type TTextDefaultType,
  type TNumberDefaultType,
  type TWbColorDefaultType,
  type IWbSliderConfigType,
  TWbTextAlignDefaultType,
  IWbTextAlignConfigType,
  IWbMenuSelectorConfigType,
} from '../../core/types';
import { TWorkbenchWidthSelectKeyType, workbenchBaseConfig, workbenchBaseDefault, type ICommonBaseWorkbenchType, type TWorkbenchStatusSelectKeyType } from '../../config/workbenchShared';
import {
  WORKBENCH_STATUS_OPTIONS,
  WORKBENCH_STATUS_VALUES,
  WORKBENCH_CONFIG_TYPES,
  CONFIG_TYPES,
  VERTICAL_ALIGN_OPTIONS,
  WORKBENCH_WIDTH_OPTIONS,
  WORKBENCH_WIDTH_VALUES
} from '../../core/constants';
import { ALIGN_OPTIONS, ALIGN_VALUES } from '../../../constants';
import {
  buttonBackgroundColorConfig,
  buttonLabelConfig,
  buttonTextAlignConfig,
  buttonTextColorConfig,
  buttonTextSizeConfig,
  jumpExternalUrlConfig,
  jumpPageIdConfig,
  jumpTypeConfig
} from '../../config/commonConfig';
export interface XButtonWorkbenchSchema {
  editData: TXButtonWorkbenchEditData;
  config: XButtonWorkbenchConfig;
}

export type TXButtonWorkbenchEditData = Array<
  | ILabelConfigType
  | IWbColorConfigType
  | IColorConfigType
  | INumberConfigType
  | IAlignConfigType<any>
  | IStatusConfigType<any>
  | ITextConfigType
  | IWbSliderConfigType
  | IWbTextAlignConfigType
  | IWbMenuSelectorConfigType
>;

export interface XButtonWorkbenchConfig extends ICommonBaseWorkbenchType {
  /**
   * 按钮标题
   * text：标题名称
   * display：是否显示标题
   */
  label: {
    text: TTextDefaultType;
    display: TBooleanDefaultType;
  };

  /**
   * 按钮背景颜色
   */
  backgroundColor: TWbColorDefaultType;

  /**
   * 按钮文本颜色
   */
  textColor: string;

  /**
   * 按钮文本大小
   */
  textSize: TNumberDefaultType;

  /**
   * 按钮文本对齐方式
   */
  textAlign: TWbTextAlignDefaultType;

  /**
   * 跳转类型：关联已有页面 | 跳转外部链接
   */
  jumpType: TRadioDefaultType<string>;

  /**
   * 关联的页面ID（当跳转类型为关联已有页面时使用）
   */
  jumpPageId?: TTextDefaultType;

  /**
   * 外部链接地址（当跳转类型为跳转外部链接时使用）
   */
  jumpExternalUrl?: TTextDefaultType;

  /**
   * 组件状态：可用、隐藏、只读
   * 可选值: 'default' | 'hidden' | 'readonly'
   */
  status?: TRadioDefaultType<TWorkbenchStatusSelectKeyType>;

  /**
   * 组件宽度
   */
  width: TSelectDefaultType<TWorkbenchWidthSelectKeyType>;
}

const XButtonWorkbench: XButtonWorkbenchSchema = {
  editData: [
    ...workbenchBaseConfig,
    buttonLabelConfig,
    buttonBackgroundColorConfig,
    buttonTextColorConfig,
    buttonTextSizeConfig,
    buttonTextAlignConfig,
    jumpTypeConfig,
    jumpPageIdConfig,
    jumpExternalUrlConfig
  ],
  config: {
    ...workbenchBaseDefault,
    label: {
      text: '按钮',
      display: true
    },
    backgroundColor: 'rgb(var(--primary-6))',
    textColor: '#FFFFFF',
    textSize: 16,
    textAlign: {
      horizontal: ALIGN_VALUES[ALIGN_OPTIONS.CENTER],
      vertical: VERTICAL_ALIGN_OPTIONS.MIDDLE
    },
    jumpType: 'internal',
    jumpPageId: '',
    jumpExternalUrl: '',
    status: WORKBENCH_STATUS_VALUES[WORKBENCH_STATUS_OPTIONS.DEFAULT],
    width: WORKBENCH_WIDTH_VALUES[WORKBENCH_WIDTH_OPTIONS.FULL],
  }
};

export default XButtonWorkbench;
