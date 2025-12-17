import {
  baseDefault,
  widthConfig,
  labelConfig,
  dividerTooltipConfig,
  dividerStyleTypeConfig,
  type ICommonBaseType,
  type TWidthSelectKeyType
} from '../../../common';
import {
  WIDTH_OPTIONS,
  WIDTH_VALUES
} from '../../../constants';
import type {
  ILabelConfigType,
  IWidthConfigType,
  TBooleanDefaultType,
  TTextDefaultType,
  TRadioDefaultType,
  IDividerTooltipConfigType,
  IDividerStyleTypeConfigType,
  ICommonConfigType
} from '../../../types';

export interface XDividerSchema {
  editData: TXDividerEditData;
  config: XDividerConfig;
}

export type TXDividerEditData = Array<
  | ILabelConfigType
  | IDividerTooltipConfigType
  | IDividerStyleTypeConfigType
  | IWidthConfigType<TWidthSelectKeyType>
  | ICommonConfigType
>;

export interface XDividerConfig extends ICommonBaseType {
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
   * 描述信息
   */
  tooltip?: {
    text: TTextDefaultType;
    display: TBooleanDefaultType;
  };

  /**
   * 样式
   */
  styleType: TTextDefaultType;

  /**
   * 配色
  */
  color: TTextDefaultType;

  /**
   * 标题颜色
  */
  titleColor: TTextDefaultType;

  /**
   * 描述颜色
   */
  descriptionColor: TTextDefaultType;

  /**
   * 字段宽度
   */
  width: TRadioDefaultType<TWidthSelectKeyType>;
}

const XDivider: XDividerSchema = {
  editData: [
    labelConfig,
    dividerTooltipConfig,
    dividerStyleTypeConfig,
    widthConfig,
  ],
  config: {
    ...baseDefault,
    label: {
      text: '分割线',
      display: true
    },
    tooltip: {
      text: '',
      display: true
    },
    styleType: 'style1',
    color: '#C9CDD4',
    titleColor: '#1D2129',
    descriptionColor: '#86909C',
    width: WIDTH_VALUES[WIDTH_OPTIONS.HALF],
  }
};

export default XDivider;
