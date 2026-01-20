import {
    alertContentConfig,
    alertStatusConfig,
    alertTypeConfig,
    allowCloseConfig,
    baseDefault,
    labelConfig,
    showIconConfig,
    TAlertTypeSelectKeyType,
    TStatusSelectKeyType,
    widthConfig,
    type ICommonBaseType,
    type TWidthSelectKeyType
} from '../../../common';
import {
    STATUS_OPTIONS,
    STATUS_VALUES,
    WIDTH_OPTIONS,
    WIDTH_VALUES
} from '../../../constants';
import type {
    IAlertContentConfigType,
    IAlertTypeConfigType,
    ICommonConfigType,
    ILabelConfigType,
    IWidthConfigType,
    TBooleanDefaultType,
    TRadioDefaultType,
    TSelectDefaultType,
    TTextDefaultType
} from '../../../types';

  export interface XAlertSchema {
    editData: TXAlertEditData;
    config: XAlertConfig;
  }

  export type TXAlertEditData = Array<
    | ILabelConfigType
    | IAlertContentConfigType
    | IAlertTypeConfigType<TAlertTypeSelectKeyType>
    | IWidthConfigType<TWidthSelectKeyType>
    | ICommonConfigType
  >;

  export interface XAlertConfig extends ICommonBaseType {
    label: {
      text: TTextDefaultType;
      display: TBooleanDefaultType;
    };
    content: {
        text: TTextDefaultType;
        display: TBooleanDefaultType;
    };
    showIcon: TBooleanDefaultType;
    allowClose: TBooleanDefaultType;

    status?: TRadioDefaultType<TStatusSelectKeyType>;
    alertType: TSelectDefaultType<TAlertTypeSelectKeyType>;

    /**
     * 字段宽度
     */
    width: TRadioDefaultType<TWidthSelectKeyType>;
  }

  const XAlert: XAlertSchema = {
    editData: [
      labelConfig,
      alertContentConfig,
      widthConfig,
      showIconConfig,
      allowCloseConfig,
      alertStatusConfig,
      alertTypeConfig,
    ],
    config: {
      ...baseDefault,
      label: {
        text: '提示框',
        display: true
      },
      content: {
        text: '提示信息',
        display: true
      },
      alertType: 'info',
      showIcon: false,
      allowClose: false,
      status: STATUS_VALUES[STATUS_OPTIONS.DEFAULT],
      width: WIDTH_VALUES[WIDTH_OPTIONS.FULL]
    }
  };

  export default XAlert;
